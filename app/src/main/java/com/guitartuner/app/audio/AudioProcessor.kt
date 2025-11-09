package com.guitartuner.app.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.UniversalAudioInputStream
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream

/**
 * Handles audio capture and processing using Android AudioRecord API
 */
class AudioProcessor(private val pitchDetector: PitchDetector) {
    
    private var audioDispatcher: AudioDispatcher? = null
    private var processingJob: Job? = null
    private var audioRecord: AudioRecord? = null
    
    companion object {
        private const val TAG = "AudioProcessor"
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
    
    /**
     * Gets the detected frequency flow from the pitch detector
     */
    val detectedFrequency: StateFlow<Double>
        get() = pitchDetector.detectedFrequency
    
    /**
     * Starts audio capture and pitch detection
     */
    fun startListening() {
        if (processingJob?.isActive == true) {
            Log.d(TAG, "Already listening")
            return
        }
        
        processingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val bufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT
                )
                
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
                
                val audioStream = AndroidAudioInputStream(audioRecord!!)
                val audioFormat = TarsosDSPAudioFormat(
                    SAMPLE_RATE.toFloat(),
                    16,
                    1,
                    true,
                    false
                )
                
                audioDispatcher = AudioDispatcher(
                    UniversalAudioInputStream(audioStream, audioFormat),
                    pitchDetector.getBufferSize(),
                    0
                )
                
                audioDispatcher?.addAudioProcessor(pitchDetector.createPitchProcessor())
                
                audioRecord?.startRecording()
                Log.d(TAG, "Starting audio processing")
                audioDispatcher?.run()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error in audio processing", e)
            }
        }
    }
    
    /**
     * Stops audio capture and pitch detection
     */
    fun stopListening() {
        Log.d(TAG, "Stopping audio processing")
        
        audioDispatcher?.stop()
        audioDispatcher = null
        
        processingJob?.cancel()
        processingJob = null
        
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio record", e)
        }
    }
    
    /**
     * Checks if audio processing is currently active
     */
    fun isListening(): Boolean {
        return processingJob?.isActive == true
    }
    
    /**
     * Custom InputStream wrapper for Android AudioRecord
     */
    private class AndroidAudioInputStream(private val audioRecord: AudioRecord) : InputStream() {
        
        override fun read(): Int {
            val buffer = ByteArray(1)
            val result = read(buffer, 0, 1)
            return if (result == -1) -1 else buffer[0].toInt() and 0xFF
        }
        
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            return audioRecord.read(b, off, len)
        }
        
        override fun close() {
            try {
                audioRecord.stop()
                audioRecord.release()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
