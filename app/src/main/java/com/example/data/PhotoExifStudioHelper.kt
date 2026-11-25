package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import com.example.model.PhotoItem
import com.example.player.VideoStudioUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class PhotoExifInfo(
    val cameraMakeModel: String?,
    val dateTime: String?,
    val dimensions: String?,
    val aperture: String?,
    val exposureTime: String?,
    val iso: String?,
    val focalLength: String?,
) {
    val hasCameraData: Boolean
        get() = listOf(cameraMakeModel, dateTime, aperture, exposureTime, iso, focalLength).any { !it.isNullOrBlank() }
}

object PhotoExifStudioHelper {

    suspend fun readExif(context: Context, photo: PhotoItem): PhotoExifInfo = withContext(Dispatchers.IO) {
        val defaultDims = if (photo.width > 0 && photo.height > 0) "${photo.width} × ${photo.height} px" else null
        if (photo.resId != null || photo.uriString.isBlank()) {
            return@withContext PhotoExifInfo(
                cameraMakeModel = null,
                dateTime = null,
                dimensions = defaultDims,
                aperture = null,
                exposureTime = null,
                iso = null,
                focalLength = null
            )
        }

        return@withContext runCatching {
            context.contentResolver.openInputStream(Uri.parse(photo.uriString))?.use { stream ->
                val exif = ExifInterface(stream)
                val make = exif.getAttribute(ExifInterface.TAG_MAKE)?.trim().orEmpty()
                val model = exif.getAttribute(ExifInterface.TAG_MODEL)?.trim().orEmpty()
                val camera = listOf(make, model)
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(" ")
                    .takeIf { it.isNotBlank() }

                val w = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, photo.width)
                val h = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, photo.height)
                val dims = if (w > 0 && h > 0) "$w × $h px" else defaultDims

                val date = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                    ?: exif.getAttribute(ExifInterface.TAG_DATETIME)

                val fNum = exif.getAttribute(ExifInterface.TAG_F_NUMBER)?.let { "f/$it" }
                val exp = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.toDoubleOrNull()?.let { sec ->
                    if (sec > 0.0 && sec < 1.0) "1/${(1.0 / sec).toInt()}s" else "${sec}s"
                }
                val iso = (exif.getAttribute("ISOSpeedRatings") ?: exif.getAttribute("PhotographicSensitivity"))
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "ISO $it" }
                val focal = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let { raw ->
                    val parts = raw.split("/")
                    if (parts.size == 2) {
                        val num = parts[0].toDoubleOrNull()
                        val den = parts[1].toDoubleOrNull()
                        if (num != null && den != null && den > 0.0) {
                            String.format(java.util.Locale.US, "%.1f mm", num / den)
                        } else raw
                    } else raw
                }

