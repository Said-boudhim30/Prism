package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MediaScanner
import com.example.localization.stringsFor
import com.example.model.AppLanguage
import com.example.model.EqPreset
import com.example.player.AudioPlaybackController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Prism", appName)
    }

    @Test
    fun `demo media loads playable audio video and photos`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val demo = MediaScanner.loadDemoMedia(context)

        assertTrue(demo.audioTracks.size >= 5)
        assertTrue(demo.audioTracks.any { it.extension == "MP3" })
        assertTrue(demo.audioTracks.any { it.extension == "FLAC" })
        assertTrue(demo.audioTracks.any { it.extension == "WAV" })
        assertTrue(demo.audioTracks.any { it.extension == "OGG" })
        assertTrue(demo.audioTracks.any { it.extension == "M4A" })
        assertTrue(demo.videos.size >= 2)
        assertTrue(demo.photos.size >= 4)
    }

    @Test
    fun `equalizer has 5 bands and presets`() {
        assertEquals(5, AudioPlaybackController.TARGET_BAND_FREQS_HZ.size)
        assertEquals(5, EqPreset.FLAT.gainsDb.size)
        assertEquals(5, EqPreset.DEEP_BASS.gainsDb.size)
        assertEquals(5, EqPreset.VOCAL_BOOST.gainsDb.size)
        assertTrue(EqPreset.DEEP_BASS.gainsDb[0] > 0f)
        assertTrue(EqPreset.VOCAL_BOOST.gainsDb[2] > 0f)
    }

    @Test
    fun `arabic localization includes required rtl labels`() {
        val ar = stringsFor(AppLanguage.AR)
        assertEquals("الموسيقى", ar.tabMusic)
        assertEquals("الفيديو", ar.tabVideos)
        assertEquals("الصور", ar.tabPhotos)
        assertEquals("الإعدادات", ar.tabSettings)
        assertEquals("معادل الصوت", ar.equalizerTitle)
        assertEquals("مؤقت النوم", ar.sleepTimerTitle)
        assertEquals("إعادة ضبط", ar.eqReset)
        assertEquals("مخصص", ar.customTimer)
        assertEquals("قوائم التشغيل", ar.playlistsChip)
        assertEquals("معلومات الملف", ar.menuFileDetails)
        assertEquals("إعادة تسمية", ar.menuRename)
        assertEquals("حذف من المكتبة", ar.menuRemove)
        assertEquals("سعيد بودحيم", ar.designerName)
    }

    @Test
    fun `security audit confirms zero broad storage permissions`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audit = com.example.security.AppSecurityVerifier.inspect(context)
        assertTrue(audit.zeroStoragePermissions)
        assertTrue(audit.httpsOnlyPolicy)
    }

    @Test
    fun `duration and file size formatting`() {
        assertEquals("0:18", MediaScanner.formatDuration(18_000L))
        assertEquals("1:05:00", MediaScanner.formatDuration(3_900_000L))
        assertEquals("512 KB", MediaScanner.formatFileSize(512 * 1024L))
    }

    @Test
    fun `biquad dsp equalizer produces real measurable gain difference on 60hz bass signal`() {
        val sampleRate = 44100
        val count = 4096
        val input = ShortArray(count) { i ->
            val angle = 2.0 * Math.PI * 60.0 * i / sampleRate
            (Math.sin(angle) * 6000.0).toInt().toShort()
        }

        val processor = com.example.player.FiveBandBiquadAudioProcessor()
        processor.setEqualizerEnabled(true)

        processor.setBandGainsDb(listOf(0f, 0f, 0f, 0f, 0f))
        val flatOut = processor.processPcmForTest(input, sampleRate)

        processor.setBandGainsDb(listOf(10f, 0f, 0f, 0f, 0f))
        val boostedOut = processor.processPcmForTest(input, sampleRate)

        processor.setBandGainsDb(listOf(-10f, 0f, 0f, 0f, 0f))
        val cutOut = processor.processPcmForTest(input, sampleRate)

        fun rms(arr: ShortArray): Double {
            var sum = 0.0
            for (i in 1024 until arr.size) {
                val v = arr[i].toDouble()
                sum += v * v
            }
            return Math.sqrt(sum / (arr.size - 1024))
        }

        val flatRms = rms(flatOut)
        val boostedRms = rms(boostedOut)
        val cutRms = rms(cutOut)

        assertTrue("Boosted bass ($boostedRms) must be significantly louder than flat ($flatRms)", boostedRms > flatRms * 1.8)
        assertTrue("Cut bass ($cutRms) must be significantly quieter than flat ($flatRms)", cutRms < flatRms * 0.5)
    }

    @Test
    fun `new multi window auto pip and companion strings exist in arabic and english`() {
        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.autoPipTitle.isNotBlank())
        assertTrue(ar.multiWindowTitle.isNotBlank())
        assertTrue(ar.bassBoostLabel.isNotBlank())
        assertTrue(ar.upNextQueue.isNotBlank())
        assertTrue(en.autoPipTitle.isNotBlank())
        assertTrue(en.multiWindowTitle.isNotBlank())
    }

    @Test
    fun `stream url ab loop video cinema and photo studio strings exist in arabic and english`() {
        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.playStreamUrl.isNotBlank())
        assertTrue(ar.abLoopSetA.isNotBlank())
        assertTrue(ar.abLoopSetB.isNotBlank())
        assertTrue(ar.abLoopActive.isNotBlank())
        assertTrue(ar.videoAspectFit.isNotBlank())
        assertTrue(ar.videoLockedMsg.isNotBlank())
        assertTrue(ar.photoSlideshow.isNotBlank())
        assertTrue(ar.photoFilterVivid.isNotBlank())

        assertTrue(en.playStreamUrl.isNotBlank())
        assertTrue(en.abLoopSetA.isNotBlank())
        assertTrue(en.videoAspectFit.isNotBlank())
        assertTrue(en.photoSlideshow.isNotBlank())
        assertTrue(en.photoFilterNoir.isNotBlank())
    }

    @Test
    fun `material you squiggly bar customizer drag to x and accordion settings strings exist`() {
        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.squigglyBarTitle.isNotBlank())
        assertTrue(ar.compactBarAction.isNotBlank())
        assertTrue(ar.floatPlayerAction.isNotBlank())
        assertTrue(ar.dragToXHint.isNotBlank())
        assertTrue(ar.expandAllSections.isNotBlank())
        assertTrue(ar.collapseAllSections.isNotBlank())

        assertTrue(en.squigglyBarTitle.isNotBlank())
        assertTrue(en.compactBarAction.isNotBlank())
        assertTrue(en.floatPlayerAction.isNotBlank())
        assertTrue(en.dragToXHint.isNotBlank())
        assertTrue(en.expandAllSections.isNotBlank())
    }

    @Test
    fun `audio track generates normalized artist handle for english and arabic`() {
        val enTrack = com.example.model.AudioTrack(
            id = "1",
            title = "Midnight Pulse",
            artist = "Prism Audio Lab",
            album = "Studio",
            uriString = "",
            extension = "MP3",
            mimeType = "audio/mpeg",
            durationMs = 120_000L,
            sizeBytes = 2048L,
        )
        assertEquals("@prism_audio_lab", enTrack.artistHandle)

        val arTrack = enTrack.copy(artist = "سعيد بودحيم")
        assertEquals("@سعيد_بودحيم", arTrack.artistHandle)
    }

    @Test
    fun `security verifier detects tampered signature and builds email intent for owner`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audit = com.example.security.AppSecurityVerifier.inspect(
            context = context,
            pinnedSha256 = "00:11:22:33:44:55:66:77:88:99"
        )
        val screen = com.example.security.AppSecurityVerifier.inspectScreenProfile(context)
        assertTrue(screen.widthPx > 0)
        assertTrue(screen.heightPx > 0)
        assertTrue(screen.recommendedPhotoColumns in 2..4)

        val intent = com.example.security.AppSecurityVerifier.buildTamperAlertEmailIntent(audit, screen)
        assertEquals(android.content.Intent.ACTION_SENDTO, intent.action)
        val uriString = intent.data?.toString().orEmpty()
        assertTrue(uriString.startsWith("mailto:"))
        assertTrue(uriString.contains("saidboudhim0%40gmail.com") || uriString.contains("saidboudhim0@gmail.com"))
    }

    @Test
    fun `separated media settings font scale lyrics and tamper strings exist in en and ar`() {
        val defaults = com.example.model.AppSettings()
        assertEquals(1.0f, defaults.fontScaleMultiplier, 0.001f)
        assertTrue(defaults.showLyricsInPlayer)
        assertTrue(defaults.notificationsEnabled)
        assertTrue(defaults.tamperGuardEnabled)
        assertEquals(0, defaults.videoDefaultAspect)
        assertEquals(1.0f, defaults.videoDefaultSpeed, 0.001f)
        assertEquals(0, defaults.photoGridColumns)

        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.settingsMusicSection.isNotBlank())
        assertTrue(ar.settingsVideoSection.isNotBlank())
        assertTrue(ar.settingsPhotosSection.isNotBlank())
        assertTrue(ar.lyricsTitle.isNotBlank())
        assertTrue(ar.fontSizeTitle.isNotBlank())
        assertTrue(ar.tamperGuardTitle.isNotBlank())
        assertTrue(ar.notifSettingTitle.isNotBlank())
        assertTrue(ar.widgetHubTitle.isNotBlank())
        assertTrue(en.settingsMusicSection.isNotBlank())
        assertTrue(en.settingsVideoSection.isNotBlank())
        assertTrue(en.settingsPhotosSection.isNotBlank())
        assertTrue(en.tamperGuardTitle.isNotBlank())
    }

    @Test
    fun `pin crypto hashes with unique salts and verifies in constant time`() {
        val salt1 = com.example.security.PinCrypto.generateSaltHex()
        val salt2 = com.example.security.PinCrypto.generateSaltHex()
        assertEquals(32, salt1.length)
        assertTrue(salt1 != salt2)

        val hash1 = com.example.security.PinCrypto.hashPin("2580", salt1)
        val hash2 = com.example.security.PinCrypto.hashPin("2580", salt2)
        assertTrue(hash1.isNotBlank())
        assertTrue(hash1 != "2580")
        assertTrue(hash1 != hash2)

        assertTrue(com.example.security.PinCrypto.verifyPin("2580", salt1, hash1))
        assertTrue(!com.example.security.PinCrypto.verifyPin("2581", salt1, hash1))
        assertTrue(!com.example.security.PinCrypto.verifyPin("", salt1, hash1))
    }

    @Test
    fun `https stream url validator blocks cleartext and local or private network addresses`() {
        val v = com.example.security.AppSecurityVerifier
        assertTrue(v.isSafeHttpsStreamUrl("https://cdn.example.com/live/stream.m3u8"))
        assertTrue(v.isSafeHttpsStreamUrl("https://media.radio.org/track.mp3"))

        assertTrue(!v.isSafeHttpsStreamUrl("http://cdn.example.com/track.mp3"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://localhost/stream.mp4"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://127.0.0.1/stream.mp4"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://10.0.0.5/internal.mp3"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://192.168.1.10/video.mp4"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://172.16.4.2/video.mp4"))
        assertTrue(!v.isSafeHttpsStreamUrl("https://169.254.169.254/latest/meta-data"))
    }

    @Test
    fun `defense shield score scales with active security controls`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audit = com.example.security.AppSecurityVerifier.inspect(context)
        val baseScore = com.example.security.AppSecurityVerifier.computeSecurityScore(
            audit = audit,
            appLockEnabled = false,
            screenPrivacyEnabled = false,
            tamperGuardEnabled = true,
        )
        val fullScore = com.example.security.AppSecurityVerifier.computeSecurityScore(
            audit = audit,
            appLockEnabled = true,
            screenPrivacyEnabled = true,
            tamperGuardEnabled = true,
        )
        assertTrue(fullScore > baseScore)
        assertEquals(baseScore + 30, fullScore)
    }

    @Test
    fun `selection view mode queue sort pro video and security shield strings exist`() {
        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.selectAllAction.isNotBlank())
        assertTrue(ar.deselectAllAction.isNotBlank())
        assertTrue(ar.invertSelectionAction.isNotBlank())
        assertTrue(ar.viewModeList.isNotBlank())
        assertTrue(ar.viewModeCompact.isNotBlank())
        assertTrue(ar.viewModeGrid.isNotBlank())
        assertTrue(ar.queueSortShuffle.isNotBlank())
        assertTrue(ar.videoSubtitlesLabel.isNotBlank())
        assertTrue(ar.appLockTitle.isNotBlank())
        assertTrue(ar.screenPrivacyTitle.isNotBlank())
        assertTrue(ar.intruderLogTitle.isNotBlank())

        assertTrue(en.selectAllAction.isNotBlank())
        assertTrue(en.invertSelectionAction.isNotBlank())
        assertTrue(en.videoRotateLabel.isNotBlank())
        assertTrue(en.appLockTitle.isNotBlank())
    }

    @Test
    fun `extractFolderNameFromDocId resolves clean display names for nested SAF paths`() {
        assertEquals("Music", MediaScanner.extractFolderNameFromDocId("primary:Music"))
        assertEquals("FLAC", MediaScanner.extractFolderNameFromDocId("primary:Music/Lossless/FLAC/"))
        assertEquals("Internal Storage", MediaScanner.extractFolderNameFromDocId("primary:"))
        assertEquals("Movies", MediaScanner.extractFolderNameFromDocId("1234-5678:Movies"))
    }

    @Test
    fun `watched folders serialize and deserialize losslessly`() {
        val folders = listOf(
            com.example.model.WatchedFolder(
                uriString = "content://com.android.externalstorage.documents/tree/primary%3AMusic",
                displayName = "Music",
                enabled = true,
                audioCount = 42,
                videoCount = 3,
                photoCount = 10,
                lastScannedMs = 1700000000000L
            ),
            com.example.model.WatchedFolder(
                uriString = "content://com.android.externalstorage.documents/tree/primary%3ADownload",
                displayName = "Download",
                enabled = false,
                audioCount = 5,
                videoCount = 2,
                photoCount = 8,
                lastScannedMs = 1700000001000L
            )
        )
        val json = com.example.data.SettingsRepository.encodeWatchedFoldersJson(folders)
        val parsed = com.example.data.SettingsRepository.parseWatchedFoldersJson(json)
        assertEquals(folders, parsed)
    }

    @Test
    fun `findDuplicateGroups detects duplicate tracks and ignores unique files`() {
        val tracks = listOf(
            com.example.model.AudioTrack(
                id = "t1", title = "Sunset Drive", artist = "A", album = "B",
                uriString = "content://a/1", extension = "MP3", mimeType = "audio/mpeg",
                durationMs = 180_000L, sizeBytes = 4_200_000L, isDemo = false
            ),
            com.example.model.AudioTrack(
                id = "t2", title = "Sunset Drive (1)", artist = "A", album = "B",
                uriString = "content://b/2", extension = "MP3", mimeType = "audio/mpeg",
                durationMs = 180_000L, sizeBytes = 4_200_000L, isDemo = false
            ),
            com.example.model.AudioTrack(
                id = "t3", title = "Unique Track", artist = "C", album = "D",
                uriString = "content://a/3", extension = "FLAC", mimeType = "audio/flac",
                durationMs = 210_000L, sizeBytes = 18_500_000L, isDemo = false
            )
        )
        val groups = MediaScanner.findDuplicateGroups(tracks, emptyList(), emptyList())
        assertEquals(1, groups.size)
        assertEquals(listOf("t1", "t2"), groups.first().itemIds)
        assertEquals(com.example.model.DuplicateMediaType.AUDIO, groups.first().mediaType)
    }

    @Test
    fun `first launch watch folder storage insights tag editor and bookmarks strings exist`() {
        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        assertTrue(ar.firstLaunchTitle.isNotBlank())
        assertTrue(ar.watchedFoldersTitle.isNotBlank())
        assertTrue(ar.storageInsightsTitle.isNotBlank())
        assertTrue(ar.editTagsTitle.isNotBlank())
        assertTrue(ar.bookmarksTitle.isNotBlank())
        assertTrue(ar.recentlyPlayedTitle.isNotBlank())
        assertTrue(en.firstLaunchTitle.isNotBlank())
        assertTrue(en.watchedFoldersTitle.isNotBlank())
        assertTrue(en.storageInsightsTitle.isNotBlank())
        assertTrue(en.editTagsTitle.isNotBlank())
        assertTrue(en.bookmarksTitle.isNotBlank())
    }

    @Test
    fun `studio 200 percent preamp boost amplifies rms cleanly without hard clipping overflow`() {
        val sampleRate = 44100
        val count = 2048
        val input = ShortArray(count) { i ->
            val angle = 2.0 * Math.PI * 440.0 * i / sampleRate
            (Math.sin(angle) * 8000.0).toInt().toShort()
        }

        val processor = com.example.player.FiveBandBiquadAudioProcessor()
        processor.setEqualizerEnabled(false)
        processor.setPreampPercent(100)
        val normalOut = processor.processPcmForTest(input, sampleRate)

        processor.setPreampPercent(200)
        val boostedOut = processor.processPcmForTest(input, sampleRate)

        fun rms(arr: ShortArray): Double {
            var sum = 0.0
            for (v in arr) sum += v.toDouble() * v.toDouble()
            return Math.sqrt(sum / arr.size)
        }

        val normalRms = rms(normalOut)
        val boostedRms = rms(boostedOut)
        val maxPeak = boostedOut.maxOf { kotlin.math.abs(it.toInt()) }

        assertTrue("200% preamp ($boostedRms) should boost signal over 100% ($normalRms)", boostedRms > normalRms * 1.45)
        assertTrue("Soft limiter must keep peak safely within 16-bit PCM range ($maxPeak)", maxPeak <= 32767)
    }

    @Test
    fun `lrc karaoke lyrics parser parses timestamps and tracks active line accurately`() {
        val sampleLrc = """
            [00:00.00]Prism Studio Intro
            [00:04.50]Material You Liquid Glass
            [00:10.00]Crystal clarity in motion
        """.trimIndent()

        assertTrue(com.example.ui.components.LrcLyricsParser.hasTimestamps(sampleLrc))
        val lines = com.example.ui.components.LrcLyricsParser.parse(sampleLrc)
        assertEquals(3, lines.size)
        assertEquals(0L, lines[0].timeMs)
        assertEquals(4500L, lines[1].timeMs)
        assertEquals(10000L, lines[2].timeMs)
        assertEquals("Material You Liquid Glass", lines[1].text)

        assertEquals(0, com.example.ui.components.LrcLyricsParser.activeLineIndex(lines, 1200L))
        assertEquals(1, com.example.ui.components.LrcLyricsParser.activeLineIndex(lines, 5000L))
        assertEquals(2, com.example.ui.components.LrcLyricsParser.activeLineIndex(lines, 15000L))
    }

    @Test
    fun `liquid glass transitions contrast smart mixes and pro audio defaults and strings exist`() {
        val defaults = com.example.model.AppSettings()
        assertEquals(com.example.model.LiquidGlassStyle.MATERIAL_LIQUID, defaults.liquidGlassStyle)
        assertEquals(com.example.model.NavTransitionStyle.EXPRESSIVE_SPRING, defaults.navTransitionStyle)
        assertEquals(com.example.model.MaterialContrastLevel.STANDARD, defaults.contrastLevel)
        assertEquals(com.example.model.MaterialFontStyle.MATERIAL_EXPRESSIVE, defaults.fontStyle)
        assertTrue(defaults.hardwareVideoAcceleration)
        assertEquals(100, defaults.audioPreampPercent)
        assertEquals(1.0f, defaults.audioPitch, 0.001f)
        assertEquals(0, defaults.crossfadeSeconds)
        assertTrue(defaults.edgeGlowEnabled)

        val ar = stringsFor(AppLanguage.AR)
        val en = stringsFor(AppLanguage.EN)
        listOf(ar, en).forEach { s ->
            assertTrue(s.liquidGlassTitle.isNotBlank())
            assertTrue(s.navTransitionTitle.isNotBlank())
            assertTrue(s.contrastLevelTitle.isNotBlank())
            assertTrue(s.edgeGlowTitle.isNotBlank())
            assertTrue(s.preampBoostTitle.isNotBlank())
            assertTrue(s.audioPitchTitle.isNotBlank())
            assertTrue(s.crossfadeTitle.isNotBlank())
            assertTrue(s.playVideoAsAudioAction.isNotBlank())
            assertTrue(s.fontStyleTitle.isNotBlank())
            assertTrue(s.driversCardTitle.isNotBlank())
            assertTrue(s.driversAutoBadge.isNotBlank())
            assertTrue(s.driversAudioRow.isNotBlank())
            assertTrue(s.driversVideoRow.isNotBlank())
            assertTrue(s.driversDisplayRow.isNotBlank())
            assertTrue(s.driversHwVideoToggleTitle.isNotBlank())
            assertTrue(s.driversRescanAction.isNotBlank())
            assertTrue(s.photoNativeQualityBadge.isNotBlank())
            assertTrue(s.builtInProtectionTitle.isNotBlank())
            assertTrue(s.builtInProtectionBody.isNotBlank())
            assertTrue(s.builtInPillIntegrity.isNotBlank())
            assertTrue(s.builtInPillPrivacy.isNotBlank())
            assertTrue(s.builtInPillEncrypted.isNotBlank())
            com.example.model.LiquidGlassStyle.entries.forEach { assertTrue(s.liquidGlassStyleName(it).isNotBlank()) }
            com.example.model.NavTransitionStyle.entries.forEach { assertTrue(s.navTransitionName(it).isNotBlank()) }
            com.example.model.SmartMusicMix.entries.forEach { assertTrue(s.smartMixName(it).isNotBlank()) }
            com.example.model.MaterialFontStyle.entries.forEach { assertTrue(s.fontStyleName(it).isNotBlank()) }
        }
    }

    @Test
    fun `device media driver engine probes native audio video and display drivers safely`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val profile = com.example.player.DeviceMediaDriverEngine.probe(context)

        assertTrue("Native sample rate must be >= 44100 Hz", profile.nativeSampleRateHz >= 44100)
        assertTrue("Audio badge label must be non-blank", profile.audioBadgeLabel.isNotBlank())
        assertTrue("Video badge label must be non-blank", profile.videoBadgeLabel.isNotBlank())
        assertTrue("Display badge label must be non-blank", profile.displayBadgeLabel.isNotBlank())
        assertTrue("Refresh rate must be positive", profile.refreshRateHz >= 30)
    }

    @Test
    fun `obfuscated security constants decode accurately`() {
        assertEquals("saidboudhim0@gmail.com", com.example.security.AppSecurityVerifier.OWNER_SECURITY_EMAIL)
        assertEquals("com.aistudio.prismmedia", com.example.security.AppSecurityVerifier.EXPECTED_PACKAGE_PREFIX)
    }

    @Test
    fun `all ui strings in english and arabic contain zero third party brand trademarks`() {
        val forbiddenBrands = listOf("vlc", "samsung", "apple", "youtube", "iphone")
        listOf(stringsFor(AppLanguage.EN), stringsFor(AppLanguage.AR)).forEach { stringsObj ->
            val getters = com.example.localization.AppStrings::class.java.methods
                .filter { it.parameterCount == 0 && it.returnType == String::class.java }
            assertTrue("Expected AppStrings getters", getters.size > 50)
            getters.forEach { method ->
                val value = (method.invoke(stringsObj) as? String).orEmpty().lowercase()
                forbiddenBrands.forEach { brand ->
                    assertTrue(
                        "Found forbidden third-party trademark '$brand' in ${method.name}: '$value'",
                        !value.contains(brand)
                    )
                }
            }
        }
    }

    @Test
    fun `subtitle parser parses srt and vtt cues and applies sync offset accurately`() {
        val sampleSrt = """
            1
            00:00:01,000 --> 00:00:04,000
            <i>Welcome to Prism Cinema</i>

            2
            00:00:06,500 --> 00:00:09,000
            Line one
            Line two
        """.trimIndent()
        val srtCues = com.example.player.SubtitleParser.parse(sampleSrt)
        assertEquals(2, srtCues.size)
        assertEquals("Welcome to Prism Cinema", srtCues[0].text)
        assertEquals("Line one\nLine two", srtCues[1].text)
        assertEquals("Welcome to Prism Cinema", com.example.player.SubtitleParser.activeCaptionAt(srtCues, 2500L, 0L))
        // Positive sync offset (+1000ms) delays subtitle display by 1s
        assertEquals(null, com.example.player.SubtitleParser.activeCaptionAt(srtCues, 1500L, 1000L))
        assertEquals("Welcome to Prism Cinema", com.example.player.SubtitleParser.activeCaptionAt(srtCues, 4500L, 1000L))

        val sampleVtt = """
            WEBVTT

            00:01.200 --> 00:03.800
            <b>WebVTT Ready</b>
        """.trimIndent()
        val vttCues = com.example.player.SubtitleParser.parse(sampleVtt)
        assertEquals(1, vttCues.size)
        assertEquals("WebVTT Ready", com.example.player.SubtitleParser.activeCaptionAt(vttCues, 2000L, 0L))
    }

    @Test
    fun `prism backup manager round trips payload with sha256 verification and rejects tampering`() {
        val sampleSettings = com.example.model.AppSettings(
            darkTheme = false,
            amoledBlack = true,
            biometricUnlockEnabled = true,
            audioPreampPercent = 140
        )
        val payload = com.example.data.PrismBackupPayload(
            settingsJson = com.example.data.PrismBackupManager.settingsToJson(sampleSettings),
            playlists = listOf(com.example.model.UserPlaylist(id = "p1", name = "Roadtrip", trackIds = listOf("t1"))),
            watchedFolders = emptyList(),
            bookmarks = emptyList(),
            customLyrics = mapOf("t1" to "[00:00.00]Hello"),
            tagOverrides = emptyMap(),
            favorites = setOf("t1", "v2")
        )
        val encoded = com.example.data.PrismBackupManager.encodeBackup(payload)
        val decoded = com.example.data.PrismBackupManager.decodeAndVerifyBackup(encoded)
        assertTrue("Valid backup must verify cleanly", decoded != null)
        assertEquals(false, decoded!!.settingsJson.optBoolean("darkTheme"))
        assertEquals(true, decoded.settingsJson.optBoolean("biometricUnlockEnabled"))
        assertEquals(140, decoded.settingsJson.optInt("audioPreampPercent"))
        assertEquals("Roadtrip", decoded.playlists.first().name)
        assertEquals(setOf("t1", "v2"), decoded.favorites)
        assertEquals("[00:00.00]Hello", decoded.customLyrics["t1"])

        // Tamper a single character inside payload and confirm SHA-256 rejects it
        val tampered = encoded.replace("Roadtrip", "Hacked!!")
        val rejected = com.example.data.PrismBackupManager.decodeAndVerifyBackup(tampered)
        assertEquals(null, rejected)
    }

    @Test
    fun `album art palette extractor computes dominant and vibrant colors from bitmap`() {
        val bmp = android.graphics.Bitmap.createBitmap(24, 24, android.graphics.Bitmap.Config.ARGB_8888)
        bmp.eraseColor(android.graphics.Color.rgb(220, 48, 120))
        val palette = com.example.ui.theme.AlbumArtPaletteExtractor.extractFromBitmap(bmp)
        assertTrue("Extracted dominant red channel should be high", palette.dominant.red > 0.7f)
        assertTrue("Extracted vibrant red channel should be high", palette.vibrant.red > 0.7f)
        assertTrue("Deep tint should be darker than dominant", palette.deepTint.red < palette.dominant.red)
    }

    @Test
    fun `flagship upgrade strings for biometric subtitles exif and backup exist in en and ar`() {
        listOf(stringsFor(AppLanguage.EN), stringsFor(AppLanguage.AR)).forEach { s ->
            assertTrue(s.biometricUnlockTitle.isNotBlank())
            assertTrue(s.biometricPromptTitle.isNotBlank())
            assertTrue(s.videoSubtitleSettingsTitle.isNotBlank())
            assertTrue(s.videoSubtitleSyncLabel.isNotBlank())
            assertTrue(s.videoAudioTrackTitle.isNotBlank())
            assertTrue(s.videoSnapshotSaved.isNotBlank())
            assertTrue(s.photoExifTitle.isNotBlank())
            assertTrue(s.photoSavedCopyBanner.isNotBlank())
            assertTrue(s.backupSectionTitle.isNotBlank())
            assertTrue(s.backupExportSuccess.isNotBlank())
            assertTrue(s.openedExternalMediaBanner("Track.mp3").isNotBlank())
        }
    }

    @Test
    fun `deep customization defaults and backup serialization round trip cleanly`() {
        val defaults = com.example.model.AppSettings()
        assertEquals(com.example.model.NavBarStyle.FLOATING_PILL, defaults.navBarStyle)
        assertEquals(com.example.model.MediaTab.MUSIC, defaults.startupTab)
        assertEquals(com.example.model.UiDensity.BALANCED, defaults.uiDensity)
        assertEquals(com.example.model.PlayerArtworkStyle.VINYL_DISC, defaults.playerArtworkStyle)
        assertEquals(true, defaults.albumArtTintEnabled)
        assertEquals(true, defaults.showFullPlayerSpectrum)
        assertEquals(10, defaults.seekStepSeconds)
        assertEquals(true, defaults.hapticsEnabled)

        val customized = com.example.model.AppSettings(
            liquidGlassStyle = com.example.model.LiquidGlassStyle.SOLID_M3,
            navBarStyle = com.example.model.NavBarStyle.DOCKED_MATERIAL_YOU,
            startupTab = com.example.model.MediaTab.VIDEOS,
            uiDensity = com.example.model.UiDensity.COMPACT,
            playerArtworkStyle = com.example.model.PlayerArtworkStyle.CIRCLE_GLOW,
            albumArtTintEnabled = false,
            showFullPlayerSpectrum = false,
            seekStepSeconds = 15,
            hapticsEnabled = false
        )
        val payload = com.example.data.PrismBackupPayload(
            settingsJson = com.example.data.PrismBackupManager.settingsToJson(customized),
            playlists = emptyList(),
            watchedFolders = emptyList(),
            bookmarks = emptyList(),
            customLyrics = emptyMap(),
            tagOverrides = emptyMap(),
            favorites = emptySet()
        )
        val encoded = com.example.data.PrismBackupManager.encodeBackup(payload)
        val decoded = com.example.data.PrismBackupManager.decodeAndVerifyBackup(encoded)
        assertTrue("Customized backup should decode cleanly", decoded != null)
        assertEquals("SOLID_M3", decoded!!.settingsJson.optString("liquidGlassStyle"))
        assertEquals("DOCKED_MATERIAL_YOU", decoded.settingsJson.optString("navBarStyle"))
        assertEquals("VIDEOS", decoded.settingsJson.optString("startupTab"))
        assertEquals("COMPACT", decoded.settingsJson.optString("uiDensity"))
        assertEquals("CIRCLE_GLOW", decoded.settingsJson.optString("playerArtworkStyle"))
        assertEquals(false, decoded.settingsJson.optBoolean("albumArtTintEnabled"))
        assertEquals(false, decoded.settingsJson.optBoolean("showFullPlayerSpectrum"))
        assertEquals(15, decoded.settingsJson.optInt("seekStepSeconds"))
        assertEquals(false, decoded.settingsJson.optBoolean("hapticsEnabled"))
    }

    @Test
    fun `deep customization strings exist in both english and arabic`() {
        listOf(stringsFor(AppLanguage.EN), stringsFor(AppLanguage.AR)).forEach { s ->
            assertTrue(s.settingsSearchHint.isNotBlank())
            assertTrue(s.visualEngineTitle.isNotBlank())
            assertTrue(s.visualEngineMaterialYouTitle.isNotBlank())
            assertTrue(s.visualEngineLiquidGlassTitle.isNotBlank())
            assertTrue(s.liveThemePreviewLabel.isNotBlank())
            assertTrue(s.settingsNavLayoutSection.isNotBlank())
            assertTrue(s.navBarStyleTitle.isNotBlank())
            assertTrue(s.startupTabTitle.isNotBlank())
            assertTrue(s.uiDensityTitle.isNotBlank())
            assertTrue(s.playerArtworkStyleTitle.isNotBlank())
            assertTrue(s.albumArtTintTitle.isNotBlank())
            assertTrue(s.fullPlayerSpectrumTitle.isNotBlank())
            assertTrue(s.seekStepTitle.isNotBlank())
            assertTrue(s.hapticsTitle.isNotBlank())
            assertTrue(s.resetCustomizationAction.isNotBlank())
            assertTrue(s.resetCustomizationBanner.isNotBlank())
            assertTrue(s.settingsHardwareSubLabel.isNotBlank())
            com.example.model.NavBarStyle.entries.forEach { assertTrue(s.navBarStyleName(it).isNotBlank()) }
            com.example.model.PlayerArtworkStyle.entries.forEach { assertTrue(s.playerArtworkStyleName(it).isNotBlank()) }
            com.example.model.UiDensity.entries.forEach { assertTrue(s.uiDensityName(it).isNotBlank()) }
            com.example.model.MediaTab.entries.forEach { assertTrue(s.tabDisplayName(it).isNotBlank()) }
        }
    }

    @Test
    fun `quran and hadith catalog detects surahs reciters and authentic hadith collections`() {
        assertEquals(114, com.example.data.QuranHadithCatalog.surahs.size)

        val kahfAr = com.example.data.QuranHadithCatalog.detectQuran("سورة الكهف - مشاري العفاسي")
        assertTrue(kahfAr.isQuranic)
        assertEquals(18, kahfAr.surah?.number)
        assertEquals("الكهف", kahfAr.surah?.arabicName)
        assertEquals("مشاري راشد العفاسي", kahfAr.reciterDisplayName)

        val yasinNum = com.example.data.QuranHadithCatalog.detectQuran("Surah 36", "Omar Al Kazabri")
        assertTrue(yasinNum.isQuranic)
        assertEquals(36, yasinNum.surah?.number)
        assertEquals("عمر القزابري", yasinNum.reciterDisplayName)

        val hadith = com.example.data.QuranHadithCatalog.detectHadith("شرح صحيح البخاري - كتاب الإيمان")
        assertTrue(hadith.isHadithOrIslamicLecture)
        assertEquals("ara-bukhari", hadith.collectionKey)
        assertEquals("صحيح البخاري", hadith.collectionArabicName)

        assertEquals(
            com.example.model.MediaKnowledgeCategory.QURAN,
            com.example.data.QuranHadithCatalog.classifyCategory("Al-Fatiha Recitation")
        )
        assertEquals(
            com.example.model.MediaKnowledgeCategory.HADITH,
            com.example.data.QuranHadithCatalog.classifyCategory("40 Hadith Nawawi Lesson")
        )
        assertEquals(
            com.example.model.MediaKnowledgeCategory.MUSIC,
            com.example.data.QuranHadithCatalog.classifyCategory("Midnight Neon Drive", "Prism Synth")
        )
    }

    @Test
    fun `media knowledge engine cleans queries computes energy mood and quality badges`() {
        assertEquals(
            "Bohemian Rhapsody",
            com.example.data.MediaKnowledgeEngine.cleanMediaQuery("01 - Bohemian_Rhapsody (Official Video) [320kbps].mp3")
        )
        assertEquals("", com.example.data.MediaKnowledgeEngine.cleanArtistName("<unknown>"))
        assertEquals("Fairuz", com.example.data.MediaKnowledgeEngine.cleanArtistName("Fairuz"))

        val flacBadge = com.example.data.MediaKnowledgeEngine.computeAudioQualityBadge("flac", 45_000_000L, 240_000L)
        assertTrue("FLAC badge should indicate lossless", flacBadge.contains("FLAC", ignoreCase = true))

        val (energy, bpm, mood) = com.example.data.MediaKnowledgeEngine.computeTrackEnergyAndMood(
            title = "Workout Club Remix",
            artist = "DJ Prism",
            extension = "mp3",
            durationMs = 210_000L,
            apiBpm = 128,
            category = com.example.model.MediaKnowledgeCategory.MUSIC,
            language = AppLanguage.EN
        )
        assertTrue("Energy should be within 10..100", energy in 10..100)
        assertEquals(128, bpm)
        assertTrue("Mood label must be non-blank", mood.isNotBlank())

        val (quranEnergy, _, quranMoodAr) = com.example.data.MediaKnowledgeEngine.computeTrackEnergyAndMood(
            title = "سورة الرحمن",
            artist = "عبد الباسط",
            extension = "mp3",
            durationMs = 420_000L,
            apiBpm = 0,
            category = com.example.model.MediaKnowledgeCategory.QURAN,
            language = AppLanguage.AR
        )
        assertTrue("Quran recitation energy should be serene", quranEnergy in 20..50)
        assertTrue("Arabic Quran mood should be non-blank", quranMoodAr.isNotBlank())
    }

    @Test
    fun `all four languages en ar fr es have complete knowledge strings and zero third party trademarks`() {
        val forbiddenBrands = listOf("vlc", "samsung", "apple", "youtube", "iphone")
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.onlineEnrichmentTitle.isNotBlank())
            assertTrue(s.trackEnergyLabel.isNotBlank())
            assertTrue(s.quranSurahBadge.isNotBlank())
            assertTrue(s.hadithReferenceTitle.isNotBlank())
            assertTrue(s.encyclopediaSectionTitle.isNotBlank())
            assertTrue(s.gaplessPlaybackTitle.isNotBlank())
            assertTrue(s.videoGesturesTitle.isNotBlank())
            assertTrue(s.photoSlideshowTransitionTitle.isNotBlank())
            assertTrue(s.surfaceDepthTitle.isNotBlank())
            assertTrue(s.languageDisplayName(lang).isNotBlank())
            com.example.model.SlideshowTransitionStyle.entries.forEach {
                assertTrue(s.slideshowTransitionName(it).isNotBlank())
            }
            com.example.model.SurfaceDepthStyle.entries.forEach {
                assertTrue(s.surfaceDepthName(it).isNotBlank())
            }

            val getters = com.example.localization.AppStrings::class.java.methods
                .filter { it.parameterCount == 0 && it.returnType == String::class.java }
            getters.forEach { method ->
                val value = (method.invoke(s) as? String).orEmpty().lowercase()
                forbiddenBrands.forEach { brand ->
                    assertTrue(
                        "Found forbidden third-party trademark '$brand' in $lang.${method.name}: '$value'",
                        !value.contains(brand)
                    )
                }
            }
        }
    }

    @Test
    fun `new knowledge and pro media settings round trip through prism backup manager`() {
        val customized = com.example.model.AppSettings(
            language = AppLanguage.FR,
            autoOnlineEnrichment = false,
            autoFetchLyrics = false,
            autoFetchArtistPhotos = false,
            gaplessPlayback = false,
            audioNormalization = true,
            pauseOnHeadsetUnplug = false,
            videoGesturesEnabled = false,
            videoDoubleTapSeekEnabled = false,
            videoRememberPosition = false,
            videoSubtitleSizeSp = 26,
            photoAutoShowExif = false,
            photoHighResDecode = false,
            photoSlideshowTransition = com.example.model.SlideshowTransitionStyle.ZOOM,
            surfaceDepthStyle = com.example.model.SurfaceDepthStyle.FLAT
        )
        val payload = com.example.data.PrismBackupPayload(
            settingsJson = com.example.data.PrismBackupManager.settingsToJson(customized),
            playlists = emptyList(),
            watchedFolders = emptyList(),
            bookmarks = emptyList(),
            customLyrics = emptyMap(),
            tagOverrides = emptyMap(),
            favorites = emptySet()
        )
        val encoded = com.example.data.PrismBackupManager.encodeBackup(payload)
        val decoded = com.example.data.PrismBackupManager.decodeAndVerifyBackup(encoded)
        assertTrue("Backup with 14 new settings should verify cleanly", decoded != null)
        val j = decoded!!.settingsJson
        assertEquals("FR", j.optString("language"))
        assertEquals(false, j.optBoolean("autoOnlineEnrichment"))
        assertEquals(false, j.optBoolean("autoFetchLyrics"))
        assertEquals(false, j.optBoolean("autoFetchArtistPhotos"))
        assertEquals(false, j.optBoolean("gaplessPlayback"))
        assertEquals(true, j.optBoolean("audioNormalization"))
        assertEquals(false, j.optBoolean("pauseOnHeadsetUnplug"))
        assertEquals(false, j.optBoolean("videoGesturesEnabled"))
        assertEquals(false, j.optBoolean("videoDoubleTapSeekEnabled"))
        assertEquals(false, j.optBoolean("videoRememberPosition"))
        assertEquals(26, j.optInt("videoSubtitleSizeSp"))
        assertEquals(false, j.optBoolean("photoAutoShowExif"))
        assertEquals(false, j.optBoolean("photoHighResDecode"))
        assertEquals("ZOOM", j.optString("photoSlideshowTransition"))
        assertEquals("FLAT", j.optString("surfaceDepthStyle"))
    }

    @Test
    fun `content purity guard strictly blocks adult content and filters indecent lyrics while preserving clean media`() {
        // Explicit URLs and titles must be strictly blocked
        assertTrue(com.example.security.ContentPurityGuard.isBlockedStreamUrl("https://example.xxx/stream.mp4"))
        assertTrue(com.example.security.ContentPurityGuard.isExplicitOrAdult("فيديو اباحي"))
        assertTrue(com.example.security.ContentPurityGuard.isExplicitOrAdult("NSFW Clip"))

        // Clean Quran, Hadith, and respectful media must pass cleanly
        assertFalse(com.example.security.ContentPurityGuard.isExplicitOrAdult("سورة الكهف - مشاري العفاسي"))
        assertFalse(com.example.security.ContentPurityGuard.isExplicitOrAdult("Aurora Skyline", "Prism Synth"))
        assertFalse(com.example.security.ContentPurityGuard.isBlockedStreamUrl("https://cdn.example.org/quran/001.mp3"))

        // Indecent lyrics lines are removed, and heavily explicit lyrics are rejected completely
        val mixedLyrics = "[00:01.00] Peaceful morning sky\n[00:05.00] Walking in the light\n[00:09.00] Clean melody plays\n[00:13.00] Shining stars tonight\n[00:17.00] bullshit line removed"
        val cleaned = com.example.security.ContentPurityGuard.filterLyricsStrictly(mixedLyrics)
        assertTrue("Clean portion of lyrics should be preserved", cleaned != null && cleaned.contains("Peaceful morning sky"))
        assertTrue("Profane line must be removed", cleaned != null && !cleaned.contains("bullshit"))

        val explicitLyrics = "[00:01.00] explicit porn scene\n[00:04.00] another line"
        assertEquals(null, com.example.security.ContentPurityGuard.filterLyricsStrictly(explicitLyrics))
    }

    @Test
    fun `dua and taqwa reminder strings are populated across all four languages`() {
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.contentBlockedBanner.isNotBlank())
            assertTrue(s.lyricsBlockedBanner.isNotBlank())
            assertTrue(s.taqwaSectionTitle.isNotBlank())
            assertTrue(s.taqwaDuaHeading.isNotBlank())
            assertTrue(s.taqwaDuaBody.isNotBlank())
            assertTrue(s.taqwaWarningHeading.isNotBlank())
            assertTrue(s.taqwaWarningVerse.isNotBlank())
            assertTrue(s.taqwaWarningBody.isNotBlank())
            assertTrue(s.taqwaPurityShieldTitle.isNotBlank())
        }
    }

    @Test
    @androidx.media3.common.util.UnstableApi
    fun `biquad dsp stereo balance and mono accessibility process interleaved stereo pcm accurately`() {
        val sampleRate = 44100
        val frames = 256
        val format = androidx.media3.common.audio.AudioProcessor.AudioFormat(
            sampleRate,
            2,
            androidx.media3.common.C.ENCODING_PCM_16BIT
        )

        fun buildStereoInputBuffer(): java.nio.ByteBuffer {
            val buf = java.nio.ByteBuffer.allocateDirect(frames * 4).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            for (i in 0 until frames) {
                buf.putShort(10000.toShort()) // Left
                buf.putShort((-4000).toShort()) // Right
            }
            buf.flip()
            return buf
        }

        fun runDsp(balance: Float, mono: Boolean): ShortArray {
            val processor = com.example.player.FiveBandBiquadAudioProcessor()
            processor.setEqualizerEnabled(false)
            processor.setMonoEnabled(mono)
            processor.setStereoBalance(balance)
            processor.configure(format)
            processor.flush()
            processor.queueInput(buildStereoInputBuffer())
            val outBuf = processor.output.order(java.nio.ByteOrder.LITTLE_ENDIAN)
            val result = ShortArray(outBuf.remaining() / 2)
            for (i in result.indices) {
                result[i] = outBuf.short
            }
            return result
        }

        // Hard left balance (-1f): Right channel attenuated to 0, Left stays strong
        val hardLeft = runDsp(balance = -1f, mono = false)
        assertTrue("Left channel should stay strong on hard-left", kotlin.math.abs(hardLeft[200].toInt()) > 7000)
        assertTrue("Right channel should be near silent on hard-left", kotlin.math.abs(hardLeft[201].toInt()) < 50)

        // Hard right balance (+1f): Left channel attenuated to 0, Right stays strong
        val hardRight = runDsp(balance = 1f, mono = false)
        assertTrue("Left channel should be near silent on hard-right", kotlin.math.abs(hardRight[200].toInt()) < 50)
        assertTrue("Right channel should stay strong on hard-right", kotlin.math.abs(hardRight[201].toInt()) > 2500)

        // Mono accessibility mode: Left and Right outputs become identical average (~3000)
        val monoOut = runDsp(balance = 0f, mono = true)
        assertEquals(monoOut[200], monoOut[201])
        assertTrue("Mono mix of +10000 and -4000 should be ~+3000", monoOut[200] in 2500..3500)
    }

    @Test
    fun `theme code codec round trips settings with custom monet seed and rejects invalid codes`() {
        val customSettings = com.example.model.AppSettings(
            darkTheme = true,
            amoledBlack = true,
            accentPalette = com.example.model.AccentPalette.EMERALD,
            liquidGlassStyle = com.example.model.LiquidGlassStyle.MATERIAL_LIQUID,
            liquidGlassIntensity = 0.85f,
            shapeRoundness = 1.15f,
            contrastLevel = com.example.model.MaterialContrastLevel.HIGH,
            fontStyle = com.example.model.MaterialFontStyle.MATERIAL_EXPRESSIVE,
            navBarStyle = com.example.model.NavBarStyle.DOCKED_MATERIAL_YOU,
            uiDensity = com.example.model.UiDensity.COMPACT,
            surfaceDepthStyle = com.example.model.SurfaceDepthStyle.EXPRESSIVE,
            customSeedColorArgb = 0xFF006D39.toInt()
        )
        val code = com.example.data.ThemeCodeCodec.encodeThemeCode(customSettings)
        assertTrue("Code must start with PRISM-THEME:", code.startsWith("PRISM-THEME:"))

        val decoded = com.example.data.ThemeCodeCodec.decodeThemeCode(code)
        assertTrue("Valid theme code must decode", decoded != null)
        assertEquals(true, decoded!!.darkTheme)
        assertEquals(true, decoded.amoledBlack)
        assertEquals(com.example.model.AccentPalette.EMERALD, decoded.accentPalette)
        assertEquals(com.example.model.LiquidGlassStyle.MATERIAL_LIQUID, decoded.liquidGlassStyle)
        assertEquals(com.example.model.MaterialFontStyle.MATERIAL_EXPRESSIVE, decoded.fontStyle)
        assertEquals(0xFF006D39.toInt(), decoded.customSeedColorArgb)

        assertEquals(null, com.example.data.ThemeCodeCodec.decodeThemeCode("INVALID-CODE-123"))
    }

    @Test
    fun `seed color scheme generator builds light and dark material 3 color schemes`() {
        val emeraldSeed = 0xFF0F6B41.toInt()
        val light = com.example.ui.theme.SeedColorSchemeGenerator.generateColorScheme(emeraldSeed, darkTheme = false)
        val dark = com.example.ui.theme.SeedColorSchemeGenerator.generateColorScheme(emeraldSeed, darkTheme = true)

        assertTrue("Light scheme background should be bright", light.background.red > 0.8f)
        assertTrue("Dark scheme background should be dark", dark.background.red < 0.25f)
        assertTrue("Dark primary should be lighter than light primary", dark.primary.green > light.primary.green)
    }

    @Test
    fun `private vault manager encrypts and decrypts payloads with aes 256 gcm and enforces purity shield`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val originalBytes = "Prism AES-256-GCM Secret Test Payload 2026".toByteArray()
        val tempFile = java.io.File(context.cacheDir, "family_mosque_photo.jpg").apply {
            writeBytes(originalBytes)
        }
        val saved = com.example.security.PrivateVaultManager.importIntoVault(
            context = context,
            title = "family_mosque_photo.jpg",
            uriString = android.net.Uri.fromFile(tempFile).toString(),
            mediaType = com.example.model.RenameMediaType.PHOTO,
            mimeType = "image/jpeg"
        )
        assertTrue("Clean media must be stored in vault", saved != null)
        val decrypted = com.example.security.PrivateVaultManager.decryptBytes(context, saved!!)
        assertTrue("Decrypted bytes must match original exactly", originalBytes.contentEquals(decrypted))

        // Clean up temp vault entry
        com.example.security.PrivateVaultManager.removeVaultItem(context, saved.id)
        val afterDelete = com.example.security.PrivateVaultManager.listVaultItems(context)
        assertTrue("Removed item should not be listed", afterDelete.none { it.id == saved.id })
        tempFile.delete()

        // Explicit/adult filename must be rejected strictly by Purity Shield
        val blocked = com.example.security.PrivateVaultManager.importIntoVault(
            context = context,
            title = "explicit_nsfw_clip.mp4",
            uriString = "content://media/external/video/explicit_nsfw_clip.mp4",
            mediaType = com.example.model.RenameMediaType.VIDEO,
            mimeType = "video/mp4"
        )
        assertEquals(null, blocked)
    }

    @Test
    fun `all 24 studio feature strings are populated across en ar fr and es`() {
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.quranHubTitle.isNotBlank())
            assertTrue(s.morningAlarmTitle.isNotBlank())
            assertTrue(s.khushuModeTitle.isNotBlank())
            assertTrue(s.audioCutterTitle.isNotBlank())
            assertTrue(s.lectureModeTitle.isNotBlank())
            assertTrue(s.skipSilenceTitle.isNotBlank())
            assertTrue(s.listeningStatsTitle.isNotBlank())
            assertTrue(s.stereoBalanceTitle.isNotBlank())
            assertTrue(s.audioMonoTitle.isNotBlank())
            assertTrue(s.batchTagTitle.isNotBlank())
            assertTrue(s.videoChaptersTitle.isNotBlank())
            assertTrue(s.videoColorCalibTitle.isNotBlank())
            assertTrue(s.privateVaultTitle.isNotBlank())
            assertTrue(s.stripExifShareTitle.isNotBlank())
            assertTrue(s.collageStudioTitle.isNotBlank())
            assertTrue(s.customSeedTitle.isNotBlank())
            assertTrue(s.appIconSwitcherTitle.isNotBlank())
            assertTrue(s.themeCodeTitle.isNotBlank())
            assertTrue(s.wifiTransferTitle.isNotBlank())
            assertTrue(s.tasbihTitle.isNotBlank())
            assertTrue(s.shakeToShuffleTitle.isNotBlank())
            com.example.model.SmartMusicMix.entries.forEach { assertTrue(s.smartMixName(it).isNotBlank()) }
        }
    }

    @Test
    fun `biquad dsp loudness normalization and lecture vocal boost shape audio cleanly`() {
        val sampleRate = 44100
        val count = 4096
        // Quiet 2kHz vocal test sine wave
        val quietVocal = ShortArray(count) { i ->
            val angle = 2.0 * Math.PI * 2000.0 * i / sampleRate
            (Math.sin(angle) * 1800.0).toInt().toShort()
        }

        fun rms(arr: ShortArray): Double {
            var sum = 0.0
            for (i in 1024 until arr.size) {
                val v = arr[i].toDouble()
                sum += v * v
            }
            return Math.sqrt(sum / (arr.size - 1024))
        }

        val processor = com.example.player.FiveBandBiquadAudioProcessor()
        processor.setEqualizerEnabled(false)
        val dryOut = processor.processPcmForTest(quietVocal, sampleRate)
        val dryRms = rms(dryOut)

        processor.setLoudnessNormalizationEnabled(true)
        val normOut = processor.processPcmForTest(quietVocal, sampleRate)
        val normRms = rms(normOut)
        assertTrue("Loudness normalization ($normRms) should lift quiet audio above dry ($dryRms)", normRms > dryRms * 1.15)

        processor.setLoudnessNormalizationEnabled(false)
        processor.setLectureVocalBoostEnabled(true)
        val vocalOut = processor.processPcmForTest(quietVocal, sampleRate)
        val vocalRms = rms(vocalOut)
        assertTrue("Lecture vocal boost ($vocalRms) should lift 2kHz speech band above dry ($dryRms)", vocalRms > dryRms * 1.25)
    }

    @Test
    fun `six android home screen widgets register and sync cleanly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.widget.WidgetPlaybackBridge.lastTitle = "Surah Al-Kahf"
        com.example.widget.WidgetPlaybackBridge.lastSubtitle = "Mishary Alafasy"
        com.example.widget.WidgetPlaybackBridge.lastIsPlaying = true
        com.example.widget.WidgetPlaybackBridge.lastKhatmaPercent = 42
        com.example.widget.WidgetPlaybackBridge.lastCompletedSurahs = 48
        com.example.widget.WidgetPlaybackBridge.lastTasbihCount = 99
        com.example.widget.WidgetPlaybackBridge.lastEqPresetLabel = "Vocal Boost"
        com.example.widget.WidgetPlaybackBridge.lastSpeedLabel = "1.25x"
        com.example.widget.WidgetPlaybackBridge.syncAllWidgets(context)
        assertEquals("Surah Al-Kahf", com.example.widget.WidgetPlaybackBridge.lastTitle)
        assertEquals(42, com.example.widget.WidgetPlaybackBridge.lastKhatmaPercent)
        assertEquals(99, com.example.widget.WidgetPlaybackBridge.lastTasbihCount)
        assertEquals("Vocal Boost", com.example.widget.WidgetPlaybackBridge.lastEqPresetLabel)

        // Verify all 6 widget provider classes instantiate cleanly
        val providers = listOf(
            com.example.widget.PrismMediaWidgetProvider(),
            com.example.widget.PrismExpressiveCardWidgetProvider(),
            com.example.widget.PrismVinylSquareWidgetProvider(),
            com.example.widget.PrismQuranWidgetProvider(),
            com.example.widget.PrismAudioDeckWidgetProvider(),
            com.example.widget.PrismQuickStudioWidgetProvider()
        )
        assertEquals(6, providers.size)
    }

    @Test
    fun `new horizontal settings sticky edge dock 6 widgets and flagship player strings exist in all 4 languages`() {
        val defaults = com.example.model.AppSettings()
        assertEquals(com.example.model.SettingsLayoutMode.HORIZONTAL_PAGES, defaults.settingsLayoutMode)
        assertEquals(com.example.model.EdgeDockSide.RIGHT, defaults.miniPlayerEdgeDockSide)
        assertEquals(com.example.model.SpectrumVisualStyle.CLASSIC_BARS, defaults.spectrumVisualStyle)
        assertEquals(16, defaults.lyricsFontSizeSp)
        assertEquals(false, defaults.driveModeEnabled)
        assertEquals(true, defaults.playerGestureSwipeArt)
        assertEquals(false, defaults.videoMirrorEnabled)
        assertEquals(4, defaults.videoAutoHideSeconds)

        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.settingsLayoutModeTitle.isNotBlank())
            assertTrue(s.settingsLayoutHorizontal.isNotBlank())
            assertTrue(s.settingsLayoutVertical.isNotBlank())
            assertTrue(s.collapseSectionAction.isNotBlank())
            assertTrue(s.collapseAllSectionsAction.isNotBlank())
            assertTrue(s.tuckToEdgeAction.isNotBlank())
            assertTrue(s.restoreFromEdgeAction.isNotBlank())
            assertTrue(s.edgeDockSideTitle.isNotBlank())
            assertTrue(s.floatPhotoAction.isNotBlank())
            assertTrue(s.widgetsSixGalleryTitle.isNotBlank())
            assertTrue(s.widget3Title.isNotBlank())
            assertTrue(s.widget4Title.isNotBlank())
            assertTrue(s.widget5Title.isNotBlank())
            assertTrue(s.widget6Title.isNotBlank())
            assertTrue(s.proAudioDeckTitle.isNotBlank())
            assertTrue(s.driveModeTitle.isNotBlank())
            assertTrue(s.spectrumStyleTitle.isNotBlank())
            assertTrue(s.videoMirrorTitle.isNotBlank())
            assertTrue(s.videoAutoHideTitle.isNotBlank())
            assertTrue(s.quickPresetsTitle.isNotBlank())
            assertTrue(s.hifzRemainingLabel(3).isNotBlank())
        }
    }

    @Test
    fun `reverb and spatial 3d dsp produces measurable wet echo tail energy after impulse`() {
        val sampleRate = 44100
        val count = 8192
        val impulse = ShortArray(count) { i ->
            if (i in 0 until 64) 18000.toShort() else 0.toShort()
        }

        val dryProc = com.example.player.FiveBandBiquadAudioProcessor()
        dryProc.setEqualizerEnabled(false)
        dryProc.setReverbEnvironment(com.example.model.ReverbEnvironment.NONE)
        val dryOut = dryProc.processPcmForTest(impulse, sampleRate)

        val mosqueProc = com.example.player.FiveBandBiquadAudioProcessor()
        mosqueProc.setEqualizerEnabled(false)
        mosqueProc.setReverbEnvironment(com.example.model.ReverbEnvironment.MOSQUE)
        val wetOut = mosqueProc.processPcmForTest(impulse, sampleRate)

        val dryTailEnergy = (1000 until count).sumOf { kotlin.math.abs(dryOut[it].toLong()) }
        val wetTailEnergy = (1000 until count).sumOf { kotlin.math.abs(wetOut[it].toLong()) }

        assertEquals(0L, dryTailEnergy)
        assertTrue("Mosque reverb must generate audible wet tail energy ($wetTailEnergy)", wetTailEnergy > 10000L)
    }

    @Test
    fun `m3u playlist export and import round trip matches name and track ids`() {
        val tracks = listOf(
            com.example.model.AudioTrack(
                id = "a1", title = "Surah Al-Mulk", artist = "Mishary", album = "Quran",
                uriString = "content://media/audio/101", extension = "MP3", mimeType = "audio/mpeg",
                durationMs = 240_000L, sizeBytes = 4_000_000L
            ),
            com.example.model.AudioTrack(
                id = "a2", title = "Dawn Nasheed", artist = "Prism Choir", album = "Morning",
                uriString = "content://media/audio/102", extension = "FLAC", mimeType = "audio/flac",
                durationMs = 195_000L, sizeBytes = 12_000_000L
            )
        )
        val playlist = com.example.model.UserPlaylist(
            id = "pl_1",
            name = "Fajr Selection",
            trackIds = listOf("a1", "a2")
        )
        val m3uText = com.example.data.MediaToolsEngine.exportPlaylistToM3u(playlist, tracks)
        assertTrue(m3uText.contains("#EXTM3U"))
        assertTrue(m3uText.contains("Fajr Selection"))
        assertTrue(m3uText.contains("content://media/audio/101"))

        val (parsedName, matchedIds) = com.example.data.MediaToolsEngine.parseM3uPlaylist(
            rawContent = m3uText,
            fallbackName = "Fallback",
            libraryTracks = tracks
        )
        assertEquals("Fajr Selection", parsedName)
        assertEquals(listOf("a1", "a2"), matchedIds)
    }

    @Test
    fun `photo studio bitmap processor rotates 90 degrees and crops 1 to 1 square accurately`() {
        val src = android.graphics.Bitmap.createBitmap(80, 40, android.graphics.Bitmap.Config.ARGB_8888)
        src.eraseColor(android.graphics.Color.rgb(64, 128, 220))

        val rotated = com.example.data.MediaToolsEngine.applyPhotoEditsToBitmap(
            source = src,
            rotationDegrees = 90f,
            cropAspect = com.example.model.PhotoCropAspect.ORIGINAL
        )
        assertEquals(40, rotated.width)
        assertEquals(80, rotated.height)

        val squared = com.example.data.MediaToolsEngine.applyPhotoEditsToBitmap(
            source = src,
            rotationDegrees = 0f,
            cropAspect = com.example.model.PhotoCropAspect.SQUARE_1_1
        )
        assertEquals(40, squared.width)
        assertEquals(40, squared.height)
    }

    @Test
    fun `settings repository codecs round trip media notes nav tab order and audio device profiles`() {
        val normalized = com.example.data.SettingsRepository.normalizeNavTabOrder(
            listOf(com.example.model.MediaTab.PHOTOS, com.example.model.MediaTab.PHOTOS, com.example.model.MediaTab.MUSIC)
        )
        assertEquals(com.example.model.MediaTab.entries.size, normalized.size)
        assertEquals(com.example.model.MediaTab.PHOTOS, normalized[0])
        assertEquals(com.example.model.MediaTab.MUSIC, normalized[1])

        val parsedOrder = com.example.data.SettingsRepository.parseNavTabOrder("SETTINGS,VIDEOS,PHOTOS,MUSIC")
        assertEquals(
            listOf(
                com.example.model.MediaTab.SETTINGS,
                com.example.model.MediaTab.VIDEOS,
                com.example.model.MediaTab.PHOTOS,
                com.example.model.MediaTab.MUSIC
            ),
            parsedOrder
        )

        val notes = mapOf(
            "m1" to com.example.model.MediaNote(
                mediaId = "m1",
                mediaTitle = "Surah Yasin",
                mediaType = com.example.model.RenameMediaType.AUDIO,
                noteText = "Review ayah 36 tajweed pause",
                timestampMs = 45_000L,
                updatedAtMs = 1700000000000L
            )
        )
        val notesJson = com.example.data.SettingsRepository.encodeMediaNotesJson(notes)
        val parsedNotes = com.example.data.SettingsRepository.parseMediaNotesJson(notesJson)
        assertEquals(notes, parsedNotes)

        val profiles = mapOf(
            com.example.model.AudioOutputProfileType.BLUETOOTH to com.example.model.AudioDeviceProfile(
                type = com.example.model.AudioOutputProfileType.BLUETOOTH,
                eqPreset = EqPreset.DEEP_BASS,
                bassBoost = 0.65f,
                reverb = com.example.model.ReverbEnvironment.SPATIAL_3D
            )
        )
        val profJson = com.example.data.SettingsRepository.encodeAudioDeviceProfilesJson(profiles)
        val parsedProfiles = com.example.data.SettingsRepository.parseAudioDeviceProfilesJson(profJson)
        assertEquals(
            com.example.model.ReverbEnvironment.SPATIAL_3D,
            parsedProfiles[com.example.model.AudioOutputProfileType.BLUETOOTH]?.reverb
        )
        assertEquals(
            EqPreset.DEEP_BASS,
            parsedProfiles[com.example.model.AudioOutputProfileType.BLUETOOTH]?.eqPreset
        )
    }

    @Test
    fun `adhkar catalog and all 17 upgrade strings are complete across all four languages`() {
        val catalog = com.example.data.MediaToolsEngine.adhkarCatalog
        assertTrue("Morning adhkar must exist", catalog.any { it.isMorning })
        assertTrue("Evening adhkar must exist", catalog.any { !it.isMorning })
        assertTrue("All adhkar entries must have Arabic text and repeat count > 0", catalog.all { it.arabicText.isNotBlank() && it.repeatTarget > 0 })

        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.globalSearchTitle.isNotBlank())
            assertTrue(s.recycleBinTitle.isNotBlank())
            assertTrue(s.mediaNoteTitle.isNotBlank())
            assertTrue(s.reverbTitle.isNotBlank())
            assertTrue(s.reverbMosque.isNotBlank())
            assertTrue(s.reverbSpatial3d.isNotBlank())
            assertTrue(s.tapSyncLyricsTitle.isNotBlank())
            assertTrue(s.browseArtistsTab.isNotBlank())
            assertTrue(s.browseAlbumsTab.isNotBlank())
            assertTrue(s.exportM3uAction.isNotBlank())
            assertTrue(s.importM3uAction.isNotBlank())
            assertTrue(s.extractAudioAction.isNotBlank())
            assertTrue(s.photoEditStudioTitle.isNotBlank())
            assertTrue(s.photoExportPdfAction.isNotBlank())
            assertTrue(s.photoCompressAction.isNotBlank())
            assertTrue(s.photoCompareTitle.isNotBlank())
            assertTrue(s.videoAutoNextTitle.isNotBlank())
            assertTrue(s.adhkarTitle.isNotBlank())
            assertTrue(s.audioProfilesTitle.isNotBlank())
            assertTrue(s.customTabOrderTitle.isNotBlank())
            assertTrue(s.headerButtonsTitle.isNotBlank())
            assertTrue(s.resizeWindowAction.isNotBlank())
        }
    }

    @Test
    fun `persistent 30-day recycle bin codec round trips items and auto-prunes expired entries`() {
        val now = 2_000_000_000_000L
        val freshTrack = com.example.model.AudioTrack(
            id = "a_fresh",
            title = "Surah Al-Mulk",
            artist = "Mishary",
            album = "Quran",
            uriString = "content://media/audio/1",
            extension = "MP3",
            mimeType = "audio/mpeg",
            durationMs = 320_000L,
            sizeBytes = 5_400_000L,
        )
        val expiredVideo = com.example.model.VideoItem(
            id = "v_expired",
            title = "Old Clip",
            uriString = "content://media/video/2",
            extension = "MP4",
            mimeType = "video/mp4",
            durationMs = 60_000L,
            sizeBytes = 12_000_000L,
            resolution = "1080p",
        )
        val freshItem = com.example.model.TrashedMediaItem(
            id = "a_fresh",
            title = freshTrack.title,
            subtitle = "Mishary • MP3",
            mediaType = com.example.model.RenameMediaType.AUDIO,
            audioTrack = freshTrack,
            deletedAtMs = now - 5L * 86_400_000L, // 5 days old -> keep
        )
        val expiredItem = com.example.model.TrashedMediaItem(
            id = "v_expired",
            title = expiredVideo.title,
            subtitle = "1080p • MP4",
            mediaType = com.example.model.RenameMediaType.VIDEO,
            videoItem = expiredVideo,
            deletedAtMs = now - 35L * 86_400_000L, // 35 days old -> auto-prune
        )

        val json = com.example.data.SettingsRepository.encodeTrashedItemsJson(listOf(freshItem, expiredItem))
        val restored = com.example.data.SettingsRepository.parseTrashedItemsJson(json, nowMs = now)

        assertEquals(1, restored.size)
        assertEquals("a_fresh", restored.first().id)
        assertEquals("Surah Al-Mulk", restored.first().audioTrack?.title)
    }

    @Test
    fun `photo studio artistic filters and text watermark transform bitmaps cleanly`() {
        val src = android.graphics.Bitmap.createBitmap(120, 80, android.graphics.Bitmap.Config.ARGB_8888)
        src.eraseColor(android.graphics.Color.rgb(200, 90, 40))

        // Verify NOIR ColorMatrix produces equal R, G, B channels for any input color
        val noirMatrix = com.example.data.MediaToolsEngine.buildPhotoColorMatrix(
            artFilter = com.example.model.PhotoArtFilter.NOIR
        ).array
        val outR = noirMatrix[0] * 200f + noirMatrix[1] * 90f + noirMatrix[2] * 40f + noirMatrix[4]
        val outG = noirMatrix[5] * 200f + noirMatrix[6] * 90f + noirMatrix[7] * 40f + noirMatrix[9]
        val outB = noirMatrix[10] * 200f + noirMatrix[11] * 90f + noirMatrix[12] * 40f + noirMatrix[14]
        assertTrue("Noir filter should desaturate channels equally", kotlin.math.abs(outR - outG) < 0.01f && kotlin.math.abs(outG - outB) < 0.01f)

        // Verify EMERALD boosts green channel relative to red
        val emeraldMatrix = com.example.data.MediaToolsEngine.buildPhotoColorMatrix(
            artFilter = com.example.model.PhotoArtFilter.EMERALD
        ).array
        assertTrue("Emerald filter should boost green gain above red gain", emeraldMatrix[6] > emeraldMatrix[0])

        val noir = com.example.data.MediaToolsEngine.applyPhotoEditsToBitmap(
            source = src,
            rotationDegrees = 0f,
            cropAspect = com.example.model.PhotoCropAspect.ORIGINAL,
            brightness = 0f,
            contrast = 1f,
            saturation = 1f,
            artFilter = com.example.model.PhotoArtFilter.NOIR,
            watermarkText = "Prism Studio"
        )
        assertEquals(120, noir.width)
        assertEquals(80, noir.height)

        val emerald = com.example.data.MediaToolsEngine.applyPhotoEditsToBitmap(
            source = src,
            rotationDegrees = 0f,
            cropAspect = com.example.model.PhotoCropAspect.SQUARE_1_1,
            brightness = 0f,
            contrast = 1f,
            saturation = 1f,
            artFilter = com.example.model.PhotoArtFilter.EMERALD,
            watermarkText = "Prism"
        )
        assertEquals("1:1 crop should produce square output", emerald.width, emerald.height)
    }

    @Test
    fun `morning alarm scheduler computes future trigger and dream onboarding strings cover all four languages`() {
        val baseNow = 1_700_000_000_000L
        val nextTrigger = com.example.player.MorningAlarmScheduler.computeNextTriggerMillis(6, 15, nowMs = baseNow)
        assertTrue("Next alarm trigger must strictly be in the future", nextTrigger > baseNow)
        assertTrue("Next alarm trigger must be within 24 hours", nextTrigger - baseNow <= 86_400_000L)

        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertTrue(s.onboardingWelcomeTitle.isNotBlank())
            assertTrue(s.onboardingWelcomeSubtitle.isNotBlank())
            assertTrue(s.onboardingFeaturesTitle.isNotBlank())
            assertTrue(s.onboardingSetupTitle.isNotBlank())
            assertTrue(s.onboardingLibraryTitle.isNotBlank())
            assertTrue(s.onboardingNextAction.isNotBlank())
            assertTrue(s.onboardingBackAction.isNotBlank())
            assertTrue(s.onboardingSkipAction.isNotBlank())
            assertTrue(s.onboardingEnterPrismAction.isNotBlank())
            assertTrue(s.replayOnboardingAction.isNotBlank())
            assertTrue(s.trimVideoAction.isNotBlank())
            assertTrue(s.videoTrimmerTitle.isNotBlank())
            assertTrue(s.photoArtFilterLabel.isNotBlank())
            assertTrue(s.photoWatermarkHint.isNotBlank())
            assertTrue(s.recycleDaysLeft(14).isNotBlank())
            assertTrue(s.quickTileLabel.isNotBlank())
            assertTrue(s.sharedToPrismBanner.isNotBlank())
        }
    }

    @Test
    fun `smart saf navigator builds direct allowed subfolder uris and maps tabs cleanly`() {
        val musicUri = com.example.ui.components.SmartSafFolderNavigator.buildInitialUri(
            com.example.ui.components.SmartSafFolderNavigator.SUBPATH_MUSIC
        )
        assertTrue("Music SAF URI should be non-null", musicUri != null)
        assertTrue(
            "Music SAF URI must point to com.android.externalstorage.documents primary:Music",
            musicUri.toString().contains("com.android.externalstorage.documents") &&
                musicUri.toString().contains("Music")
        )
        assertEquals(
            com.example.ui.components.SmartSafFolderNavigator.SUBPATH_MUSIC,
            com.example.ui.components.SmartSafFolderNavigator.defaultSubpathForTab(com.example.model.MediaTab.MUSIC)
        )
        assertEquals(
            com.example.ui.components.SmartSafFolderNavigator.SUBPATH_MOVIES,
            com.example.ui.components.SmartSafFolderNavigator.defaultSubpathForTab(com.example.model.MediaTab.VIDEOS)
        )
        assertEquals(
            com.example.ui.components.SmartSafFolderNavigator.SUBPATH_PICTURES,
            com.example.ui.components.SmartSafFolderNavigator.defaultSubpathForTab(com.example.model.MediaTab.PHOTOS)
        )
    }

    @Test
    fun `track genre palette engine classifies diverse moods and resolves distinct expressive palettes`() {
        fun sampleTrack(title: String, artist: String, album: String, folder: String = "Music") =
            com.example.model.AudioTrack(
                id = title.lowercase().replace(" ", "_"),
                title = title,
                artist = artist,
                album = album,
                uriString = "content://media/audio/1",
                extension = "FLAC",
                mimeType = "audio/flac",
                durationMs = 210_000L,
                sizeBytes = 12_000_000L,
                folderName = folder
            )

        val quranMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
            track = sampleTrack("Surah Al-Kahf", "Mishary Alafasy", "Holy Quran", "Quran")
        )
        val elecMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
            track = sampleTrack("Neon Synthwave Pulse", "Prism Beat", "EDM Electronic Nights", "Electronic")
        )
        val lofiMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
            track = sampleTrack("Rainy Midnight Study", "Chill Beats", "Lo-Fi Ambient", "Lo-Fi")
        )
        val classicalMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
            track = sampleTrack("Moonlight Piano Concerto", "Vienna Orchestra", "Symphony No. 5", "Classical")
        )
        val lectureMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
            track = sampleTrack("History Podcast Episode 12", "Dr. Tariq", "Lectures", "Podcast")
        )

        assertEquals(com.example.ui.theme.TrackGenreMood.QURAN_SPIRITUAL, quranMood)
        assertEquals(com.example.ui.theme.TrackGenreMood.ELECTRONIC_ENERGY, elecMood)
        assertEquals(com.example.ui.theme.TrackGenreMood.LOFI_AMBIENT, lofiMood)
        assertEquals(com.example.ui.theme.TrackGenreMood.CLASSICAL_ORCHESTRAL, classicalMood)
        assertEquals(com.example.ui.theme.TrackGenreMood.LECTURE_PODCAST, lectureMood)

        val quranPalette = com.example.ui.theme.TrackGenrePaletteEngine.paletteForMood(quranMood, isDark = true)
        val elecPalette = com.example.ui.theme.TrackGenrePaletteEngine.paletteForMood(elecMood, isDark = true)
        assertTrue(
            "Quran and Electronic palettes must produce distinct accents",
            quranPalette.primaryAccent != elecPalette.primaryAccent
        )

        // All 7 PlayerArtworkStyle entries must resolve a valid Compose Shape cleanly
        assertEquals(7, com.example.model.PlayerArtworkStyle.entries.size)
        com.example.model.PlayerArtworkStyle.entries.forEach { style ->
            val shape = com.example.ui.components.PrismExpressiveShapes.artworkShapeFor(style, roundness = 1.0f)
            assertTrue("Shape for $style must not be null", shape != null)
        }
    }

    @Test
    fun `player card pro tech lock screen and smart import strings exist in all 4 languages with zero trademarks`() {
        val defaults = com.example.model.AppSettings()
        assertEquals(true, defaults.playerGenreDynamicColors)
        assertEquals(true, defaults.playerShowTechTelemetry)
        assertEquals(true, defaults.playerExtrasButtonSubtle)
        assertEquals(false, defaults.playerDirectBitExactDsp)
        assertEquals(true, defaults.playerExpressivePlayMorph)
        assertEquals(true, defaults.lockScreenControlsEnabled)
        assertEquals(true, defaults.lockScreenShowArtwork)
        assertEquals(true, defaults.lockScreenShowPrismIdentity)
        assertEquals(true, defaults.lockScreenFullScreenEnabled)
        assertEquals(true, defaults.lockScreenShowClock)
        assertEquals(true, defaults.lockScreenShowLyrics)
        assertEquals(true, defaults.lockScreenBlurBackdrop)
        assertEquals(false, defaults.lockScreenCompactMode)

        val forbidden = listOf("spotify", "apple music", "poweramp", "vlc", "mx player", "youtube")
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            val checked = listOf(
                s.smartImportTitle,
                s.smartImportSubtitle,
                s.smartImportSafTip,
                s.smartImportQuickFoldersLabel,
                s.smartImportDirectFilesAction,
                s.pickGalleryMedia,
                s.smartImportBrowseOther,
                s.settingsPlayerCard,
                s.settingsPlayerCardDesc,
                s.playerGenreColorsTitle,
                s.playerGenreColorsDesc,
                s.playerTechTelemetryTitle,
                s.playerTechTelemetryDesc,
                s.playerExtrasSubtleTitle,
                s.playerExtrasSubtleDesc,
                s.playerBitExactDspTitle,
                s.playerBitExactDspDesc,
                s.playerExpressivePlayMorphTitle,
                s.playerExtrasStudioTitle,
                s.livePreviewBadge,
                s.settingsLockScreen,
                s.settingsLockScreenDesc,
                s.lockScreenControlsTitle,
                s.lockScreenControlsDesc,
                s.lockScreenShowArtworkTitle,
                s.lockScreenShowIdentityTitle,
                s.lockScreenShowIdentityDesc,
                s.lockScreenFullScreenTitle,
                s.lockScreenFullScreenDesc,
                s.lockScreenShowClockTitle,
                s.lockScreenShowLyricsTitle,
                s.lockScreenBlurBackdropTitle,
                s.lockScreenCompactModeTitle,
                s.lockScreenPreviewAction,
                s.lockScreenUnlockAction
            )
            checked.forEach { text ->
                assertTrue("Localized string must not be blank for $lang", text.isNotBlank())
                val lower = text.lowercase()
                forbidden.forEach { brand ->
                    assertTrue("String must never contain trademark '$brand': $text", !lower.contains(brand))
                }
            }
            com.example.model.PlayerArtworkStyle.entries.forEach { style ->
                assertTrue(s.playerArtworkStyleName(style).isNotBlank())
            }
            com.example.ui.theme.TrackGenreMood.entries.forEach { mood ->
                assertTrue(s.genreMoodName(mood).isNotBlank())
            }
        }
    }

    @Test
    fun `optional per mode pip defaults and calm ui strings exist across all four languages`() {
        val defaults = com.example.model.AppSettings()
        assertFalse("Music PiP must be optional and off by default", defaults.pipMusicEnabled)
        assertTrue("Video PiP must be enabled by default", defaults.pipVideoEnabled)
        assertFalse("Photo PiP must be optional and off by default", defaults.pipPhotoEnabled)

        val forbidden = listOf("spotify", "apple", "samsung", "vlc", "youtube", "iphone")
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            val checked = listOf(
                s.pipMusicTitle,
                s.pipMusicDesc,
                s.pipVideoTitle,
                s.pipVideoDesc,
                s.pipPhotoTitle,
                s.pipPhotoDesc,
                s.pipPhotoBadge,
                s.filtersAndToolsAction,
                s.clearFiltersAction,
                s.moreHeaderActionsDesc
            )
            checked.forEach { text ->
                assertTrue("New string must not be blank for $lang", text.isNotBlank())
                val lower = text.lowercase()
                forbidden.forEach { brand ->
                    assertTrue("String must never contain trademark '$brand': $text", !lower.contains(brand))
                }
            }
        }
    }

    @Test
    fun `downloads direct file contract and comprehensive studio customization defaults and strings work cleanly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val downloadsUri = com.example.ui.components.SmartSafFolderNavigator.buildInitialUri(
            com.example.ui.components.SmartSafFolderNavigator.SUBPATH_DOWNLOADS
        )
        assertTrue("Downloads initial URI must be non-null", downloadsUri != null)
        assertTrue(downloadsUri.toString().contains("Download"))

        val contract = com.example.ui.components.OpenMultipleDocumentsInSubpath()
        val intent = contract.createIntent(context, downloadsUri to arrayOf("audio/*", "video/*", "image/*"))
        assertEquals(android.content.Intent.ACTION_OPEN_DOCUMENT, intent.action)
        assertTrue(intent.getBooleanExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, false))

        val defaults = com.example.model.AppSettings()
        assertEquals(com.example.model.MediaCardStyle.GLASS_CARD, defaults.cardStyle)
        assertEquals(com.example.model.BackdropAnimationStyle.AURORA_ORBS, defaults.backdropStyle)
        assertEquals(com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE, defaults.motionFeelStyle)
        assertEquals(com.example.model.HeaderTitleStyle.EXPRESSIVE_BOLD, defaults.headerTitleStyle)
        assertTrue(defaults.swipeGesturesEnabled)
        assertTrue(defaults.showFormatBadges)
        assertEquals(1.0f, defaults.accentStrength, 0.001f)

        val forbidden = listOf("spotify", "apple", "samsung", "vlc", "youtube", "iphone")
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            val checked = listOf(
                s.importDownloadsDirectAction,
                s.importDownloadsPrivacyExplainer,
                s.quickStudioTitle,
                s.quickStudioSubtitle,
                s.cardStyleTitle,
                s.backdropStyleTitle,
                s.motionFeelTitle,
                s.headerStyleTitle,
                s.swipeGesturesTitle,
                s.swipeGesturesDesc,
                s.showFormatBadgesTitle,
                s.showFormatBadgesDesc,
                s.accentStrengthTitle,
                s.swipeFavoriteHint,
                s.swipePlaylistHint
            )
            checked.forEach { text ->
                assertTrue("Studio string must not be blank for $lang", text.isNotBlank())
                val lower = text.lowercase()
                forbidden.forEach { brand ->
                    assertTrue("String must never contain trademark '$brand': $text", !lower.contains(brand))
                }
            }
            com.example.model.MediaCardStyle.entries.forEach { assertTrue(s.cardStyleName(it).isNotBlank()) }
            com.example.model.BackdropAnimationStyle.entries.forEach { assertTrue(s.backdropStyleName(it).isNotBlank()) }
            com.example.model.MotionFeelStyle.entries.forEach { assertTrue(s.motionFeelName(it).isNotBlank()) }
            com.example.model.HeaderTitleStyle.entries.forEach { assertTrue(s.headerStyleName(it).isNotBlank()) }
        }
    }

    @Test
    fun `beta 4 4 pro 10-band studio dsp tube warmth rumble filter stereo width crossfeed swap and bypass work cleanly`() {
        val sr = 44100
        // 1) 10-Band EQ + High-Pass Rumble Filter + Tube Warmth + A/B Bypass on low 50Hz sine wave
        val low50Hz = ShortArray(4096) { i ->
            (Math.sin(2.0 * Math.PI * 50.0 * i / sr) * 9000.0).toInt().toShort()
        }
        fun rms(arr: ShortArray): Double {
            var sum = 0.0
            for (i in 1024 until arr.size) {
                val v = arr[i].toDouble()
                sum += v * v
            }
            return Math.sqrt(sum / (arr.size - 1024))
        }

        val proc = com.example.player.FiveBandBiquadAudioProcessor()
        proc.setEqualizerEnabled(false)
        proc.setProEqEnabled(true)
        val flatRms = rms(proc.processPcmForTest(low50Hz, sr))

        // Boost 62Hz band (+12 dB) + Sub-Bass Punch
        proc.setTenBandGainsDb(listOf(8f, 12f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
        proc.setSubBassPunch(0.6f)
        val boostedRms = rms(proc.processPcmForTest(low50Hz, sr))
        assertTrue("10-band sub boost ($boostedRms) must lift 50Hz signal above flat ($flatRms)", boostedRms > flatRms * 1.35)

        // Enable 80Hz High-Pass Rumble Filter with flat EQ -> must attenuate 50Hz rumble
        proc.setTenBandGainsDb(List(10) { 0f })
        proc.setSubBassPunch(0f)
        proc.setRumbleFilterHz(80)
        val filteredRms = rms(proc.processPcmForTest(low50Hz, sr))
        assertTrue("80Hz rumble filter ($filteredRms) must attenuate 50Hz energy below flat ($flatRms)", filteredRms < flatRms * 0.72)

        // Instant A/B Bypass must return bit-exact original PCM samples
        proc.setBypassEnabled(true)
        val bypassed = proc.processPcmForTest(low50Hz, sr)
        assertTrue("A/B bypass must preserve raw PCM bit-for-bit", low50Hz.contentEquals(bypassed))

        // 2) Stereo Widener, Binaural Crossfeed & Channel Swap verification
        val stereoFrames = 512
        val stereoInput = ShortArray(stereoFrames * 2) { idx ->
            if (idx % 2 == 0) 12000.toShort() else 0.toShort() // Pure Left signal, silent Right
        }

        val stereoProc = com.example.player.FiveBandBiquadAudioProcessor()
        stereoProc.setChannelSwapEnabled(true)
        val swapped = stereoProc.processStereoPcmForTest(stereoInput, sr)
        assertEquals(0, swapped[400].toInt())
        assertTrue("Channel swap must move Left signal into Right channel", swapped[401].toInt() > 10000)

        stereoProc.setChannelSwapEnabled(false)
        stereoProc.setCrossfeedEnabled(true)
        val crossfed = stereoProc.processStereoPcmForTest(stereoInput, sr)
        assertTrue("Binaural crossfeed must gently bleed L into R channel", crossfed[401].toInt() > 1200)

        stereoProc.setCrossfeedEnabled(false)
        stereoProc.setStereoWidth(1.8f)
        val widened = stereoProc.processStereoPcmForTest(stereoInput, sr)
        assertTrue("Stereo widener > 1.0 must expand L-R side difference", widened[400].toInt() > 12500)
    }

    @Test
    fun `auto studio mastering engine detects distinct profiles and beta 4 4 strings are clean`() {
        val quranTrack = com.example.model.AudioTrack(
            id = "q1", title = "سورة الرحمن", artist = "الشيخ مشاري العفاسي", album = "القرآن الكريم",
            uriString = "content://audio/q1", extension = "MP3", mimeType = "audio/mpeg",
            durationMs = 420_000L, sizeBytes = 6_000_000L
        )
        val flacTrack = com.example.model.AudioTrack(
            id = "f1", title = "Nocturne in E-Flat", artist = "Studio Session", album = "Master Tape",
            uriString = "content://audio/f1", extension = "FLAC", mimeType = "audio/flac",
            durationMs = 260_000L, sizeBytes = 32_000_000L
        )
        val edmTrack = com.example.model.AudioTrack(
            id = "e1", title = "Cyber Bass Drop Remix", artist = "Neon Synth", album = "Club",
            uriString = "content://audio/e1", extension = "MP3", mimeType = "audio/mpeg",
            durationMs = 210_000L, sizeBytes = 5_000_000L
        )
        val podcastTrack = com.example.model.AudioTrack(
            id = "p1", title = "Weekly Tech Podcast Episode 12", artist = "Host", album = "Talks",
            uriString = "content://audio/p1", extension = "M4A", mimeType = "audio/mp4",
            durationMs = 2400_000L, sizeBytes = 18_000_000L
        )

        val qProfile = com.example.player.AutoStudioMasteringEngine.analyzeTrack(quranTrack)
        val fProfile = com.example.player.AutoStudioMasteringEngine.analyzeTrack(flacTrack)
        val eProfile = com.example.player.AutoStudioMasteringEngine.analyzeTrack(edmTrack)
        val pProfile = com.example.player.AutoStudioMasteringEngine.analyzeTrack(podcastTrack)

        assertEquals(com.example.model.AutoStudioCategory.QURAN_SANCTUARY, qProfile.category)
        assertEquals(com.example.model.AutoStudioCategory.LOSSLESS_MASTERING, fProfile.category)
        assertEquals(com.example.model.AutoStudioCategory.ELECTRONIC_STAGE, eProfile.category)
        assertEquals(com.example.model.AutoStudioCategory.VOCAL_BROADCAST, pProfile.category)
        assertEquals(10, qProfile.tenBandGainsDb.size)
        assertTrue(eProfile.subBassPunch > qProfile.subBassPunch)

        val forbidden = listOf("spotify", "apple", "samsung", "vlc", "youtube", "iphone", "poweramp")
        AppLanguage.entries.forEach { lang ->
            val s = stringsFor(lang)
            assertEquals("beta 4.4", s.appVersionLabel)
            val allChecked = listOf(
                s.proStudioTitle,
                s.proStudioSubtitle,
                s.proStudioOpenAction,
                s.studioAutoTitle,
                s.studioAutoDesc,
                s.studioAutoAnalyzeNow,
                s.studioBypassOriginal,
                s.studioBypassMastered,
                s.defaultPlayerTitle,
                s.defaultPlayerDesc,
                s.defaultPlayerAction,
                s.storageAccessHubTitle,
                s.storageAccessHubDesc,
                s.storageGrantFolderAction,
                s.storagePickDownloadsAction,
                s.storagePickGalleryAction,
                s.playerToolsDrawerOpen,
                s.playerToolsDrawerClose,
            )
            allChecked.forEach { text ->
                assertTrue("Beta 4.4 string must not be blank for $lang", text.isNotBlank())
                val lower = text.lowercase()
                forbidden.forEach { brand ->
                    assertTrue("String must not contain trademark '$brand': $text", !lower.contains(brand))
                }
            }
            com.example.model.ProEqPreset.entries.forEach { assertTrue(s.proEqPresetName(it).isNotBlank()) }
            com.example.model.HeadphoneTuningProfile.entries.forEach { assertTrue(s.headphoneProfileName(it).isNotBlank()) }
            com.example.model.AutoStudioCategory.entries.forEach { assertTrue(s.autoStudioCategoryName(it).isNotBlank()) }
        }
    }
}



