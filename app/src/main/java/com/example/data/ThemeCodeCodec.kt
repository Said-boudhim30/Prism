package com.example.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Base64
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.MainActivity
import com.example.R
import com.example.localization.AppStrings
import com.example.model.AccentPalette
import com.example.model.AppIconVariant
import com.example.model.AppSettings
import com.example.model.IconPackStyle
import com.example.model.LiquidGlassStyle
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.NavBarStyle
import com.example.model.NavTransitionStyle
import com.example.model.PlayerArtworkStyle
import com.example.model.SurfaceDepthStyle
import com.example.model.UiDensity
import org.json.JSONObject

data class DecodedThemePreset(
    val darkTheme: Boolean,
    val amoledBlack: Boolean,
    val accentPalette: AccentPalette,
    val customSeedColorArgb: Int?,
    val liquidGlassStyle: LiquidGlassStyle,
    val liquidGlassIntensity: Float,
    val shapeRoundness: Float,
    val fontStyle: MaterialFontStyle,
    val iconStyle: IconPackStyle,
    val contrastLevel: MaterialContrastLevel,
    val navBarStyle: NavBarStyle,
    val navTransitionStyle: NavTransitionStyle,
    val playerArtworkStyle: PlayerArtworkStyle,
    val surfaceDepthStyle: SurfaceDepthStyle,
    val uiDensity: UiDensity,
    val edgeGlowEnabled: Boolean,
)

/**
 * Shareable Theme Code Encoder/Decoder (`PRISM-THEME:...`), Dynamic Launcher Icon Switcher,
 * and Android Home Screen Dynamic Quick-Launch Shortcuts.
 */
object ThemeCodeCodec {
    private const val PREFIX = "PRISM-THEME:"

    fun encodeThemeCode(s: AppSettings): String {
        val json = JSONObject().apply {
            put("v", 1)
            put("dk", s.darkTheme)
            put("am", s.amoledBlack)
            put("ap", s.accentPalette.name)
            if (s.customSeedColorArgb != null) put("sd", s.customSeedColorArgb)
            put("gs", s.liquidGlassStyle.name)
            put("gi", s.liquidGlassIntensity.toDouble())
            put("sr", s.shapeRoundness.toDouble())
            put("fs", s.fontStyle.name)
            put("is", s.iconStyle.name)
            put("cl", s.contrastLevel.name)
            put("nb", s.navBarStyle.name)
            put("nt", s.navTransitionStyle.name)
            put("pa", s.playerArtworkStyle.name)
            put("sd2", s.surfaceDepthStyle.name)
            put("ud", s.uiDensity.name)
            put("eg", s.edgeGlowEnabled)
        }
        val b64 = Base64.encodeToString(
            json.toString().toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        return "$PREFIX$b64"
    }

    fun decodeThemeCode(raw: String): DecodedThemePreset? {
        val trimmed = raw.trim()
        if (!trimmed.startsWith(PREFIX)) return null
        return runCatching {
            val payload = trimmed.removePrefix(PREFIX)
            val jsonStr = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8)
            val j = JSONObject(jsonStr)
            DecodedThemePreset(
                darkTheme = j.optBoolean("dk", true),
                amoledBlack = j.optBoolean("am", false),
                accentPalette = runCatching { AccentPalette.valueOf(j.optString("ap")) }.getOrDefault(AccentPalette.SYSTEM),
                customSeedColorArgb = if (j.has("sd")) j.optInt("sd") else null,
                liquidGlassStyle = runCatching { LiquidGlassStyle.valueOf(j.optString("gs")) }.getOrDefault(LiquidGlassStyle.MATERIAL_LIQUID),
                liquidGlassIntensity = j.optDouble("gi", 0.55).toFloat().coerceIn(0f, 1f),
                shapeRoundness = j.optDouble("sr", 1.0).toFloat().coerceIn(0.6f, 1.4f),
                fontStyle = runCatching { MaterialFontStyle.valueOf(j.optString("fs")) }.getOrDefault(MaterialFontStyle.MATERIAL_EXPRESSIVE),
                iconStyle = runCatching { IconPackStyle.valueOf(j.optString("is")) }.getOrDefault(IconPackStyle.GOOGLE_ROUNDED),
                contrastLevel = runCatching { MaterialContrastLevel.valueOf(j.optString("cl")) }.getOrDefault(MaterialContrastLevel.STANDARD),
                navBarStyle = runCatching { NavBarStyle.valueOf(j.optString("nb")) }.getOrDefault(NavBarStyle.FLOATING_PILL),
                navTransitionStyle = runCatching { NavTransitionStyle.valueOf(j.optString("nt")) }.getOrDefault(NavTransitionStyle.EXPRESSIVE_SPRING),
                playerArtworkStyle = runCatching { PlayerArtworkStyle.valueOf(j.optString("pa")) }.getOrDefault(PlayerArtworkStyle.VINYL_DISC),
                surfaceDepthStyle = runCatching { SurfaceDepthStyle.valueOf(j.optString("sd2")) }.getOrDefault(SurfaceDepthStyle.EXPRESSIVE),
                uiDensity = runCatching { UiDensity.valueOf(j.optString("ud")) }.getOrDefault(UiDensity.BALANCED),
                edgeGlowEnabled = j.optBoolean("eg", true),
            )
        }.getOrNull()
    }
}

