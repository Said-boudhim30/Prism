package com.example.data

import com.example.model.MediaKnowledgeCategory

data class SurahCatalogEntry(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val ayahCount: Int,
    val isMeccan: Boolean,
    val aliases: List<String> = emptyList(),
)

data class QuranDetectionResult(
    val surah: SurahCatalogEntry?,
    val reciterDisplayName: String?,
    val isQuranic: Boolean,
)

data class HadithDetectionResult(
    val isHadithOrIslamicLecture: Boolean,
    val collectionKey: String?,
    val collectionArabicName: String?,
    val collectionEnglishName: String?,
    val searchTopic: String,
)

/**
 * Built-in verified index of all 114 Surahs of the Holy Quran, renowned reciters,
 * and authentic Hadith collections so Prism Media can instantly identify Islamic
 * audio/video files and query verified Quran/Hadith/Encyclopedia sources.
 */
object QuranHadithCatalog {

    private val DIACRITICS_REGEX = Regex("[\u064B-\u065F\u0670\u0640]")

    fun normalizeArabic(input: String): String {
        return input
            .replace(DIACRITICS_REGEX, "")
            .replace('أ', 'ا')
            .replace('إ', 'ا')
            .replace('آ', 'ا')
            .replace('ة', 'ه')
            .replace('ى', 'ي')
            .lowercase()
            .trim()
    }

