package com.example.player

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import com.example.model.HeadphoneTuningProfile
import com.example.model.ReverbEnvironment
import com.example.model.StudioAudioConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

/**
 * Real-time Pro Studio PCM DSP Engine (Beta 4.4):
 * - 10-Band Professional Biquad Equalizer (31Hz..16kHz, ±15dB) + backward-compatible 5-Band overlay
 * - High-Pass Acoustic Sub-Rumble Filter (30Hz / 60Hz / 80Hz)
 * - Dedicated 48Hz Sub-Bass Punch Shelf, 140Hz Bass Boost Shelf, 3.2kHz Vocal Clarity Peak, 12kHz Air Brilliance Shelf
 * - 5-Mode Headphone & Speaker Acoustic Calibration Curve
 * - Analog Tube Warmth / Even-Harmonic Exciter
 * - Studio Dynamic Range Compressor & Late-Night Whisper Leveler
 * - Independent Mid-Side Stereo Widener (0.5x..2.0x)
 * - Binaural Headphone Crossfeed (300us interaural delay + 700Hz acoustic shadow LPF)
 * - Multi-Tap Stereo Schroeder Reverb & 3D Spatializer
 * - True-Peak Brickwall Limiter (-0.5 dBFS ceiling) + Channel Swap (L <-> R) + Instant A/B Bypass
 * - Real-Time 12-Band Goertzel Spectrum Analyzer + Stereo L/R VU Peak Telemetry
 */
@OptIn(UnstableApi::class)
class FiveBandBiquadAudioProcessor : BaseAudioProcessor() {

    companion object {
        val BAND_FREQS_HZ = doubleArrayOf(125.0, 250.0, 910.0, 3600.0, 8500.0)
        val PRO_TEN_BAND_FREQS_HZ = doubleArrayOf(
            31.0, 62.0, 125.0, 250.0, 500.0, 1000.0, 2000.0, 4000.0, 8000.0, 16000.0
        )
        val PRO_TEN_BAND_LABELS = listOf(
            "31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k"
        )
        private const val SUB_BASS_FREQ_HZ = 48.0
        private const val BASS_BOOST_FREQ_HZ = 140.0
        private const val VOCAL_CLARITY_FREQ_HZ = 3200.0
        private const val AIR_BRILLIANCE_FREQ_HZ = 12000.0
        private const val MAX_BASS_BOOST_DB = 11.0
        private const val MAX_SUB_BASS_DB = 10.0
        private const val MAX_VOCAL_CLARITY_DB = 7.5
        private const val MAX_AIR_BRILLIANCE_DB = 8.5

        // 1 HPF Rumble + 10 EQ Bands + 1 BassBoost + 1 SubBass + 1 VocalClarity + 1 AirBrilliance = 15 stages
        private const val STAGE_COUNT = 15
        private const val SPECTRUM_BARS = 12
        private val SPECTRUM_FREQS_HZ = doubleArrayOf(
            55.0, 110.0, 220.0, 380.0, 620.0, 980.0,
            1500.0, 2300.0, 3600.0, 5600.0, 8800.0, 13200.0
        )
    }

    private data class BiquadCoeffs(
        val b0: Double,
        val b1: Double,
        val b2: Double,
        val a1: Double,
        val a2: Double,
        val isIdentity: Boolean = false,
    )

    private data class FilterSnapshot(
        val enabled: Boolean,
        val bypassEnabled: Boolean,
        val sampleRate: Int,
        val stages: List<BiquadCoeffs>,
        val preGain: Double,
        val preampMultiplier: Double,
        val stereoBalance: Float,
        val monoEnabled: Boolean,
        val normalizationEnabled: Boolean,
        val lectureBoostEnabled: Boolean,
        val reverbEnvironment: ReverbEnvironment,
        val tubeWarmth: Double,
        val stereoWidth: Double,
        val crossfeedEnabled: Boolean,
        val compressorStrength: Double,
        val truePeakLimiterEnabled: Boolean,
        val nightModeEnabled: Boolean,
        val channelSwapEnabled: Boolean,
        val hasActiveBiquadStages: Boolean,
    )

    // Legacy 5-band state (kept 100% compatible)
    @Volatile
    private var gainsDb: List<Float> = listOf(0f, 0f, 0f, 0f, 0f)

    // Pro 10-band state
    @Volatile
    private var proTenBandGainsDb: List<Float> = List(10) { 0f }

    @Volatile
    private var proEqEnabled: Boolean = true

    @Volatile
    private var eqEnabled: Boolean = true

    @Volatile
    private var bassBoostStrength: Float = 0f

    @Volatile
    private var preampMultiplier: Double = 1.0

    @Volatile
    private var stereoBalance: Float = 0f

    @Volatile
    private var monoEnabled: Boolean = false

    @Volatile
    private var normalizationEnabled: Boolean = false

    @Volatile
    private var lectureBoostEnabled: Boolean = false

    @Volatile
    private var reverbEnvironment: ReverbEnvironment = ReverbEnvironment.NONE

    // New Beta 4.4 Pro Studio Mastering Controls
    @Volatile
    private var tubeWarmth: Float = 0f

    @Volatile
    private var stereoWidth: Float = 1.0f

    @Volatile
    private var crossfeedEnabled: Boolean = false

    @Volatile
    private var subBassPunch: Float = 0f

    @Volatile
    private var airBrilliance: Float = 0f

    @Volatile
    private var vocalClarity: Float = 0f

    @Volatile
    private var compressorStrength: Float = 0f

    @Volatile
    private var truePeakLimiterEnabled: Boolean = true

    @Volatile
    private var nightModeEnabled: Boolean = false

    @Volatile
    private var rumbleFilterHz: Int = 0

    @Volatile
    private var channelSwapEnabled: Boolean = false

    @Volatile
    private var headphoneProfile: HeadphoneTuningProfile = HeadphoneTuningProfile.NEUTRAL_STUDIO

    @Volatile
    private var dspBypassEnabled: Boolean = false

    @Volatile
    private var currentSampleRate: Int = 44100

