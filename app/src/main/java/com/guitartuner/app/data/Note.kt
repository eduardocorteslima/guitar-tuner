package com.guitartuner.app.data

/**
 * Represents a musical note with its name and octave
 */
data class Note(
    val name: String,
    val octave: Int,
    val frequency: Double
) {
    val displayName: String
        get() = "$name$octave"
    
    companion object {
        // Chromatic scale starting from C
        private val NOTE_NAMES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        
        /**
         * Converts a frequency to a Note
         * Uses the formula: n = 12 × log2(f/440) + 69
         * where n is the MIDI note number, and A4 = 440 Hz = MIDI note 69
         */
        fun fromFrequency(frequency: Double): Note {
            if (frequency <= 0) {
                return Note("?", 0, 0.0)
            }
            
            // Calculate MIDI note number
            val midiNote = 12 * (Math.log(frequency / 440.0) / Math.log(2.0)) + 69
            val noteNumber = midiNote.toInt().coerceIn(0, 127)
            
            // Calculate octave and note name
            val octave = (noteNumber / 12) - 1
            val noteIndex = noteNumber % 12
            val noteName = NOTE_NAMES[noteIndex]
            
            return Note(noteName, octave, frequency)
        }
        
        /**
         * Gets the closest target frequency for a detected note
         */
        fun getClosestNoteFrequency(frequency: Double): Double {
            val midiNote = 12 * (Math.log(frequency / 440.0) / Math.log(2.0)) + 69
            val closestMidi = Math.round(midiNote).toInt().coerceIn(0, 127)
            
            // Convert back to frequency: f = 440 × 2^((n-69)/12)
            return 440.0 * Math.pow(2.0, (closestMidi - 69) / 12.0)
        }
    }
}