    val surahs: List<SurahCatalogEntry> = listOf(
        SurahCatalogEntry(1, "الفاتحة", "Al-Fatihah", 7, true, listOf("fatiha", "fatihah", "الفاتحه")),
        SurahCatalogEntry(2, "البقرة", "Al-Baqarah", 286, false, listOf("baqara", "baqarah", "البقره")),
        SurahCatalogEntry(3, "آل عمران", "Aal-E-Imran", 200, false, listOf("imran", "al imran", "ال عمران")),
        SurahCatalogEntry(4, "النساء", "An-Nisa", 176, false, listOf("nisa", "nisaa")),
        SurahCatalogEntry(5, "المائدة", "Al-Ma'idah", 120, false, listOf("maida", "maidah", "المائده")),
        SurahCatalogEntry(6, "الأنعام", "Al-An'am", 165, true, listOf("anam", "anaam")),
        SurahCatalogEntry(7, "الأعراف", "Al-A'raf", 206, true, listOf("araf", "aaraf")),
        SurahCatalogEntry(8, "الأنفال", "Al-Anfal", 75, false, listOf("anfal")),
        SurahCatalogEntry(9, "التوبة", "At-Tawbah", 129, false, listOf("tawba", "tawbah", "التوبه")),
        SurahCatalogEntry(10, "يونس", "Yunus", 109, true, listOf("yunus", "younus")),
        SurahCatalogEntry(11, "هود", "Hud", 123, true, listOf("hud", "hood")),
        SurahCatalogEntry(12, "يوسف", "Yusuf", 111, true, listOf("yusuf", "youssef", "yousuf")),
        SurahCatalogEntry(13, "الرعد", "Ar-Ra'd", 43, false, listOf("raad", "rad")),
        SurahCatalogEntry(14, "إبراهيم", "Ibrahim", 52, true, listOf("ibrahim", "ابراهيم")),
        SurahCatalogEntry(15, "الحجر", "Al-Hijr", 99, true, listOf("hijr")),
        SurahCatalogEntry(16, "النحل", "An-Nahl", 128, true, listOf("nahl")),
        SurahCatalogEntry(17, "الإسراء", "Al-Isra", 111, true, listOf("isra", "israa", "الاسراء")),
        SurahCatalogEntry(18, "الكهف", "Al-Kahf", 110, true, listOf("kahf", "كهف")),
        SurahCatalogEntry(19, "مريم", "Maryam", 98, true, listOf("maryam", "mariam")),
        SurahCatalogEntry(20, "طه", "Taha", 135, true, listOf("taha", "ta-ha")),
        SurahCatalogEntry(21, "الأنبياء", "Al-Anbiya", 112, true, listOf("anbiya", "anbiyaa")),
        SurahCatalogEntry(22, "الحج", "Al-Hajj", 78, false, listOf("hajj")),
        SurahCatalogEntry(23, "المؤمنون", "Al-Mu'minun", 118, true, listOf("muminun", "mominun")),
        SurahCatalogEntry(24, "النور", "An-Nur", 64, false, listOf("noor", "nur")),
        SurahCatalogEntry(25, "الفرقان", "Al-Furqan", 77, true, listOf("furqan")),
        SurahCatalogEntry(26, "الشعراء", "Ash-Shu'ara", 227, true, listOf("shuara")),
        SurahCatalogEntry(27, "النمل", "An-Naml", 93, true, listOf("naml")),
        SurahCatalogEntry(28, "القصص", "Al-Qasas", 88, true, listOf("qasas")),
        SurahCatalogEntry(29, "العنكبوت", "Al-Ankabut", 69, true, listOf("ankabut")),
        SurahCatalogEntry(30, "الروم", "Ar-Rum", 60, true, listOf("rum", "room")),
        SurahCatalogEntry(31, "لقمان", "Luqman", 34, true, listOf("luqman", "lokman")),
        SurahCatalogEntry(32, "السجدة", "As-Sajdah", 30, true, listOf("sajda", "sajdah", "السجده")),
        SurahCatalogEntry(33, "الأحزاب", "Al-Ahzab", 73, false, listOf("ahzab")),
        SurahCatalogEntry(34, "سبأ", "Saba", 54, true, listOf("saba")),
        SurahCatalogEntry(35, "فاطر", "Fatir", 45, true, listOf("fatir")),
        SurahCatalogEntry(36, "يس", "Ya-Sin", 83, true, listOf("yasin", "yaseen", "ya-sin", "سورة يس")),
        SurahCatalogEntry(37, "الصافات", "As-Saffat", 182, true, listOf("saffat")),
        SurahCatalogEntry(38, "ص", "Sad", 88, true, listOf("surah sad", "سورة ص")),
        SurahCatalogEntry(39, "الزمر", "Az-Zumar", 75, true, listOf("zumar")),
        SurahCatalogEntry(40, "غافر", "Ghafir", 85, true, listOf("ghafir")),
        SurahCatalogEntry(41, "فصلت", "Fussilat", 54, true, listOf("fussilat")),
        SurahCatalogEntry(42, "الشورى", "Ash-Shura", 53, true, listOf("shura")),
        SurahCatalogEntry(43, "الزخرف", "Az-Zukhruf", 89, true, listOf("zukhruf")),
        SurahCatalogEntry(44, "الدخان", "Ad-Dukhan", 59, true, listOf("dukhan")),
        SurahCatalogEntry(45, "الجاثية", "Al-Jathiyah", 37, true, listOf("jathiya", "الجاثيه")),
        SurahCatalogEntry(46, "الأحقاف", "Al-Ahqaf", 35, true, listOf("ahqaf")),
        SurahCatalogEntry(47, "محمد", "Muhammad", 38, false, listOf("surah muhammad", "سورة محمد")),
        SurahCatalogEntry(48, "الفتح", "Al-Fath", 29, false, listOf("fath")),
        SurahCatalogEntry(49, "الحجرات", "Al-Hujurat", 18, false, listOf("hujurat")),
        SurahCatalogEntry(50, "ق", "Qaf", 45, true, listOf("qaf", "سورة ق")),
        SurahCatalogEntry(51, "الذاريات", "Adh-Dhariyat", 60, true, listOf("dhariyat", "zariyat")),
        SurahCatalogEntry(52, "الطور", "At-Tur", 49, true, listOf("at-tur")),
        SurahCatalogEntry(53, "النجم", "An-Najm", 62, true, listOf("najm")),
        SurahCatalogEntry(54, "القمر", "Al-Qamar", 55, true, listOf("qamar")),
        SurahCatalogEntry(55, "الرحمن", "Ar-Rahman", 78, false, listOf("rahman", "الرحمان")),
        SurahCatalogEntry(56, "الواقعة", "Al-Waqi'ah", 96, true, listOf("waqiah", "waqia", "الواقعه")),
        SurahCatalogEntry(57, "الحديد", "Al-Hadid", 29, false, listOf("hadid")),
        SurahCatalogEntry(58, "المجادلة", "Al-Mujadila", 22, false, listOf("mujadila")),
        SurahCatalogEntry(59, "الحشر", "Al-Hashr", 24, false, listOf("hashr")),
        SurahCatalogEntry(60, "الممتحنة", "Al-Mumtahanah", 13, false, listOf("mumtahana")),
        SurahCatalogEntry(61, "الصف", "As-Saf", 14, false, listOf("as-saf")),
        SurahCatalogEntry(62, "الجمعة", "Al-Jumu'ah", 11, false, listOf("jumuah", "jumua", "الجمعه")),
        SurahCatalogEntry(63, "المنافقون", "Al-Munafiqun", 11, false, listOf("munafiqun")),
        SurahCatalogEntry(64, "التغابن", "At-Taghabun", 18, false, listOf("taghabun")),
        SurahCatalogEntry(65, "الطلاق", "At-Talaq", 12, false, listOf("talaq")),
        SurahCatalogEntry(66, "التحريم", "At-Tahrim", 12, false, listOf("tahrim")),
        SurahCatalogEntry(67, "الملك", "Al-Mulk", 30, true, listOf("mulk", "تبارك")),
        SurahCatalogEntry(68, "القلم", "Al-Qalam", 52, true, listOf("qalam")),
        SurahCatalogEntry(69, "الحاقة", "Al-Haqqah", 52, true, listOf("haqqah", "الحاقه")),
        SurahCatalogEntry(70, "المعارج", "Al-Ma'arij", 44, true, listOf("maarij")),
        SurahCatalogEntry(71, "نوح", "Nuh", 28, true, listOf("surah nuh", "سورة نوح")),
        SurahCatalogEntry(72, "الجن", "Al-Jinn", 28, true, listOf("jinn")),
        SurahCatalogEntry(73, "المزمل", "Al-Muzzammil", 20, true, listOf("muzzammil", "muzammil")),
        SurahCatalogEntry(74, "المدثر", "Al-Muddaththir", 56, true, listOf("muddaththir", "mudassir")),
        SurahCatalogEntry(75, "القيامة", "Al-Qiyamah", 40, true, listOf("qiyamah", "القيامه")),
        SurahCatalogEntry(76, "الإنسان", "Al-Insan", 31, false, listOf("insan", "الانسان")),
        SurahCatalogEntry(77, "المرسلات", "Al-Mursalat", 50, true, listOf("mursalat")),
        SurahCatalogEntry(78, "النبأ", "An-Naba", 40, true, listOf("naba", "النبأ")),
        SurahCatalogEntry(79, "النازعات", "An-Nazi'at", 46, true, listOf("naziat")),
        SurahCatalogEntry(80, "عبس", "Abasa", 42, true, listOf("abasa")),
        SurahCatalogEntry(81, "التكوير", "At-Takwir", 29, true, listOf("takwir")),
        SurahCatalogEntry(82, "الانفطار", "Al-Infitar", 19, true, listOf("infitar")),
        SurahCatalogEntry(83, "المطففين", "Al-Mutaffifin", 36, true, listOf("mutaffifin")),
        SurahCatalogEntry(84, "الانشقاق", "Al-Inshiqaq", 25, true, listOf("inshiqaq")),
        SurahCatalogEntry(85, "البروج", "Al-Buruj", 22, true, listOf("buruj")),
        SurahCatalogEntry(86, "الطارق", "At-Tariq", 17, true, listOf("tariq")),
        SurahCatalogEntry(87, "الأعلى", "Al-A'la", 19, true, listOf("ala", "الاعلي")),
        SurahCatalogEntry(88, "الغاشية", "Al-Ghashiyah", 26, true, listOf("ghashiyah", "الغاشيه")),
        SurahCatalogEntry(89, "الفجر", "Al-Fajr", 30, true, listOf("fajr")),
        SurahCatalogEntry(90, "البلد", "Al-Balad", 20, true, listOf("balad")),
        SurahCatalogEntry(91, "الشمس", "Ash-Shams", 15, true, listOf("shams")),
        SurahCatalogEntry(92, "الليل", "Al-Layl", 21, true, listOf("layl")),
        SurahCatalogEntry(93, "الضحى", "Ad-Duhaa", 11, true, listOf("duha", "duhaa", "الضحي")),
        SurahCatalogEntry(94, "الشرح", "Ash-Sharh", 8, true, listOf("sharh", "inshirah")),
        SurahCatalogEntry(95, "التين", "At-Tin", 8, true, listOf("at-tin")),
        SurahCatalogEntry(96, "العلق", "Al-Alaq", 19, true, listOf("alaq")),
        SurahCatalogEntry(97, "القدر", "Al-Qadr", 5, true, listOf("qadr")),
        SurahCatalogEntry(98, "البينة", "Al-Bayyinah", 8, false, listOf("bayyinah", "البينه")),
        SurahCatalogEntry(99, "الزلزلة", "Az-Zalzalah", 8, false, listOf("zalzalah", "الزلزله")),
        SurahCatalogEntry(100, "العاديات", "Al-Adiyat", 11, true, listOf("adiyat")),
        SurahCatalogEntry(101, "القارعة", "Al-Qari'ah", 11, true, listOf("qariah", "القارعه")),
        SurahCatalogEntry(102, "التكاثر", "At-Takathur", 8, true, listOf("takathur")),
        SurahCatalogEntry(103, "العصر", "Al-Asr", 3, true, listOf("asr")),
        SurahCatalogEntry(104, "الهمزة", "Al-Humazah", 9, true, listOf("humazah")),
        SurahCatalogEntry(105, "الفيل", "Al-Fil", 5, true, listOf("al-fil")),
        SurahCatalogEntry(106, "قريش", "Quraysh", 4, true, listOf("quraysh", "quraish")),
        SurahCatalogEntry(107, "الماعون", "Al-Ma'un", 7, true, listOf("maun")),
        SurahCatalogEntry(108, "الكوثر", "Al-Kawthar", 3, true, listOf("kawthar", "kauthar")),
        SurahCatalogEntry(109, "الكافرون", "Al-Kafirun", 6, true, listOf("kafirun")),
        SurahCatalogEntry(110, "النصر", "An-Nasr", 3, false, listOf("nasr")),
        SurahCatalogEntry(111, "المسد", "Al-Masad", 5, true, listOf("masad")),
        SurahCatalogEntry(112, "الإخلاص", "Al-Ikhlas", 4, true, listOf("ikhlas", "الاخلاص")),
        SurahCatalogEntry(113, "الفلق", "Al-Falaq", 5, true, listOf("falaq")),
        SurahCatalogEntry(114, "الناس", "An-Nas", 6, true, listOf("an-nas", "al-nas")),
    )

