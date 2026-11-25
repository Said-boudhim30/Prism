package com.example.security

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat

/**
 * Native Android BiometricPrompt helper (Fingerprint / Face Unlock) on API 28+
 * with zero external dependencies and clean compatibility across ComponentActivity.
 */
object BiometricAuthHelper {

    fun isBiometricAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bm = context.getSystemService(BiometricManager::class.java)
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bm?.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                } else {
                    @Suppress("DEPRECATION")
                    bm?.canAuthenticate()
                }
                status == BiometricManager.BIOMETRIC_SUCCESS
            } else {
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
            }
        }.getOrDefault(false)
    }

    fun authenticate(
        context: Context,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {},
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || !isBiometricAvailable(context)) {
            onError("Biometric hardware unavailable")
            return
        }
        runCatching {
            val executor = ContextCompat.getMainExecutor(context)
            val cancellationSignal = CancellationSignal()
            val prompt = BiometricPrompt.Builder(context)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButton(negativeButtonText, executor) { _, _ ->
                    // User prefers entering PIN manually
                }
                .build()

            prompt.authenticate(
                cancellationSignal,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        if (errorCode != BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.BIOMETRIC_ERROR_CANCELED &&
                            errorCode != 13
                        ) {
                            onError(errString?.toString().orEmpty())
                        }
                    }
                }
            )
        }.onFailure { err ->
            onError(err.localizedMessage.orEmpty())
        }
    }
}
