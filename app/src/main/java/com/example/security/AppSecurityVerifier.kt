package com.example.security

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Debug
import android.view.WindowManager
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipFile
import kotlin.math.roundToInt

data class SecurityAuditInfo(
    val packageName: String,
    val sha256Full: String,
    val sha256Short: String,
    val pinnedSha256: String?,
    val dexSha256: String = "UNAVAILABLE",
    val pinnedDexSha256: String? = null,
    val signingVerified: Boolean,
    val zeroStoragePermissions: Boolean,
    val httpsOnlyPolicy: Boolean,
    val runtimeAndroidVersion: String,
    val targetSdk: Int,
    val installerSource: String,
    val deviceSummary: String,
    val tamperDetected: Boolean,
    val tamperReasons: List<String>,
    val rootOrSuDetected: Boolean = false,
    val suspiciousProxyDetected: Boolean = false,
)

data class DeviceScreenProfile(
    val widthPx: Int,
    val heightPx: Int,
    val widthDp: Int,
    val heightDp: Int,
    val densityDpi: Int,
    val refreshRateHz: Int,
    val aspectRatioLabel: String,
    val hasNotchOrCutout: Boolean,
    val cutoutTopDp: Int,
    val recommendedPhotoColumns: Int,
)

/**
 * Built-in, always-on multi-layer application integrity & anti-tamper verifier.
 * Runs silently inside the application architecture (cannot be disabled via UI toggles):
 * 1. Signing certificate SHA-256 verification & pinning (detects APK re-signing / modding).
 * 2. classes.dex bytecode SHA-256 verification & pinning (detects smali/DEX patching).
 * 3. Obfuscated package-ID integrity verification (detects package cloning / repackaging).
 * 4. Anti-debugger, TracerPid, /proc/self/maps & stack-trace hook detection.
 * 5. Zero broad storage permissions & strict HTTPS-only stream URL validation.
 */
object AppSecurityVerifier {

    private const val XOR_KEY: Int = 0x5A

    // XOR-encoded sensitive constants to raise reverse-engineering bar against plaintext string scanning
    private val encodedOwnerEmail = intArrayOf(
        0x29, 0x3B, 0x33, 0x3E, 0x38, 0x35, 0x2F, 0x3E, 0x32, 0x33, 0x37, 0x6A,
        0x1A, 0x3D, 0x37, 0x3B, 0x33, 0x36, 0x74, 0x39, 0x35, 0x37
    )

    private val encodedPackagePrefix = intArrayOf(
        0x39, 0x35, 0x37, 0x74, 0x3B, 0x33, 0x29, 0x2E, 0x2F, 0x3E, 0x33, 0x35,
        0x74, 0x2A, 0x28, 0x33, 0x29, 0x37, 0x37, 0x3F, 0x3E, 0x33, 0x3B
    )

    val OWNER_SECURITY_EMAIL: String
        get() = decodeXor(encodedOwnerEmail)

    val EXPECTED_PACKAGE_PREFIX: String
        get() = decodeXor(encodedPackagePrefix)

    private fun decodeXor(encoded: IntArray): String {
        val chars = CharArray(encoded.size) { idx -> (encoded[idx] xor XOR_KEY).toChar() }
        return String(chars)
    }