    private val knownReciters: List<Pair<List<String>, String>> = listOf(
        listOf("العفاسي", "مشاري", "alafasy", "afasy", "mishary") to "مشاري راشد العفاسي",
        listOf("المنشاوي", "minshawi", "menshawi") to "محمد صديق المنشاوي",
        listOf("الحصري", "husary", "hussary", "hosary") to "محمود خليل الحصري",
        listOf("عبد الباسط", "عبدالباسط", "abdulbasit", "abdul basit", "abd al-basit") to "عبد الباسط عبد الصمد",
        listOf("السديس", "sudais", "sudays") to "عبد الرحمن السديس",
        listOf("المعيقلي", "ماهر المعيقلي", "maher al muaiqly", "muaiqly") to "ماهر المعيقلي",
        listOf("الدوسري", "ياسر الدوسري", "dosari", "yasser al-dosari") to "ياسر الدوسري",
        listOf("فارس عباد", "fares abbad", "faris abbad") to "فارس عباد",
        listOf("إسلام صبحي", "اسلام صبحي", "islam sobhi") to "إسلام صبحي",
        listOf("سعد الغامدي", "الغامدي", "ghamdi", "saad al ghamdi") to "سعد الغامدي",
        listOf("أحمد العجمي", "احمد العجمي", "العجمي", "ajmi", "ajamy") to "أحمد بن علي العجمي",
        listOf("الشريم", "سعود الشريم", "shuraim") to "سعود الشريم",
        listOf("ناصر القطامي", "القطامي", "qatami") to "ناصر القطامي",
        listOf("عمر القزابري", "القزابري", "kazabri", "qazabri") to "عمر القزابري",
        listOf("العيون الكوشي", "الكوشي", "kouchi", "koshi") to "العيون الكوشي",
        listOf("هزاع البلوشي", "البلوشي", "hazza", "balushi") to "هزاع البلوشي",
    )