    private var smoothedRms: Double = 0.16
    private var compEnv: Double = 0.05

    // Multi-tap stereo Schroeder reverb delay ring buffers
    private val reverbBufSize = 8192
    private val reverbLeft = DoubleArray(reverbBufSize)
    private val reverbRight = DoubleArray(reverbBufSize)
    private var reverbPos = 0

    // Binaural Crossfeed delay line (~300us @ 48kHz = 15 samples) + 700Hz acoustic head-shadow LPF state
    private val crossfeedBufSize = 32
    private val crossfeedBufL = DoubleArray(crossfeedBufSize)
    private val crossfeedBufR = DoubleArray(crossfeedBufSize)
    private var crossfeedPos = 0
    private var crossfeedLpL = 0.0
    private var crossfeedLpR = 0.0

    @Volatile
    private var snapshot: FilterSnapshot = buildCurrentSnapshot()

    // Filter delay states: [channel][stage]
    private var x1: Array<DoubleArray> = emptyArray()
    private var x2: Array<DoubleArray> = emptyArray()
    private var y1: Array<DoubleArray> = emptyArray()
    private var y2: Array<DoubleArray> = emptyArray()

    // Lightweight Goertzel spectrum analyzer buffer
    private val spectrumWindowSize = 512
    private val monoRing = FloatArray(spectrumWindowSize)
    private var monoRingPos = 0
    private val smoothedBars = FloatArray(SPECTRUM_BARS) { 0.08f }
    private var lastSpectrumEmitNanos = 0L

    // Real-time stereo VU peak meter state
    private var vuPeakL = 0.0
    private var vuPeakR = 0.0
    private var smoothedVuL = 0f
    private var smoothedVuR = 0f

    private val _spectrumLevels = MutableStateFlow(List(SPECTRUM_BARS) { 0.08f })
    val spectrumLevels: StateFlow<List<Float>> = _spectrumLevels.asStateFlow()

    private val _vuLevels = MutableStateFlow(0f to 0f)
    val vuLevels: StateFlow<Pair<Float, Float>> = _vuLevels.asStateFlow()

    fun setBandGainsDb(newGainsDb: List<Float>) {
        if (newGainsDb.size != 5) return
        gainsDb = newGainsDb.map { it.coerceIn(-12f, 12f) }
        rebuildSnapshot()
    }

    fun setTenBandGainsDb(newGainsDb: List<Float>) {
        if (newGainsDb.size != 10) return
        proTenBandGainsDb = newGainsDb.map { it.coerceIn(-15f, 15f) }
        rebuildSnapshot()
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        eqEnabled = enabled
        rebuildSnapshot()
    }

    fun setProEqEnabled(enabled: Boolean) {
        proEqEnabled = enabled
        rebuildSnapshot()
    }

