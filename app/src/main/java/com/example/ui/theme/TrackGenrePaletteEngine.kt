package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.example.model.AudioTrack
import com.example.model.MediaKnowledgeCategory
import com.example.model.MediaKnowledgeReport
import com.example.model.PlayerArtworkStyle

enum class TrackGenreMood {
    QURAN_SPIRITUAL,
    NASHEED_WARM,
    CLASSICAL_ORCHESTRAL,
    ELECTRONIC_ENERGY,
    LOFI_AMBIENT,
    LECTURE_PODCAST,
    ACOUSTIC_POP,
}

data class GenreDynamicPalette(
    val mood: TrackGenreMood,
    val primaryAccent: Color,
    val secondaryGlow: Color,
    val deepBackdrop: Color,
    val badgeTint: Color,
    val suggestedShape: PlayerArtworkStyle,
)

/**
 * Real-time Genre & Mood Dynamic Color Engine.
 * Analyzes track metadata, format, folder, and optional knowledge report to synthesize
 * expressive Material 3 color atmospheres that adapt seamlessly to every genre of audio.
 */
object TrackGenrePaletteEngine {

    fun classifyMood(
        track: AudioTrack?,
        report: MediaKnowledgeReport? = null,
    ): TrackGenreMood {
        if (track == null) return TrackGenreMood.ACOUSTIC_POP
        if (report?.category == MediaKnowledgeCategory.QURAN || report?.category == MediaKnowledgeCategory.HADITH) {
            return TrackGenreMood.QURAN_SPIRITUAL
        }

        val haystack = buildString {
            append(track.title).append(' ')
            append(track.artist).append(' ')
            append(track.album).append(' ')
            append(track.folderName).append(' ')
            report?.genre?.let { append(it).append(' ') }
            report?.moodLabel?.let { append(it).append(' ') }
        }.lowercase()

        return when {
            haystack.containsAny(
                "quran", "surah", "sura", "ayah", "recitation", "tilawah", "mishary", "sudais", "husary", "minshawi", "basit", "ajmi", "maher",
                "قرآن", "سورة", "تلاوة", "المعيقلي", "العفاسي", "السديس", "الحصري", "المنشاوي", "أذكار", "رقية", "دعاء"
            ) -> TrackGenreMood.QURAN_SPIRITUAL

            haystack.containsAny(
                "nasheed", "anashid", "maher zain", "sami yusuf", "hymn", "islamic",
                "نشيد", "أناشيد", "مديح", "صلوات", "إنشاد", "رمضان"
            ) -> TrackGenreMood.NASHEED_WARM

            haystack.containsAny(
                "lecture", "podcast", "khutbah", "dars", "audiobook", "talk", "speech", "interview", "story", "history",
                "محاضرة", "درس", "خطبة", "بودكاست", "قصة", "كتاب صوتي", "تفسير", "سيرة"
            ) || track.durationMs >= 20L * 60_000L -> TrackGenreMood.LECTURE_PODCAST

            haystack.containsAny(
                "classical", "piano", "violin", "orchestra", "symphony", "concerto", "cello", "instrumental", "oud", "flute", "mozart", "chopin", "beethoven",
                "كلاسيك", "بيانو", "كمان", "عود", "موسيقى هادئة", "أوركسترا", "معزوفة"
            ) -> TrackGenreMood.CLASSICAL_ORCHESTRAL

            haystack.containsAny(
                "lofi", "lo-fi", "chill", "ambient", "sleep", "rain", "study", "meditation", "zen",
                "هادئ", "مطر", "نوم", "استرخاء", "هدوء"
            ) -> TrackGenreMood.LOFI_AMBIENT

            haystack.containsAny(
                "synth", "electronic", "edm", "club", "bass", "beat", "techno", "house", "trap", "remix", "cyber", "neon", "pulse", "dubstep",
                "إلكتروني", "ريمكس", "حماس", "طاقة"
            ) -> TrackGenreMood.ELECTRONIC_ENERGY

            haystack.containsAny(
                "night", "midnight", "dream", "moon", "calm", "focus",
                "ليل", "حلم"
            ) -> TrackGenreMood.LOFI_AMBIENT

            else -> TrackGenreMood.ACOUSTIC_POP
        }
    }

