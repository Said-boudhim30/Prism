package com.example.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.example.model.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class AudioPlaybackController(
    context: Context,
    private val scope: CoroutineScope,
    private val onTrackEnded: () -> Unit,
    private val onSavePosition: (String, Long) -> Unit,
    private val onSleepTimerFinished: () -> Unit,
) {
    companion object {
        val TARGET_BAND_FREQS_HZ = listOf(60, 230, 910, 3600, 14000)
        val BAND_LABELS = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
        val SPEED_STEPS = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    }

    private val biquadProcessor = FiveBandBiquadAudioProcessor()

    val driverProfile: DeviceDriverProfile = DeviceMediaDriverEngine.probe(context)

    private val renderersFactory = object : DefaultRenderersFactory(context) {
        override fun buildAudioSink(
            context: Context,
            enableFloatOutput: Boolean,
            enableAudioTrackPlaybackParams: Boolean,
        ): AudioSink {
            return DefaultAudioSink.Builder(context)
                .setAudioProcessors(arrayOf(biquadProcessor))
                .setEnableFloatOutput(enableFloatOutput || driverProfile.supportsFloatPcm)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .build()
        }
    }.apply {
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        setEnableDecoderFallback(true)
        setEnableAudioFloatOutput(driverProfile.supportsFloatPcm)
        setEnableAudioTrackPlaybackParams(true)
    }

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context, renderersFactory)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_LOCAL)
        .build()

    val spectrumLevels: StateFlow<List<Float>> = biquadProcessor.spectrumLevels
    val vuLevels: StateFlow<Pair<Float, Float>> = biquadProcessor.vuLevels

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _audioPitch = MutableStateFlow(1.0f)
    val audioPitch: StateFlow<Float> = _audioPitch.asStateFlow()

    private val _preampPercent = MutableStateFlow(100)
    val preampPercent: StateFlow<Int> = _preampPercent.asStateFlow()

    private var crossfadeSec: Int = 0

    private val _abLoopStartMs = MutableStateFlow<Long?>(null)
    val abLoopStartMs: StateFlow<Long?> = _abLoopStartMs.asStateFlow()

    private val _abLoopEndMs = MutableStateFlow<Long?>(null)
    val abLoopEndMs: StateFlow<Long?> = _abLoopEndMs.asStateFlow()

    private val _hifzLoopTarget = MutableStateFlow(0) // 0 = infinite A-B repeat, >0 = counted repeats
    private val _hifzRemainingLoops = MutableStateFlow(0)
    val hifzRemainingLoops: StateFlow<Int> = _hifzRemainingLoops.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _sleepTimerRemainingSec = MutableStateFlow(0)
    val sleepTimerRemainingSec: StateFlow<Int> = _sleepTimerRemainingSec.asStateFlow()

    private val _sleepTimerTotalMin = MutableStateFlow(0)
    val sleepTimerTotalMin: StateFlow<Int> = _sleepTimerTotalMin.asStateFlow()

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var crossfadeJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTicker()
                } else {
                    stopProgressTicker()
                    biquadProcessor.resetSpectrumIdle()
                    _currentTrack.value?.let { track ->
                        onSavePosition(track.id, exoPlayer.currentPosition.coerceAtLeast(0L))
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val dur = exoPlayer.duration
                    if (dur > 0) _durationMs.value = dur
                } else if (playbackState == Player.STATE_ENDED) {
                    _currentTrack.value?.let { onSavePosition(it.id, 0L) }
                    biquadProcessor.resetSpectrumIdle()
                    onTrackEnded()
                }
            }
        })
    }

    fun playTrack(track: AudioTrack, resumePositionMs: Long = 0L) {
        _currentTrack.value?.let { prev ->
            if (prev.id != track.id) {
                onSavePosition(prev.id, exoPlayer.currentPosition.coerceAtLeast(0L))
            }
        }
        _currentTrack.value = track
        _durationMs.value = track.durationMs.coerceAtLeast(1L)
        _positionMs.value = resumePositionMs
        _abLoopStartMs.value = null
        _abLoopEndMs.value = null

        val mediaItem = MediaItem.fromUri(Uri.parse(track.uriString))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playbackParameters = PlaybackParameters(_playbackSpeed.value, _audioPitch.value)
        exoPlayer.prepare()
        if (resumePositionMs in 500L until (track.durationMs - 1000L)) {
            exoPlayer.seekTo(resumePositionMs)
        } else {
            exoPlayer.seekTo(0L)
            _positionMs.value = 0L
        }
        startSmoothCrossfadeIn()
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }

    private fun startSmoothCrossfadeIn() {
        crossfadeJob?.cancel()
        if (_isMuted.value) {
            exoPlayer.volume = 0f
            return
        }
        val fadeSec = crossfadeSec.coerceIn(0, 12)
        if (fadeSec <= 0) {
            exoPlayer.volume = 1.0f
            return
        }
        exoPlayer.volume = 0.08f
        crossfadeJob = scope.launch(Dispatchers.Main) {
            val steps = (fadeSec * 12).coerceIn(10, 144)
            val stepDelay = (fadeSec * 1000L) / steps
            for (i in 1..steps) {
                if (!isActive || _isMuted.value) break
                val t = i.toFloat() / steps.toFloat()
                exoPlayer.volume = (0.08f + 0.92f * t * t).coerceIn(0.08f, 1.0f)
                delay(stepDelay)
            }
            if (!_isMuted.value) exoPlayer.volume = 1.0f
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            val dur = exoPlayer.duration
            val atEnd = exoPlayer.playbackState == Player.STATE_ENDED ||
                (dur > 0L && exoPlayer.currentPosition >= dur - 350L)
            if (atEnd) {
                exoPlayer.seekTo(0L)
                _positionMs.value = 0L
            }
            if (exoPlayer.playbackState == Player.STATE_IDLE) {
                _currentTrack.value?.let { track ->
                    exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(track.uriString)))
                }
                exoPlayer.prepare()
            }
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        }
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceIn(0L, _durationMs.value.coerceAtLeast(1L))
        exoPlayer.seekTo(clamped)
        _positionMs.value = clamped
    }

    fun seekRelative(deltaMs: Long) {
        val target = (exoPlayer.currentPosition + deltaMs).coerceIn(0L, _durationMs.value.coerceAtLeast(1L))
        exoPlayer.seekTo(target)
        _positionMs.value = target
    }

    fun setPlaybackSpeed(speed: Float) {
        val clamped = speed.coerceIn(0.5f, 2.5f)
        _playbackSpeed.value = clamped
        exoPlayer.playbackParameters = PlaybackParameters(clamped, _audioPitch.value)
    }

    fun setPitch(pitch: Float) {
        val clamped = pitch.coerceIn(0.75f, 1.50f)
        _audioPitch.value = clamped
        exoPlayer.playbackParameters = PlaybackParameters(_playbackSpeed.value, clamped)
    }

    fun setPreampPercent(percent: Int) {
        val clamped = percent.coerceIn(100, 200)
        _preampPercent.value = clamped
        biquadProcessor.setPreampPercent(clamped)
    }

    fun setCrossfadeSeconds(seconds: Int) {
        crossfadeSec = seconds.coerceIn(0, 12)
        if (crossfadeSec == 0) {
            crossfadeJob?.cancel()
            exoPlayer.volume = 1.0f
        }
    }

    fun setStereoBalance(balance: Float) {
        biquadProcessor.setStereoBalance(balance)
    }

    fun setMonoEnabled(enabled: Boolean) {
        biquadProcessor.setMonoEnabled(enabled)
    }

    fun setSkipSilenceEnabled(enabled: Boolean) {
        runCatching { exoPlayer.skipSilenceEnabled = enabled }
    }

    fun setLoudnessNormalizationEnabled(enabled: Boolean) {
        biquadProcessor.setLoudnessNormalizationEnabled(enabled)
    }

    fun setReverbEnvironment(env: com.example.model.ReverbEnvironment) {
        biquadProcessor.setReverbEnvironment(env)
    }

    fun setLectureVocalBoostEnabled(enabled: Boolean) {
        biquadProcessor.setLectureVocalBoostEnabled(enabled)
    }

    fun setPauseOnHeadsetUnplug(enabled: Boolean) {
        runCatching { exoPlayer.setHandleAudioBecomingNoisy(enabled) }
    }

    fun toggleMute() {
        val next = !_isMuted.value
        _isMuted.value = next
        crossfadeJob?.cancel()
        exoPlayer.volume = if (next) 0f else 1.0f
    }

    fun setHifzLoopTarget(target: Int) {
        val clamped = target.coerceAtLeast(0)
        _hifzLoopTarget.value = clamped
        if (_abLoopStartMs.value != null && _abLoopEndMs.value != null) {
            _hifzRemainingLoops.value = clamped
        }
    }

    fun playWithGentleFadeIn(track: AudioTrack, fadeSeconds: Int = 30) {
        val prevFade = crossfadeSec
        crossfadeSec = fadeSeconds.coerceIn(5, 60)
        playTrack(track, resumePositionMs = 0L)
        crossfadeSec = prevFade
    }

    fun toggleAbLoopPoint() {
        val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
        val start = _abLoopStartMs.value
        val end = _abLoopEndMs.value
        when {
            start == null -> {
                _abLoopStartMs.value = pos
                _abLoopEndMs.value = null
                _hifzRemainingLoops.value = 0
            }
            end == null -> {
                if (pos > start + 800L) {
                    _abLoopEndMs.value = pos
                    _hifzRemainingLoops.value = _hifzLoopTarget.value
                    exoPlayer.seekTo(start)
                } else {
                    _abLoopStartMs.value = pos
                }
            }
            else -> {
                _abLoopStartMs.value = null
                _abLoopEndMs.value = null
                _hifzRemainingLoops.value = 0
            }
        }
    }

    fun clearAbLoop() {
        _abLoopStartMs.value = null
        _abLoopEndMs.value = null
        _hifzRemainingLoops.value = 0
    }

    fun stopAndClear() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        biquadProcessor.resetSpectrumIdle()
        _currentTrack.value = null
        _isPlaying.value = false
        _positionMs.value = 0L
        _durationMs.value = 0L
        _abLoopStartMs.value = null
        _abLoopEndMs.value = null
    }

    fun applyEqualizerGains(gainsDb: List<Float>) {
        if (gainsDb.size != 5) return
        biquadProcessor.setBandGainsDb(gainsDb)
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        biquadProcessor.setEqualizerEnabled(enabled)
    }

    fun setBassBoost(strength: Float) {
        biquadProcessor.setBassBoost(strength)
    }

    fun applyStudioConfig(config: com.example.model.StudioAudioConfig) {
        biquadProcessor.applyStudioConfig(config)
    }

    fun setTenBandGainsDb(gainsDb: List<Float>) {
        biquadProcessor.setTenBandGainsDb(gainsDb)
    }

    fun setProEqEnabled(enabled: Boolean) {
        biquadProcessor.setProEqEnabled(enabled)
    }

    fun setTubeWarmth(amount: Float) {
        biquadProcessor.setTubeWarmth(amount)
    }

    fun setStereoWidth(width: Float) {
        biquadProcessor.setStereoWidth(width)
    }

    fun setCrossfeedEnabled(enabled: Boolean) {
        biquadProcessor.setCrossfeedEnabled(enabled)
    }

    fun setSubBassPunch(amount: Float) {
        biquadProcessor.setSubBassPunch(amount)
    }

    fun setAirBrilliance(amount: Float) {
        biquadProcessor.setAirBrilliance(amount)
    }

    fun setVocalClarity(amount: Float) {
        biquadProcessor.setVocalClarity(amount)
    }

    fun setCompressorStrength(strength: Float) {
        biquadProcessor.setCompressorStrength(strength)
    }

    fun setTruePeakLimiterEnabled(enabled: Boolean) {
        biquadProcessor.setTruePeakLimiterEnabled(enabled)
    }

    fun setNightModeEnabled(enabled: Boolean) {
        biquadProcessor.setNightModeEnabled(enabled)
    }

    fun setRumbleFilterHz(hz: Int) {
        biquadProcessor.setRumbleFilterHz(hz)
    }

    fun setChannelSwapEnabled(enabled: Boolean) {
        biquadProcessor.setChannelSwapEnabled(enabled)
    }

    fun setHeadphoneProfile(profile: com.example.model.HeadphoneTuningProfile) {
        biquadProcessor.setHeadphoneProfile(profile)
    }

    fun setDspBypassEnabled(bypass: Boolean) {
        biquadProcessor.setBypassEnabled(bypass)
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimerRemainingSec.value = 0
            _sleepTimerTotalMin.value = 0
            return
        }
        _sleepTimerTotalMin.value = minutes
        _sleepTimerRemainingSec.value = minutes * 60
        sleepTimerJob = scope.launch(Dispatchers.Main) {
            while (_sleepTimerRemainingSec.value > 0 && isActive) {
                delay(1000L)
                _sleepTimerRemainingSec.value = (_sleepTimerRemainingSec.value - 1).coerceAtLeast(0)
            }
            if (_sleepTimerRemainingSec.value == 0) {
                exoPlayer.pause()
                _sleepTimerTotalMin.value = 0
                onSleepTimerFinished()
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemainingSec.value = 0
        _sleepTimerTotalMin.value = 0
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = scope.launch(Dispatchers.Main) {
            while (isActive && exoPlayer.isPlaying) {
                val cur = exoPlayer.currentPosition.coerceAtLeast(0L)
                val loopStart = _abLoopStartMs.value
                val loopEnd = _abLoopEndMs.value
                if (loopStart != null && loopEnd != null && cur >= loopEnd) {
                    val rem = _hifzRemainingLoops.value
                    if (rem > 1) {
                        _hifzRemainingLoops.value = rem - 1
                        exoPlayer.seekTo(loopStart)
                        _positionMs.value = loopStart
                    } else if (rem == 1) {
                        // Completed counted Hifz repeats; release A-B loop cleanly and continue
                        _hifzRemainingLoops.value = 0
                        _abLoopStartMs.value = null
                        _abLoopEndMs.value = null
                        _positionMs.value = cur
                    } else {
                        // Infinite A-B loop mode (0)
                        exoPlayer.seekTo(loopStart)
                        _positionMs.value = loopStart
                    }
                } else {
                    _positionMs.value = cur
                }
                val dur = exoPlayer.duration
                if (dur > 0) _durationMs.value = dur
                delay(180L)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
    }

    fun release() {
        progressJob?.cancel()
        sleepTimerJob?.cancel()
        crossfadeJob?.cancel()
        biquadProcessor.resetSpectrumIdle()
        exoPlayer.release()
    }
}
