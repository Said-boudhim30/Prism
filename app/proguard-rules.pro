# ==========================================================
# Prism Media — Hardened R8 Obfuscation & Anti-Tamper Rules
# ==========================================================
-optimizationpasses 5
-allowaccessmodification
-repackageclasses 'p'
-renamesourcefileattribute SourceFile

# Strip verbose debug logs in release builds to reduce reverse-engineering surface
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Keep Media3 / ExoPlayer classes intact for hardware codec & audio pipelines
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep DataStore Preferences & AppWidgetProviders / Receivers registered in AndroidManifest
-keep class androidx.datastore.** { *; }
-keep public class com.example.widget.PrismMediaWidgetProvider { *; }
-keep public class com.example.widget.PrismExpressiveCardWidgetProvider { *; }
-keep public class com.example.player.MediaNotificationReceiver { *; }

# Keep Jetpack Compose & Kotlin Coroutines runtime metadata safely
-keepattributes RuntimeVisibleAnnotations,Signature,InnerClasses,EnclosingMethod
-dontwarn kotlinx.coroutines.**
