package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.AudioTrack
import com.example.model.PhotoItem
import com.example.model.VideoItem
import com.example.security.ContentPurityGuard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

data class WifiTransferStatus(
    val isRunning: Boolean = false,
    val url: String = "",
    val transferredCount: Int = 0,
    val lastActivity: String = "",
)

/**
 * Cloud-free Local Wi-Fi File Transfer Server.
 * Runs a lightweight HTTP server strictly on the local LAN so the user can browse, download,
 * or upload media between Phone and PC/Tablet over Wi-Fi without cables or internet.
 */
class LocalWifiTransferServer(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaProvider: () -> Triple<List<AudioTrack>, List<VideoItem>, List<PhotoItem>>,
    private val onUploadedFile: (Uri, String) -> Unit,
) {
    private val _status = MutableStateFlow(WifiTransferStatus())
    val status: StateFlow<WifiTransferStatus> = _status.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null

    fun start(port: Int = 8787) {
        if (_status.value.isRunning) return
        serverJob = scope.launch(Dispatchers.IO) {
            try {
                val sock = runCatching { ServerSocket(port) }
                    .getOrElse { ServerSocket(0) }
                serverSocket = sock
                val ip = resolveLocalIpv4Address() ?: "127.0.0.1"
                val url = "http://$ip:${sock.localPort}"
                _status.value = WifiTransferStatus(
                    isRunning = true,
                    url = url,
                    transferredCount = 0,
                    lastActivity = "Ready on LAN"
                )
                while (isActive && !sock.isClosed) {
                    val client = runCatching { sock.accept() }.getOrNull() ?: break
                    launch(Dispatchers.IO) { handleClient(client) }
                }
            } catch (_: Throwable) {
                stop()
            }
        }
    }

    fun stop() {
        runCatching { serverSocket?.close() }
        serverSocket = null
        serverJob?.cancel()
        serverJob = null
        _status.value = _status.value.copy(isRunning = false, url = "")
    }

    private fun handleClient(socket: Socket) {
        socket.use { client ->
            client.soTimeout = 12_000
            val input = BufferedInputStream(client.getInputStream())
            val out = client.getOutputStream()

            val headerBytes = readUntilHeaderEnd(input) ?: return
            val headerText = headerBytes.toString(Charsets.UTF_8)
            val lines = headerText.split("\r\n")
            val reqLine = lines.firstOrNull()?.split(" ") ?: return
            if (reqLine.size < 2) return
            val method = reqLine[0].uppercase()
            val rawPath = reqLine[1]
            val headers = lines.drop(1).associate { line ->
                val idx = line.indexOf(':')
                if (idx > 0) {
                    line.substring(0, idx).trim().lowercase() to line.substring(idx + 1).trim()
                } else "" to ""
            }

            when {
                method == "GET" && (rawPath == "/" || rawPath.startsWith("/index")) -> {
                    serveDashboardHtml(out)
                }
                method == "GET" && rawPath.startsWith("/download/") -> {
                    val parts = rawPath.removePrefix("/download/").split("/", limit = 2)
                    if (parts.size == 2) {
                        val type = parts[0]
                        val id = URLDecoder.decode(parts[1], "UTF-8")
                        serveMediaDownload(type, id, out)
                    } else {
                        sendText(out, 404, "text/plain", "Not Found")
                    }
                }
                method == "POST" && rawPath.startsWith("/upload") -> {
                    val contentLength = headers["content-length"]?.toIntOrNull() ?: 0
                    val rawName = rawPath.substringAfter("name=", "upload_${System.currentTimeMillis()}.bin")
                    val decodedName = URLDecoder.decode(rawName, "UTF-8")
                        .replace(Regex("[^a-zA-Z0-9._\\u0600-\\u06FF-]"), "_")
                        .ifBlank { "upload_${System.currentTimeMillis()}.bin" }
                    handleUpload(input, contentLength, decodedName, out)
                }
                else -> {
                    sendText(out, 404, "text/plain", "Not Found")
                }
            }
        }
    }

    private fun serveDashboardHtml(out: OutputStream) {
        val (audio, videos, photos) = mediaProvider()
        val sb = StringBuilder()
        sb.append(
            """
            <!DOCTYPE html><html lang="ar"><head><meta charset="utf-8">
            <meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Prism Media — Wi-Fi Transfer</title>
            <style>
            body{font-family:system-ui,-apple-system,sans-serif;background:#121018;color:#EFEBF8;margin:0;padding:24px;}
            .card{background:#1D1A26;border:1px solid #352F44;border-radius:18px;padding:18px;margin-bottom:16px;}
            h1{color:#D0BCFF;margin:0 0 6px;} h2{color:#9EE8B5;font-size:18px;margin:0 0 10px;}
            .row{display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid #2A2538;gap:12px;}
            a.btn,button{background:#4F378B;color:#EADDFF;border:none;padding:8px 14px;border-radius:12px;text-decoration:none;font-weight:600;cursor:pointer;}
            input[type=file]{color:#EFEBF8;margin-bottom:10px;}
            </style></head><body>
            <div class="card"><h1>Prism Media • النقل المحلي عبر Wi-Fi</h1>
            <p>أرسل واستقبل ملفاتك مباشرة بين الهاتف والحاسوب بدون إنترنت.</p>
            <input id="f" type="file" accept="audio/*,video/*,image/*"><br>
            <button onclick="up()">رفع إلى Prism / Upload</button>
            <span id="st" style="margin-inline-start:12px;color:#9EE8B5"></span></div>
            <script>
            async function up(){
              const el=document.getElementById('f'); if(!el.files.length) return;
              const file=el.files[0]; document.getElementById('st').textContent='Uploading...';
              const res=await fetch('/upload?name='+encodeURIComponent(file.name),{method:'POST',body:file});
              document.getElementById('st').textContent=await res.text();
            }
            </script>
            """.trimIndent()
        )
        sb.append("<div class=\"card\"><h2>Audio (${audio.size})</h2>")
        audio.take(40).forEach { t ->
            val encId = java.net.URLEncoder.encode(t.id, "UTF-8")
            sb.append("<div class=\"row\"><span>${escapeHtml(t.title)} — ${escapeHtml(t.artist)}</span><a class=\"btn\" href=\"/download/audio/$encId\">Download</a></div>")
        }
        sb.append("</div><div class=\"card\"><h2>Videos (${videos.size})</h2>")
        videos.take(30).forEach { v ->
            val encId = java.net.URLEncoder.encode(v.id, "UTF-8")
            sb.append("<div class=\"row\"><span>${escapeHtml(v.title)}</span><a class=\"btn\" href=\"/download/video/$encId\">Download</a></div>")
        }
        sb.append("</div><div class=\"card\"><h2>Photos (${photos.size})</h2>")
        photos.take(30).forEach { p ->
            val encId = java.net.URLEncoder.encode(p.id, "UTF-8")
            sb.append("<div class=\"row\"><span>${escapeHtml(p.title)}</span><a class=\"btn\" href=\"/download/photo/$encId\">Download</a></div>")
        }
        sb.append("</div></body></html>")
        sendText(out, 200, "text/html; charset=utf-8", sb.toString())
    }

    private fun serveMediaDownload(type: String, id: String, out: OutputStream) {
        val (audio, videos, photos) = mediaProvider()
        val target: Triple<String, String, String>? = when (type) {
            "audio" -> audio.firstOrNull { it.id == id }?.let { Triple(it.uriString, "${it.title}.${it.extension.lowercase()}", it.mimeType) }
            "video" -> videos.firstOrNull { it.id == id }?.let { Triple(it.uriString, "${it.title}.${it.extension.lowercase()}", it.mimeType) }
            "photo" -> photos.firstOrNull { it.id == id }?.let { Triple(it.uriString, "${it.title}.${it.extension.lowercase()}", it.mimeType) }
            else -> null
        }
        if (target == null || target.first.isBlank()) {
            sendText(out, 404, "text/plain", "Media not found")
            return
        }
        val bytes = runCatching {
            context.contentResolver.openInputStream(Uri.parse(target.first))?.use { it.readBytes() }
        }.getOrNull()
        if (bytes == null) {
            sendText(out, 404, "text/plain", "Cannot read media")
            return
        }
        val safeName = target.second.replace("\"", "_")
        val header = buildString {
            append("HTTP/1.1 200 OK\r\n")
            append("Content-Type: ${target.third.ifBlank { "application/octet-stream" }}\r\n")
            append("Content-Disposition: attachment; filename=\"$safeName\"\r\n")
            append("Content-Length: ${bytes.size}\r\n")
            append("Connection: close\r\n\r\n")
        }
        out.write(header.toByteArray(Charsets.UTF_8))
        out.write(bytes)
        out.flush()
        _status.value = _status.value.copy(
            transferredCount = _status.value.transferredCount + 1,
            lastActivity = "Sent: ${target.second}"
        )
    }

    private fun handleUpload(input: BufferedInputStream, contentLength: Int, fileName: String, out: OutputStream) {
        if (ContentPurityGuard.isExplicitOrAdult(fileName)) {
            sendText(out, 403, "text/plain; charset=utf-8", "Blocked by Prism Purity Shield")
            return
        }
        if (contentLength <= 0 || contentLength > 40 * 1024 * 1024) {
            sendText(out, 400, "text/plain; charset=utf-8", "Invalid file size (max 40MB)")
            return
        }
        val dir = File(context.filesDir, "wifi_uploads").apply { mkdirs() }
        val dest = File(dir, fileName)
        var remaining = contentLength
        val buf = ByteArray(16 * 1024)
        dest.outputStream().use { fos ->
            while (remaining > 0) {
                val read = input.read(buf, 0, minOf(buf.size, remaining))
                if (read <= 0) break
                fos.write(buf, 0, read)
                remaining -= read
            }
        }
        onUploadedFile(Uri.fromFile(dest), fileName)
        _status.value = _status.value.copy(
            transferredCount = _status.value.transferredCount + 1,
            lastActivity = "Received: $fileName"
        )
        sendText(out, 200, "text/plain; charset=utf-8", "Uploaded: $fileName")
    }

    private fun sendText(out: OutputStream, status: Int, mime: String, body: String) {
        val bytes = body.toByteArray(Charsets.UTF_8)
        val statusText = when (status) {
            200 -> "OK"
            400 -> "Bad Request"
            403 -> "Forbidden"
            else -> "Not Found"
        }
        val header = "HTTP/1.1 $status $statusText\r\nContent-Type: $mime\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n"
        out.write(header.toByteArray(Charsets.UTF_8))
        out.write(bytes)
        out.flush()
    }

    private fun readUntilHeaderEnd(input: BufferedInputStream): ByteArray? {
        val bos = ByteArrayOutputStream()
        var state = 0
        while (bos.size() < 16_384) {
            val b = input.read()
            if (b == -1) return null
            bos.write(b)
            state = when {
                state == 0 && b == '\r'.code -> 1
                state == 1 && b == '\n'.code -> 2
                state == 2 && b == '\r'.code -> 3
                state == 3 && b == '\n'.code -> return bos.toByteArray()
                else -> 0
            }
        }
        return bos.toByteArray()
    }

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

    companion object {
        fun resolveLocalIpv4Address(): String? = runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@runCatching null
            for (iface in interfaces) {
                if (!iface.isUp || iface.isLoopback) continue
                for (addr in iface.inetAddresses) {
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        val host = addr.hostAddress
                        if (!host.isNullOrBlank()) return@runCatching host
                    }
                }
            }
            null
        }.getOrNull()
    }
}
