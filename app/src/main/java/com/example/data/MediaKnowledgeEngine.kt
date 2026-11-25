package com.example.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.model.AppLanguage
import com.example.model.AudioTrack
import com.example.model.MediaDetailsInfo
import com.example.model.MediaKnowledgeCategory
import com.example.model.MediaKnowledgeReport
import com.example.model.RenameMediaType
import com.example.security.ContentPurityGuard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import kotlin.math.abs

/**
 * Real HTTPS Open-API Media Knowledge & Encyclopedia Enrichment Engine.
 * Zero API keys required — uses verified open public APIs:
 * - Deezer Public API (real Artist Portrait, Album Art, BPM, Release Metadata)
 * - LRCLIB Open API (real Timestamped Synced .lrc & Plain Lyrics)
 * - Wikipedia REST v1 / OpenSearch API (real Artist Biographies & Encyclopedia Summaries in EN/AR/FR/ES)
 * - AlQuran Cloud API (verified Uthmani Arabic Verses, Surah Metadata & Translations)
 * - Open Hadith CDN API (authentic Hadith text & collection metadata)
 * - OpenStreetMap Nominatim (reverse geocoding for Photo EXIF GPS coordinates)
 * Includes local disk JSON caching for instant offline repeat viewing.
 */
object MediaKnowledgeEngine {

    private const val CONNECT_TIMEOUT_MS = 5500
    private const val READ_TIMEOUT_MS = 7500
    private const val USER_AGENT = "PrismMediaStudio/2.0 (Android; Media Knowledge Engine)"

    internal data class WikiResult(
        val title: String,
        val description: String,
        val extract: String,
        val imageUrl: String?,
        val pageUrl: String?,
    )

    private data class DeezerResult(
        val artistName: String?,
        val artistPhotoUrl: String?,
        val albumCoverUrl: String?,
        val albumTitle: String?,
        val bpm: Int,
        val releaseYear: String?,
        val genre: String?,
    )

    private data class ItunesResult(
        val artistName: String?,
        val trackTitle: String?,
        val albumTitle: String?,
        val albumCoverUrl: String?,
        val releaseYear: String?,
        val genre: String?,
    )

    private data class QuranApiResult(
        val arabicSample: String,
        val translationSample: String,
    )

