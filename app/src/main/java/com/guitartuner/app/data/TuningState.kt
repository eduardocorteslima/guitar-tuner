package com.guitartuner.app.data

/**
 * Represents the current tuning state
 */
data class TuningState(
    val detectedNote: Note = Note("?", 0, 0.0),
    val targetFrequency: Double = 0.0,
    val cents: Double = 0.0,
    val tuningStatus: TuningStatus = TuningStatus.DETECTING
)

/**
 * Tuning status indicators
 */
enum class TuningStatus {
    DETECTING,      // No clear signal
    TOO_LOW,        // Below target (yellow)
    IN_TUNE,        // Within tolerance (green)
    TOO_HIGH        // Above target (red)
}