    fun paletteForMood(mood: TrackGenreMood, isDark: Boolean): GenreDynamicPalette = when (mood) {
        TrackGenreMood.QURAN_SPIRITUAL -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
            secondaryGlow = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
            deepBackdrop = if (isDark) Color(0xFF052E24) else Color(0xFFD1FAE5),
            badgeTint = Color(0xFF10B981),
            suggestedShape = PlayerArtworkStyle.SOFT_PILL_ARCH,
        )
        TrackGenreMood.NASHEED_WARM -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFFF59E0B) else Color(0xFFB45309),
            secondaryGlow = if (isDark) Color(0xFFFB7185) else Color(0xFFE11D48),
            deepBackdrop = if (isDark) Color(0xFF2E1A07) else Color(0xFFFEF3C7),
            badgeTint = Color(0xFFF59E0B),
            suggestedShape = PlayerArtworkStyle.SCALLOPED_COOKIE,
        )
        TrackGenreMood.CLASSICAL_ORCHESTRAL -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFF60A5FA) else Color(0xFF1D4ED8),
            secondaryGlow = if (isDark) Color(0xFFC084FC) else Color(0xFF7C3AED),
            deepBackdrop = if (isDark) Color(0xFF0B1C3D) else Color(0xFFDBEAFE),
            badgeTint = Color(0xFF3B82F6),
            suggestedShape = PlayerArtworkStyle.VINYL_DISC,
        )
        TrackGenreMood.ELECTRONIC_ENERGY -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFFC084FC) else Color(0xFF7E22CE),
            secondaryGlow = if (isDark) Color(0xFF22D3EE) else Color(0xFF0891B2),
            deepBackdrop = if (isDark) Color(0xFF220A38) else Color(0xFFF3E8FF),
            badgeTint = Color(0xFFA855F7),
            suggestedShape = PlayerArtworkStyle.CLOVER_FOUR,
        )
        TrackGenreMood.LOFI_AMBIENT -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFF818CF8) else Color(0xFF4338CA),
            secondaryGlow = if (isDark) Color(0xFFF472B6) else Color(0xFFDB2777),
            deepBackdrop = if (isDark) Color(0xFF151236) else Color(0xFFE0E7FF),
            badgeTint = Color(0xFF6366F1),
            suggestedShape = PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE,
        )
        TrackGenreMood.LECTURE_PODCAST -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFF2DD4BF) else Color(0xFF0F766E),
            secondaryGlow = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
            deepBackdrop = if (isDark) Color(0xFF06292A) else Color(0xFFCCFBF1),
            badgeTint = Color(0xFF14B8A6),
            suggestedShape = PlayerArtworkStyle.ROUNDED_CARD,
        )
        TrackGenreMood.ACOUSTIC_POP -> GenreDynamicPalette(
            mood = mood,
            primaryAccent = if (isDark) Color(0xFFFB7185) else Color(0xFFBE123C),
            secondaryGlow = if (isDark) Color(0xFFFBBF24) else Color(0xFFEA580C),
            deepBackdrop = if (isDark) Color(0xFF2E0D1B) else Color(0xFFFFE4E6),
            badgeTint = Color(0xFFF43F5E),
            suggestedShape = PlayerArtworkStyle.SCALLOPED_COOKIE,
        )
    }

    /**
     * Blends the Genre Dynamic Palette with the extracted Album Art Palette when both are enabled,
     * creating an unmistakable living signature unique to both the track's genre and cover art.
     */
    fun resolveCombinedPalette(
        track: AudioTrack?,
        report: MediaKnowledgeReport?,
        artPalette: AlbumArtPalette?,
        isDark: Boolean,
        genreColorsEnabled: Boolean,
        albumTintEnabled: Boolean,
        fallbackPrimary: Color,
        fallbackSecondary: Color,
        fallbackDeep: Color,
    ): GenreDynamicPalette {
        val mood = classifyMood(track, report)
        val genrePal = paletteForMood(mood, isDark)
        return when {
            genreColorsEnabled && albumTintEnabled && artPalette != null -> genrePal.copy(
                primaryAccent = lerp(genrePal.primaryAccent, artPalette.vibrant, 0.42f),
                secondaryGlow = lerp(genrePal.secondaryGlow, artPalette.dominant, 0.35f),
                deepBackdrop = lerp(genrePal.deepBackdrop, artPalette.deepTint, 0.45f),
            )
            genreColorsEnabled -> genrePal
            albumTintEnabled && artPalette != null -> genrePal.copy(
                primaryAccent = artPalette.vibrant,
                secondaryGlow = artPalette.dominant,
                deepBackdrop = artPalette.deepTint,
            )
            else -> genrePal.copy(
                primaryAccent = fallbackPrimary,
                secondaryGlow = fallbackSecondary,
                deepBackdrop = fallbackDeep,
            )
        }
    }

    private fun String.containsAny(vararg needles: String): Boolean =
        needles.any { this.contains(it, ignoreCase = true) }
}
