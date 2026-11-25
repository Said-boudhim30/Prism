package com.example.player

import com.example.model.AudioTrack
import com.example.model.AutoStudioCategory
import com.example.model.AutoStudioProfile
import com.example.model.MediaKnowledgeReport
import com.example.model.MediaKnowledgeCategory
import com.example.model.ProEqPreset
import com.example.model.ReverbEnvironment

/**
 * Intelligent Auto-Studio Mastering Analyzer.
 *
 * Inspects the active audio track's semantic profile (Quran recitation, vocal/lecture/podcast,
 * lossless hi-res mastering, acoustic/classical, deep electronic, cinematic soundtrack) combined
 * with its file codec/format and optional live spectral balance to automatically compute a
 * cohesive, studio-mastered 10-band EQ curve + warmth + stereo width + air + dynamics chain.
 */
object AutoStudioMasteringEngine {

    private val quranKeywords = listOf(
        "quran", "surah", "sura", "ayah", "tilawah", "tilawa", "recitation", "tajweed",
        "قرآن", "سورة", "آية", "تلاوة", "مصحف", "الشيخ", "قارئ", "دعاء", "أذكار", "أذان",
        "mishary", "alafasy", "sudais", "minshawi", "husary", "basit", "dosari", "ajmi", "maher"
    )

    private val vocalPodcastKeywords = listOf(
        "podcast", "lecture", "speech", "khutbah", "dars", "interview", "audiobook",
        "talk", "voice", "memo", "recording", "nasheed", "anashid", "acapella",
        "محاضرة", "درس", "خطبة", "بودكاست", "أنشودة", "أناشيد", "صوت", "تسجيل"
    )

    private val electronicKeywords = listOf(
        "electronic", "edm", "bass", "trap", "house", "techno", "dubstep", "synth",
        "cyber", "club", "beat", "remix", "phonk", "trance", "dance", "drift"
    )

    private val acousticClassicalKeywords = listOf(
        "acoustic", "piano", "violin", "oud", "guitar", "classical", "orchestra",
        "symphony", "cello", "flute", "jazz", "unplugged", "ballad", "live",
        "عود", "بيانو", "كمان", "أكوستيك", "كلاسيك", "موسيقى هادئة"
    )

    private val cinematicKeywords = listOf(
        "ost", "soundtrack", "cinema", "epic", "score", "theme", "ambient", "chill",
        "lofi", "lo-fi", "horizon", "aurora", "rain", "nature", "sleep", "meditation"
    )

    private val losslessExtensions = setOf("FLAC", "WAV", "ALAC", "AIF", "AIFF", "DSF", "APE")

