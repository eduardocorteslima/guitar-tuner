# Architecture Documentation

This document describes the technical architecture of the Guitar Tuner application.

## Overview

Guitar Tuner follows Clean Architecture principles with MVVM (Model-View-ViewModel) pattern. The application is built with Kotlin and Jetpack Compose, leveraging modern Android development practices.

## Architecture Diagram

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                │
│  ┌─────────────┐      ┌─────────────┐      │
│  │   UI        │◄─────┤  ViewModel  │      │
│  │ (Compose)   │      │             │      │
│  └─────────────┘      └──────┬──────┘      │
└─────────────────────────────┼──────────────┘
                              │
┌─────────────────────────────┼──────────────┐
│           Domain Layer      │              │
│         ┌──────────────────▼───────────┐   │
│         │   TuningEngine               │   │
│         │  (Business Logic)            │   │
│         └────────────┬─────────────────┘   │
└──────────────────────┼─────────────────────┘
                       │
┌──────────────────────┼─────────────────────┐
│           Data Layer │                     │
│  ┌─────────────┐  ┌──▼──────────┐         │
│  │   Audio     │  │   Models    │         │
│  │ Processor   │  │   (Data)    │         │
│  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────┘
```

## Core Components

### Presentation Layer

#### MainActivity
Entry point and lifecycle manager.
- Handles microphone permission requests
- Manages app lifecycle (start/stop listening)
- Hosts Compose UI

#### TunerViewModel
State management with reactive streams.
- Coordinates audio processing
- Manages tuning mode and string selection
- Provides StateFlow for UI updates

#### TunerScreen (Composable)
Main UI presentation.
- Tuning mode selector
- Note and frequency display
- Chromatic scale visualization
- Visual tuning bar
- String selector buttons

#### TuningBar (Composable)
Custom visual feedback component.
- Color-coded bar animation
- Cents deviation display
- Position indicator

### Domain Layer

#### TuningEngine
Core business logic.

**Key Methods:**
- `calculateTuningState(frequency)`: Converts frequency to tuning state
- `findClosestTargetFrequency(frequency)`: Finds target from tuning mode or selected string
- `calculateCents(detected, target)`: Computes cents deviation

**Algorithms:**
```kotlin
// Frequency to MIDI note
midiNote = 12 × log₂(f/440) + 69

// Cents deviation
cents = 1200 × log₂(f_detected / f_target)

// Tuning status
if |cents| ≤ 5 → IN_TUNE
else if cents < 0 → TOO_LOW
else → TOO_HIGH
```

### Data Layer

#### AudioProcessor
Manages microphone input.
- Captures audio at 44.1 kHz via AudioRecord
- Provides audio stream to pitch detector
- Runs on background coroutine

#### PitchDetector
Extracts pitch from audio.
- Uses TarsosDSP YIN algorithm
- Implements noise filtering
- Applies median smoothing filter
- Exposes frequency via StateFlow

**Noise Filtering:**
- Probability threshold: 95%
- Frequency range: 60-500 Hz
- Median filter: 5-sample window
- Stability threshold: 10 Hz

#### Data Models

**Note**
```kotlin
data class Note(
    val name: String,       // "E", "A", "F#", etc.
    val octave: Int,        // 2, 3, 4, etc.
    val frequency: Double   // Hz
)
```

**TuningMode**
```kotlin
enum class TuningMode(
    val displayName: String,
    val frequencies: List<Double>,
    val noteNames: List<String>
)
```

**TuningState**
```kotlin
data class TuningState(
    val detectedNote: Note,
    val targetFrequency: Double,
    val cents: Double,
    val tuningStatus: TuningStatus
)
```

## Data Flow

### Complete Pipeline

```
Microphone
    ↓ (AudioRecord, 44.1 kHz)
AudioProcessor
    ↓ (PCM samples)
PitchDetector (TarsosDSP YIN)
    ↓ (Median filter + thresholding)
StateFlow<Frequency>
    ↓ (collect in ViewModel)
