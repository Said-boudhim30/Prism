package com.example.player

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaCodecList
import android.media.Spatializer
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kotlin.math.roundToInt

/**
 * Live hardware driver & codec profile detected directly from the user's Android device.
 * Used to auto-configure Media3 audio/video pipelines and high-resolution photo decoding
 * so Prism delivers full native fidelity tailored to each device.
 */
data class DeviceDriverProfile(
    val hwVideoCodecs: List<String>,
    val hwAudioCodecs: List<String>,
    val maxVideoWidth: Int,
    val maxVideoHeight: Int,
    val maxVideoTierLabel: String,
    val hdrFormats: List<String>,
    val wideColorGamut: Boolean,
    val refreshRateHz: Int,
    val nativeSampleRateHz: Int,
    val nativeFramesPerBuffer: Int,
    val supportsFloatPcm: Boolean,
    val supportsSpatialAudio: Boolean,
    val activeAudioOutputDriver: String,
    val hardwareVideoAvailable: Boolean,
) {
    val audioBadgeLabel: String
        get() {
            val khz = if (nativeSampleRateHz % 1000 == 0) {
                "${nativeSampleRateHz / 1000}kHz"
            } else {
                String.format(java.util.Locale.US, "%.1fkHz", nativeSampleRateHz / 1000f)
            }
            val depth = if (supportsFloatPcm) "Float32" else "PCM16"
            val spatial = if (supportsSpatialAudio) " • Spatial" else ""
            return "$activeAudioOutputDriver • $khz $depth$spatial"
        }

    val videoBadgeLabel: String
        get() {
            val codecs = hwVideoCodecs.take(3).joinToString("/").ifBlank { "SW" }
            val hdr = hdrFormats.firstOrNull()?.let { " • $it" } ?: ""
            return "${if (hardwareVideoAvailable) "HW" else "SW"} $codecs • $maxVideoTierLabel$hdr"
        }

    val displayBadgeLabel: String
        get() {
            val color = if (wideColorGamut) "DCI-P3" else "sRGB"
            val hdr = if (hdrFormats.isNotEmpty()) " • ${hdrFormats.joinToString("/")}" else ""
            return "${refreshRateHz}Hz • $color$hdr"
        }
}

object DeviceMediaDriverEngine {