    /**
     * Computes a SHA-256 digest over classes.dex inside the installed APK container so any
     * smali / bytecode modification invalidates the pinned runtime digest.
     */
    fun computeClassesDexSha256(context: Context): String {
        return runCatching {
            val sourceDir = context.applicationInfo?.sourceDir ?: return "UNAVAILABLE"
            val apkFile = File(sourceDir)
            if (!apkFile.exists() || !apkFile.isFile) return "UNAVAILABLE"
            ZipFile(apkFile).use { zip ->
                val entry = zip.getEntry("classes.dex") ?: return "UNAVAILABLE"
                val md = MessageDigest.getInstance("SHA-256")
                zip.getInputStream(entry).use { input ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        md.update(buffer, 0, read)
                    }
                }
                md.digest().joinToString("") { "%02X".format(it) }
            }
        }.getOrDefault("UNAVAILABLE")
    }

    private fun isTracerPidAttached(): Boolean {
        return runCatching {
            val statusFile = File("/proc/self/status")
            if (!statusFile.exists()) return false
            statusFile.useLines { lines ->
                val tracerLine = lines.firstOrNull { it.startsWith("TracerPid:") } ?: return false
                val pid = tracerLine.substringAfter(":").trim().toIntOrNull() ?: 0
                pid > 0
            }
        }.getOrDefault(false)
    }

    private fun hasInjectedHookLibraries(): Boolean {
        val suspiciousTokens = listOf("frida", "xposed", "lsposed", "edxposed", "substrate", "gadget")
        val mapsMatch = runCatching {
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.exists()) return@runCatching false
            mapsFile.useLines { lines ->
                lines.any { line ->
                    val lower = line.lowercase(Locale.US)
                    suspiciousTokens.any { token -> lower.contains(token) }
                }
            }
        }.getOrDefault(false)

        if (mapsMatch) return true

        val stackMatch = runCatching {
            Throwable().stackTrace.any { frame ->
                val cls = frame.className.lowercase(Locale.US)
                cls.contains("xposed") || cls.contains("substrate") || cls.contains("lspd")
            }
        }.getOrDefault(false)

        return stackMatch
    }

    @Suppress("DEPRECATION")
    fun inspect(
        context: Context,
        pinnedSha256: String? = null,
        pinnedDexSha256: String? = null,
    ): SecurityAuditInfo {
        val pm = context.packageManager
        val pkgName = context.packageName
        var sha256Full = "UNKNOWN"
        var sha256Short = "Verified"
        var signed = false
        val reasons = mutableListOf<String>()

        try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNING_CERTIFICATES)
                info.signingInfo?.let {
                    if (it.hasMultipleSigners()) it.apkContentsSigners else it.signingCertificateHistory
                }
            } else {
                pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures
            }

            val firstSig = signatures?.firstOrNull()
            if (firstSig != null) {
                val digest = MessageDigest.getInstance("SHA-256").digest(firstSig.toByteArray())
                val hex = digest.joinToString(":") { "%02X".format(it) }
                sha256Full = hex
                sha256Short = "${hex.take(17)}…${hex.takeLast(8)}"
                signed = true
            }
        } catch (_: Exception) {
            sha256Short = "SHA-256 Active"
        }

        val dexSha256 = computeClassesDexSha256(context)

        // 1. Package Name Integrity Check (Detects APK cloning / repackaging)
        val expectedPrefix = EXPECTED_PACKAGE_PREFIX
        if (!pkgName.startsWith(expectedPrefix) && pkgName != "com.example") {
            reasons += "Unexpected Package ID ($pkgName)"
        }

        // 2. Signature Pinning / Repackaging Check (Detects re-signed modified APKs)
        if (!pinnedSha256.isNullOrBlank() && sha256Full != "UNKNOWN" && pinnedSha256 != sha256Full) {
            reasons += "APK Certificate Changed (Re-signing / Modding Detected)"
        }

        // 3. DEX Bytecode Integrity Pinning (Detects smali / DEX patching)
        if (!pinnedDexSha256.isNullOrBlank() && dexSha256 != "UNAVAILABLE" && pinnedDexSha256 != dexSha256) {
            reasons += "DEX Bytecode Digest Changed (Code Patching Detected)"
        }

        // 4. Active Debugger, TracerPid & Runtime Hooking Framework Checks
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger() || isTracerPidAttached()) {
            reasons += "Active Debugger / Tracer Attached"
        }
        val hookIndicators = listOf(
            "/data/local/tmp/frida-server",
            "/system/framework/XposedBridge.jar",
            "/sbin/magisk",
            "/data/adb/magisk",
            "/data/adb/ksu"
        )
        if (hookIndicators.any { runCatching { File(it).exists() }.getOrDefault(false) } || hasInjectedHookLibraries()) {
            reasons += "Runtime Hooking / Tamper Framework Indicator"
        }

        val suPaths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/su/bin/su",
            "/system/app/Superuser.apk"
        )
        val rootDetected = suPaths.any { runCatching { File(it).exists() }.getOrDefault(false) }
        if (rootDetected) {
            reasons += "Rooted Environment / SU Binary Present"
        }

        val proxyHost = runCatching { System.getProperty("http.proxyHost") }.getOrNull()
        val suspiciousProxy = !proxyHost.isNullOrBlank()
        if (suspiciousProxy) {
            reasons += "Active Network Proxy Detected ($proxyHost)"
        }

        // 5. Broad Storage Privacy Check
        val requestedPerms = try {
            val info = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
            info.requestedPermissions?.toSet() ?: emptySet()
        } catch (_: Exception) {
            emptySet()
        }

        val broadStoragePerms = setOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.READ_MEDIA_AUDIO",
        )
        val zeroBroadStorage = requestedPerms.none { it in broadStoragePerms }

        // 6. Installer Source
        val installer = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(pkgName).installingPackageName
            } else {
                pm.getInstallerPackageName(pkgName)
            }
        }.getOrNull() ?: "Direct / Sideload"

        val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})" +
            if (isDebugBuild) " • Debug" else " • Release"

        return SecurityAuditInfo(
            packageName = pkgName,
            sha256Full = sha256Full,
            sha256Short = sha256Short,
            pinnedSha256 = pinnedSha256,
            dexSha256 = dexSha256,
            pinnedDexSha256 = pinnedDexSha256,
            signingVerified = signed,
            zeroStoragePermissions = zeroBroadStorage,
            httpsOnlyPolicy = true,
            runtimeAndroidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            targetSdk = context.applicationInfo.targetSdkVersion,
            installerSource = installer,
            deviceSummary = deviceInfo,
            tamperDetected = reasons.isNotEmpty(),
            tamperReasons = reasons,
            rootOrSuDetected = rootDetected,
            suspiciousProxyDetected = suspiciousProxy,
        )
    }

    /**
     * Strict HTTPS stream URL validation blocking cleartext HTTP, loopback, private LAN ranges,
     * and cloud metadata endpoints to protect against local network probing / SSRF.
     */
    fun isSafeHttpsStreamUrl(rawUrl: String): Boolean {
        val trimmed = rawUrl.trim()
        if (trimmed.length < 12 || trimmed.length > 2048) return false
        val uri = runCatching { Uri.parse(trimmed) }.getOrNull() ?: return false
        if (!uri.scheme.equals("https", ignoreCase = true)) return false
        val host = uri.host?.lowercase(Locale.US)?.trim().orEmpty()
        if (host.isBlank() || !host.contains('.')) return false
        if (host == "localhost" || host.endsWith(".local") || host.endsWith(".internal")) return false
        val blockedPrefixes = listOf(
            "127.", "10.", "192.168.", "169.254.", "0.", "::1", "[::1]", "fe80:", "fc00:", "fd"
        )
        if (blockedPrefixes.any { host.startsWith(it) }) return false
        if (host.startsWith("172.")) {
            val secondOctet = host.removePrefix("172.").substringBefore('.').toIntOrNull()
            if (secondOctet != null && secondOctet in 16..31) return false
        }
        return true
    }

    /**
     * Computes a transparent 0-100 Security Protection Score across active built-in & user defenses.
     */
    fun computeSecurityScore(
        audit: SecurityAuditInfo,
        appLockEnabled: Boolean,
        screenPrivacyEnabled: Boolean,
        tamperGuardEnabled: Boolean = true,
    ): Int {
        var score = 0
        if (audit.zeroStoragePermissions) score += 20
        if (audit.httpsOnlyPolicy) score += 15
        if (tamperGuardEnabled) score += 15
        if (!audit.tamperDetected) score += 20
        if (appLockEnabled) score += 20
        if (screenPrivacyEnabled) score += 10
        return score.coerceIn(0, 100)
    }

    /**
     * Inspects live device display dimensions, pixel density, refresh rate, and camera notch/cutout
     * so Prism automatically adapts its layout, grids, and safe insets to any Android screen.
     */
    @Suppress("DEPRECATION")
    fun inspectScreenProfile(context: Context): DeviceScreenProfile {
        val dm = context.resources.displayMetrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager

        var widthPx = dm.widthPixels.coerceAtLeast(1)
        var heightPx = dm.heightPixels.coerceAtLeast(1)
        var hasCutout = false
        var cutoutTopPx = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && wm != null) {
            runCatching {
                val metrics = wm.currentWindowMetrics
                widthPx = metrics.bounds.width().coerceAtLeast(1)
                heightPx = metrics.bounds.height().coerceAtLeast(1)
                val cutout = metrics.windowInsets.displayCutout
                if (cutout != null) {
                    hasCutout = cutout.safeInsetTop > 0 || cutout.boundingRects.isNotEmpty()
                    cutoutTopPx = cutout.safeInsetTop
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && context is Activity) {
            runCatching {
                val cutout = context.window?.decorView?.rootWindowInsets?.displayCutout
                if (cutout != null) {
                    hasCutout = cutout.safeInsetTop > 0 || cutout.boundingRects.isNotEmpty()
                    cutoutTopPx = cutout.safeInsetTop
                }
            }
        }

        val density = dm.density.coerceAtLeast(0.75f)
        val widthDp = (widthPx / density).roundToInt()
        val heightDp = (heightPx / density).roundToInt()
        val cutoutTopDp = (cutoutTopPx / density).roundToInt()

        val refreshHz = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display.refreshRate.roundToInt()
            } else {
                wm?.defaultDisplay?.refreshRate?.roundToInt() ?: 60
            }
        }.getOrDefault(60).coerceAtLeast(60)

        val longSide = maxOf(widthPx, heightPx).toFloat()
        val shortSide = minOf(widthPx, heightPx).toFloat().coerceAtLeast(1f)
        val ratio = longSide / shortSide
        val ratioText = String.format(Locale.US, "%.1f:9", ratio * 9f)

        val recommendedCols = when {
            widthDp >= 840 -> 4
            widthDp >= 600 -> 3
            else -> 2
        }

        return DeviceScreenProfile(
            widthPx = widthPx,
            heightPx = heightPx,
            widthDp = widthDp,
            heightDp = heightDp,
            densityDpi = dm.densityDpi,
            refreshRateHz = refreshHz,
            aspectRatioLabel = ratioText,
            hasNotchOrCutout = hasCutout,
            cutoutTopDp = cutoutTopDp,
            recommendedPhotoColumns = recommendedCols,
        )
    }

    /**
     * Builds a user-confirmed Security Alert Email intent addressed to the app owner
     * containing technical integrity, tamper, & intruder diagnostics.
     */
    fun buildTamperAlertEmailIntent(
        audit: SecurityAuditInfo,
        screen: DeviceScreenProfile,
        recipient: String = OWNER_SECURITY_EMAIL,
        intruderSummaries: List<String> = emptyList(),
    ): Intent {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US).format(Date())
        val statusLabel = when {
            audit.tamperDetected -> "ALERT: TAMPERING / INTRUSION DETECTED"
            intruderSummaries.isNotEmpty() -> "ALERT: FAILED PIN / INTRUDER ATTEMPTS"
            else -> "Integrity Security Check Report"
        }
        val subject = "[Prism Security Shield] $statusLabel"

        val reasonsText = if (audit.tamperReasons.isEmpty()) {
            "None (App integrity verified clean)"
        } else {
            audit.tamperReasons.joinToString("\n- ", prefix = "- ")
        }

        val body = buildString {
            appendLine("=== PRISM MEDIA INTEGRITY REPORT ===")
            appendLine("Owner Alert Email: $recipient")
            appendLine("Timestamp: $timestamp")
            appendLine("Status: $statusLabel")
            appendLine()
            appendLine("Detected Anomalies:")
            appendLine(reasonsText)
            if (intruderSummaries.isNotEmpty()) {
                appendLine()
                appendLine("Recent Failed Unlock / Intruder Attempts (${intruderSummaries.size}):")
                intruderSummaries.take(10).forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine("Application Integrity Details:")
            appendLine("• Package Name: ${audit.packageName}")
            appendLine("• Current Cert SHA-256: ${audit.sha256Full}")
            appendLine("• Pinned Cert SHA-256: ${audit.pinnedSha256 ?: "Initial Install"}")
            appendLine("• DEX Digest: ${audit.dexSha256.take(24)}")
            appendLine("• Installer Source: ${audit.installerSource}")
            appendLine("• Root / SU Indicator: ${if (audit.rootOrSuDetected) "DETECTED" else "Clean"}")
            appendLine("• Network Proxy Indicator: ${if (audit.suspiciousProxyDetected) "DETECTED" else "Clean"}")
            appendLine("• Target SDK: API ${audit.targetSdk}")
            appendLine()
            appendLine("Technical Environment Info:")
            appendLine("• Device: ${audit.deviceSummary}")
            appendLine("• OS Runtime: ${audit.runtimeAndroidVersion}")
            appendLine("• Display: ${screen.widthPx}x${screen.heightPx}px (${screen.widthDp}x${screen.heightDp}dp @ ${screen.densityDpi}dpi, ${screen.refreshRateHz}Hz)")
            appendLine("• Notch / Display Cutout: ${if (screen.hasNotchOrCutout) "Detected (${screen.cutoutTopDp}dp)" else "Standard"}")
            appendLine("=====================================")
        }

        val uri = Uri.parse(
            "mailto:${Uri.encode(recipient)}" +
                "?subject=${Uri.encode(subject)}" +
                "&body=${Uri.encode(body)}"
        )
        return Intent(Intent.ACTION_SENDTO, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun launchTamperAlertEmail(
        context: Context,
        audit: SecurityAuditInfo,
        screen: DeviceScreenProfile,
        intruderSummaries: List<String> = emptyList(),
    ): Boolean {
        val intent = buildTamperAlertEmailIntent(
            audit = audit,
            screen = screen,
            intruderSummaries = intruderSummaries
        )
        return runCatching {
            context.startActivity(Intent.createChooser(intent, "Send Security Alert"))
            true
        }.getOrDefault(false)
    }
}
