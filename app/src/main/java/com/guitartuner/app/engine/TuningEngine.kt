package com.guitartuner.app.engine

import com.guitartuner.app.data.Note
import com.guitartuner.app.data.TuningMode
import com.guitartuner.app.data.TuningState
import com.guitartuner.app.data.TuningStatus
import kotlin.math.abs
import kotlin.math.log2

/**
 * Engine for calculating tuning state from detected frequencies
 */
class TuningEngine(private var currentTuningMode: TuningMode = TuningMode.STANDARD) {
    
    private var targetStringIndex: Int? = null
    
    companion object {
        private const val IN_TUNE_THRESHOLD = 5.0 // cents
        private const val MIN_FREQUENCY = 60.0 // Hz (below this is likely noise)
        private const val MAX_FREQUENCY = 500.0 // Hz (above this is out of guitar range)
    }
    
    /**
     * Updates the current tuning mode
     */
    fun setTuningMode(mode: TuningMode) {
        currentTuningMode = mode
    }
    
    /**
     * Sets a specific target string to tune
     */
    fun setTargetString(stringIndex: Int) {
        targetStringIndex = stringIndex
    }
    
    /**
     * Clears the target string selection (tune any string)
     */
    fun clearTargetString() {
        targetStringIndex = null
    }
    
    /**
     * Calculates the tuning state from a detected frequency
     */
    fun calculateTuningState(detectedFrequency: Double): TuningState {
        // Check if frequency is in valid range
        if (detectedFrequency < MIN_FREQUENCY || detectedFrequency > MAX_FREQUENCY) {
            return TuningState(
                detectedNote = Note.fromFrequency(detectedFrequency),
                targetFrequency = 0.0,
                cents = 0.0,
                tuningStatus = TuningStatus.DETECTING
            )
        }
        
        // Get the detected note
        val detectedNote = Note.fromFrequency(detectedFrequency)
        
        // Find the closest target frequency from current tuning mode
        val targetFrequency = findClosestTargetFrequency(detectedFrequency)
        
        // Calculate cents deviation
        val cents = calculateCents(detectedFrequency, targetFrequency)
        
        // Determine tuning status
        val tuningStatus = when {
            abs(cents) <= IN_TUNE_THRESHOLD -> TuningStatus.IN_TUNE
            cents < 0 -> TuningStatus.TOO_LOW
            else -> TuningStatus.TOO_HIGH
        }
        
        return TuningState(
            detectedNote = detectedNote,
            targetFrequency = targetFrequency,
            cents = cents,
            tuningStatus = tuningStatus
        )
    }
    
    /**
     * Calculates the cents deviation between detected and target frequency
     * Formula: cents = 1200 × log2(f_detected / f_target)
     */
    private fun calculateCents(detectedFrequency: Double, targetFrequency: Double): Double {
        if (targetFrequency <= 0 || detectedFrequency <= 0) return 0.0
        return 1200.0 * log2(detectedFrequency / targetFrequency)
    }
    
    /**
     * Finds the closest target frequency from the current tuning mode
     */
    private fun findClosestTargetFrequency(detectedFrequency: Double): Double {
        val frequencies = currentTuningMode.frequencies
        
        // If a specific string is selected, only use that frequency
        if (targetStringIndex != null && targetStringIndex!! in frequencies.indices) {
            return frequencies[targetStringIndex!!]
        }
        
        // Find the closest frequency from the tuning mode
        var closestFreq = frequencies[0]
        var minDiff = abs(detectedFrequency - closestFreq)
        
        for (freq in frequencies) {
            val diff = abs(detectedFrequency - freq)
            if (diff < minDiff) {
                minDiff = diff
                closestFreq = freq
            }
        }
        
        return closestFreq
    }
    
    /**
     * Gets all target notes for the current tuning mode
     */
    fun getTargetNotes(): List<String> {
        return currentTuningMode.noteNames
    }
}


