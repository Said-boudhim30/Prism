package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.model.AccentPalette
import com.example.model.BackdropAnimationStyle
import com.example.model.IconPackStyle
import com.example.model.LiquidGlassStyle
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.MediaCardStyle
import com.example.model.MotionFeelStyle
import com.example.model.SurfaceDepthStyle
import com.example.model.UiDensity
import com.example.ui.components.LiquidGlassConfig
import com.example.ui.components.LocalLiquidGlassConfig

val LocalIconPackStyle = staticCompositionLocalOf { IconPackStyle.GOOGLE_ROUNDED }
val LocalUiDensity = staticCompositionLocalOf { UiDensity.BALANCED }
val LocalHapticsEnabled = staticCompositionLocalOf { true }
val LocalMediaCardStyle = staticCompositionLocalOf { MediaCardStyle.GLASS_CARD }
val LocalBackdropStyle = staticCompositionLocalOf { BackdropAnimationStyle.AURORA_ORBS }
val LocalMotionFeelStyle = staticCompositionLocalOf { MotionFeelStyle.BOUNCY_EXPRESSIVE }
val LocalShowFormatBadges = staticCompositionLocalOf { true }
val LocalAccentStrength = staticCompositionLocalOf { 1.0f }

val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

fun createExpressiveShapes(roundness: Float): Shapes {
    val factor = roundness.coerceIn(0.6f, 1.4f)
    return Shapes(
        extraSmall = RoundedCornerShape((12f * factor).dp),
        small = RoundedCornerShape((16f * factor).dp),
        medium = RoundedCornerShape((24f * factor).dp),
        large = RoundedCornerShape((28f * factor).dp),
        extraLarge = RoundedCornerShape((32f * factor).dp),
    )
}

private fun applyContrastAdjustment(
    scheme: ColorScheme,
    darkTheme: Boolean,
    contrast: MaterialContrastLevel,
): ColorScheme {
    if (contrast == MaterialContrastLevel.STANDARD) return scheme
    val boost = if (contrast == MaterialContrastLevel.HIGH) 0.34f else 0.16f
    return if (darkTheme) {
        scheme.copy(
            onSurface = lerp(scheme.onSurface, Color.White, boost),
            onSurfaceVariant = lerp(scheme.onSurfaceVariant, Color.White, boost * 0.85f),
            primary = lerp(scheme.primary, Color.White, boost * 0.45f),
            outlineVariant = lerp(scheme.outlineVariant, Color.White, boost * 0.6f),
            surfaceContainer = lerp(scheme.surfaceContainer, Color.Black, boost * 0.35f),
            surfaceContainerHigh = lerp(scheme.surfaceContainerHigh, Color.Black, boost * 0.28f),
        )
    } else {
        scheme.copy(
            onSurface = lerp(scheme.onSurface, Color.Black, boost),
            onSurfaceVariant = lerp(scheme.onSurfaceVariant, Color.Black, boost * 0.85f),
            primary = lerp(scheme.primary, Color.Black, boost * 0.40f),
            outlineVariant = lerp(scheme.outlineVariant, Color.Black, boost * 0.55f),
            surfaceContainer = lerp(scheme.surfaceContainer, Color.White, boost * 0.35f),
        )
    }
}

private fun applyAccentStrength(
    scheme: ColorScheme,
    strength: Float,
): ColorScheme {
    val clamped = strength.coerceIn(0.5f, 1.4f)
    if (kotlin.math.abs(clamped - 1.0f) < 0.04f) return scheme
    return if (clamped < 1.0f) {
        val t = (1.0f - clamped) * 0.72f
        scheme.copy(
            primaryContainer = lerp(scheme.primaryContainer, scheme.surfaceContainerHigh, t),
            secondaryContainer = lerp(scheme.secondaryContainer, scheme.surfaceContainerHigh, t),
            tertiaryContainer = lerp(scheme.tertiaryContainer, scheme.surfaceContainerHigh, t),
        )
    } else {
        val t = (clamped - 1.0f) * 0.55f
        scheme.copy(
            primaryContainer = lerp(scheme.primaryContainer, scheme.primary, t * 0.45f),
            secondaryContainer = lerp(scheme.secondaryContainer, scheme.secondary, t * 0.4f),
            tertiaryContainer = lerp(scheme.tertiaryContainer, scheme.tertiary, t * 0.4f),
        )
    }
}

@Composable
fun PrismTheme(
    darkTheme: Boolean,
    accentPalette: AccentPalette,
    isArabic: Boolean,
    amoledBlack: Boolean = false,
    fontStyle: MaterialFontStyle = MaterialFontStyle.MATERIAL_EXPRESSIVE,
    iconStyle: IconPackStyle = IconPackStyle.GOOGLE_ROUNDED,
    contrastLevel: MaterialContrastLevel = MaterialContrastLevel.STANDARD,
    shapeRoundness: Float = 1.0f,
    liquidGlassStyle: LiquidGlassStyle = LiquidGlassStyle.MATERIAL_LIQUID,
    liquidGlassIntensity: Float = 0.55f,
    edgeGlowEnabled: Boolean = true,
    uiDensity: UiDensity = UiDensity.BALANCED,
    hapticsEnabled: Boolean = true,
    surfaceDepthStyle: SurfaceDepthStyle = SurfaceDepthStyle.EXPRESSIVE,
    customSeedColorArgb: Int? = null,
    cardStyle: MediaCardStyle = MediaCardStyle.GLASS_CARD,
    backdropStyle: BackdropAnimationStyle = BackdropAnimationStyle.AURORA_ORBS,
    motionFeelStyle: MotionFeelStyle = MotionFeelStyle.BOUNCY_EXPRESSIVE,
    showFormatBadges: Boolean = true,
    accentStrength: Float = 1.0f,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val baseScheme = when {
        customSeedColorArgb != null -> SeedColorSchemeGenerator.generateColorScheme(customSeedColorArgb, darkTheme)
        accentPalette == AccentPalette.SYSTEM && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getPaletteScheme(accentPalette, darkTheme)
    }

    val contrastedScheme = applyAccentStrength(
        applyContrastAdjustment(baseScheme, darkTheme, contrastLevel),
        accentStrength
    )

    val colorScheme = if (darkTheme && amoledBlack) {
        contrastedScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF0A0A0D),
            surfaceContainer = Color(0xFF121216),
            surfaceContainerHigh = Color(0xFF1A1A20),
            surfaceContainerHighest = Color(0xFF24242C),
        )
    } else {
        contrastedScheme
    }

    val shapes = remember(shapeRoundness) { createExpressiveShapes(shapeRoundness) }
    val typography = remember(isArabic, fontStyle) { createAppTypography(isArabic, fontStyle) }
    val glassConfig = remember(liquidGlassStyle, liquidGlassIntensity, shapeRoundness, edgeGlowEnabled, surfaceDepthStyle) {
        LiquidGlassConfig(
            style = liquidGlassStyle,
            intensity = liquidGlassIntensity,
            shapeRoundness = shapeRoundness,
            edgeGlowEnabled = edgeGlowEnabled,
            depthStyle = surfaceDepthStyle,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalLiquidGlassConfig provides glassConfig,
        LocalIconPackStyle provides iconStyle,
        LocalUiDensity provides uiDensity,
        LocalHapticsEnabled provides hapticsEnabled,
        LocalMediaCardStyle provides cardStyle,
        LocalBackdropStyle provides backdropStyle,
        LocalMotionFeelStyle provides motionFeelStyle,
        LocalShowFormatBadges provides showFormatBadges,
        LocalAccentStrength provides accentStrength,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}