    suspend fun enrichAudioTrack(
        context: Context,
        track: AudioTrack,
        language: AppLanguage,
        fetchLyrics: Boolean = true,
        fetchArtistPhoto: Boolean = true,
        forceRefresh: Boolean = false,
    ): MediaKnowledgeReport = withContext(Dispatchers.IO) {
        val cacheKey = buildCacheKey("audio_${track.id}_${track.title}_${track.artist}_${language.name}")
        if (!forceRefresh) {
            readCachedReport(context, cacheKey)?.let { return@withContext it }
        }

        val (parsedArtist, parsedTitle) = parseArtistAndTitleFromFilename(track.title, track.artist)
        val cleanTitle = cleanMediaQuery(parsedTitle)
        val cleanArtist = cleanArtistName(parsedArtist)

        // Built-in Ethics & Purity Shield: strictly refuse adult/explicit lookups
        if (ContentPurityGuard.isExplicitOrAdult(track.title, track.artist, track.album, track.uriString)) {
            return@withContext MediaKnowledgeReport(
                mediaId = track.id,
                category = MediaKnowledgeCategory.MUSIC,
                artistName = "",
                energyPercent = 50,
                bpm = 0,
                sourceAttribution = "Prism Purity Shield",
                fetchedAtMs = System.currentTimeMillis()
            )
        }

        val quranDetect = QuranHadithCatalog.detectQuran(cleanTitle, cleanArtist, track.album)
        val hadithDetect = QuranHadithCatalog.detectHadith(cleanTitle, cleanArtist, track.album)
        val langCode = language.wikiCode()

        // Extract local bitrate/quality hint safely
        val audioQualityBadge = computeAudioQualityBadge(track.extension, track.sizeBytes, track.durationMs)

        val report = when {
            quranDetect.isQuranic -> {
                coroutineScope {
                    val surah = quranDetect.surah
                    val reciterQuery = quranDetect.reciterDisplayName ?: cleanArtist.takeIf { it.isNotBlank() }
                    val quranDeferred = async {
                        surah?.let { fetchAlQuranSurah(it.number, langCode) }
                    }
                    val reciterWikiDeferred = async {
                        if (fetchArtistPhoto && !reciterQuery.isNullOrBlank()) {
                            fetchWikipediaWithFallback(reciterQuery, langCode)
                        } else null
                    }
                    val surahWikiDeferred = async {
                        val query = surah?.let {
                            if (langCode == "ar") "سورة ${it.arabicName}" else "Surah ${it.englishName}"
                        } ?: cleanTitle
                        fetchWikipediaWithFallback(query, langCode)
                    }

                    val quranData = quranDeferred.await()
                    val reciterWiki = reciterWikiDeferred.await()
                    val surahWiki = surahWikiDeferred.await()

                    val (energy, bpm, mood) = computeTrackEnergyAndMood(
                        title = cleanTitle,
                        artist = reciterQuery ?: cleanArtist,
                        extension = track.extension,
                        durationMs = track.durationMs,
                        apiBpm = 0,
                        category = MediaKnowledgeCategory.QURAN,
                        language = language
                    )

                    MediaKnowledgeReport(
                        mediaId = track.id,
                        category = MediaKnowledgeCategory.QURAN,
                        artistName = reciterQuery ?: track.artist,
                        artistPhotoUrl = reciterWiki?.imageUrl,
                        artistBio = reciterWiki?.extract ?: surahWiki?.extract.orEmpty(),
                        artistRole = reciterWiki?.description.orEmpty(),
                        albumTitle = track.album,
                        genre = if (language == AppLanguage.AR) "القرآن الكريم" else "Holy Quran",
                        energyPercent = energy,
                        bpm = bpm,
                        moodLabel = mood,
                        audioQualityBadge = audioQualityBadge,
                        quranSurahNumber = surah?.number,
                        quranSurahNameAr = surah?.arabicName,
                        quranSurahNameEn = surah?.englishName,
                        quranRevelationType = surah?.let {
                            if (it.isMeccan) {
                                if (language == AppLanguage.AR) "مكية" else "Meccan"
                            } else {
                                if (language == AppLanguage.AR) "مدنية" else "Medinan"
                            }
                        },
                        quranAyahCount = surah?.ayahCount,
                        quranSampleAyahs = quranData?.arabicSample,
                        quranTranslation = quranData?.translationSample,
                        reciterName = reciterQuery,
                        encyclopediaTitle = surahWiki?.title ?: surah?.arabicName,
                        encyclopediaSummary = surahWiki?.extract ?: reciterWiki?.extract,
                        encyclopediaImageUrl = reciterWiki?.imageUrl ?: surahWiki?.imageUrl,
                        encyclopediaUrl = surahWiki?.pageUrl ?: reciterWiki?.pageUrl,
                        sourceAttribution = "AlQuran Cloud • Wikipedia",
                        fetchedAtMs = System.currentTimeMillis()
                    )
                }
            }

            hadithDetect.isHadithOrIslamicLecture -> {
                coroutineScope {
                    val wikiTopic = hadithDetect.collectionArabicName?.takeIf { language == AppLanguage.AR }
                        ?: hadithDetect.collectionEnglishName
                        ?: cleanTitle
                    val wikiDeferred = async { fetchWikipediaWithFallback(wikiTopic, langCode) }
                    val scholarWikiDeferred = async {
                        if (cleanArtist.isNotBlank()) fetchWikipediaWithFallback(cleanArtist, langCode) else null
                    }
                    val hadithDeferred = async {
                        hadithDetect.collectionKey?.let { fetchSampleHadith(it) }
                    }

                    val wiki = wikiDeferred.await()
                    val scholarWiki = scholarWikiDeferred.await()
                    val hadithText = hadithDeferred.await()

                    val (energy, bpm, mood) = computeTrackEnergyAndMood(
                        title = cleanTitle,
                        artist = cleanArtist,
                        extension = track.extension,
                        durationMs = track.durationMs,
                        apiBpm = 0,
                        category = MediaKnowledgeCategory.HADITH,
                        language = language
                    )

                    MediaKnowledgeReport(
                        mediaId = track.id,
                        category = MediaKnowledgeCategory.HADITH,
                        artistName = cleanArtist.ifBlank { track.artist },
                        artistPhotoUrl = scholarWiki?.imageUrl ?: wiki?.imageUrl,
                        artistBio = scholarWiki?.extract ?: wiki?.extract.orEmpty(),
                        artistRole = scholarWiki?.description ?: wiki?.description.orEmpty(),
                        albumTitle = track.album,
                        genre = if (language == AppLanguage.AR) "الحديث والسيرة" else "Hadith & Islamic Studies",
                        energyPercent = energy,
                        bpm = bpm,
                        moodLabel = mood,
                        audioQualityBadge = audioQualityBadge,
                        hadithBook = if (language == AppLanguage.AR) {
                            hadithDetect.collectionArabicName ?: "الموسوعة الحديثية"
                        } else {
                            hadithDetect.collectionEnglishName ?: "Hadith Reference"
                        },
                        hadithText = hadithText,
                        encyclopediaTitle = wiki?.title ?: hadithDetect.searchTopic,
                        encyclopediaSummary = wiki?.extract,
                        encyclopediaImageUrl = wiki?.imageUrl,
                        encyclopediaUrl = wiki?.pageUrl ?: scholarWiki?.pageUrl,
                        sourceAttribution = "Hadith Open API • Wikipedia",
                        fetchedAtMs = System.currentTimeMillis()
                    )
                }
            }

            else -> {
                // Standard Music Track: Deezer + iTunes Fallback + Offline ID3/LRCLIB Multi-Pass Lyrics + Wikipedia in parallel!
                coroutineScope {
                    val deezerDeferred = async {
                        if (!track.isDemo && (cleanTitle.isNotBlank() || cleanArtist.isNotBlank())) {
                            fetchDeezerTrackAndArtist(cleanTitle, cleanArtist)
                        } else null
                    }
                    val itunesDeferred = async {
                        if (!track.isDemo && (cleanTitle.isNotBlank() || cleanArtist.isNotBlank())) {
                            fetchItunesTrackMetadata(cleanTitle, cleanArtist)
                        } else null
                    }
                    val lyricsDeferred = async {
                        if (fetchLyrics && !track.isDemo) {
                            extractEmbeddedId3Lyrics(context, track.uriString)
                                ?: if (cleanTitle.isNotBlank()) {
                                    fetchLrclibLyrics(cleanTitle, cleanArtist, track.album, (track.durationMs / 1000L).toInt())
                                } else null
                        } else null
                    }
                    val artistWikiDeferred = async {
                        val artistQuery = cleanArtist.ifBlank { cleanTitle }
                        if (!track.isDemo && artistQuery.isNotBlank()) {
                            fetchWikipediaWithFallback(artistQuery, langCode)
                        } else null
                    }

                    val rawDeezer = deezerDeferred.await()
                    val rawItunes = itunesDeferred.await()
                    val rawLyrics = lyricsDeferred.await()
                    val rawWiki = artistWikiDeferred.await()

                    val deezer = rawDeezer?.takeUnless {
                        ContentPurityGuard.isExplicitOrAdult(it.artistName, it.albumTitle, it.genre)
                    }
                    val itunes = rawItunes?.takeUnless {
                        ContentPurityGuard.isExplicitOrAdult(it.artistName, it.albumTitle, it.genre)
                    }
                    val lyrics = ContentPurityGuard.filterLyricsStrictly(rawLyrics)
                    val wiki = rawWiki?.takeUnless {
                        ContentPurityGuard.isExplicitOrAdult(it.title, it.description, it.extract)
                    }

                    val bestArtist = deezer?.artistName
                        ?: itunes?.artistName
                        ?: cleanArtist.takeIf { it.isNotBlank() }
                        ?: track.artist

                    // MusicBrainz + Cover Art Archive fallback only if neither Deezer nor iTunes returned HD cover art
                    var resolvedAlbumCover = if (fetchArtistPhoto) {
                        deezer?.albumCoverUrl ?: itunes?.albumCoverUrl
                    } else null
                    if (fetchArtistPhoto && resolvedAlbumCover == null && !track.isDemo && cleanTitle.isNotBlank()) {
                        resolvedAlbumCover = fetchMusicBrainzCoverArt(cleanTitle, bestArtist)
                    }

                    val resolvedArtistPhoto = if (fetchArtistPhoto) {
                        deezer?.artistPhotoUrl ?: wiki?.imageUrl ?: resolvedAlbumCover
                    } else null

                    val (energy, bpm, mood) = computeTrackEnergyAndMood(
                        title = cleanTitle,
                        artist = bestArtist,
                        extension = track.extension,
                        durationMs = track.durationMs,
                        apiBpm = deezer?.bpm ?: 0,
                        category = MediaKnowledgeCategory.MUSIC,
                        language = language
                    )

                    val sources = buildList {
                        if (deezer != null) add("Deezer")
                        if (itunes != null) add("Catalog HD")
                        if (!lyrics.isNullOrBlank()) add("Lyrics")
                        if (wiki != null) add("Wikipedia")
                        if (isEmpty()) add("Prism Local Audio Analyzer")
                    }.joinToString(" • ")

                    MediaKnowledgeReport(
                        mediaId = track.id,
                        category = MediaKnowledgeCategory.MUSIC,
                        artistName = bestArtist,
                        artistPhotoUrl = resolvedArtistPhoto,
                        albumCoverUrl = resolvedAlbumCover,
                        artistBio = ContentPurityGuard.sanitizeEncyclopediaText(wiki?.extract).orEmpty(),
                        artistRole = ContentPurityGuard.sanitizeEncyclopediaText(wiki?.description).orEmpty(),
                        albumTitle = deezer?.albumTitle ?: itunes?.albumTitle ?: track.album,
                        releaseYear = deezer?.releaseYear ?: itunes?.releaseYear,
                        genre = deezer?.genre ?: itunes?.genre,
                        energyPercent = energy,
                        bpm = bpm,
                        moodLabel = mood,
                        audioQualityBadge = audioQualityBadge,
                        fetchedLyrics = lyrics,
                        encyclopediaTitle = wiki?.title,
                        encyclopediaSummary = ContentPurityGuard.sanitizeEncyclopediaText(wiki?.extract),
                        encyclopediaImageUrl = wiki?.imageUrl ?: resolvedAlbumCover,
                        encyclopediaUrl = wiki?.pageUrl,
                        sourceAttribution = sources,
                        fetchedAtMs = System.currentTimeMillis()
                    )
                }
            }
        }

        saveCachedReport(context, cacheKey, report)
        report
    }

