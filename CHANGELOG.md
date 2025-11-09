# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-11-09

### Added
- Real-time pitch detection using YIN algorithm via TarsosDSP
- Support for 6 tuning modes (Standard, Drop D, Drop C, Open G, Open D, DADGAD)
- String selection feature - choose specific strings to tune
- Visual feedback system with color-coded bars (green/yellow/red)
- Chromatic note display showing all 12 notes
- Frequency display in Hz
- Cents deviation display with ±0.1 cent precision
- Material Design 3 UI with Jetpack Compose
- Noise filtering and frequency smoothing (median filter)
- Bilingual support (English and Portuguese)
- Microphone permission handling
- Proper lifecycle management (pause/resume)

### Technical Details
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Kotlin 1.9.20
- Jetpack Compose 1.5.4
- TarsosDSP 2.5
- Gradle 8.5

### Performance
- Audio latency: 80-170ms
- Frequency accuracy: ±1 Hz for 60-500 Hz range
- Probability threshold: 95% for valid detection
- Median filter with 5-sample window
- Stability threshold: 10 Hz

---

## Future Releases

### Planned for 1.1.0
- Custom tuning mode creation
- Haptic feedback when in tune
- Improved noise gate controls
- Performance optimizations

### Planned for 1.2.0
- Tuning history tracking
- Support for additional instruments
- Improved tablet and landscape layouts

### Under Consideration
- Dark/Light theme toggle
- Audio recording feature
- Metronome integration
- Practice mode with timer

---

For detailed changes, see commit history on GitHub.

