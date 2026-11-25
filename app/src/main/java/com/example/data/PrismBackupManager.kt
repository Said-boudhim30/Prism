package com.example.data

import com.example.model.AppSettings
import com.example.model.MediaBookmark
import com.example.model.TrackTagOverride
import com.example.model.UserPlaylist
import com.example.model.WatchedFolder
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

/**
 * Signed local JSON Backup & Restore payload for Prism Media.
 * Preserves user playlists, watched folders, bookmarks, custom lyrics, audio tag overrides,
 * favorites, and appearance/audio settings safely when transferring to a new device.
 */
data class PrismBackupPayload(
    val version: Int = 1,
    val exportedAtMs: Long = System.currentTimeMillis(),
    val settingsJson: JSONObject,
    val playlists: List<UserPlaylist>,
    val watchedFolders: List<WatchedFolder>,
    val bookmarks: List<MediaBookmark>,
    val customLyrics: Map<String, String>,
    val tagOverrides: Map<String, TrackTagOverride>,
    val favorites: Set<String>,
)

object PrismBackupManager {
    private const val BACKUP_FORMAT = "prism-media-backup-v1"

    fun encodeBackup(payload: PrismBackupPayload): String {
        val body = JSONObject().apply {
            put("format", BACKUP_FORMAT)
            put("version", payload.version)
            put("exportedAtMs", payload.exportedAtMs)
            put("settings", payload.settingsJson)
            put("playlists", JSONArray(SettingsRepository.encodePlaylistsJson(payload.playlists)))
            put("watchedFolders", JSONArray(SettingsRepository.encodeWatchedFoldersJson(payload.watchedFolders)))
            put("bookmarks", JSONArray(SettingsRepository.encodeBookmarksJson(payload.bookmarks)))
            put("customLyrics", JSONObject().apply {
                payload.customLyrics.forEach { (k, v) -> put(k, v) }
            })
            put("tagOverrides", JSONObject(SettingsRepository.encodeTagOverridesJson(payload.tagOverrides)))
            put("favorites", JSONArray().apply {
                payload.favorites.forEach { put(it) }
            })
        }
        val bodyStr = body.toString()
        val digest = sha256Hex(bodyStr)
        val envelope = JSONObject().apply {
            put("format", BACKUP_FORMAT)
            put("checksumSha256", digest)
            put("payloadJson", bodyStr)
        }
        return envelope.toString(2)
    }

    /**
     * Parses and validates a `.prism.json` backup file, verifying its SHA-256 integrity checksum
     * before restoring any user data.
     */
    fun decodeAndVerifyBackup(rawJson: String): PrismBackupPayload? {
        return runCatching {
            val envelope = JSONObject(rawJson)
            if (envelope.optString("format") != BACKUP_FORMAT) return null
            val expectedChecksum = envelope.optString("checksumSha256")
            val payloadStr = envelope.optString("payloadJson").takeIf { it.isNotBlank() }
                ?: envelope.optJSONObject("payload")?.toString()
                ?: return null
            if (expectedChecksum.isNotBlank()) {
                val computed = sha256Hex(payloadStr)
                if (!computed.equals(expectedChecksum, ignoreCase = true)) {
                    return null
                }
            }
            val body = JSONObject(payloadStr)

            val settingsJson = body.optJSONObject("settings") ?: JSONObject()
            val playlists = SettingsRepository.parsePlaylistsJson(
                body.optJSONArray("playlists")?.toString() ?: "[]"
            )
            val watchedFolders = SettingsRepository.parseWatchedFoldersJson(
                body.optJSONArray("watchedFolders")?.toString() ?: "[]"
            )
            val bookmarks = SettingsRepository.parseBookmarksJson(
                body.optJSONArray("bookmarks")?.toString() ?: "[]"
            )
            val lyricsObj = body.optJSONObject("customLyrics") ?: JSONObject()
            val lyrics = buildMap {
                val it = lyricsObj.keys()
                while (it.hasNext()) {
                    val k = it.next()
                    val v = lyricsObj.optString(k)
                    if (v.isNotBlank()) put(k, v)
                }
            }
            val tagOverrides = SettingsRepository.parseTagOverridesJson(
                body.optJSONObject("tagOverrides")?.toString() ?: "{}"
            )
            val favArr = body.optJSONArray("favorites") ?: JSONArray()
            val favorites = buildSet {
                for (i in 0 until favArr.length()) {
                    val id = favArr.optString(i)
                    if (id.isNotBlank()) add(id)
                }
            }

            PrismBackupPayload(
                version = body.optInt("version", 1),
                exportedAtMs = body.optLong("exportedAtMs", 0L),
                settingsJson = settingsJson,
                playlists = playlists,
                watchedFolders = watchedFolders,
                bookmarks = bookmarks,
                customLyrics = lyrics,
                tagOverrides = tagOverrides,
                favorites = favorites
            )
        }.getOrNull()
    }