    /**
     * Universal Deep Knowledge Lookup for ANY file (Audio, Video, or Photo) from MediaDetailsDialog.
     */
    suspend fun enrichMediaDetails(
        context: Context,
        details: MediaDetailsInfo,
        language: AppLanguage,
        forceRefresh: Boolean = false,
    ): MediaKnowledgeReport = withContext(Dispatchers.IO) {
        val idKey = details.mediaId.ifBlank { details.uriString }
        val cacheKey = buildCacheKey("details_${idKey}_${details.title}_${language.name}")
        if (!forceRefresh) {
            readCachedReport(context, cacheKey)?.let { return@withContext it }
        }

        val cleanTitle = cleanMediaQuery(details.title)
        val cleanArtist = cleanArtistName(details.artist)
        if (ContentPurityGuard.isExplicitOrAdult(details.title, details.artist, details.uriString)) {
            return@withContext MediaKnowledgeReport(
                mediaId = idKey,
                category = MediaKnowledgeCategory.ENCYCLOPEDIA,
                sourceAttribution = "Prism Purity Shield",
                fetchedAtMs = System.currentTimeMillis()
            )
        }
        val langCode = language.wikiCode()

        // Check if this video/audio is Quran or Hadith first!
        val quranDetect = QuranHadithCatalog.detectQuran(cleanTitle, cleanArtist)
        if (quranDetect.isQuranic) {
            val syntheticTrack = AudioTrack(
                id = idKey,
                title = details.title,
                artist = details.artist,
                album = "",
                uriString = details.uriString,
                extension = details.extension,
                mimeType = details.mimeType,
                durationMs = 0L,
                sizeBytes = details.sizeBytes,
            )
            return@withContext enrichAudioTrack(context, syntheticTrack, language, fetchLyrics = false, forceRefresh = forceRefresh)
        }

        val hadithDetect = QuranHadithCatalog.detectHadith(cleanTitle, cleanArtist)
        if (hadithDetect.isHadithOrIslamicLecture) {
            val syntheticTrack = AudioTrack(
                id = idKey,
                title = details.title,
                artist = details.artist,
                album = "",
                uriString = details.uriString,
                extension = details.extension,
                mimeType = details.mimeType,
                durationMs = 0L,
                sizeBytes = details.sizeBytes,
            )
            return@withContext enrichAudioTrack(context, syntheticTrack, language, fetchLyrics = false, forceRefresh = forceRefresh)
        }

        // For photos: try reverse geocoding GPS coordinates if present in EXIF
        var photoPlace: String? = null
        if (details.mediaType == RenameMediaType.PHOTO && details.uriString.startsWith("content://")) {
            photoPlace = extractAndReverseGeocodePhotoLocation(context, Uri.parse(details.uriString), langCode)
        }

        // Query Wikipedia Encyclopedia using place name (if photo GPS found) or cleaned title
        val searchQuery = photoPlace?.substringBefore(",")?.trim()?.takeIf { it.length >= 3 }
            ?: cleanTitle.ifBlank { cleanArtist }
        val wiki = if (searchQuery.isNotBlank()) {
            fetchWikipediaWithFallback(searchQuery, langCode)
        } else null

        val category = when (details.mediaType) {
            RenameMediaType.AUDIO -> MediaKnowledgeCategory.MUSIC
            RenameMediaType.VIDEO -> MediaKnowledgeCategory.VIDEO
            RenameMediaType.PHOTO -> MediaKnowledgeCategory.PHOTO
        }

        val (energy, bpm, mood) = computeTrackEnergyAndMood(
            title = cleanTitle,
            artist = cleanArtist,
            extension = details.extension,
            durationMs = 0L,
            apiBpm = 0,
            category = category,
            language = language
        )

        val sources = buildList {
            if (wiki != null) add("Wikipedia")
            if (!photoPlace.isNullOrBlank()) add("OpenStreetMap")
            if (isEmpty()) add("Prism Media Inspector")
        }.joinToString(" • ")

        val report = MediaKnowledgeReport(
            mediaId = idKey,
            category = category,
            artistName = cleanArtist.ifBlank { details.title },
            artistPhotoUrl = wiki?.imageUrl,
            artistBio = wiki?.extract.orEmpty(),
            artistRole = wiki?.description.orEmpty(),
            energyPercent = energy,
            bpm = bpm,
            moodLabel = mood,
            audioQualityBadge = computeAudioQualityBadge(details.extension, details.sizeBytes, 0L),
            encyclopediaTitle = wiki?.title ?: details.title,
            encyclopediaSummary = wiki?.extract,
            encyclopediaImageUrl = wiki?.imageUrl,
            encyclopediaUrl = wiki?.pageUrl,
            photoLocationLabel = photoPlace,
            sourceAttribution = sources,
            fetchedAtMs = System.currentTimeMillis()
        )
        saveCachedReport(context, cacheKey, report)
        report
    }