    fun analyzeTrack(
        track: AudioTrack,
        knowledgeReport: MediaKnowledgeReport? = null,
        liveSpectrum: List<Float> = emptyList(),
    ): AutoStudioProfile {
        val haystack = buildString {
            append(track.title).append(' ')
            append(track.artist).append(' ')
            append(track.album).append(' ')
            append(track.folderName).append(' ')
            knowledgeReport?.genre?.let { append(it).append(' ') }
            knowledgeReport?.moodLabel?.let { append(it).append(' ') }
        }.lowercase()

        val ext = track.extension.uppercase()
        val isLossless = ext in losslessExtensions

        val detectedCategory = when {
            knowledgeReport?.category == MediaKnowledgeCategory.QURAN ||
                quranKeywords.any { haystack.contains(it) } -> AutoStudioCategory.QURAN_SANCTUARY

            vocalPodcastKeywords.any { haystack.contains(it) } ||
                (track.durationMs >= 20 * 60_000L && !isLossless) -> AutoStudioCategory.VOCAL_BROADCAST

            electronicKeywords.any { haystack.contains(it) } -> AutoStudioCategory.ELECTRONIC_STAGE

            acousticClassicalKeywords.any { haystack.contains(it) } -> AutoStudioCategory.ACOUSTIC_WARMTH

            cinematicKeywords.any { haystack.contains(it) } -> AutoStudioCategory.CINEMATIC_WIDE

            isLossless -> AutoStudioCategory.LOSSLESS_MASTERING

            else -> AutoStudioCategory.BALANCED_STUDIO
        }

        val baseProfile = when (detectedCategory) {
            AutoStudioCategory.QURAN_SANCTUARY -> AutoStudioProfile(
                category = AutoStudioCategory.QURAN_SANCTUARY,
                tenBandGainsDb = ProEqPreset.QURAN_CRYSTAL.gainsDb,
                tubeWarmth = 0.22f,
                stereoWidth = 1.12f,
                subBassPunch = 0.0f,
                airBrilliance = 0.28f,
                vocalClarity = 0.55f,
                compressorStrength = 0.25f,
                crossfeedEnabled = true,
                rumbleFilterHz = 60,
                reverbEnvironment = ReverbEnvironment.MOSQUE,
                confidencePercent = 98,
                summaryReason = "Crystal Tajweed vocal focus + 60Hz rumble cut + gentle sanctuary resonance",
            )

            AutoStudioCategory.VOCAL_BROADCAST -> AutoStudioProfile(
                category = AutoStudioCategory.VOCAL_BROADCAST,
                tenBandGainsDb = ProEqPreset.VOCAL_AIR.gainsDb,
                tubeWarmth = 0.18f,
                stereoWidth = 1.0f,
                subBassPunch = 0.0f,
                airBrilliance = 0.24f,
                vocalClarity = 0.68f,
                compressorStrength = 0.42f,
                crossfeedEnabled = true,
                rumbleFilterHz = 80,
                reverbEnvironment = ReverbEnvironment.NONE,
                confidencePercent = 95,
                summaryReason = "Broadcast vocal presence + 80Hz mic rumble filter + smooth speech leveling",
            )

            AutoStudioCategory.ELECTRONIC_STAGE -> AutoStudioProfile(
                category = AutoStudioCategory.ELECTRONIC_STAGE,
                tenBandGainsDb = ProEqPreset.ELECTRONIC_PUNCH.gainsDb,
                tubeWarmth = 0.28f,
                stereoWidth = 1.38f,
                subBassPunch = 0.58f,
                airBrilliance = 0.45f,
                vocalClarity = 0.18f,
                compressorStrength = 0.38f,
                crossfeedEnabled = false,
                rumbleFilterHz = 30,
                reverbEnvironment = ReverbEnvironment.STUDIO,
                confidencePercent = 96,
                summaryReason = "48Hz sub-bass punch + wide M/S stage + crisp 12kHz high-end sparkle",
            )

            AutoStudioCategory.ACOUSTIC_WARMTH -> AutoStudioProfile(
                category = AutoStudioCategory.ACOUSTIC_WARMTH,
                tenBandGainsDb = ProEqPreset.ACOUSTIC_WARMTH.gainsDb,
                tubeWarmth = 0.44f,
                stereoWidth = 1.24f,
                subBassPunch = 0.14f,
                airBrilliance = 0.36f,
                vocalClarity = 0.28f,
                compressorStrength = 0.18f,
                crossfeedEnabled = true,
                rumbleFilterHz = 30,
                reverbEnvironment = ReverbEnvironment.STUDIO,
                confidencePercent = 95,
                summaryReason = "Analog tube warmth + natural binaural crossfeed + organic studio room",
            )

            AutoStudioCategory.CINEMATIC_WIDE -> AutoStudioProfile(
                category = AutoStudioCategory.CINEMATIC_WIDE,
                tenBandGainsDb = ProEqPreset.CONCERT_HALL.gainsDb,
                tubeWarmth = 0.30f,
                stereoWidth = 1.48f,
                subBassPunch = 0.40f,
                airBrilliance = 0.42f,
                vocalClarity = 0.20f,
                compressorStrength = 0.22f,
                crossfeedEnabled = false,
                rumbleFilterHz = 0,
                reverbEnvironment = ReverbEnvironment.SPATIAL_3D,
                confidencePercent = 94,
                summaryReason = "3D panoramic soundstage + deep harmonic bloom + velvety cinematic highs",
            )

            AutoStudioCategory.LOSSLESS_MASTERING -> AutoStudioProfile(
                category = AutoStudioCategory.LOSSLESS_MASTERING,
                tenBandGainsDb = ProEqPreset.STUDIO_MASTER.gainsDb,
                tubeWarmth = 0.20f,
                stereoWidth = 1.16f,
                subBassPunch = 0.18f,
                airBrilliance = 0.32f,
                vocalClarity = 0.22f,
                compressorStrength = 0.15f,
                crossfeedEnabled = true,
                rumbleFilterHz = 0,
                reverbEnvironment = ReverbEnvironment.NONE,
                confidencePercent = 97,
                summaryReason = "Reference mastering transparency for lossless $ext with micro-detail air",
            )

            AutoStudioCategory.BALANCED_STUDIO -> AutoStudioProfile(
                category = AutoStudioCategory.BALANCED_STUDIO,
                tenBandGainsDb = ProEqPreset.STUDIO_MASTER.gainsDb,
                tubeWarmth = 0.26f,
                stereoWidth = 1.22f,
                subBassPunch = 0.24f,
                airBrilliance = 0.34f,
                vocalClarity = 0.26f,
                compressorStrength = 0.28f,
                crossfeedEnabled = true,
                rumbleFilterHz = 30,
                reverbEnvironment = ReverbEnvironment.STUDIO,
                confidencePercent = 92,
                summaryReason = "Balanced mastering console curve + subtle analog glue + stereo polish",
            )
        }

        // Real-time adaptive micro-calibration using live PCM spectrum energy if currently playing
        if (liveSpectrum.size >= 12) {
            val lowEnergy = (liveSpectrum[0] + liveSpectrum[1] + liveSpectrum[2]) / 3f
            val highEnergy = (liveSpectrum[9] + liveSpectrum[10] + liveSpectrum[11]) / 3f
            val adjustedGains = baseProfile.tenBandGainsDb.toMutableList()
            if (lowEnergy > 0.65f) {
                // Trim excess muddy boom automatically
                adjustedGains[0] = (adjustedGains[0] - 1.5f).coerceIn(-15f, 15f)
                adjustedGains[1] = (adjustedGains[1] - 1.2f).coerceIn(-15f, 15f)
            } else if (lowEnergy < 0.22f && detectedCategory != AutoStudioCategory.QURAN_SANCTUARY) {
                // Gently restore thin bass
                adjustedGains[1] = (adjustedGains[1] + 1.5f).coerceIn(-15f, 15f)
            }
            if (highEnergy < 0.20f) {
                // Gently restore dull compressed MP3 high frequencies
                adjustedGains[8] = (adjustedGains[8] + 1.2f).coerceIn(-15f, 15f)
                adjustedGains[9] = (adjustedGains[9] + 1.8f).coerceIn(-15f, 15f)
            }
            return baseProfile.copy(tenBandGainsDb = adjustedGains)
        }

        return baseProfile
    }
}
