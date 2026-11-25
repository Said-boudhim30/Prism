package com.example.security

import java.net.URI
import java.util.Locale

/**
 * Always-On Built-In Ethical & Clean Content Shield for Prism Media.
 * Strictly blocks explicit/adult/NSFW media, streams, online knowledge queries,
 * and filters/rejects indecent or profane lyrics across Arabic, English, French, and Spanish.
 * Permanently active inside the core engine with zero opt-out.
 */
object ContentPurityGuard {

    // Explicit/NSFW host & keyword roots (word-boundary matched to avoid false positives on clean words)
    private val adultWordPatterns: List<Regex> = listOf(
        "porn", "porno", "pornography", "xxx", "nsfw", "hentai", "xvideos", "xnxx",
        "pornhub", "redtube", "youporn", "brazzers", "onlyfans", "chaturbate",
        "camgirl", "camwhore", "stripclub", "stripper", "sexvideo", "sextape",
        "erotic", "erotica", "nude", "nudes", "nudity", "naked", "playboy",
        "milf", "bdsm", "fetish", "incest", "orgy", "blowjob", "handjob",
        "gangbang", "threesome", "hardcore", "softcore", "escort", "brothel",
        "prostitute", "hooker", "javhd", "rule34", "fap", "nsfl",
        // French / Spanish explicit tokens
        "porno", "pornographie", "pornografia", "desnudo", "desnuda", "puta", "pute",
        "salope", "bordel", "sexe", "sexo", "caliente18", "xxx18"
    ).map { Regex("""\b${Regex.escape(it)}\b""", RegexOption.IGNORE_CASE) }

    // Substring domain/slug checks for unambiguous adult sites & tags
    private val adultHostFragments: List<String> = listOf(
        "pornhub", "xvideos", "xnxx", "redtube", "youporn", "xhamster",
        "brazzers", "bangbros", "naughtyamerica", "chaturbate", "stripchat",
        "livejasmin", "onlyfans", "spankbang", "eporner", "tnaflix", "tube8",
        "beeg", "youjizz", "hentaihaven", "nhentai", "rule34", "sex.com",
        "adultfriendfinder", "cam4", "bongacams", "myfreecams"
    )

    // Arabic explicit / adult terms (matched on normalized diacritic-free Arabic)
    private val arabicExplicitTerms: List<String> = listOf(
        "اباحي", "اباحيه", "اباحيات", "سكس", "سكسي", "بورن", "بورنو",
        "نيك", "عاري", "عاريه", "عراه", "تعري", "دعاره", "فاحشه",
        "شرموط", "شرموطه", "قحبه", "ممحون", "ممحونه", "جنسيه صريحه",
        "افلام جنس", "فيلم جنس", "مقطع جنسي", "مقاطع جنسيه", "مقاطع اباحيه",
        "فيديو اباحي", "فيديو سكس", "افلام سكس", "مواقع اباحيه", "بث اباحي",
        "زنا", "لواط", "سحاق", "بورنهاب", "اكس فيديوز"
    )

    // Indecent / profane words strictly filtered from lyrics & user-entered text
    private val profanityWordPatterns: List<Regex> = listOf(
        "fuck", "fucking", "fucked", "fucker", "motherfucker", "mf",
        "shit", "bullshit", "shitty", "bitch", "bitches", "bastard",
        "asshole", "dick", "dickhead", "cock", "pussy", "cunt", "whore",
        "slut", "hoe", "hoes", "slutty", "boobs", "tits", "titties",
        "penis", "vagina", "orgasm", "horny", "cum", "cumming", "jizz",
        "dildo", "vibrator", "masturbate", "masturbation", "fuckboy",
        // French / Spanish profanity
        "putain", "merde", "encule", "connard", "batard", "nique", "baise",
        "baiser", "couille", "chatte", "bite", "branleur",
        "mierda", "joder", "coño", "cabron", "pendejo", "verga", "chingar",
        "zorra", "polla", "follar", "maricon", "hijueputa"
    ).map { Regex("""\b${Regex.escape(it)}\b""", RegexOption.IGNORE_CASE) }