    fun clearCache(context: Context): Int {
        val dir = File(context.cacheDir, "prism_knowledge_cache")
        if (!dir.exists()) return 0
        var deleted = 0
        dir.listFiles()?.forEach { if (it.delete()) deleted++ }
        return deleted
    }

    /**
     * Smart Filename Parser: when an audio file has an empty or placeholder ID3 artist tag
     * (very common on phone downloads like "Saad Lamjarred - Lm3allem.mp3" or "عمرو دياب - تملي معاك"),
     * automatically splits "Artist - Title" into clean (artist, title) search tokens.
     */
    fun parseArtistAndTitleFromFilename(rawTitle: String, rawArtist: String): Pair<String, String> {
        val cleanedArtist = cleanArtistName(rawArtist)
        val baseTitle = cleanMediaQuery(rawTitle)
        if (cleanedArtist.isNotBlank()) {
            // Strip redundant "Artist - " prefix from title if both exist
            val prefixMatch = Regex(
                """^${Regex.escape(cleanedArtist)}\s*[-–—|:]\s*(.+)$""",
                RegexOption.IGNORE_CASE
            ).find(baseTitle)
            val stripped = prefixMatch?.groupValues?.getOrNull(1)?.trim().orEmpty()
            return cleanedArtist to (stripped.ifBlank { baseTitle })
        }
        val separators = listOf(" - ", " – ", " — ", " | ")
        for (sep in separators) {
            val idx = baseTitle.indexOf(sep)
            if (idx in 2 until (baseTitle.length - sep.length - 1)) {
                val left = baseTitle.substring(0, idx).trim()
                val right = baseTitle.substring(idx + sep.length).trim()
                if (left.length in 2..60 && right.length >= 2 && !left.all(Char::isDigit)) {
                    return left to right
                }
            }
        }
        return "" to baseTitle
    }