    /**
     * Probes the installed Android device's real hardware video/audio codecs,
     * native audio HAL sample rate, output device driver, Spatializer, and HDR display capabilities.
     */
    @Suppress("DEPRECATION")
    fun probe(context: Context): DeviceDriverProfile {
        val hwVideoSet = linkedSetOf<String>()
        val hwAudioSet = linkedSetOf<String>()
        var maxW = 1920
        var maxH = 1080
        var anyHwVideo = false

        runCatching {
            val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
            for (info in codecList.codecInfos) {
                if (info.isEncoder) continue
                val nameLower = info.name.lowercase(java.util.Locale.US)
                val isSoftware = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.isSoftwareOnly
                } else {
                    nameLower.startsWith("omx.google.") ||
                        nameLower.startsWith("c2.android.") ||
                        nameLower.contains(".sw.")
                }
                val isHardware = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    info.isHardwareAccelerated
                } else {
                    !isSoftware
                }

                for (mime in info.supportedTypes) {
                    val m = mime.lowercase(java.util.Locale.US)
                    if (m.startsWith("video/") && isHardware) {
                        anyHwVideo = true
                        when {
                            m.contains("av01") -> hwVideoSet += "AV1"
                            m.contains("hevc") -> hwVideoSet += "HEVC"
                            m.contains("avc") -> hwVideoSet += "H.264"
                            m.contains("vp9") -> hwVideoSet += "VP9"
                            m.contains("dolby-vision") -> hwVideoSet += "Dolby Vision"
                            m.contains("vp8") -> hwVideoSet += "VP8"
                        }
                        runCatching {
                            val vCaps = info.getCapabilitiesForType(mime).videoCapabilities
                            if (vCaps != null) {
                                val upperW = vCaps.supportedWidths.upper
                                val upperH = vCaps.supportedHeights.upper
                                if (upperW > maxW) maxW = upperW
                                if (upperH > maxH) maxH = upperH
                            }
                        }
                    } else if (m.startsWith("audio/")) {
                        when {
                            m.contains("flac") -> hwAudioSet += "FLAC"
                            m.contains("opus") -> hwAudioSet += "Opus"
                            m.contains("mp4a") -> hwAudioSet += "AAC"
                            m.contains("mpeg") -> hwAudioSet += "MP3"
                            m.contains("eac3") -> hwAudioSet += "E-AC3"
                            m.contains("ac3") -> hwAudioSet += "AC3"
                            m.contains("vorbis") -> hwAudioSet += "Vorbis"
                            m.contains("raw") -> hwAudioSet += "Hi-Res PCM"
                        }
                    }
                }
            }
        }

        val maxTier = when {
            maxW >= 7680 || maxH >= 4320 -> "8K UHD"
            maxW >= 3840 || maxH >= 2160 -> "4K UHD"
            maxW >= 2560 || maxH >= 1440 -> "2K QHD"
            else -> "1080p HD"
        }

        // Audio HAL Native Sample Rate, Buffer, Output Route & Spatializer
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val nativeSampleRate = runCatching {
            audioManager?.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toIntOrNull()
        }.getOrNull()?.takeIf { it >= 8000 } ?: 48000

        val nativeFrames = runCatching {
            audioManager?.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toIntOrNull()
        }.getOrNull()?.takeIf { it > 0 } ?: 256

        val activeOutputDriver = detectActiveAudioOutputDriver(audioManager)
        val supportsFloatPcm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        val supportsSpatial = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2 && audioManager != null) {
                val sp = audioManager.spatializer
                sp.immersiveAudioLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE && sp.isAvailable
            } else {
                false
            }
        }.getOrDefault(false)

        // Display HDR, Wide Color Gamut & Refresh Rate
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val display: Display? = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display
            } else {
                wm?.defaultDisplay
            }
        }.getOrNull()

        val refreshHz = runCatching { display?.refreshRate?.roundToInt() }
            .getOrNull()?.coerceIn(60, 240) ?: 60

        val hdrFormats = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && display != null) {
            runCatching {
                val caps = display.hdrCapabilities
                caps?.supportedHdrTypes?.forEach { type ->
                    when (type) {
                        Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> hdrFormats += "Dolby Vision"
                        Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> hdrFormats += "HDR10+"
                        Display.HdrCapabilities.HDR_TYPE_HDR10 -> hdrFormats += "HDR10"
                        Display.HdrCapabilities.HDR_TYPE_HLG -> hdrFormats += "HLG"
                    }
                }
            }
        }

        val wideColor = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.resources.configuration.isScreenWideColorGamut
            } else {
                false
            }
        }.getOrDefault(false)

        return DeviceDriverProfile(
            hwVideoCodecs = hwVideoSet.toList().ifEmpty { listOf("H.264", "VP9") },
            hwAudioCodecs = hwAudioSet.toList().ifEmpty { listOf("FLAC", "Opus", "AAC", "MP3") },
            maxVideoWidth = maxW,
            maxVideoHeight = maxH,
            maxVideoTierLabel = maxTier,
            hdrFormats = hdrFormats.distinct(),
            wideColorGamut = wideColor,
            refreshRateHz = refreshHz,
            nativeSampleRateHz = nativeSampleRate,
            nativeFramesPerBuffer = nativeFrames,
            supportsFloatPcm = supportsFloatPcm,
            supportsSpatialAudio = supportsSpatial,
            activeAudioOutputDriver = activeOutputDriver,
            hardwareVideoAvailable = anyHwVideo,
        )
    }

    private fun detectActiveAudioOutputDriver(audioManager: AudioManager?): String {
        if (audioManager == null) return "Audio HAL"
        return runCatching {
            val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            when {
                outputs.any { it.type == AudioDeviceInfo.TYPE_USB_HEADSET || it.type == AudioDeviceInfo.TYPE_USB_DEVICE } ->
                    "USB Hi-Res DAC"
                outputs.any { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && it.type == AudioDeviceInfo.TYPE_BLE_HEADSET } ->
                    "LE Audio"
                outputs.any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP } ->
                    "BT Hi-Fi"
                outputs.any { it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET } ->
                    "Wired Hi-Fi"
                outputs.any { it.type == AudioDeviceInfo.TYPE_HDMI || it.type == AudioDeviceInfo.TYPE_HDMI_ARC } ->
                    "HDMI Audio"
                else -> "Device DSP"
            }
        }.getOrDefault("Audio HAL")
    }

    /**
     * Builds an optimized Media3 RenderersFactory tailored to the device's hardware codecs.
     * Prefers hardware acceleration when enabled and seamlessly falls back to software decoding
     * for uncommon container/codec combinations so playback never stalls.
     */
    @OptIn(UnstableApi::class)
    fun buildOptimizedVideoRenderersFactory(
        context: Context,
        profile: DeviceDriverProfile,
        preferHardwareDecoding: Boolean,
    ): DefaultRenderersFactory {
        return DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(
                if (preferHardwareDecoding && profile.hardwareVideoAvailable) {
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                } else {
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                }
            )
            setEnableDecoderFallback(true)
            setEnableAudioFloatOutput(profile.supportsFloatPcm)
            setEnableAudioTrackPlaybackParams(true)
        }
    }

    /**
     * Builds an adaptive Media3 TrackSelector matching the device's real maximum decoded resolution
     * and native display capabilities for full-quality playback.
     */
    @OptIn(UnstableApi::class)
    fun buildOptimizedVideoTrackSelector(
        context: Context,
        profile: DeviceDriverProfile,
    ): DefaultTrackSelector {
        return DefaultTrackSelector(context).apply {
            setParameters(
                buildUponParameters()
                    .setMaxVideoSize(profile.maxVideoWidth.coerceAtLeast(1920), profile.maxVideoHeight.coerceAtLeast(1080))
                    .setForceHighestSupportedBitrate(true)
                    .setAllowVideoNonSeamlessAdaptiveness(true)
                    .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN)
            )
        }
    }
}