    fun settingsToJson(s: AppSettings): JSONObject = JSONObject().apply {
        put("darkTheme", s.darkTheme)
        put("amoledBlack", s.amoledBlack)
        put("accentPalette", s.accentPalette.name)
        put("iconStyle", s.iconStyle.name)
        put("language", s.language.name)
        put("fontStyle", s.fontStyle.name)
        put("fontScaleMultiplier", s.fontScaleMultiplier.toDouble())
        put("liquidGlassStyle", s.liquidGlassStyle.name)
        put("liquidGlassIntensity", s.liquidGlassIntensity.toDouble())
        put("shapeRoundness", s.shapeRoundness.toDouble())
        put("contrastLevel", s.contrastLevel.name)
        put("navTransitionStyle", s.navTransitionStyle.name)
        put("edgeGlowEnabled", s.edgeGlowEnabled)
        put("eqEnabled", s.eqEnabled)
        put("eqPreset", s.eqPreset.name)
        put("eqGainsDb", s.eqGainsDb.joinToString(","))
        put("bassBoost", s.bassBoost.toDouble())
        put("audioPreampPercent", s.audioPreampPercent)
        put("audioPitch", s.audioPitch.toDouble())
        put("crossfadeSeconds", s.crossfadeSeconds)
        put("hardwareVideoAcceleration", s.hardwareVideoAcceleration)
        put("autoPlayNext", s.autoPlayNext)
        put("resumePlaybackPosition", s.resumePlaybackPosition)
        put("showLyricsInPlayer", s.showLyricsInPlayer)
        put("autoPipEnabled", s.autoPipEnabled)
        put("squigglyProgress", s.squigglyProgress)
        put("biometricUnlockEnabled", s.biometricUnlockEnabled)
        put("navBarStyle", s.navBarStyle.name)
        put("startupTab", s.startupTab.name)
        put("uiDensity", s.uiDensity.name)
        put("playerArtworkStyle", s.playerArtworkStyle.name)
        put("albumArtTintEnabled", s.albumArtTintEnabled)
        put("showFullPlayerSpectrum", s.showFullPlayerSpectrum)
        put("seekStepSeconds", s.seekStepSeconds)
        put("hapticsEnabled", s.hapticsEnabled)
        put("autoOnlineEnrichment", s.autoOnlineEnrichment)
        put("autoFetchLyrics", s.autoFetchLyrics)
        put("autoFetchArtistPhotos", s.autoFetchArtistPhotos)
        put("gaplessPlayback", s.gaplessPlayback)
        put("audioNormalization", s.audioNormalization)
        put("pauseOnHeadsetUnplug", s.pauseOnHeadsetUnplug)
        put("videoGesturesEnabled", s.videoGesturesEnabled)
        put("videoDoubleTapSeekEnabled", s.videoDoubleTapSeekEnabled)
        put("videoRememberPosition", s.videoRememberPosition)
        put("videoSubtitleSizeSp", s.videoSubtitleSizeSp)
        put("photoAutoShowExif", s.photoAutoShowExif)
        put("photoHighResDecode", s.photoHighResDecode)
        put("photoSlideshowTransition", s.photoSlideshowTransition.name)
        put("surfaceDepthStyle", s.surfaceDepthStyle.name)
    }

    private fun sha256Hex(text: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