    /**
     * Downloads a fetched HD album cover / artist image once into persistent internal app storage
     * so Full Player, Mini Player, Music List, Lock Screen & Notification render it instantly OFFLINE!
     */
    suspend fun downloadAndCacheArtwork(
        context: Context,
        imageUrl: String?,
        trackId: String,
    ): String? = withContext(Dispatchers.IO) {
        val urlStr = imageUrl?.trim()?.takeIf { it.startsWith("https://") } ?: return@withContext null
        val dir = File(context.filesDir, "prism_artwork_cache").apply { mkdirs() }
        val fileName = "${buildCacheKey(trackId)}.jpg"
        val outFile = File(dir, fileName)
        if (outFile.exists() && outFile.length() > 1024L) {
            return@withContext Uri.fromFile(outFile).toString()
        }
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", USER_AGENT)
            }
            if (conn.responseCode in 200..299) {
                val bytes = conn.inputStream.use { input ->
                    val maxBytes = 2 * 1024 * 1024 // 2 MB cap
                    val buf = java.io.ByteArrayOutputStream()
                    val chunk = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val read = input.read(chunk)
                        if (read <= 0) break
                        total += read
                        if (total > maxBytes) return@withContext urlStr
                        buf.write(chunk, 0, read)
                    }
                    buf.toByteArray()
                }
                if (bytes.size > 512) {
                    outFile.writeBytes(bytes)
                    return@withContext Uri.fromFile(outFile).toString()
                }
            }
            null
        } catch (_: Throwable) {
            urlStr // Fallback to direct HTTPS URL via Coil cache
        } finally {
            conn?.disconnect()
        }
    }

    /**
     * Reads embedded ID3v2 USLT (Unsynchronized Lyrics) frames directly from local MP3 files
     * so embedded offline lyrics display even without an internet connection.
     */
    fun extractEmbeddedId3Lyrics(context: Context, uriString: String): String? {
        if (!uriString.startsWith("content://") && !uriString.startsWith("file://")) return null
        return try {
            val headerBytes = context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                val maxRead = 196 * 1024 // ID3v2 header + tags at start of file
                val buf = ByteArray(maxRead)
                var offset = 0
                while (offset < maxRead) {
                    val r = stream.read(buf, offset, maxRead - offset)
                    if (r <= 0) break
                    offset += r
                }
                buf.copyOf(offset)
            } ?: return null
            parseId3v2UsltFrame(headerBytes)
        } catch (_: Throwable) {
            null
        }
    }

    internal fun parseId3v2UsltFrame(bytes: ByteArray): String? {
        if (bytes.size < 20) return null
        if (bytes[0] != 'I'.code.toByte() || bytes[1] != 'D'.code.toByte() || bytes[2] != '3'.code.toByte()) {
            return null
        }
        val majorVersion = bytes[3].toInt() and 0xFF
        if (majorVersion !in 3..4) return null
        val tagSize = ((bytes[6].toInt() and 0x7F) shl 21) or
            ((bytes[7].toInt() and 0x7F) shl 14) or
            ((bytes[8].toInt() and 0x7F) shl 7) or
            (bytes[9].toInt() and 0x7F)
        val end = minOf(bytes.size, 10 + tagSize)
        var pos = 10
        while (pos + 10 < end) {
            val frameId = String(bytes, pos, 4, Charsets.ISO_8859_1)
            if (frameId[0] == '\u0000') break
            val frameSize = if (majorVersion == 4) {
                ((bytes[pos + 4].toInt() and 0x7F) shl 21) or
                    ((bytes[pos + 5].toInt() and 0x7F) shl 14) or
                    ((bytes[pos + 6].toInt() and 0x7F) shl 7) or
                    (bytes[pos + 7].toInt() and 0x7F)
            } else {
                ((bytes[pos + 4].toInt() and 0xFF) shl 24) or
                    ((bytes[pos + 5].toInt() and 0xFF) shl 16) or
                    ((bytes[pos + 6].toInt() and 0xFF) shl 8) or
                    (bytes[pos + 7].toInt() and 0xFF)
            }
            if (frameSize <= 0 || pos + 10 + frameSize > end) break
            if (frameId == "USLT" && frameSize > 5) {
                val payloadStart = pos + 10
                val encoding = bytes[payloadStart].toInt() and 0xFF
                // Skip encoding (1 byte) + language (3 bytes) + null-terminated content descriptor
                var textStart = payloadStart + 4
                val payloadEnd = payloadStart + frameSize
                if (encoding == 1 || encoding == 2) {
                    while (textStart + 1 < payloadEnd) {
                        if (bytes[textStart] == 0.toByte() && bytes[textStart + 1] == 0.toByte()) {
                            textStart += 2
                            break
                        }
                        textStart += 2
                    }
                } else {
                    while (textStart < payloadEnd) {
                        if (bytes[textStart] == 0.toByte()) {
                            textStart += 1
                            break
                        }
                        textStart += 1
                    }
                }
                if (textStart < payloadEnd) {
                    val charset = when (encoding) {
                        1 -> Charsets.UTF_16
                        2 -> Charsets.UTF_16BE
                        3 -> Charsets.UTF_8
                        else -> Charsets.ISO_8859_1
                    }
                    val text = String(bytes, textStart, payloadEnd - textStart, charset)
                        .replace("\u0000", "")
                        .trim()
                    if (text.length >= 16) return text
                }
            }
            pos += 10 + frameSize
        }
        return null
    }

    /**
     * Cleans noisy filenames like "01 - Artist - Song (Official Video) [320kbps].mp3"
     * into a crisp search query.
     */
    fun cleanMediaQuery(raw: String): String {
        return raw
            .replace(Regex("""\.[a-zA-Z0-9]{2,5}$"""), "")
            .replace(Regex("""^\d{1,3}\s*[-._)]\s*"""), "")
            .replace(Regex("""[\[(].*?(official|hd|4k|1080p|720p|320kbps|mp3|flac|lyrics|video|audio|clip|remaster|حصرية|فيديو كليب).*?[\])]""", RegexOption.IGNORE_CASE), " ")
            .replace('_', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    fun cleanArtistName(raw: String): String {
        val trimmed = raw.trim()
        val lower = trimmed.lowercase()
        if (lower.isBlank() || lower == "unknown" || lower == "unknown artist" || lower == "<unknown>" ||
            lower == "local artist" || lower == "فنان غير معروف" || lower == "prism studio"
        ) {
            return ""
        }
        return trimmed
            .replace(Regex("""\s*(feat\.?|ft\.?|x|&|,)\s+.*$""", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    fun computeAudioQualityBadge(extension: String, sizeBytes: Long, durationMs: Long): String {
        val ext = extension.uppercase()
        val kbps = if (durationMs > 1000L && sizeBytes > 0L) {
            ((sizeBytes * 8L) / durationMs).toInt()
        } else 0
        return when {
            ext in setOf("FLAC", "WAV", "ALAC", "DSD", "AIFF") ->
                if (kbps > 0) "Hi-Res Lossless • $ext (${kbps} kbps)" else "Hi-Res Lossless • $ext"
            kbps >= 256 -> "Studio HD • $ext (${kbps} kbps)"
            kbps >= 128 -> "Balanced HQ • $ext (${kbps} kbps)"
            ext.isNotBlank() -> "$ext Audio"
            else -> "Standard Media"
        }
    }

    fun computeTrackEnergyAndMood(
        title: String,
        artist: String,
        extension: String,
        durationMs: Long,
        apiBpm: Int,
        category: MediaKnowledgeCategory,
        language: AppLanguage,
    ): Triple<Int, Int, String> {
        if (category == MediaKnowledgeCategory.QURAN) {
            val mood = when (language) {
                AppLanguage.AR -> "روحاني وهادئ"
                AppLanguage.FR -> "Spirituel & Serein"
                AppLanguage.ES -> "Espiritual y Sereno"
                AppLanguage.EN -> "Spiritual & Serene"
            }
            return Triple(28, 62, mood)
        }
        if (category == MediaKnowledgeCategory.HADITH) {
            val mood = when (language) {
                AppLanguage.AR -> "تأملي ومعرفي"
                AppLanguage.FR -> "Réflexif & Éducatif"
                AppLanguage.ES -> "Reflexivo y Educativo"
                AppLanguage.EN -> "Reflective & Knowledge"
            }
            return Triple(34, 68, mood)
        }

        val lower = "$title $artist".lowercase()
        val seed = abs("$title|$artist|$extension".hashCode())
        val resolvedBpm = when {
            apiBpm in 50..210 -> apiBpm
            lower.contains("club") || lower.contains("remix") || lower.contains("dance") || lower.contains("trap") || lower.contains("workout") -> 124 + (seed % 18)
            lower.contains("rock") || lower.contains("rap") || lower.contains("hip") || lower.contains("energetic") -> 108 + (seed % 20)
            lower.contains("chill") || lower.contains("lofi") || lower.contains("acoustic") || lower.contains("piano") || lower.contains("ambient") || lower.contains("هادئ") -> 72 + (seed % 18)
            else -> 88 + (seed % 34)
        }
        val energy = when {
            resolvedBpm >= 126 -> (78 + (seed % 19)).coerceAtMost(98)
            resolvedBpm >= 106 -> (62 + (seed % 20)).coerceAtMost(88)
            resolvedBpm >= 88 -> (48 + (seed % 20)).coerceAtMost(74)
            else -> (28 + (seed % 22)).coerceAtMost(56)
        }
        val mood = when {
            energy >= 78 -> when (language) {
                AppLanguage.AR -> "طاقة عالية وإيقاع حماسي"
                AppLanguage.FR -> "Haute Énergie & Dynamique"
                AppLanguage.ES -> "Alta Energía y Dinámico"
                AppLanguage.EN -> "High Energy & Upbeat"
            }
            energy >= 58 -> when (language) {
                AppLanguage.AR -> "متوازن وإيقاعي"
                AppLanguage.FR -> "Équilibré & Rythmé"
                AppLanguage.ES -> "Equilibrado y Rítmico"
                AppLanguage.EN -> "Groovy & Balanced"
            }
            else -> when (language) {
                AppLanguage.AR -> "هادئ وعميق"
                AppLanguage.FR -> "Calme & Atmosphérique"
                AppLanguage.ES -> "Tranquilo y Atmosférico"
                AppLanguage.EN -> "Chill & Atmospheric"
            }
        }
        return Triple(energy, resolvedBpm, mood)
    }

    private fun fetchDeezerTrackAndArtist(title: String, artist: String): DeezerResult? {
        val query = listOf(artist, title).filter { it.isNotBlank() }.joinToString(" ").trim()
        if (query.isBlank()) return null
        val encoded = URLEncoder.encode(query, "UTF-8")
        val trackJson = httpGetJson("https://api.deezer.com/search?limit=1&q=$encoded")
        val firstTrack = trackJson?.optJSONArray("data")?.optJSONObject(0)
        if (firstTrack != null) {
            val trackId = firstTrack.optLong("id", 0L)
            val artistObj = firstTrack.optJSONObject("artist")
            val albumObj = firstTrack.optJSONObject("album")
            var bpm = 0
            var releaseYear: String? = null
            if (trackId > 0L) {
                val detail = httpGetJson("https://api.deezer.com/track/$trackId")
                if (detail != null) {
                    bpm = detail.optDouble("bpm", 0.0).toInt()
                    releaseYear = detail.optString("release_date", "").take(4).takeIf { it.length == 4 }
                }
            }
            val photoUrl = artistObj?.optString("picture_xl")?.takeIf { it.startsWith("https://") }
                ?: artistObj?.optString("picture_big")?.takeIf { it.startsWith("https://") }
            val coverUrl = albumObj?.optString("cover_xl")?.takeIf { it.startsWith("https://") }
                ?: albumObj?.optString("cover_big")?.takeIf { it.startsWith("https://") }
                ?: albumObj?.optString("cover_medium")?.takeIf { it.startsWith("https://") }
            return DeezerResult(
                artistName = artistObj?.optString("name")?.takeIf { it.isNotBlank() },
                artistPhotoUrl = photoUrl,
                albumCoverUrl = coverUrl,
                albumTitle = albumObj?.optString("title")?.takeIf { it.isNotBlank() },
                bpm = bpm,
                releaseYear = releaseYear,
                genre = null
            )
        }
        if (artist.isNotBlank()) {
            val artistEnc = URLEncoder.encode(artist, "UTF-8")
            val artistRes = httpGetJson("https://api.deezer.com/search/artist?limit=1&q=$artistEnc")
            val firstArtist = artistRes?.optJSONArray("data")?.optJSONObject(0)
            if (firstArtist != null) {
                val photoUrl = firstArtist.optString("picture_xl").takeIf { it.startsWith("https://") }
                    ?: firstArtist.optString("picture_big").takeIf { it.startsWith("https://") }
                return DeezerResult(
                    artistName = firstArtist.optString("name").takeIf { it.isNotBlank() },
                    artistPhotoUrl = photoUrl,
                    albumCoverUrl = null,
                    albumTitle = null,
                    bpm = 0,
                    releaseYear = null,
                    genre = null
                )
            }
        }
        return null
    }

    /**
     * Free public HTTPS catalog fallback (massive Arabic, Moroccan, Rai, Chaabi & global coverage).
     * Upgrades 100x100 artwork to crisp 600x600 HD cover art automatically.
     */
    private fun fetchItunesTrackMetadata(title: String, artist: String): ItunesResult? {
        val term = listOf(artist, title).filter { it.isNotBlank() }.joinToString(" ").trim()
        if (term.isBlank()) return null
        val encoded = URLEncoder.encode(term, "UTF-8")
        val json = httpGetJson("https://itunes.apple.com/search?media=music&entity=song&limit=3&term=$encoded")
        val results = json?.optJSONArray("results") ?: return null
        if (results.length() == 0) return null
        val first = results.optJSONObject(0) ?: return null
        val art100 = first.optString("artworkUrl100", "").takeIf { it.startsWith("https://") }
        val artHd = art100?.replace("100x100bb", "600x600bb")
        val year = first.optString("releaseDate", "").take(4).takeIf { it.length == 4 && it.all(Char::isDigit) }
        return ItunesResult(
            artistName = first.optString("artistName", "").takeIf { it.isNotBlank() },
            trackTitle = first.optString("trackName", "").takeIf { it.isNotBlank() },
            albumTitle = first.optString("collectionName", "").takeIf { it.isNotBlank() },
            albumCoverUrl = artHd,
            releaseYear = year,
            genre = first.optString("primaryGenreName", "").takeIf { it.isNotBlank() }
        )
    }

    /**
     * Open MusicBrainz + Cover Art Archive fallback when neither primary catalog returns an album cover.
     */
    private fun fetchMusicBrainzCoverArt(title: String, artist: String): String? {
        val q = buildString {
            append("recording:\"").append(title.replace("\"", "")).append("\"")
            if (artist.isNotBlank()) {
                append(" AND artist:\"").append(artist.replace("\"", "")).append("\"")
            }
        }
        val encoded = URLEncoder.encode(q, "UTF-8")
        val json = httpGetJson("https://musicbrainz.org/ws/2/recording/?query=$encoded&limit=1&fmt=json")
        val rec = json?.optJSONArray("recordings")?.optJSONObject(0) ?: return null
        val releaseId = rec.optJSONArray("releases")?.optJSONObject(0)?.optString("id")
            ?.takeIf { it.length >= 16 } ?: return null
        val caa = httpGetJson("https://coverartarchive.org/release/$releaseId")
        val images = caa?.optJSONArray("images") ?: return null
        for (i in 0 until images.length()) {
            val img = images.optJSONObject(i) ?: continue
            val thumb = img.optJSONObject("thumbnails")?.optString("500")
                ?.replace("http://", "https://")
                ?.takeIf { it.startsWith("https://") }
            val full = img.optString("image", "")
                .replace("http://", "https://")
                .takeIf { it.startsWith("https://") }
            if (thumb != null || full != null) return thumb ?: full
        }
        return null
    }

    private fun fetchLrclibLyrics(
        title: String,
        artist: String,
        album: String,
        durationSec: Int,
    ): String? {
        if (title.isBlank()) return null
        // 1. Exact match endpoint when artist is known
        if (artist.isNotBlank()) {
            val params = buildString {
                append("track_name=").append(URLEncoder.encode(title, "UTF-8"))
                append("&artist_name=").append(URLEncoder.encode(artist, "UTF-8"))
                if (album.isNotBlank() && !album.equals("Unknown Album", ignoreCase = true)) {
                    append("&album_name=").append(URLEncoder.encode(album, "UTF-8"))
                }
                if (durationSec in 15..1800) {
                    append("&duration=").append(durationSec)
                }
            }
            val exact = httpGetJson("https://lrclib.net/api/get?$params")
            val synced = exact?.optString("syncedLyrics")?.takeIf { it.isNotBlank() && it != "null" }
            if (synced != null) return synced
            val plain = exact?.optString("plainLyrics")?.takeIf { it.isNotBlank() && it != "null" }
            if (plain != null) return plain
        }
        // 2. Broad search pass (Artist + Title, then swapped Title + Artist if filename was reversed, then Title only)
        val queries = linkedSetOf(
            listOf(title, artist).filter { it.isNotBlank() }.joinToString(" "),
            listOf(artist, title).filter { it.isNotBlank() }.joinToString(" "),
            title,
        ).filter { it.isNotBlank() }

        for (q in queries) {
            val arr = httpGetJsonArray("https://lrclib.net/api/search?q=${URLEncoder.encode(q, "UTF-8")}")
            if (arr != null && arr.length() > 0) {
                for (i in 0 until minOf(arr.length(), 4)) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val synced = obj.optString("syncedLyrics").takeIf { it.isNotBlank() && it != "null" }
                    if (synced != null) return synced
                }
                val firstPlain = arr.optJSONObject(0)?.optString("plainLyrics")
                    ?.takeIf { it.isNotBlank() && it != "null" }
                if (firstPlain != null) return firstPlain
            }
        }
        return null
    }

    private fun fetchWikipediaWithFallback(query: String, preferredLang: String): WikiResult? {
        val primary = fetchWikipediaInLang(query, preferredLang)
        if (primary != null) return primary
        val fallbackLang = if (preferredLang == "ar") "en" else "ar"
        return fetchWikipediaInLang(query, fallbackLang)
    }

    private fun fetchWikipediaInLang(query: String, langCode: String): WikiResult? {
        if (query.isBlank()) return null
        val directSlug = URLEncoder.encode(query.replace(' ', '_'), "UTF-8")
        val direct = parseWikipediaSummary(httpGetJson("https://$langCode.wikipedia.org/api/rest_v1/page/summary/$directSlug"))
        if (direct != null) return direct

        // Fallback via OpenSearch to find best matching title
        val searchUrl = "https://$langCode.wikipedia.org/w/api.php?action=opensearch&limit=2&namespace=0&format=json&search=${URLEncoder.encode(query, "UTF-8")}"
        val openSearch = httpGetJsonArray(searchUrl)
        val titles = openSearch?.optJSONArray(1)
        val bestTitle = titles?.optString(0)?.takeIf { it.isNotBlank() } ?: return null
        val bestSlug = URLEncoder.encode(bestTitle.replace(' ', '_'), "UTF-8")
        return parseWikipediaSummary(httpGetJson("https://$langCode.wikipedia.org/api/rest_v1/page/summary/$bestSlug"))
    }

    internal fun parseWikipediaSummary(json: JSONObject?): WikiResult? {
        if (json == null) return null
        val type = json.optString("type", "")
        if (type == "disambiguation" || type == "https://mediawiki.org/wiki/HyperSwitch/errors/not_found") {
            return null
        }
        val title = json.optString("title", "").trim()
        val extract = json.optString("extract", "").trim()
        if (title.isBlank() || extract.isBlank()) return null
        val description = json.optString("description", "").trim()
        val imageUrl = json.optJSONObject("originalimage")?.optString("source")?.takeIf { it.startsWith("https://") }
            ?: json.optJSONObject("thumbnail")?.optString("source")?.takeIf { it.startsWith("https://") }
        val pageUrl = json.optJSONObject("content_urls")?.optJSONObject("desktop")?.optString("page")
            ?.takeIf { it.startsWith("https://") }
        return WikiResult(
            title = title,
            description = description,
            extract = extract,
            imageUrl = imageUrl,
            pageUrl = pageUrl
        )
    }

    private fun fetchAlQuranSurah(surahNumber: Int, langCode: String): QuranApiResult? {
        val transEdition = when (langCode) {
            "fr" -> "fr.hamidullah"
            "es" -> "es.cortes"
            else -> "en.sahih"
        }
        val url = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,$transEdition"
        val json = httpGetJson(url) ?: return null
        val data = json.optJSONArray("data") ?: return null
        if (data.length() < 1) return null

        val arabicAyahs = data.optJSONObject(0)?.optJSONArray("ayahs")
        val transAyahs = if (data.length() > 1) data.optJSONObject(1)?.optJSONArray("ayahs") else null

        val arSample = buildString {
            val count = minOf(arabicAyahs?.length() ?: 0, 5)
            for (i in 0 until count) {
                val a = arabicAyahs?.optJSONObject(i) ?: continue
                val num = a.optInt("numberInSurah", i + 1)
                val text = a.optString("text", "").trim()
                if (text.isNotBlank()) {
                    if (isNotEmpty()) append("  ")
                    append(text).append(" ﴿").append(num).append("﴾")
                }
            }
        }
        val trSample = buildString {
            val count = minOf(transAyahs?.length() ?: 0, 3)
            for (i in 0 until count) {
                val a = transAyahs?.optJSONObject(i) ?: continue
                val num = a.optInt("numberInSurah", i + 1)
                val text = a.optString("text", "").trim()
                if (text.isNotBlank()) {
                    if (isNotEmpty()) append("\n")
                    append("($num) ").append(text)
                }
            }
        }
        if (arSample.isBlank()) return null
        return QuranApiResult(arabicSample = arSample, translationSample = trSample)
    }

    private fun fetchSampleHadith(collectionKey: String): String? {
        // Fetch verified opening Hadith (e.g. Hadith #1: Actions are by intentions) from open Hadith CDN
        val url = "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/$collectionKey/1.min.json"
        val json = httpGetJson(url) ?: return null
        val hadiths = json.optJSONArray("hadiths") ?: return null
        return hadiths.optJSONObject(0)?.optString("text")?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun extractAndReverseGeocodePhotoLocation(
        context: Context,
        uri: Uri,
        langCode: String,
    ): String? {
        return try {
            val exif = context.contentResolver.openInputStream(uri)?.use { stream ->
                android.media.ExifInterface(stream)
            } ?: return null
            val latLong = FloatArray(2)
            @Suppress("DEPRECATION")
            val hasGps = exif.getLatLong(latLong)
            if (!hasGps) return null
            val lat = latLong[0].toDouble()
            val lon = latLong[1].toDouble()
            val url = "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$lat&lon=$lon&accept-language=$langCode"
            val json = httpGetJson(url) ?: return null
            val address = json.optJSONObject("address")
            val city = address?.optString("city")?.takeIf { it.isNotBlank() }
                ?: address?.optString("town")?.takeIf { it.isNotBlank() }
                ?: address?.optString("state")?.takeIf { it.isNotBlank() }
            val country = address?.optString("country")?.takeIf { it.isNotBlank() }
            listOfNotNull(city, country).joinToString(", ").ifBlank {
                json.optString("display_name", "").take(70).takeIf { it.isNotBlank() }
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun httpGetString(urlString: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty("Accept", "application/json")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else null
        } catch (_: Throwable) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun httpGetJson(urlString: String): JSONObject? {
        val raw = httpGetString(urlString) ?: return null
        return try {
            JSONObject(raw)
        } catch (_: Throwable) {
            null
        }
    }

    private fun httpGetJsonArray(urlString: String): JSONArray? {
        val raw = httpGetString(urlString) ?: return null
        return try {
            JSONArray(raw)
        } catch (_: Throwable) {
            null
        }
    }

    private fun AppLanguage.wikiCode(): String = when (this) {
        AppLanguage.AR -> "ar"
        AppLanguage.FR -> "fr"
        AppLanguage.ES -> "es"
        AppLanguage.EN -> "en"
    }

    private fun buildCacheKey(raw: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray(Charsets.UTF_8))
        return digest.take(12).joinToString("") { "%02x".format(it) }
    }

    private fun saveCachedReport(context: Context, key: String, report: MediaKnowledgeReport) {
        try {
            val dir = File(context.cacheDir, "prism_knowledge_cache").apply { mkdirs() }
            val json = JSONObject().apply {
                put("mediaId", report.mediaId)
                put("category", report.category.name)
                put("artistName", report.artistName)
                put("artistPhotoUrl", report.artistPhotoUrl ?: JSONObject.NULL)
                put("albumCoverUrl", report.albumCoverUrl ?: JSONObject.NULL)
                put("artistBio", report.artistBio)
                put("artistRole", report.artistRole)
                put("albumTitle", report.albumTitle)
                put("releaseYear", report.releaseYear ?: JSONObject.NULL)
                put("genre", report.genre ?: JSONObject.NULL)
                put("energyPercent", report.energyPercent)
                put("bpm", report.bpm)
                put("moodLabel", report.moodLabel)
                put("audioQualityBadge", report.audioQualityBadge)
                put("fetchedLyrics", report.fetchedLyrics ?: JSONObject.NULL)
                put("quranSurahNumber", report.quranSurahNumber ?: JSONObject.NULL)
                put("quranSurahNameAr", report.quranSurahNameAr ?: JSONObject.NULL)
                put("quranSurahNameEn", report.quranSurahNameEn ?: JSONObject.NULL)
                put("quranRevelationType", report.quranRevelationType ?: JSONObject.NULL)
                put("quranAyahCount", report.quranAyahCount ?: JSONObject.NULL)
                put("quranSampleAyahs", report.quranSampleAyahs ?: JSONObject.NULL)
                put("quranTranslation", report.quranTranslation ?: JSONObject.NULL)
                put("reciterName", report.reciterName ?: JSONObject.NULL)
                put("hadithBook", report.hadithBook ?: JSONObject.NULL)
                put("hadithText", report.hadithText ?: JSONObject.NULL)
                put("encyclopediaTitle", report.encyclopediaTitle ?: JSONObject.NULL)
                put("encyclopediaSummary", report.encyclopediaSummary ?: JSONObject.NULL)
                put("encyclopediaImageUrl", report.encyclopediaImageUrl ?: JSONObject.NULL)
                put("encyclopediaUrl", report.encyclopediaUrl ?: JSONObject.NULL)
                put("photoLocationLabel", report.photoLocationLabel ?: JSONObject.NULL)
                put("sourceAttribution", report.sourceAttribution)
                put("fetchedAtMs", report.fetchedAtMs)
            }
            File(dir, "$key.json").writeText(json.toString(), Charsets.UTF_8)
        } catch (_: Throwable) {
            // Cache failure is non-fatal
        }
    }

    private fun readCachedReport(context: Context, key: String): MediaKnowledgeReport? {
        return try {
            val file = File(File(context.cacheDir, "prism_knowledge_cache"), "$key.json")
            if (!file.exists()) return null
            val j = JSONObject(file.readText(Charsets.UTF_8))
            fun JSONObject.optStrOrNull(name: String): String? =
                if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() && it != "null" }
            fun JSONObject.optIntOrNull(name: String): Int? =
                if (isNull(name)) null else optInt(name)

            MediaKnowledgeReport(
                mediaId = j.optString("mediaId"),
                category = runCatching { MediaKnowledgeCategory.valueOf(j.optString("category")) }
                    .getOrDefault(MediaKnowledgeCategory.MUSIC),
                artistName = j.optString("artistName"),
                artistPhotoUrl = j.optStrOrNull("artistPhotoUrl"),
                albumCoverUrl = j.optStrOrNull("albumCoverUrl"),
                artistBio = j.optString("artistBio"),
                artistRole = j.optString("artistRole"),
                albumTitle = j.optString("albumTitle"),
                releaseYear = j.optStrOrNull("releaseYear"),
                genre = j.optStrOrNull("genre"),
                energyPercent = j.optInt("energyPercent", 65),
                bpm = j.optInt("bpm", 100),
                moodLabel = j.optString("moodLabel"),
                audioQualityBadge = j.optString("audioQualityBadge"),
                fetchedLyrics = j.optStrOrNull("fetchedLyrics"),
                quranSurahNumber = j.optIntOrNull("quranSurahNumber"),
                quranSurahNameAr = j.optStrOrNull("quranSurahNameAr"),
                quranSurahNameEn = j.optStrOrNull("quranSurahNameEn"),
                quranRevelationType = j.optStrOrNull("quranRevelationType"),
                quranAyahCount = j.optIntOrNull("quranAyahCount"),
                quranSampleAyahs = j.optStrOrNull("quranSampleAyahs"),
                quranTranslation = j.optStrOrNull("quranTranslation"),
                reciterName = j.optStrOrNull("reciterName"),
                hadithBook = j.optStrOrNull("hadithBook"),
                hadithText = j.optStrOrNull("hadithText"),
                encyclopediaTitle = j.optStrOrNull("encyclopediaTitle"),
                encyclopediaSummary = j.optStrOrNull("encyclopediaSummary"),
                encyclopediaImageUrl = j.optStrOrNull("encyclopediaImageUrl"),
                encyclopediaUrl = j.optStrOrNull("encyclopediaUrl"),
                photoLocationLabel = j.optStrOrNull("photoLocationLabel"),
                sourceAttribution = j.optString("sourceAttribution"),
                fetchedAtMs = j.optLong("fetchedAtMs", 0L),
            )
        } catch (_: Throwable) {
            null
        }
    }
}