object AppIconSwitcher {
    private val aliasMap = mapOf(
        AppIconVariant.OBSIDIAN to "com.example.MainActivityObsidian",
        AppIconVariant.CRYSTAL to "com.example.MainActivityCrystal",
        AppIconVariant.EMERALD to "com.example.MainActivityEmerald",
        AppIconVariant.GOLD to "com.example.MainActivityGold",
    )

    /**
     * Switches the active home-screen launcher alias safely while always keeping MainActivity usable.
     */
    fun applyAppIcon(context: Context, variant: AppIconVariant): Boolean {
        return runCatching {
            val pm = context.packageManager
            val pkg = context.packageName
            aliasMap.forEach { (key, className) ->
                val state = if (key == variant) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                pm.setComponentEnabledSetting(
                    ComponentName(pkg, className),
                    state,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        }.getOrDefault(false)
    }
}

object DynamicShortcutsHelper {
    const val EXTRA_QUICK_ACTION = "com.example.EXTRA_QUICK_ACTION"
    const val ACTION_QURAN_HUB = "quran_hub"
    const val ACTION_PRIVATE_VAULT = "private_vault"
    const val ACTION_WIFI_TRANSFER = "wifi_transfer"
    const val ACTION_LISTENING_STATS = "listening_stats"

    fun publishShortcuts(context: Context, strings: AppStrings) {
        runCatching {
            val shortcuts = listOf(
                buildShortcut(context, "shortcut_quran", strings.quranHubTitle, ACTION_QURAN_HUB, R.drawable.ic_launcher_foreground),
                buildShortcut(context, "shortcut_vault", strings.privateVaultTitle, ACTION_PRIVATE_VAULT, R.drawable.ic_launcher_foreground),
                buildShortcut(context, "shortcut_wifi", strings.wifiTransferTitle, ACTION_WIFI_TRANSFER, R.drawable.ic_launcher_foreground),
                buildShortcut(context, "shortcut_stats", strings.listeningStatsTitle, ACTION_LISTENING_STATS, R.drawable.ic_launcher_foreground),
            )
            ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
        }
    }

    private fun buildShortcut(
        context: Context,
        id: String,
        label: String,
        action: String,
        iconRes: Int,
    ): ShortcutInfoCompat {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.action = Intent.ACTION_VIEW
            putExtra(EXTRA_QUICK_ACTION, action)
        }
        return ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(label.take(24))
            .setLongLabel(label.take(40))
            .setIcon(IconCompat.createWithResource(context, iconRes))
            .setIntent(intent)
            .build()
    }
}
