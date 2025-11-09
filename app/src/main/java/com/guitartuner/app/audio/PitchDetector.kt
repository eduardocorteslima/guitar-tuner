package com.guitartuner.app.audio

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles pitch detection using TarsosDSP library with noise filtering
 */
class PitchDetector {
    
    private val _detectedFrequency = MutableStateFlow(0.0)
    val detectedFrequency: StateFlow<Double> = _detectedFrequency.asStateFlow()
    
    // Smoothing filter for frequency values
    private val frequencyHistory = mutableListOf<Double>()
    private var lastStableFrequency = 0.0
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE = 4096
        private const val OVERLAP = 0
        
        // Increased probability threshold for more stable detection
        private const val MIN_PROBABILITY = 0.95f
        
        // Smoothing parameters
        private const val HISTORY_SIZE = 5
        private const val STABILITY_THRESHOLD = 10.0 // Hz difference to consider stable
        private const val MIN_FREQUENCY = 60.0 // Minimum guitar frequency
        private const val MAX_FREQUENCY = 500.0 // Maximum guitar frequency
    }
    
    /**
     * Creates a PitchProcessor for use with TarsosDSP AudioDispatcher
     */
    fun createPitchProcessor(): PitchProcessor {
        val handler = PitchDetectionHandler { result, _ ->
            handlePitchDetection(result)
        }
        
        return PitchProcessor(
            PitchEstimationAlgorithm.YIN,
            SAMPLE_RATE.toFloat(),
            BUFFER_SIZE,
            handler
        )
    }
    
    /**
     * Handles pitch detection results with smoothing filter
     */
    private fun handlePitchDetection(result: PitchDetectionResult) {
        val pitch = result.pitch
        val probability = result.probability
        
        // Only accept pitch if it's detected with high probability and in valid range
        if (pitch != -1f && probability >= MIN_PROBABILITY) {
            val frequency = pitch.toDouble()
            
            // Filter out frequencies outside guitar range
            if (frequency < MIN_FREQUENCY || frequency > MAX_FREQUENCY) {
                return
            }
            
            // Add to history
            frequencyHistory.add(frequency)
            if (frequencyHistory.size > HISTORY_SIZE) {
                frequencyHistory.removeAt(0)
            }
            
            // Calculate smoothed frequency (median filter to reduce noise)
            if (frequencyHistory.size >= 3) {
                val smoothedFrequency = frequencyHistory.sorted()[frequencyHistory.size / 2]
                
                // Only update if frequency is stable
                if (lastStableFrequency == 0.0 || 
                    Math.abs(smoothedFrequency - lastStableFrequency) < STABILITY_THRESHOLD) {
                    lastStableFrequency = smoothedFrequency
                    _detectedFrequency.value = smoothedFrequency
                } else {
                    // Frequency jumped too much, reset history
                    frequencyHistory.clear()
                    lastStableFrequency = smoothedFrequency
                }
            }
        } else {
            // Clear history if no valid pitch detected
            if (frequencyHistory.size > 0) {
                frequencyHistory.clear()
            }
            _detectedFrequency.value = 0.0
        }
    }
    
    /**
     * Gets the sample rate used for audio processing
     */
    fun getSampleRate(): Int = SAMPLE_RATE
    
    /**
     * Gets the buffer size used for audio processing
     */
    fun getBufferSize(): Int = BUFFER_SIZE
}


