# Testing Guide

This document provides comprehensive testing instructions for the Guitar Tuner application.

## Prerequisites

- Android device with Android 7.0 (API 24) or higher
- Working microphone
- Guitar or bass instrument (or tone generator app)
- Quiet testing environment

## Test Setup

### Option 1: Real Instrument Testing
1. Have a guitar or bass ready
2. Find a quiet room with minimal background noise
3. Position device 20-30cm from guitar
4. Ensure instrument is reasonably in tune

### Option 2: Tone Generator Testing
Use a tone generator app to produce specific frequencies:
- Android: "Frequency Sound Generator", "Signal Generator"
- iOS: "Tone Generator", "SignalSuite"
- Web: Online tone generators (onlinetonegenerator.com, szynalski.com/tone-generator)

## Test Cases

### 1. Permission Handling

**Test 1.1: First Launch**
- Launch app for first time
- Verify permission request dialog appears
- Grant permission
- Verify tuner screen appears

**Test 1.2: Permission Denied**
- Deny permission
- Verify permission explanation screen remains
- Tap "Grant Permission" button
- Grant permission
- Verify tuner screen appears

**Test 1.3: Permission from Settings**
- Deny permission initially
- Go to Settings → Apps → Guitar Tuner → Permissions
- Manually grant microphone permission
- Return to app
- Verify app starts working

### 2. Pitch Detection Accuracy

Test frequencies for Standard tuning:

| String | Note | Frequency | 
|--------|------|-----------|
| 6th (low E) | E2 | 82.41 Hz |
| 5th (A) | A2 | 110.00 Hz |
| 4th (D) | D3 | 146.83 Hz |
| 3rd (G) | G3 | 196.00 Hz |
| 2nd (B) | B3 | 246.94 Hz |
| 1st (high E) | E4 | 329.63 Hz |

**Test 2.1: Note Detection**
- For each string, play note clearly
- Verify correct note name displayed (E2, A2, etc.)
- Verify frequency within ±2 Hz
- Verify cents deviation updates

**Test 2.2: Sharp/Flat Detection**
- Play string slightly sharp
- Verify red bar appears with positive cents
- Play string slightly flat
- Verify yellow bar appears with negative cents
- Tune to exact pitch
- Verify green bar appears with cents near 0

### 3. String Selection Feature

**Test 3.1: Selecting Strings**
- Tap string 1 button (E4)
- Verify button turns green
- Verify indicator appears: "Afinando Corda 1: E4"
- Play string 1 (high E)
- Verify tuning works normally
- Play string 6 (low E)
- Verify app still focuses on E4 (329 Hz), not E2

**Test 3.2: Switching Strings**
- Select string 6 (E2)
- Verify button turns green, string 1 button returns to dark
- Play string 6
- Verify tuning to E2 (82 Hz)

**Test 3.3: All Strings Mode**
- Select a specific string
- Tap "Todas" (All) button
- Verify all string buttons return to dark
- Verify indicator disappears
- Play any string
- Verify app detects closest matching note

### 4. Tuning Modes

**Test 4.1: Mode Selection**
- Tap tuning mode dropdown
- Verify all 6 modes listed
- Select each mode
- Verify target notes update correctly

**Test 4.2: Drop D Tuning**
- Select "Drop D" mode
- Verify target notes: D2, A2, D3, G3, B3, E4
- Select string 6
- Play or generate D2 (73.42 Hz)
- Verify detection and tuning

**Test 4.3: Other Tunings**
- Test Drop C, Open G, Open D, DADGAD
- Verify each mode's target frequencies
- Test at least 2 strings per mode

### 5. Visual Feedback

**Test 5.1: Color States**
- Play note 10 cents flat
- Verify yellow bar and negative cents
- Tune up gradually
- Verify bar transitions to green near 0
- Continue tuning sharp
- Verify bar transitions to red with positive cents

**Test 5.2: Stability**
- Play steady note
- Verify reading doesn't oscillate rapidly
- Should be stable within ±2 cents
- No rapid color changes

**Test 5.3: Cents Display**
- Verify cents value updates smoothly
- Check precision (1 decimal place)
- Verify sign (+ or -) displays correctly

### 6. Noise Filtering

**Test 6.1: Background Noise**
- Test with moderate background music
- Verify detection still works
- May require playing louder

**Test 6.2: Multiple Notes**
- Strum multiple strings simultaneously
- Verify app detects strongest frequency
- Should not rapidly jump between notes

**Test 6.3: Silence**
- Don't play anything
- Verify shows "Listening..." or "?"
- Verify no false detections

### 7. Edge Cases

**Test 7.1: Very Low Frequencies**
- Generate 40 Hz tone
- Verify app shows "Detecting" or ignores

**Test 7.2: Very High Frequencies**
- Generate 600 Hz tone
- Verify handled gracefully

**Test 7.3: Rapid Changes**
- Quickly play different notes
- Verify app keeps up without lag
- Verify smooth transitions

### 8. Lifecycle Management

**Test 8.1: Background/Foreground**
- Start app and begin listening
- Press home button
- Verify audio processing stops
- Return to app
- Verify audio processing resumes

**Test 8.2: Screen Rotation**
- Enable auto-rotate
- Rotate device while tuning
- Verify UI adapts
- Verify state preserved

**Test 8.3: Incoming Call**
- Start tuning
- Receive phone call
- Verify app pauses
- End call and return
- Verify app resumes

### 9. Performance

**Test 9.1: Latency**
- Play note and measure display update time
- Should update within 100-200ms

**Test 9.2: Battery Usage**
- Use app for 30 minutes
- Monitor battery drain
- Should be ~10-15% for 30 min

**Test 9.3: Memory**
- Check memory usage in developer options
- Should be stable, no leaks
- Typically <100MB

## Automated Testing

### Unit Tests (when implemented)

```bash
./gradlew test
```

### Instrumented Tests (when implemented)

```bash
./gradlew connectedAndroidTest
```

## Known Limitations

1. **Polyphonic Input**: YIN algorithm works best with single notes
2. **Ambient Noise**: Very loud environments affect accuracy
3. **Harmonics**: Distortion may cause harmonic detection
4. **Low Notes**: Notes below ~60 Hz less reliable

## Bug Reporting

When reporting issues, include:
- Device model and Android version
- Tuning mode being used
- String selection (if any)
- Frequency/note being played
- Expected vs actual behavior
- Screenshots if applicable
- Logcat output if app crashes

## Success Criteria

App passes testing if:
- All standard tuning notes detect accurately (±5 Hz)
- All 6 tuning modes work correctly
- String selection feature functions properly
- Visual feedback is clear and stable
- No rapid oscillation between states
- Permissions handled correctly
- App doesn't crash under normal use
- Performance is acceptable

## Test Environment

### Recommended
- Quiet room with minimal echo
- Acoustic guitar in good condition
- Device 20-30cm from sound source
- Single string played at moderate volume

### Not Recommended
- Very noisy environments
- Multiple sound sources simultaneously
- Damaged or very old guitar strings
- Extreme distance from microphone

## Performance Benchmarks

Target metrics:
- Pitch detection accuracy: ±1 Hz
- Update frequency: 5-10 Hz (100-200ms per update)
- False positive rate: <5%
- Stability: ±2 cents when holding steady note

---

For additional documentation, see:
- [README.md](README.md) - General information
- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Build guide
- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical architecture
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