TuningEngine
    ↓ (Calculate note, cents, status)
StateFlow<TuningState>
    ↓ (collectAsState in Compose)
UI Update
```

### Reactive Updates

```kotlin
// PitchDetector emits frequency
private val _detectedFrequency = MutableStateFlow(0.0)

// ViewModel observes and processes
viewModelScope.launch {
    pitchDetector.detectedFrequency.collect { frequency ->
        val state = tuningEngine.calculateTuningState(frequency)
        _tuningState.value = state
    }
}

// UI reacts to state changes
val tuningState by viewModel.tuningState.collectAsState()
```

## Threading Model

### Main Thread
- Compose UI rendering
- User interactions
- StateFlow updates

### IO Dispatcher
- Audio capture (AudioProcessor)
- Pitch detection (TarsosDSP)

### Coroutine Scopes
- **ViewModelScope**: Tied to ViewModel lifecycle
- **Custom Scope**: For audio processing

## Performance Optimizations

### Audio Processing
- Buffer size: 4096 samples (balanced latency/accuracy)
- No FFT overlap (faster processing)
- Single-threaded audio capture

### Noise Filtering
1. **Probability Filter**: Only accept >95% probability
2. **Range Filter**: Reject <60 Hz or >500 Hz
3. **Median Filter**: 5-sample window to reduce spikes
4. **Stability Check**: Reject jumps >10 Hz

### UI Performance
- Compose recomposes only changed components
- Animations use hardware acceleration
- StateFlow caches latest value only

### Memory Management
- Audio buffers reused
- StateFlow doesn't retain history
- ViewModel survives config changes
- Proper cleanup in onCleared()

## Security & Privacy

### Permissions
- RECORD_AUDIO: Required for microphone access
- Runtime permission with user explanation
- No persistent audio recording

### Data Privacy
- No audio saved to disk
- No network transmission
- All processing local on device
- No personal data collection
- No analytics or tracking

## Error Handling

### Audio Errors
```kotlin
try {
    audioDispatcher?.run()
} catch (e: Exception) {
    Log.e(TAG, "Error in audio processing", e)
    // Graceful degradation - shows "Detecting"
}
```

### Edge Cases
- Very low/high frequencies: Filtered out
- Silence: Shows "Listening..." state
- Multiple notes: Detects strongest signal
- Background noise: Filtered by probability threshold

## Testing Strategy

### Manual Testing
- Real guitar/bass testing (see TESTING.md)
- Tone generator testing
- Various Android devices
- Different acoustic environments

### Automated Testing (Future)
- Unit tests for TuningEngine
- Integration tests with mock audio
- UI tests with Compose Testing
- Performance benchmarking

## Build Configuration

### Gradle Setup
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
}
```

### ProGuard
```proguard
-keep class be.tarsos.dsp.** { *; }
```

## Dependencies

### Core
- `be.tarsos.dsp:core:2.5` - Audio DSP library
- `be.tarsos.dsp:jvm:2.5` - JVM-specific audio I/O

### UI
- `androidx.compose.ui:ui:1.5.4`
- `androidx.compose.material3:material3:1.1.2`

### Async
- `kotlinx-coroutines-android:1.7.3`

### Lifecycle
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2`

## Future Enhancements

### Technical Improvements
- GPU acceleration for FFT
- Adaptive buffer sizing
- Better noise cancellation (spectral subtraction)
- Multi-threaded pitch detection

### Features
- Custom tuning modes
- Auto-detect tuning
- Tuning history
- Haptic feedback
- Support for more instruments

### Testing
- Unit test coverage >80%
- UI test automation
- CI/CD pipeline
- Automated performance testing

## Resources

### Documentation
- [README.md](README.md) - General information
- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Build guide
- [TESTING.md](TESTING.md) - Test procedures
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [CHANGELOG.md](CHANGELOG.md) - Version history

### External Links
- [TarsosDSP](https://github.com/JorenSix/TarsosDSP)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [YIN Paper](http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf)

---

For questions about the architecture, open an issue on GitHub.

