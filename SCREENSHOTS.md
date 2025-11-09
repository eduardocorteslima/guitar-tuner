# Screenshots

This document describes the app screens for documentation purposes. Add actual screenshots in a `/screenshots` directory when available.

## Main Tuner Screen

### Layout
- **Top Section**: Tuning mode selector dropdown
- **Middle Section**: 
  - Selected string indicator (when string is selected)
  - Large note display (e.g., "E2")
  - Frequency display in Hz
  - Chromatic scale with current note highlighted
- **Bottom Section**:
  - Color-coded tuning bar
  - Cents deviation display
  - String selector buttons (1-6)

### Visual States

#### In Tune
- Green fluorescent bar (#00FF41)
- Cents value near 0 (±5 cents)
- Label: "Tuned"

#### Too Low (Needs Tightening)
- Yellow bar (#FFD700)
- Negative cents value
- Label: "Loosen"

#### Too High (Needs Loosening)
- Red bar (#FF3131)
- Positive cents value
- Label: "Tighten"

#### Detecting
- Gray indicator
- Showing "Listening..." or "?" for note
- No bar movement

## Permission Screen

### First Launch
- App icon/guitar emoji
- Title: "Microphone Access Required"
- Explanation text
- Green "Grant Permission" button

## String Selection

### Unselected State
- All 6 string buttons shown in dark background
- "Todas" (All) button highlighted in green
- Equal visual weight for all strings

### Selected State
- Chosen string button in fluorescent green (#00FF41)
- Other strings in dark background
- Green indicator at top: "Afinando Corda X: Note"
- Elevated appearance (shadow)

## Color Scheme

### Primary Colors
- Background: #1A1A1A (dark)
- Surface: #2A2A2A (slightly lighter)
- Primary: #00FF41 (fluorescent green)
- Warning: #FFD700 (yellow)
- Error: #FF3131 (red)

### Text Colors
- Primary text: White
- Secondary text: Gray
- Disabled text: Dark gray

## Typography

- Display text (note): 96sp, bold
- Frequency: 24sp, regular
- Body text: 16sp, regular
- Small text: 12sp, regular

## Add Screenshots

To add screenshots to this repository:

1. Create a `screenshots` directory
2. Take screenshots on device or emulator
3. Name files descriptively:
   - `main-screen.png`
   - `permission-screen.png`
   - `string-selection.png`
   - `in-tune-green.png`
   - `too-low-yellow.png`
   - `too-high-red.png`
4. Update README.md with image links

### Screenshot Dimensions

Recommended sizes for Google Play:
- Phone: 1080 x 1920 (portrait)
- Tablet 7": 1200 x 1920
- Tablet 10": 1600 x 2560

For GitHub README:
- Resize to max width of 800px
- Use PNG format
- Optimize file size

