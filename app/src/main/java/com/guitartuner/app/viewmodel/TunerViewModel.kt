package com.guitartuner.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guitartuner.app.audio.AudioProcessor
import com.guitartuner.app.audio.PitchDetector
import com.guitartuner.app.data.TuningMode
import com.guitartuner.app.data.TuningState
import com.guitartuner.app.engine.TuningEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the tuner screen
 */
class TunerViewModel : ViewModel() {
    
    private val pitchDetector = PitchDetector()
    private val audioProcessor = AudioProcessor(pitchDetector)
    private val tuningEngine = TuningEngine()
    
    private val _tuningState = MutableStateFlow(TuningState())
    val tuningState: StateFlow<TuningState> = _tuningState.asStateFlow()
    
    private val _currentTuningMode = MutableStateFlow(TuningMode.STANDARD)
    val currentTuningMode: StateFlow<TuningMode> = _currentTuningMode.asStateFlow()
    
    private val _selectedStringIndex = MutableStateFlow<Int?>(null)
    val selectedStringIndex: StateFlow<Int?> = _selectedStringIndex.asStateFlow()
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    init {
        // Observe detected frequency and update tuning state
        viewModelScope.launch {
            pitchDetector.detectedFrequency.collect { frequency ->
                if (frequency > 0) {
                    val newState = tuningEngine.calculateTuningState(frequency)
                    _tuningState.value = newState
                }
            }
        }
    }
    
    /**
     * Starts listening to microphone input
     */
    fun startListening() {
        if (!_isListening.value) {
            audioProcessor.startListening()
            _isListening.value = true
        }
    }
    
    /**
     * Stops listening to microphone input
     */
    fun stopListening() {
        if (_isListening.value) {
            audioProcessor.stopListening()
            _isListening.value = false
        }
    }
    
    /**
     * Changes the tuning mode
     */
    fun setTuningMode(mode: TuningMode) {
        _currentTuningMode.value = mode
        tuningEngine.setTuningMode(mode)
        _selectedStringIndex.value = null // Reset string selection
    }
    
    /**
     * Selects a specific string to tune
     */
    fun selectString(stringIndex: Int?) {
        _selectedStringIndex.value = stringIndex
        if (stringIndex != null) {
            tuningEngine.setTargetString(stringIndex)
        } else {
            tuningEngine.clearTargetString()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}