    // Arabic vulgar / indecent words filtered from lyrics & text
    private val arabicProfanityTerms: List<String> = listOf(
        "كس ام", "كسمك", "ابن الشرموطه", "ابن القحبه", "يلعن دين", "لعنة الله",
        "طيز", "زبي", "زبك", "منيوك", "متناك", "خول", "عرص", "ديوث",
        "قحب", "شرمط", "نيج", "انيك", "ينيك", "زامل", "قحبة", "شرموطة",
        "تبا لك", "ابن الكلب", "ابن الحرام", "وسخ", "سافل"
    )

    private val lrcTimestampPrefix = Regex("""^(\[\d{1,2}:\d{2}(?:[.:]\d{1,3})?]\s*)+""")

    /**
     * Returns true if any provided title, artist, filename, or text clearly refers
     * to adult/explicit/NSFW material.
     */
    fun isExplicitOrAdult(vararg fields: String?): Boolean {
        val combined = fields.filterNotNull().joinToString(" ").trim()
        if (combined.isEmpty()) return false
        val lower = combined.lowercase(Locale.ROOT)

        if (adultHostFragments.any { lower.contains(it) }) return true
        if (adultWordPatterns.any { it.containsMatchIn(lower) }) return true

        val normalizedAr = normalizeArabic(combined)
        if (arabicExplicitTerms.any { normalizedAr.contains(normalizeArabic(it)) }) return true

        return false
    }

    /**
     * Strictly rejects explicit/adult HTTPS stream URLs (checks host, path, and query).
     */
    fun isBlockedStreamUrl(rawUrl: String): Boolean {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) return false
        val lower = trimmed.lowercase(Locale.ROOT)
        if (adultHostFragments.any { lower.contains(it) }) return true
        val host = runCatching { URI(trimmed).host.orEmpty().lowercase(Locale.ROOT) }.getOrDefault("")
        if (host.endsWith(".xxx") || host.endsWith(".porn") || host.endsWith(".adult") || host.endsWith(".sex")) {
            return true
        }
        return isExplicitOrAdult(trimmed)
    }

    /**
     * Returns true if text contains profane, vulgar, or indecent language.
     */
    fun containsProfanity(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val lower = text.lowercase(Locale.ROOT)
        if (profanityWordPatterns.any { it.containsMatchIn(lower) }) return true
        val normalizedAr = normalizeArabic(text)
        if (arabicProfanityTerms.any { normalizedAr.contains(normalizeArabic(it)) }) return true
        return false
    }

    /**
     * Strictly filters song lyrics:
     * - If lyrics contain explicit/adult themes, rejects the entire lyrics block (`null`).
     * - Removes any individual lines containing vulgar/profane words.
     * - If more than 25% of non-empty lines were profane, rejects the entire song's lyrics (`null`).
     * - Returns clean lyrics only (preserving valid `.lrc` timestamps on clean lines).
     */
    fun filterLyricsStrictly(rawLyrics: String?): String? {
        if (rawLyrics.isNullOrBlank()) return null
        if (isExplicitOrAdult(rawLyrics)) return null

        val nonEmptyLines = rawLyrics.lines().filter { it.isNotBlank() }
        if (nonEmptyLines.isEmpty()) return null

        var removedCount = 0
        val cleanLines = mutableListOf<String>()

        for (line in nonEmptyLines) {
            val lyricBody = lrcTimestampPrefix.replace(line.trim(), "").trim()
            if (isExplicitOrAdult(lyricBody) || containsProfanity(lyricBody)) {
                removedCount++
            } else {
                cleanLines.add(line)
            }
        }

        // Reject completely if heavily profane or nothing clean remains
        if (cleanLines.isEmpty()) return null
        if (removedCount > 0 && removedCount.toFloat() / nonEmptyLines.size.toFloat() > 0.25f) {
            return null
        }
        return cleanLines.joinToString("\n").trim().takeIf { it.isNotBlank() }
    }

    /**
     * Ensures online encyclopedia summaries / artist bios never display explicit/NSFW or vulgar content.
     */
    fun sanitizeEncyclopediaText(text: String?): String? {
        if (text.isNullOrBlank()) return null
        if (isExplicitOrAdult(text) || containsProfanity(text)) return null
        return text
    }

    private fun normalizeArabic(input: String): String {
        return input
            .replace(Regex("[\u064B-\u065F\u0670\u0640]"), "")
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .lowercase(Locale.ROOT)
    }
}