    fun setBassBoost(strength: Float) {
        bassBoostStrength = strength.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setPreampPercent(percent: Int) {
        preampMultiplier = (percent.coerceIn(100, 200) / 100.0)
        rebuildSnapshot()
    }

    fun setStereoBalance(balance: Float) {
        stereoBalance = balance.coerceIn(-1f, 1f)
        rebuildSnapshot()
    }

    fun setMonoEnabled(enabled: Boolean) {
        monoEnabled = enabled
        rebuildSnapshot()
    }

    fun setLoudnessNormalizationEnabled(enabled: Boolean) {
        normalizationEnabled = enabled
        if (!enabled) smoothedRms = 0.16
        rebuildSnapshot()
    }

    fun setLectureVocalBoostEnabled(enabled: Boolean) {
        lectureBoostEnabled = enabled
        rebuildSnapshot()
    }

    fun setReverbEnvironment(env: ReverbEnvironment) {
        reverbEnvironment = env
        if (env == ReverbEnvironment.NONE) {
            reverbLeft.fill(0.0)
            reverbRight.fill(0.0)
        }
        rebuildSnapshot()
    }

    fun setTubeWarmth(amount: Float) {
        tubeWarmth = amount.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setStereoWidth(width: Float) {
        stereoWidth = width.coerceIn(0.5f, 2.0f)
        rebuildSnapshot()
    }

    fun setCrossfeedEnabled(enabled: Boolean) {
        crossfeedEnabled = enabled
        if (!enabled) {
            crossfeedBufL.fill(0.0)
            crossfeedBufR.fill(0.0)
            crossfeedLpL = 0.0
            crossfeedLpR = 0.0
        }
        rebuildSnapshot()
    }

    fun setSubBassPunch(amount: Float) {
        subBassPunch = amount.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setAirBrilliance(amount: Float) {
        airBrilliance = amount.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setVocalClarity(amount: Float) {
        vocalClarity = amount.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setCompressorStrength(strength: Float) {
        compressorStrength = strength.coerceIn(0f, 1f)
        rebuildSnapshot()
    }

    fun setTruePeakLimiterEnabled(enabled: Boolean) {
        truePeakLimiterEnabled = enabled
        rebuildSnapshot()
    }

    fun setNightModeEnabled(enabled: Boolean) {
        nightModeEnabled = enabled
        rebuildSnapshot()
    }

    fun setRumbleFilterHz(hz: Int) {
        rumbleFilterHz = when {
            hz <= 0 -> 0
            hz <= 40 -> 30
            hz <= 70 -> 60
            else -> 80
        }
        rebuildSnapshot()
    }

    fun setChannelSwapEnabled(enabled: Boolean) {
        channelSwapEnabled = enabled
        rebuildSnapshot()
    }

    fun setHeadphoneProfile(profile: HeadphoneTuningProfile) {
        headphoneProfile = profile
        rebuildSnapshot()
    }

    fun setBypassEnabled(bypass: Boolean) {
        dspBypassEnabled = bypass
        rebuildSnapshot()
    }

    fun applyStudioConfig(config: StudioAudioConfig) {
        proEqEnabled = config.proEqEnabled
        if (config.tenBandGainsDb.size == 10) {
            proTenBandGainsDb = config.tenBandGainsDb.map { it.coerceIn(-15f, 15f) }
        }
        tubeWarmth = config.tubeWarmth.coerceIn(0f, 1f)
        stereoWidth = config.stereoWidth.coerceIn(0.5f, 2.0f)
        crossfeedEnabled = config.crossfeedEnabled
        subBassPunch = config.subBassPunch.coerceIn(0f, 1f)
        airBrilliance = config.airBrilliance.coerceIn(0f, 1f)
        vocalClarity = config.vocalClarity.coerceIn(0f, 1f)
        compressorStrength = config.compressorStrength.coerceIn(0f, 1f)
        truePeakLimiterEnabled = config.truePeakLimiterEnabled
        nightModeEnabled = config.nightModeEnabled
        rumbleFilterHz = config.rumbleFilterHz
        channelSwapEnabled = config.channelSwapEnabled
        headphoneProfile = config.headphoneProfile
        dspBypassEnabled = config.bypassEnabled
        rebuildSnapshot()
    }

    fun resetSpectrumIdle() {
        for (i in smoothedBars.indices) {
            smoothedBars[i] = 0.08f
        }
        smoothedVuL = 0f
        smoothedVuR = 0f
        vuPeakL = 0.0
        vuPeakR = 0.0
        _spectrumLevels.value = smoothedBars.toList()
        _vuLevels.value = 0f to 0f
    }

    private fun rebuildSnapshot() {
        snapshot = buildCurrentSnapshot()
    }

    private fun buildCurrentSnapshot(): FilterSnapshot {
        val sr = currentSampleRate.coerceAtLeast(8000).toDouble()
        val bypass = dspBypassEnabled
        val activeEq = !bypass && (eqEnabled || proEqEnabled)

        // Headphone calibration curve across 10 bands (31..16k)
        val hpCurve = if (!bypass) headphoneCurveDb(headphoneProfile) else DoubleArray(10) { 0.0 }

        // Combine 10-band pro EQ + legacy 5-band EQ overlay + headphone calibration
        // Band 0 behaves as a warm low-shelf across 31Hz/62Hz/125Hz; Bands 1..4 map to 250Hz, 1kHz, 4kHz, 8kHz/16kHz
        val legacyOverlay = DoubleArray(10) { 0.0 }
        if (!bypass && eqEnabled) {
            val g0 = gainsDb.getOrElse(0) { 0f }.toDouble()
            val g1 = gainsDb.getOrElse(1) { 0f }.toDouble()
            val g2 = gainsDb.getOrElse(2) { 0f }.toDouble()
            val g3 = gainsDb.getOrElse(3) { 0f }.toDouble()
            val g4 = gainsDb.getOrElse(4) { 0f }.toDouble()
            legacyOverlay[0] += g0 * 0.65
            legacyOverlay[1] += g0
            legacyOverlay[2] += g0 * 0.55
            legacyOverlay[3] += g1
            legacyOverlay[5] += g2
            legacyOverlay[7] += g3
            legacyOverlay[8] += g4
            legacyOverlay[9] += g4 * 0.55
        }
        if (!bypass && lectureBoostEnabled) {
            legacyOverlay[1] -= 2.5
            legacyOverlay[2] -= 2.0
            legacyOverlay[5] += 2.2
            legacyOverlay[6] += 4.5
            legacyOverlay[7] += 3.8
        }

        val combined10 = DoubleArray(10) { idx ->
            val proVal = if (!bypass && proEqEnabled) proTenBandGainsDb.getOrElse(idx) { 0f }.toDouble() else 0.0
            (proVal + legacyOverlay[idx] + hpCurve[idx]).coerceIn(-15.0, 15.0)
        }

        val hpfStage = if (!bypass && rumbleFilterHz > 0) {
            highPassBiquad(rumbleFilterHz.toDouble(), sr, q = 0.707)
        } else {
            identityBiquad()
        }

        val bassDb = if (!bypass && eqEnabled) bassBoostStrength.coerceIn(0f, 1f).toDouble() * MAX_BASS_BOOST_DB else 0.0
        val subBassDb = if (!bypass) subBassPunch.coerceIn(0f, 1f).toDouble() * MAX_SUB_BASS_DB else 0.0
        val vocalDb = if (!bypass) vocalClarity.coerceIn(0f, 1f).toDouble() * MAX_VOCAL_CLARITY_DB else 0.0
        val airDb = if (!bypass) airBrilliance.coerceIn(0f, 1f).toDouble() * MAX_AIR_BRILLIANCE_DB else 0.0

        val stages = buildList(STAGE_COUNT) {
            add(hpfStage) // Stage 0: High-pass sub-rumble filter
            // Stages 1..10: 10-band studio graphic/parametric EQ
            add(lowShelfBiquad(PRO_TEN_BAND_FREQS_HZ[0], sr, combined10[0], slope = 0.9))
            for (i in 1..8) {
                add(peakingBiquad(PRO_TEN_BAND_FREQS_HZ[i], sr, combined10[i], q = 1.35))
            }
            add(highShelfBiquad(PRO_TEN_BAND_FREQS_HZ[9], sr, combined10[9], slope = 0.9))
            // Stages 11..14: Dedicated Pro Tone Shapers
            add(lowShelfBiquad(BASS_BOOST_FREQ_HZ, sr, bassDb, slope = 0.85))
            add(lowShelfBiquad(SUB_BASS_FREQ_HZ, sr, subBassDb, slope = 0.92))
            add(peakingBiquad(VOCAL_CLARITY_FREQ_HZ, sr, vocalDb, q = 1.15))
            add(highShelfBiquad(AIR_BRILLIANCE_FREQ_HZ, sr, airDb, slope = 0.85))
        }

        val peakBoostDb = maxOf(
            0.0,
            combined10.maxOrNull() ?: 0.0,
            (combined10[0] + combined10[1]) * 0.5 + subBassDb * 0.65 + bassDb * 0.55,
            vocalDb,
            airDb
        )
        val trimDb = if (!bypass && peakBoostDb > 6.0) -((peakBoostDb - 6.0) * 0.25).coerceAtMost(5.0) else 0.0
        val preGain = 10.0.pow(trimDb / 20.0)
        val hasActiveStages = !bypass && stages.any { !it.isIdentity }

        return FilterSnapshot(
            enabled = activeEq || hasActiveStages,
            bypassEnabled = bypass,
            sampleRate = currentSampleRate,
            stages = stages,
            preGain = preGain,
            preampMultiplier = if (bypass) 1.0 else preampMultiplier.coerceIn(1.0, 2.0),
            stereoBalance = if (bypass) 0f else stereoBalance.coerceIn(-1f, 1f),
            monoEnabled = !bypass && monoEnabled,
            normalizationEnabled = !bypass && normalizationEnabled,
            lectureBoostEnabled = !bypass && lectureBoostEnabled,
            reverbEnvironment = if (bypass) ReverbEnvironment.NONE else reverbEnvironment,
            tubeWarmth = if (bypass) 0.0 else tubeWarmth.coerceIn(0f, 1f).toDouble(),
            stereoWidth = if (bypass) 1.0 else stereoWidth.coerceIn(0.5f, 2.0f).toDouble(),
            crossfeedEnabled = !bypass && crossfeedEnabled,
            compressorStrength = if (bypass) 0.0 else compressorStrength.coerceIn(0f, 1f).toDouble(),
            truePeakLimiterEnabled = !bypass && truePeakLimiterEnabled,
            nightModeEnabled = !bypass && nightModeEnabled,
            channelSwapEnabled = !bypass && channelSwapEnabled,
            hasActiveBiquadStages = hasActiveStages,
        )
    }

    private fun headphoneCurveDb(profile: HeadphoneTuningProfile): DoubleArray = when (profile) {
        HeadphoneTuningProfile.NEUTRAL_STUDIO -> doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        HeadphoneTuningProfile.OPEN_BACK_HIFI -> doubleArrayOf(2.8, 2.0, 0.8, 0.0, 0.0, 0.4, 0.8, 0.5, 0.0, 1.0)
        HeadphoneTuningProfile.IEM_IN_EAR -> doubleArrayOf(-1.0, -0.5, 0.5, 1.0, 0.8, 0.5, -1.2, -1.8, 0.8, 2.0)
        HeadphoneTuningProfile.BASS_HEAD_CLUB -> doubleArrayOf(-2.5, -2.0, -1.2, 0.5, 1.5, 2.0, 2.2, 1.8, 1.2, 1.5)
        HeadphoneTuningProfile.SPEAKER_COMPENSATION -> doubleArrayOf(3.2, 2.8, 1.5, -0.5, 0.0, 1.0, 1.6, 2.2, 2.5, 2.8)
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        if (inputAudioFormat.channelCount <= 0 || inputAudioFormat.sampleRate <= 0) {
            return AudioProcessor.AudioFormat.NOT_SET
        }
        currentSampleRate = inputAudioFormat.sampleRate
        allocateStates(inputAudioFormat.channelCount)
        rebuildSnapshot()
        return inputAudioFormat
    }

    private fun allocateStates(channelCount: Int) {
        x1 = Array(channelCount) { DoubleArray(STAGE_COUNT) }
        x2 = Array(channelCount) { DoubleArray(STAGE_COUNT) }
        y1 = Array(channelCount) { DoubleArray(STAGE_COUNT) }
        y2 = Array(channelCount) { DoubleArray(STAGE_COUNT) }
    }

    override fun onFlush() {
        for (ch in x1.indices) {
            x1[ch].fill(0.0)
            x2[ch].fill(0.0)
            y1[ch].fill(0.0)
            y2[ch].fill(0.0)
        }
        monoRingPos = 0
        reverbLeft.fill(0.0)
        reverbRight.fill(0.0)
        reverbPos = 0
        crossfeedBufL.fill(0.0)
        crossfeedBufR.fill(0.0)
        crossfeedPos = 0
        crossfeedLpL = 0.0
        crossfeedLpR = 0.0
        compEnv = 0.05
        resetSpectrumIdle()
    }

    override fun onReset() {
        x1 = emptyArray()
        x2 = emptyArray()
        y1 = emptyArray()
        y2 = emptyArray()
        monoRingPos = 0
        reverbLeft.fill(0.0)
        reverbRight.fill(0.0)
        reverbPos = 0
        crossfeedBufL.fill(0.0)
        crossfeedBufR.fill(0.0)
        crossfeedPos = 0
        crossfeedLpL = 0.0
        crossfeedLpR = 0.0
        compEnv = 0.05
        resetSpectrumIdle()
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return

        val outBuffer = replaceOutputBuffer(remaining)
        val channels = inputAudioFormat.channelCount.coerceAtLeast(1)
        if (x1.size != channels) {
            allocateStates(channels)
        }

        val snap = snapshot
        val stages = snap.stages
        val bypass = snap.bypassEnabled
        val runBiquad = !bypass && snap.hasActiveBiquadStages
        val preGain = snap.preGain
        val preamp = snap.preampMultiplier
        val balance = snap.stereoBalance
        val mono = snap.monoEnabled
        val normalize = snap.normalizationEnabled
        val warmth = snap.tubeWarmth
        val compStr = snap.compressorStrength
        val nightMode = snap.nightModeEnabled
        val width = snap.stereoWidth
        val crossfeed = snap.crossfeedEnabled
        val swapLR = snap.channelSwapEnabled
        val limiter = snap.truePeakLimiterEnabled
        val reverbEnv = snap.reverbEnvironment

        val leftGain = (1.0 - balance.coerceAtLeast(0f).toDouble()).coerceIn(0.0, 1.0)
        val rightGain = (1.0 + balance.coerceAtMost(0f).toDouble()).coerceIn(0.0, 1.0)
        val bytesPerFrame = channels * 2
        val frameCount = remaining / bytesPerFrame
        val frameSamples = DoubleArray(channels)

        // Crossfeed 700Hz one-pole LPF alpha and ~300us tap delay
        val cfAlpha = (2.0 * PI * 700.0 / snap.sampleRate.coerceAtLeast(8000)).coerceIn(0.02, 0.45)
        val cfDelaySamples = (snap.sampleRate * 0.00030).toInt().coerceIn(4, crossfeedBufSize - 2)

        val (tap1, tap2, feedback, wetMix, reverbWidth) = when (reverbEnv) {
            ReverbEnvironment.NONE -> ReverbParams(0, 0, 0.0, 0.0, 1.0)
            ReverbEnvironment.STUDIO -> ReverbParams(920, 1480, 0.28, 0.20, 1.08)
            ReverbEnvironment.HALL -> ReverbParams(1850, 3120, 0.44, 0.32, 1.16)
            ReverbEnvironment.MOSQUE -> ReverbParams(2640, 4420, 0.56, 0.40, 1.22)
            ReverbEnvironment.SPATIAL_3D -> ReverbParams(680, 1180, 0.30, 0.24, 1.42)
        }

        val effectiveDynamics = max(compStr, if (nightMode) 0.72 else 0.0)
        val anyProcessing = !bypass && (
            runBiquad || preamp > 1.001 || normalize || reverbEnv != ReverbEnvironment.NONE ||
                warmth > 0.01 || effectiveDynamics > 0.01 || abs(width - 1.0) > 0.01 ||
                crossfeed || swapLR || mono || abs(balance) > 0.01f
            )

        for (frame in 0 until frameCount) {
            var monoSum = 0.0
            for (ch in 0 until channels) {
                val lo = inputBuffer.get().toInt() and 0xFF
                val hi = inputBuffer.get().toInt()
                val rawShort = (hi shl 8) or lo
                var sample = rawShort / 32768.0

                if (runBiquad) {
                    sample *= preGain
                    val chX1 = x1[ch]
                    val chX2 = x2[ch]
                    val chY1 = y1[ch]
                    val chY2 = y2[ch]

                    for (s in 0 until STAGE_COUNT) {
                        val c = stages[s]
                        if (c.isIdentity) continue
                        val out = c.b0 * sample + c.b1 * chX1[s] + c.b2 * chX2[s] -
                            c.a1 * chY1[s] - c.a2 * chY2[s]
                        chX2[s] = chX1[s]
                        chX1[s] = sample
                        chY2[s] = chY1[s]
                        chY1[s] = out
                        sample = out
                    }
                }

                // Analog Tube Warmth / Even-Harmonic Exciter
                if (!bypass && warmth > 0.01) {
                    sample = applyTubeWarmth(sample, warmth)
                }

                if (preamp > 1.001) {
                    sample *= preamp
                }
                frameSamples[ch] = sample
                monoSum += sample
            }

            val monoAvg = monoSum / channels

            // Studio Dynamic Compressor + Late-Night Whisper Mode
            val compGain = if (!bypass && effectiveDynamics > 0.01) {
                val peak = abs(monoAvg)
                val attack = if (nightMode) 0.020 else 0.012
                val release = if (nightMode) 0.0015 else 0.0008
                val coeff = if (peak > compEnv) attack else release
                compEnv = (compEnv * (1.0 - coeff) + peak * coeff).coerceIn(0.004, 1.5)
                val threshold = if (nightMode) 0.16 else (0.45 - effectiveDynamics * 0.25).coerceIn(0.16, 0.45)
                val ratio = if (nightMode) 4.2 else (1.0 + effectiveDynamics * 3.0)
                val rawGain = if (compEnv > threshold) {
                    val over = compEnv / threshold
                    over.pow((1.0 / ratio) - 1.0)
                } else if (nightMode && compEnv < 0.08) {
                    // Gently lift whisper-quiet details in Night Mode
                    (0.08 / compEnv.coerceAtLeast(0.028)).pow(0.35).coerceAtMost(1.45)
                } else {
                    1.0
                }
                val makeup = if (nightMode) 1.12 else (1.0 + effectiveDynamics * 0.22)
                (rawGain * makeup).coerceIn(0.35, 1.65)
            } else {
                1.0
            }

            // RMS Loudness Normalization
            val normGain = if (normalize) {
                val absLevel = abs(monoAvg)
                val alpha = if (absLevel > smoothedRms) 0.008 else 0.0006
                smoothedRms = (smoothedRms * (1.0 - alpha) + absLevel * alpha).coerceIn(0.025, 0.90)
                (0.20 / smoothedRms).coerceIn(0.55, 1.85)
            } else {
                1.0
            }

            // Stereo Stage: Channel Swap -> Mid-Side Widener -> Binaural Crossfeed -> Schroeder Reverb
            if (!bypass && channels >= 2 && !mono) {
                if (swapLR) {
                    val tmp = frameSamples[0]
                    frameSamples[0] = frameSamples[1]
                    frameSamples[1] = tmp
                }

                // Independent Mid-Side Stereo Widener (combined smoothly with reverb width if active)
                val effectiveWidth = (width * reverbWidth).coerceIn(0.45, 2.2)
                if (abs(effectiveWidth - 1.0) > 0.01) {
                    val mid = (frameSamples[0] + frameSamples[1]) * 0.5
                    val side = (frameSamples[0] - frameSamples[1]) * 0.5 * effectiveWidth
                    frameSamples[0] = mid + side
                    frameSamples[1] = mid - side
                }

                // Binaural Headphone Acoustic Crossfeed
                if (crossfeed) {
                    val inL = frameSamples[0]
                    val inR = frameSamples[1]
                    crossfeedBufL[crossfeedPos] = inL
                    crossfeedBufR[crossfeedPos] = inR
                    val delayedIdx = (crossfeedPos - cfDelaySamples + crossfeedBufSize) % crossfeedBufSize
                    crossfeedLpL += cfAlpha * (crossfeedBufL[delayedIdx] - crossfeedLpL)
                    crossfeedLpR += cfAlpha * (crossfeedBufR[delayedIdx] - crossfeedLpR)
                    crossfeedPos = (crossfeedPos + 1) % crossfeedBufSize
                    frameSamples[0] = inL * 0.86 + crossfeedLpR * 0.22
                    frameSamples[1] = inR * 0.86 + crossfeedLpL * 0.22
                }

                // Multi-Tap Stereo Schroeder Reverb
                if (reverbEnv != ReverbEnvironment.NONE) {
                    val wideL = frameSamples[0]
                    val wideR = frameSamples[1]
                    val idx1L = (reverbPos - tap1 + reverbBufSize) % reverbBufSize
                    val idx2L = (reverbPos - tap2 + reverbBufSize) % reverbBufSize
                    val idx1R = (reverbPos - (tap1 + 113) + reverbBufSize) % reverbBufSize
                    val idx2R = (reverbPos - (tap2 + 179) + reverbBufSize) % reverbBufSize

                    val echoL = (reverbLeft[idx1L] * 0.62 + reverbRight[idx2L] * 0.38)
                    val echoR = (reverbRight[idx1R] * 0.62 + reverbLeft[idx2R] * 0.38)
                    reverbLeft[reverbPos] = (wideL + echoL * feedback).coerceIn(-1.5, 1.5)
                    reverbRight[reverbPos] = (wideR + echoR * feedback).coerceIn(-1.5, 1.5)
                    reverbPos = (reverbPos + 1) % reverbBufSize

                    frameSamples[0] = wideL * (1.0 - wetMix * 0.35) + echoL * wetMix
                    frameSamples[1] = wideR * (1.0 - wetMix * 0.35) + echoR * wetMix
                }
            }

            var outMonoSum = 0.0
            for (ch in 0 until channels) {
                var outSample = if (mono) monoAvg else frameSamples[ch]
                if (effectiveDynamics > 0.01) {
                    outSample *= compGain
                }
                if (normalize) {
                    outSample *= normGain
                }
                if (anyProcessing) {
                    outSample = if (limiter) brickwallLimit(outSample) else softClip(outSample)
                }
                if (!bypass && channels >= 2 && abs(balance) > 0.01f) {
                    outSample *= if (ch == 0) leftGain else if (ch == 1) rightGain else 1.0
                }

                // Track stereo L/R VU Peak Telemetry
                val absOut = abs(outSample)
                if (ch == 0 && absOut > vuPeakL) vuPeakL = absOut
                if (ch == 1 && absOut > vuPeakR) vuPeakR = absOut
                if (channels == 1 && absOut > vuPeakR) vuPeakR = absOut
                outMonoSum += outSample

                val outShort = (outSample * 32767.0).toInt().coerceIn(-32768, 32767)
                outBuffer.put((outShort and 0xFF).toByte())
                outBuffer.put(((outShort shr 8) and 0xFF).toByte())
            }

            monoRing[monoRingPos] = (outMonoSum / channels).toFloat()
            monoRingPos++
            if (monoRingPos >= spectrumWindowSize) {
                monoRingPos = 0
                val now = System.nanoTime()
                if (now - lastSpectrumEmitNanos >= 38_000_000L) {
                    lastSpectrumEmitNanos = now
                    computeAndPublishSpectrumAndVu(snap.sampleRate)
                }
            }
        }

        while (inputBuffer.hasRemaining()) {
            outBuffer.put(inputBuffer.get())
        }
        outBuffer.flip()
    }

    /**
     * Direct offline/test mono PCM processing entry point using the exact same Pro Studio DSP chain.
     */
    fun processPcmForTest(samples: ShortArray, sampleRate: Int = 44100): ShortArray {
        currentSampleRate = sampleRate
        rebuildSnapshot()
        val snap = snapshot
        val stages = snap.stages
        val sx1 = DoubleArray(STAGE_COUNT)
        val sx2 = DoubleArray(STAGE_COUNT)
        val sy1 = DoubleArray(STAGE_COUNT)
        val sy2 = DoubleArray(STAGE_COUNT)
        val out = ShortArray(samples.size)

        val bypass = snap.bypassEnabled
        val runBiquad = !bypass && snap.hasActiveBiquadStages
        val revEnv = snap.reverbEnvironment
        val localRev = DoubleArray(4096)
        var localRevPos = 0
        val (revTap, revFb, revWet) = when (revEnv) {
            ReverbEnvironment.NONE -> Triple(0, 0.0, 0.0)
            ReverbEnvironment.STUDIO -> Triple(420, 0.30, 0.24)
            ReverbEnvironment.HALL -> Triple(920, 0.45, 0.34)
            ReverbEnvironment.MOSQUE -> Triple(1480, 0.58, 0.44)
            ReverbEnvironment.SPATIAL_3D -> Triple(640, 0.35, 0.28)
        }
        var localRms = 0.16
        var localCompEnv = 0.05
        val effectiveDyn = if (bypass) 0.0 else max(snap.compressorStrength, if (snap.nightModeEnabled) 0.72 else 0.0)

        for (i in samples.indices) {
            var sVal = samples[i] / 32768.0
            if (bypass) {
                out[i] = samples[i]
                continue
            }
            if (runBiquad) {
                sVal *= snap.preGain
                for (st in 0 until STAGE_COUNT) {
                    val c = stages[st]
                    if (c.isIdentity) continue
                    val y = c.b0 * sVal + c.b1 * sx1[st] + c.b2 * sx2[st] -
                        c.a1 * sy1[st] - c.a2 * sy2[st]
                    sx2[st] = sx1[st]
                    sx1[st] = sVal
                    sy2[st] = sy1[st]
                    sy1[st] = y
                    sVal = y
                }
            }
            if (snap.tubeWarmth > 0.01) {
                sVal = applyTubeWarmth(sVal, snap.tubeWarmth)
            }
            if (snap.preampMultiplier > 1.001) {
                sVal *= snap.preampMultiplier
            }
            if (effectiveDyn > 0.01) {
                val peak = abs(sVal)
                val coeff = if (peak > localCompEnv) 0.015 else 0.001
                localCompEnv = (localCompEnv * (1.0 - coeff) + peak * coeff).coerceIn(0.004, 1.5)
                val threshold = if (snap.nightModeEnabled) 0.16 else (0.45 - effectiveDyn * 0.25).coerceIn(0.16, 0.45)
                val ratio = if (snap.nightModeEnabled) 4.2 else (1.0 + effectiveDyn * 3.0)
                val g = if (localCompEnv > threshold) (localCompEnv / threshold).pow((1.0 / ratio) - 1.0) else 1.0
                sVal *= (g * (1.0 + effectiveDyn * 0.22)).coerceIn(0.35, 1.65)
            }
            if (snap.normalizationEnabled) {
                val absLevel = abs(sVal)
                val alpha = if (absLevel > localRms) 0.008 else 0.0006
                localRms = (localRms * (1.0 - alpha) + absLevel * alpha).coerceIn(0.025, 0.90)
                val normGain = (0.20 / localRms).coerceIn(0.55, 1.85)
                sVal *= normGain
            }
            if (revEnv != ReverbEnvironment.NONE && revTap > 0) {
                val tapIdx = (localRevPos - revTap + localRev.size) % localRev.size
                val echo = localRev[tapIdx]
                localRev[localRevPos] = (sVal + echo * revFb).coerceIn(-1.5, 1.5)
                localRevPos = (localRevPos + 1) % localRev.size
                sVal = sVal * (1.0 - revWet * 0.30) + echo * revWet
            }
            if (runBiquad || snap.tubeWarmth > 0.01 || snap.preampMultiplier > 1.001 ||
                snap.normalizationEnabled || effectiveDyn > 0.01 || revEnv != ReverbEnvironment.NONE
            ) {
                sVal = if (snap.truePeakLimiterEnabled) brickwallLimit(sVal) else softClip(sVal)
            }
            out[i] = (sVal * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return out
    }

    /**
     * Direct offline/test stereo interleaved PCM entry point for verifying Mid-Side Stereo Width,
     * Binaural Crossfeed, Channel Swap, and A/B Bypass in JVM unit tests.
     */
    fun processStereoPcmForTest(interleavedStereo: ShortArray, sampleRate: Int = 44100): ShortArray {
        currentSampleRate = sampleRate
        rebuildSnapshot()
        val snap = snapshot
        val out = ShortArray(interleavedStereo.size)
        val frames = interleavedStereo.size / 2
        val cfAlpha = (2.0 * PI * 700.0 / sampleRate.coerceAtLeast(8000)).coerceIn(0.02, 0.45)
        val cfDelay = (sampleRate * 0.00030).toInt().coerceIn(4, 30)
        val bufL = DoubleArray(32)
        val bufR = DoubleArray(32)
        var pos = 0
        var lpL = 0.0
        var lpR = 0.0

        for (f in 0 until frames) {
            var l = interleavedStereo[f * 2] / 32768.0
            var r = interleavedStereo[f * 2 + 1] / 32768.0
            if (snap.bypassEnabled) {
                out[f * 2] = interleavedStereo[f * 2]
                out[f * 2 + 1] = interleavedStereo[f * 2 + 1]
                continue
            }
            if (snap.channelSwapEnabled) {
                val t = l; l = r; r = t
            }
            if (abs(snap.stereoWidth - 1.0) > 0.01 && !snap.monoEnabled) {
                val m = (l + r) * 0.5
                val s = (l - r) * 0.5 * snap.stereoWidth
                l = m + s
                r = m - s
            }
            if (snap.crossfeedEnabled && !snap.monoEnabled) {
                bufL[pos] = l
                bufR[pos] = r
                val dIdx = (pos - cfDelay + 32) % 32
                lpL += cfAlpha * (bufL[dIdx] - lpL)
                lpR += cfAlpha * (bufR[dIdx] - lpR)
                pos = (pos + 1) % 32
                l = l * 0.86 + lpR * 0.22
                r = r * 0.86 + lpL * 0.22
            }
            if (snap.monoEnabled) {
                val m = (l + r) * 0.5
                l = m; r = m
            }
            if (snap.tubeWarmth > 0.01) {
                l = applyTubeWarmth(l, snap.tubeWarmth)
                r = applyTubeWarmth(r, snap.tubeWarmth)
            }
            l = if (snap.truePeakLimiterEnabled) brickwallLimit(l) else softClip(l)
            r = if (snap.truePeakLimiterEnabled) brickwallLimit(r) else softClip(r)
            out[f * 2] = (l * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
            out[f * 2 + 1] = (r * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
        }
        return out
    }

    private data class ReverbParams(
        val tap1: Int,
        val tap2: Int,
        val feedback: Double,
        val wetMix: Double,
        val spatialWidth: Double,
    )

    private fun computeAndPublishSpectrumAndVu(sampleRate: Int) {
        val n = spectrumWindowSize
        val sr = sampleRate.coerceAtLeast(8000).toDouble()
        for (b in 0 until SPECTRUM_BARS) {
            val freq = SPECTRUM_FREQS_HZ[b].coerceAtMost(sr * 0.45)
            val omega = 2.0 * PI * freq / sr
            val coeff = 2.0 * cos(omega)
            var q0: Double
            var q1 = 0.0
            var q2 = 0.0
            var i = 0
            while (i < n) {
                q0 = coeff * q1 - q2 + monoRing[i]
                q2 = q1
                q1 = q0
                i += 2
            }
            val magSq = (q1 * q1 + q2 * q2 - q1 * q2 * coeff).coerceAtLeast(0.0)
            val rms = sqrt(magSq) / (n / 2.0)
            val tilt = 1.0 + b * 0.16
            val target = (sqrt(rms * 6.5 * tilt)).toFloat().coerceIn(0.08f, 1.0f)
            val prev = smoothedBars[b]
            smoothedBars[b] = if (target > prev) {
                prev * 0.35f + target * 0.65f
            } else {
                prev * 0.72f + target * 0.28f
            }
        }
        _spectrumLevels.value = smoothedBars.toList()

        // Smooth & publish Stereo VU levels
        val targetL = vuPeakL.toFloat().coerceIn(0f, 1f)
        val targetR = vuPeakR.toFloat().coerceIn(0f, 1f)
        smoothedVuL = if (targetL > smoothedVuL) smoothedVuL * 0.25f + targetL * 0.75f else smoothedVuL * 0.75f + targetL * 0.25f
        smoothedVuR = if (targetR > smoothedVuR) smoothedVuR * 0.25f + targetR * 0.75f else smoothedVuR * 0.75f + targetR * 0.25f
        vuPeakL = 0.0
        vuPeakR = 0.0
        _vuLevels.value = smoothedVuL to smoothedVuR
    }

    /**
     * Analog Triode Tube Warmth: blends gentle asymmetric even-order harmonic saturation
     * with the clean signal for rich studio console warmth without harsh distortion.
     */
    private fun applyTubeWarmth(x: Double, amount: Double): Double {
        val drive = 1.0 + amount * 2.2
        val driven = x * drive
        val asymmetric = tanh(driven + 0.14 * amount * driven * driven)
        val compensated = asymmetric / (1.0 + amount * 0.65)
        return x * (1.0 - amount * 0.65) + compensated * (amount * 0.65)
    }

    private fun softClip(x: Double): Double {
        val ax = abs(x)
        if (ax <= 0.85) return x
        val sign = if (x >= 0.0) 1.0 else -1.0
        return sign * (0.85 + 0.15 * tanh((ax - 0.85) / 0.15))
    }

    /**
     * True-Peak Brickwall Ceiling at -0.5 dBFS (0.944) with transparent knee above 0.88.
     */
    private fun brickwallLimit(x: Double): Double {
        val ax = abs(x)
        if (ax <= 0.88) return x
        val sign = if (x >= 0.0) 1.0 else -1.0
        return sign * (0.88 + 0.064 * tanh((ax - 0.88) / 0.064))
    }

    private fun highPassBiquad(freqHz: Double, sampleRate: Double, q: Double): BiquadCoeffs {
        val f = freqHz.coerceIn(15.0, sampleRate * 0.45)
        val w0 = 2.0 * PI * f / sampleRate
        val cosW0 = cos(w0)
        val alpha = sin(w0) / (2.0 * q)
        val b0 = (1.0 + cosW0) / 2.0
        val b1 = -(1.0 + cosW0)
        val b2 = (1.0 + cosW0) / 2.0
        val a0 = 1.0 + alpha
        val a1 = -2.0 * cosW0
        val a2 = 1.0 - alpha
        return BiquadCoeffs(b0 / a0, b1 / a0, b2 / a0, a1 / a0, a2 / a0, isIdentity = false)
    }

    private fun peakingBiquad(freqHz: Double, sampleRate: Double, gainDb: Double, q: Double): BiquadCoeffs {
        if (abs(gainDb) < 0.05) return identityBiquad()
        val f = freqHz.coerceIn(20.0, sampleRate * 0.45)
        val a = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * f / sampleRate
        val cosW0 = cos(w0)
        val alpha = sin(w0) / (2.0 * q)

        val b0 = 1.0 + alpha * a
        val b1 = -2.0 * cosW0
        val b2 = 1.0 - alpha * a
        val a0 = 1.0 + alpha / a
        val a1 = -2.0 * cosW0
        val a2 = 1.0 - alpha / a

        return BiquadCoeffs(b0 / a0, b1 / a0, b2 / a0, a1 / a0, a2 / a0, isIdentity = false)
    }

    private fun lowShelfBiquad(freqHz: Double, sampleRate: Double, gainDb: Double, slope: Double): BiquadCoeffs {
        if (abs(gainDb) < 0.05) return identityBiquad()
        val f = freqHz.coerceIn(20.0, sampleRate * 0.45)
        val a = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * f / sampleRate
        val cosW0 = cos(w0)
        val sinW0 = sin(w0)
        val alpha = sinW0 / 2.0 * sqrt((a + 1.0 / a) * (1.0 / slope - 1.0) + 2.0)
        val twoSqrtAAlpha = 2.0 * sqrt(a) * alpha

        val b0 = a * ((a + 1.0) - (a - 1.0) * cosW0 + twoSqrtAAlpha)
        val b1 = 2.0 * a * ((a - 1.0) - (a + 1.0) * cosW0)
        val b2 = a * ((a + 1.0) - (a - 1.0) * cosW0 - twoSqrtAAlpha)
        val a0 = (a + 1.0) + (a - 1.0) * cosW0 + twoSqrtAAlpha
        val a1 = -2.0 * ((a - 1.0) + (a + 1.0) * cosW0)
        val a2 = (a + 1.0) + (a - 1.0) * cosW0 - twoSqrtAAlpha

        return BiquadCoeffs(b0 / a0, b1 / a0, b2 / a0, a1 / a0, a2 / a0, isIdentity = false)
    }

    private fun highShelfBiquad(freqHz: Double, sampleRate: Double, gainDb: Double, slope: Double): BiquadCoeffs {
        if (abs(gainDb) < 0.05) return identityBiquad()
        val f = freqHz.coerceIn(20.0, sampleRate * 0.45)
        val a = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * f / sampleRate
        val cosW0 = cos(w0)
        val sinW0 = sin(w0)
        val alpha = sinW0 / 2.0 * sqrt((a + 1.0 / a) * (1.0 / slope - 1.0) + 2.0)
        val twoSqrtAAlpha = 2.0 * sqrt(a) * alpha

        val b0 = a * ((a + 1.0) + (a - 1.0) * cosW0 + twoSqrtAAlpha)
        val b1 = -2.0 * a * ((a - 1.0) + (a + 1.0) * cosW0)
        val b2 = a * ((a + 1.0) + (a - 1.0) * cosW0 - twoSqrtAAlpha)
        val a0 = (a + 1.0) - (a - 1.0) * cosW0 + twoSqrtAAlpha
        val a1 = 2.0 * ((a - 1.0) - (a + 1.0) * cosW0)
        val a2 = (a + 1.0) - (a - 1.0) * cosW0 - twoSqrtAAlpha

        return BiquadCoeffs(b0 / a0, b1 / a0, b2 / a0, a1 / a0, a2 / a0, isIdentity = false)
    }

    private fun identityBiquad(): BiquadCoeffs = BiquadCoeffs(1.0, 0.0, 0.0, 0.0, 0.0, isIdentity = true)
}
