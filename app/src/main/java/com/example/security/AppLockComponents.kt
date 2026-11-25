package com.example.security

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.IntruderAttempt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.roundToInt

/**
 * Cryptographic PIN hashing helper using PBKDF2-HMAC-SHA256 with random 16-byte salt
 * and constant-time equality checks so the PIN is NEVER stored in plaintext.
 */
object PinCrypto {
    private const val ITERATIONS = 30_000
    private const val KEY_LENGTH_BITS = 256

    fun generateSaltHex(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPin(pin: String, saltHex: String): String {
        val saltBytes = saltHex.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
        val derived = runCatching {
            val spec = PBEKeySpec(pin.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH_BITS)
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        }.getOrElse {
            MessageDigest.getInstance("SHA-256").digest("$saltHex:$pin".toByteArray(Charsets.UTF_8))
        }
        return derived.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(candidatePin: String, saltHex: String, storedHashHex: String): Boolean {
        if (candidatePin.isBlank() || saltHex.isBlank() || storedHashHex.isBlank()) return false
        val computed = hashPin(candidatePin, saltHex).toByteArray(Charsets.UTF_8)
        val expected = storedHashHex.toByteArray(Charsets.UTF_8)
        return MessageDigest.isEqual(computed, expected)
    }
}

/**
 * Material 3 Expressive Full-Screen PIN Lock Shield with Anti-Brute-Force cooldown timer.
 */
@Composable
fun AppLockScreenOverlay(
    failedAttempts: Int,
    lockoutUntilMs: Long,
    strings: AppStrings,
    onVerifyPin: (String) -> Boolean,
    biometricEnabled: Boolean = false,
    onBiometricUnlock: () -> Unit = {},
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val shakeOffset = remember { Animatable(0f) }

    val remainingLockSec = ((lockoutUntilMs - nowMs).coerceAtLeast(0L) / 1000L).toInt()
    val isLockedOut = remainingLockSec > 0
    val canUseBiometric = remember(context, biometricEnabled) {
        biometricEnabled && BiometricAuthHelper.isBiometricAvailable(context)
    }

    val launchBiometric: () -> Unit = remember(context, strings, onBiometricUnlock) {
        {
            BiometricAuthHelper.authenticate(
                context = context,
                title = strings.biometricPromptTitle,
                subtitle = strings.biometricPromptSubtitle,
                negativeButtonText = strings.biometricUsePinFallback,
                onSuccess = onBiometricUnlock
            )
        }
    }

    LaunchedEffect(canUseBiometric, isLockedOut) {
        if (canUseBiometric && !isLockedOut) {
            delay(220L)
            launchBiometric()
        }
    }

    LaunchedEffect(lockoutUntilMs) {
        while (System.currentTimeMillis() < lockoutUntilMs) {
            nowMs = System.currentTimeMillis()
            delay(500L)
        }
        nowMs = System.currentTimeMillis()
    }

    fun submitPin(pin: String) {
        if (isLockedOut || pin.length < 4) return
        val ok = onVerifyPin(pin)
        if (ok) {
            enteredPin = ""
            showError = false
        } else {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            enteredPin = ""
            showError = true
            scope.launch {
                shakeOffset.snapTo(0f)
                listOf(-26f, 26f, -18f, 18f, -8f, 0f).forEach { target ->
                    shakeOffset.animateTo(target, tween(45))
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_lock_overlay"),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                )
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isLockedOut || showError) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isLockedOut) Icons.Filled.Warning else Icons.Filled.Lock,
                            contentDescription = strings.appLockTitle,
                            tint = if (isLockedOut || showError) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = strings.appLockEnterPinTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when {
                        isLockedOut -> strings.appLockCooldownMsg(remainingLockSec)
                        showError -> strings.appLockWrongPin(failedAttempts)
                        else -> strings.appLockEnterPinSubtitle
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isLockedOut || showError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = if (isLockedOut || showError) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Animated PIN Dots
                Row(
                    modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { idx ->
                        val filled = idx < enteredPin.length
                        val isOptionalSlot = idx >= 4 && !filled
                        Surface(
                            shape = CircleShape,
                            color = when {
                                filled -> MaterialTheme.colorScheme.primary
                                isOptionalSlot -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.45f)
                                else -> MaterialTheme.colorScheme.surfaceContainerHighest
                            },
                            border = BorderStroke(
                                width = if (filled) 2.dp else 1.dp,
                                color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            ),
                            modifier = Modifier.size(if (filled) 18.dp else 14.dp)
                        ) {}
                    }
                }

                if (canUseBiometric && !isLockedOut) {
                    Spacer(modifier = Modifier.height(18.dp))
                    FilledTonalButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            launchBiometric()
                        },
                        shape = CircleShape,
                        modifier = Modifier.testTag("app_lock_biometric_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = strings.biometricUnlockTitle,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.biometricUnlockTitle,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expressive Numeric Keypad
            Column(
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                )
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { digit ->
                            PinKeypadButton(
                                label = digit,
                                enabled = !isLockedOut && enteredPin.length < 6,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showError = false
                                    val next = enteredPin + digit
                                    enteredPin = next
                                    if (next.length >= 4 && onVerifyPin(next)) {
                                        enteredPin = ""
                                    } else if (next.length == 6) {
                                        submitPin(next)
                                    }
                                }
                            )
                        }
                    }
                }

                // Bottom row: Backspace, 0, Unlock Check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            if (enteredPin.isNotEmpty()) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                enteredPin = enteredPin.dropLast(1)
                            }
                        },
                        enabled = !isLockedOut && enteredPin.isNotEmpty(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("pin_key_backspace")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    PinKeypadButton(
                        label = "0",
                        enabled = !isLockedOut && enteredPin.length < 6,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showError = false
                            val next = enteredPin + "0"
                            enteredPin = next
                            if (next.length >= 4 && onVerifyPin(next)) {
                                enteredPin = ""
                            } else if (next.length == 6) {
                                submitPin(next)
                            }
                        }
                    )

