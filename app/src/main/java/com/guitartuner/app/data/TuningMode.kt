package com.guitartuner.app.data

/**
 * Represents different guitar tuning modes with their string frequencies
 */
enum class TuningMode(
    val displayName: String,
    val frequencies: List<Double>,
    val noteNames: List<String>
) {
    STANDARD(
        displayName = "Standard",
        frequencies = listOf(82.41, 110.00, 146.83, 196.00, 246.94, 329.63), // E2, A2, D3, G3, B3, E4
        noteNames = listOf("E2", "A2", "D3", "G3", "B3", "E4")
    ),
    DROP_D(
        displayName = "Drop D",
        frequencies = listOf(73.42, 110.00, 146.83, 196.00, 246.94, 329.63), // D2, A2, D3, G3, B3, E4
        noteNames = listOf("D2", "A2", "D3", "G3", "B3", "E4")
    ),
    DROP_C(
        displayName = "Drop C",
        frequencies = listOf(65.41, 98.00, 130.81, 174.61, 220.00, 293.66), // C2, G2, C3, F3, A3, D4
        noteNames = listOf("C2", "G2", "C3", "F3", "A3", "D4")
    ),
    OPEN_G(
        displayName = "Open G",
        frequencies = listOf(73.42, 98.00, 146.83, 196.00, 246.94, 293.66), // D2, G2, D3, G3, B3, D4
        noteNames = listOf("D2", "G2", "D3", "G3", "B3", "D4")
    ),
    OPEN_D(
        displayName = "Open D",
        frequencies = listOf(73.42, 110.00, 146.83, 185.00, 220.00, 293.66), // D2, A2, D3, F#3, A3, D4
        noteNames = listOf("D2", "A2", "D3", "F#3", "A3", "D4")
    ),
    DADGAD(
        displayName = "DADGAD",
        frequencies = listOf(73.42, 110.00, 146.83, 196.00, 220.00, 293.66), // D2, A2, D3, G3, A3, D4
        noteNames = listOf("D2", "A2", "D3", "G3", "A3", "D4")
    );

    companion object {
        fun fromDisplayName(name: String): TuningMode {
            return values().find { it.displayName == name } ?: STANDARD
        }
    }
}