                PhotoExifInfo(
                    cameraMakeModel = camera,
                    dateTime = date,
                    dimensions = dims,
                    aperture = fNum,
                    exposureTime = exp,
                    iso = iso,
                    focalLength = focal
                )
            }
        }.getOrNull() ?: PhotoExifInfo(null, null, defaultDims, null, null, null, null)
    }

    /**
     * Applies current rotation (0/90/180/270) and Studio color filter matrix to the photo
     * and saves a full-resolution copy into Pictures/Prism via MediaStore.
     */
    suspend fun saveEditedCopy(
        context: Context,
        photo: PhotoItem,
        rotationDegrees: Int,
        colorMatrixValues: FloatArray?,
    ): Boolean = withContext(Dispatchers.IO) {
        val sourceBitmap: Bitmap = runCatching {
            when {
                photo.resId != null -> BitmapFactory.decodeResource(context.resources, photo.resId)
                photo.uriString.isNotBlank() -> {
                    context.contentResolver.openInputStream(Uri.parse(photo.uriString))?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                }
                else -> null
            }
        }.getOrNull() ?: return@withContext false

        val normalizedRot = ((rotationDegrees % 360) + 360) % 360
        val rotated = if (normalizedRot != 0) {
            val matrix = Matrix().apply { postRotate(normalizedRot.toFloat()) }
            Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true)
        } else {
            sourceBitmap
        }

        val finalBitmap = if (colorMatrixValues != null) {
            val out = Bitmap.createBitmap(rotated.width, rotated.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(out)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix(colorMatrixValues))
            }
            canvas.drawBitmap(rotated, 0f, 0f, paint)
            out
        } else {
            rotated
        }

        val safeName = photo.title
            .replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_")
            .take(36)
            .ifBlank { "Prism_Edit" }
        val fileName = "${safeName}_edited_${System.currentTimeMillis()}.png"
        val saved = VideoStudioUtils.saveBitmapToPictures(context, finalBitmap, fileName)

        if (finalBitmap !== rotated && finalBitmap !== sourceBitmap) runCatching { finalBitmap.recycle() }
        if (rotated !== sourceBitmap) runCatching { rotated.recycle() }
        runCatching { sourceBitmap.recycle() }
        saved
    }

    /**
     * Re-encodes a photo into a fresh private cache file stripping ALL EXIF metadata
     * (GPS coordinates, camera model, timestamps) and returns a safe FileProvider URI for sharing.
     */
    suspend fun createCleanShareUri(context: Context, photo: PhotoItem): Uri? = withContext(Dispatchers.IO) {
        val bitmap: Bitmap = decodePhotoBitmap(context, photo, maxDim = 2400) ?: return@withContext null
        val sharedDir = java.io.File(context.cacheDir, "shared").apply { mkdirs() }
        val safeName = photo.title
            .replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_")
            .take(36)
            .ifBlank { "Prism_Clean" }
        val cleanFile = java.io.File(sharedDir, "${safeName}_clean_${System.currentTimeMillis()}.jpg")
        val ok = runCatching {
            cleanFile.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 94, out)
            }
            true
        }.getOrDefault(false)
        runCatching { bitmap.recycle() }
        if (!ok) return@withContext null
        runCatching {
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cleanFile
            )
        }.getOrNull()
    }

    /**
     * Renders a clean HD Photo Collage (2-4 photos) with optional custom watermark text
     * and saves directly into Pictures/Prism via MediaStore.
     */
    suspend fun createCollageAndSave(
        context: Context,
        photos: List<PhotoItem>,
        layout: com.example.model.CollageLayoutMode,
        watermarkText: String = "",
        canvasSizePx: Int = 1600,
    ): Boolean = withContext(Dispatchers.IO) {
        if (photos.isEmpty()) return@withContext false
        val bitmaps = photos.take(4).mapNotNull { decodePhotoBitmap(context, it, maxDim = 1000) }
        if (bitmaps.isEmpty()) return@withContext false

        val out = Bitmap.createBitmap(canvasSizePx, canvasSizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.parseColor("#14121B") }
        canvas.drawRect(0f, 0f, canvasSizePx.toFloat(), canvasSizePx.toFloat(), bgPaint)

        val gap = (canvasSizePx * 0.018f)
        val half = (canvasSizePx - gap * 3f) / 2f
        val full = canvasSizePx - gap * 2f

        val rects: List<android.graphics.RectF> = when {
            bitmaps.size == 1 -> listOf(android.graphics.RectF(gap, gap, gap + full, gap + full))
            layout == com.example.model.CollageLayoutMode.SIDE_BY_SIDE || bitmaps.size == 2 && layout != com.example.model.CollageLayoutMode.STACKED_2 -> listOf(
                android.graphics.RectF(gap, gap, gap + half, gap + full),
                android.graphics.RectF(gap * 2f + half, gap, canvasSizePx - gap, gap + full),
            )
            layout == com.example.model.CollageLayoutMode.STACKED_2 -> listOf(
                android.graphics.RectF(gap, gap, gap + full, gap + half),
                android.graphics.RectF(gap, gap * 2f + half, gap + full, canvasSizePx - gap),
            )
            layout == com.example.model.CollageLayoutMode.HERO_TRIO && bitmaps.size >= 3 -> listOf(
                android.graphics.RectF(gap, gap, gap + full, gap + half * 1.15f),
                android.graphics.RectF(gap, gap * 2f + half * 1.15f, gap + half, canvasSizePx - gap),
                android.graphics.RectF(gap * 2f + half, gap * 2f + half * 1.15f, canvasSizePx - gap, canvasSizePx - gap),
            )
            else -> listOf(
                android.graphics.RectF(gap, gap, gap + half, gap + half),
                android.graphics.RectF(gap * 2f + half, gap, canvasSizePx - gap, gap + half),
                android.graphics.RectF(gap, gap * 2f + half, gap + half, canvasSizePx - gap),
                android.graphics.RectF(gap * 2f + half, gap * 2f + half, canvasSizePx - gap, canvasSizePx - gap),
            )
        }

        val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        rects.forEachIndexed { idx, dst ->
            val srcBmp = bitmaps[idx % bitmaps.size]
            drawCenterCropped(canvas, srcBmp, dst, cornerRadius = 28f, paint = imgPaint)
        }

        if (watermarkText.isNotBlank()) {
            val wmClean = watermarkText.trim().take(48)
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.argb(220, 255, 255, 255)
                textSize = canvasSizePx * 0.032f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                setShadowLayer(8f, 0f, 3f, android.graphics.Color.argb(180, 0, 0, 0))
            }
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.argb(120, 18, 16, 26)
            }
            val tw = textPaint.measureText(wmClean)
            val padX = 28f
            val padY = 16f
            val right = canvasSizePx - gap * 1.6f
            val bottom = canvasSizePx - gap * 1.6f
            val bgRect = android.graphics.RectF(right - tw - padX * 2f, bottom - textPaint.textSize - padY * 2f, right, bottom)
            canvas.drawRoundRect(bgRect, 24f, 24f, pillPaint)
            canvas.drawText(wmClean, bgRect.left + padX, bgRect.bottom - padY - 6f, textPaint)
        }

        val fileName = "Prism_Collage_${System.currentTimeMillis()}.png"
        val saved = VideoStudioUtils.saveBitmapToPictures(context, out, fileName)
        bitmaps.forEach { runCatching { it.recycle() } }
        runCatching { out.recycle() }
        saved
    }

    private fun drawCenterCropped(
        canvas: Canvas,
        bitmap: Bitmap,
        dst: android.graphics.RectF,
        cornerRadius: Float,
        paint: Paint,
    ) {
        val bw = bitmap.width.toFloat().coerceAtLeast(1f)
        val bh = bitmap.height.toFloat().coerceAtLeast(1f)
        val scale = maxOf(dst.width() / bw, dst.height() / bh)
        val scaledW = bw * scale
        val scaledH = bh * scale
        val dx = dst.left + (dst.width() - scaledW) / 2f
        val dy = dst.top + (dst.height() - scaledH) / 2f
        val save = canvas.save()
        val path = android.graphics.Path().apply {
            addRoundRect(dst, cornerRadius, cornerRadius, android.graphics.Path.Direction.CW)
        }
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, null, android.graphics.RectF(dx, dy, dx + scaledW, dy + scaledH), paint)
        canvas.restoreToCount(save)
    }

    private fun decodePhotoBitmap(context: Context, photo: PhotoItem, maxDim: Int): Bitmap? {
        return runCatching {
            val raw = when {
                photo.resId != null -> BitmapFactory.decodeResource(context.resources, photo.resId)
                photo.uriString.isNotBlank() -> {
                    context.contentResolver.openInputStream(Uri.parse(photo.uriString))?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                else -> null
            } ?: return@runCatching null
            val longest = maxOf(raw.width, raw.height)
            if (longest > maxDim) {
                val scale = maxDim.toFloat() / longest.toFloat()
                val scaled = Bitmap.createScaledBitmap(
                    raw,
                    (raw.width * scale).toInt().coerceAtLeast(1),
                    (raw.height * scale).toInt().coerceAtLeast(1),
                    true
                )
                if (scaled !== raw) raw.recycle()
                scaled
            } else {
                raw
            }
        }.getOrNull()
    }
}