                    Surface(
                        onClick = { submitPin(enteredPin) },
                        enabled = !isLockedOut && enteredPin.length >= 4,
                        shape = CircleShape,
                        color = if (!isLockedOut && enteredPin.length >= 4) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("pin_key_submit")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = strings.appLockUnlockAction,
                                tint = if (!isLockedOut && enteredPin.length >= 4) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKeypadButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
        modifier = Modifier
            .size(72.dp)
            .testTag("pin_key_$label")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Dialog to create or update the 4-6 digit App Lock PIN safely.
 */
@Composable
fun SetupPinDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onConfirmPin: (String) -> Unit,
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = strings.appLockSetupTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.appLockSetupDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = pin,
                    onValueChange = { input ->
                        if (input.length <= 6 && input.all(Char::isDigit)) {
                            pin = input
                            errorMsg = null
                        }
                    },
                    label = { Text(strings.appLockPinHint) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_pin_input")
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { input ->
                        if (input.length <= 6 && input.all(Char::isDigit)) {
                            confirmPin = input
                            errorMsg = null
                        }
                    },
                    label = { Text(strings.appLockConfirmHint) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_pin_confirm_input")
                )
                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        pin.length < 4 -> errorMsg = strings.appLockMinLengthError
                        pin != confirmPin -> errorMsg = strings.appLockMismatchError
                        else -> onConfirmPin(pin)
                    }
                },
                shape = CircleShape,
                modifier = Modifier.testTag("setup_pin_save")
            ) {
                Text(strings.save, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

/**
 * Dialog displaying logged failed PIN / intruder attempts with 1-tap owner email alert & clear action.
 */
@Composable
fun IntruderLogDialog(
    attempts: List<IntruderAttempt>,
    strings: AppStrings,
    onSendReport: () -> Unit,
    onClearLog: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = if (attempts.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = strings.intruderLogTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (attempts.isEmpty()) {
                Text(
                    text = strings.intruderLogEmpty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attempts, key = { it.timestampMs }) { entry ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = entry.formattedTime,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = entry.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (attempts.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = onClearLog) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.clearLogAction)
                    }
                    Button(
                        onClick = onSendReport,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.sendReportShort, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(strings.close, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = if (attempts.isNotEmpty()) {
            {
                TextButton(onClick = onDismiss) {
                    Text(strings.close)
                }
            }
        } else null
    )
}