    private val quranKeywords = listOf(
        "سورة", "سوره", "القرآن", "القران", "قرآن", "قران", "تلاوة", "تلاوه",
        "مصحف", "قراءة", "ترتيل", "تجويد", "surah", "sura", "quran", "qoran", "coran", "tilawah", "recitation"
    )

    private val hadithCollections: List<Triple<List<String>, String, Pair<String, String>>> = listOf(
        Triple(listOf("البخاري", "صحيح البخاري", "bukhari"), "ara-bukhari", "صحيح البخاري" to "Sahih al-Bukhari"),
        Triple(listOf("صحيح مسلم", "الإمام مسلم", "الامام مسلم", "sahih muslim"), "ara-muslim", "صحيح مسلم" to "Sahih Muslim"),
        Triple(listOf("النووية", "النوويه", "الأربعون النووية", "الاربعون النوويه", "nawawi"), "ara-nawawi", "الأربعون النووية" to "40 Hadith Nawawi"),
        Triple(listOf("الترمذي", "tirmidhi"), "ara-tirmidhi", "جامع الترمذي" to "Jami at-Tirmidhi"),
        Triple(listOf("أبو داود", "ابو داود", "abi dawud", "abu dawud"), "ara-abudawud", "سنن أبي داود" to "Sunan Abi Dawud"),
        Triple(listOf("رياض الصالحين", "riyad", "salihin"), "ara-bukhari", "رياض الصالحين" to "Riyad as-Salihin"),
    )

    private val hadithKeywords = listOf(
        "حديث", "أحاديث", "احاديث", "الحديث", "السنة النبوية", "السنه النبويه",
        "الأذكار", "الاذكار", "أذكار", "اذكار", "السيرة النبوية", "السيره النبويه",
        "خطبة", "خطبه", "موعظة", "درس ديني", "hadith", "hadeeth", "sunnah", "adhkar", "seerah"
    )

    fun detectQuran(title: String, artist: String = "", album: String = ""): QuranDetectionResult {
        val combinedRaw = "$title $artist $album"
        val normalized = normalizeArabic(combinedRaw)

        val matchedReciter = knownReciters.firstOrNull { (tokens, _) ->
            tokens.any { token -> normalized.contains(normalizeArabic(token)) }
        }?.second

        val hasExplicitQuranCue = quranKeywords.any { normalized.contains(normalizeArabic(it)) } || matchedReciter != null

        // Check numeric pattern like "Surah 18" or "سورة 18" or "018 - Al-Kahf"
        val surahNumMatch = Regex("""(?:surah|sura|سورة|سوره)\s*[-:#]?\s*(\d{1,3})""", RegexOption.IGNORE_CASE)
            .find(normalized)
        val byNumber = surahNumMatch?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { num ->
            surahs.firstOrNull { it.number == num }
        }
        if (byNumber != null) {
            return QuranDetectionResult(surah = byNumber, reciterDisplayName = matchedReciter, isQuranic = true)
        }

        // Match Surah by Arabic or English name/alias
        val matchedSurah = surahs.firstOrNull { entry ->
            val arNorm = normalizeArabic(entry.arabicName)
            val enNorm = entry.englishName.lowercase()
            val strippedAr = arNorm.removePrefix("ال")
            val matchFullAr = arNorm.length >= 3 && normalized.contains(arNorm)
            val matchStrippedAr = hasExplicitQuranCue && strippedAr.length >= 3 && normalized.contains(strippedAr)
            val matchEn = enNorm.length >= 4 && normalized.contains(enNorm)
            val matchAlias = entry.aliases.any { alias ->
                val a = normalizeArabic(alias)
                if (a.length <= 4 && !hasExplicitQuranCue) {
                    Regex("""\b${Regex.escape(a)}\b""").containsMatchIn(normalized)
                } else {
                    normalized.contains(a)
                }
            }
            matchFullAr || matchStrippedAr || matchEn || matchAlias
        }

        val isQuran = matchedSurah != null || hasExplicitQuranCue
        return QuranDetectionResult(
            surah = matchedSurah,
            reciterDisplayName = matchedReciter,
            isQuranic = isQuran
        )
    }

    fun detectHadith(title: String, artist: String = "", album: String = ""): HadithDetectionResult {
        val combined = normalizeArabic("$title $artist $album")
        val matchedCollection = hadithCollections.firstOrNull { (tokens, _, _) ->
            tokens.any { combined.contains(normalizeArabic(it)) }
        }
        val hasHadithCue = matchedCollection != null || hadithKeywords.any { combined.contains(normalizeArabic(it)) }
        return HadithDetectionResult(
            isHadithOrIslamicLecture = hasHadithCue,
            collectionKey = matchedCollection?.second,
            collectionArabicName = matchedCollection?.third?.first,
            collectionEnglishName = matchedCollection?.third?.second,
            searchTopic = title.trim().ifBlank { artist.trim() }
        )
    }

    fun classifyCategory(
        title: String,
        artist: String = "",
        album: String = "",
        defaultCategory: MediaKnowledgeCategory = MediaKnowledgeCategory.MUSIC,
    ): MediaKnowledgeCategory {
        val quran = detectQuran(title, artist, album)
        if (quran.isQuranic) return MediaKnowledgeCategory.QURAN
        val hadith = detectHadith(title, artist, album)
        if (hadith.isHadithOrIslamicLecture) return MediaKnowledgeCategory.HADITH
        return defaultCategory
    }
}
