# Build Instructions

This guide provides detailed instructions for building the Guitar Tuner Android application from source.

## Prerequisites

### Required Software

1. **Android Studio** (Arctic Fox or later)
   - Download: https://developer.android.com/studio
   - Recommended: Latest stable version

2. **JDK 11 or higher**
   - Usually bundled with Android Studio
   - Or download from: https://adoptium.net/

3. **Android SDK**
   - Install via Android Studio SDK Manager
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 34 (Android 14)

### Recommended

- Physical Android device for testing (emulator has limited microphone support)
- Git for version control
- Gradle 8.5+ (included via wrapper)

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/guitar-tuner.git
cd guitar-tuner
```

### 2. Configure SDK Path

Create or edit `local.properties` in the project root:

```properties
# Windows
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# macOS
sdk.dir=/Users/YourUsername/Library/Android/sdk

# Linux
sdk.dir=/home/YourUsername/Android/Sdk
```

Find your SDK path in Android Studio: **File → Project Structure → SDK Location**

### 3. Open in Android Studio

1. Launch Android Studio
2. **File → Open**
3. Select the `guitar-tuner` directory
4. Click **OK**

### 4. Sync Gradle Dependencies

Android Studio should automatically sync. If not:
- **File → Sync Project with Gradle Files**
- Wait for sync to complete

## Building

### Debug Build (for development)

```bash
# Unix/Linux/macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (for distribution)

```bash
# Unix/Linux/macOS
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Build and Install

```bash
# Build and install on connected device
./gradlew installDebug
```

## Running

### On Physical Device

1. **Enable Developer Options**:
   - Settings → About Phone
   - Tap "Build Number" 7 times

2. **Enable USB Debugging**:
   - Settings → Developer Options → USB Debugging

3. **Connect Device**:
   - Connect via USB
   - Allow USB debugging on device

4. **Run from Android Studio**:
   - Click green Run button (▶)
   - Or: **Run → Run 'app'** (Shift + F10)

### On Emulator

1. **Create AVD** (Android Virtual Device):
   - Tools → Device Manager
   - Create Device
   - Choose device (e.g., Pixel 5)
   - Select system image (API 24+)

2. **Launch and Run**:
   - Start emulator
   - Click Run button

Note: Emulator microphone support is limited

## Signing for Release

For production builds, you need to sign the APK:

### 1. Generate Keystore

```bash
keytool -genkey -v -keystore release.keystore -alias guitar_tuner \
  -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure Signing

Create `keystore.properties` in project root (DO NOT commit this):

```properties
storePassword=YourStorePassword
keyPassword=YourKeyPassword
keyAlias=guitar_tuner
storeFile=release.keystore
```

### 3. Build Signed APK

```bash
./gradlew assembleRelease
```

## Troubleshooting

### Gradle Sync Failed

**Issue**: Dependencies not downloading
- Check internet connection
- Try: **File → Invalidate Caches → Restart**
- Clear Gradle cache: `rm -rf ~/.gradle/caches/`

**Issue**: "SDK location not found"
- Ensure `local.properties` has correct `sdk.dir` path
- Verify Android SDK is installed

### Build Failed

**Issue**: "Could not find be.tarsos.dsp:core:2.5"
- Check internet connection
- Verify `https://mvn.0110.be/releases` is accessible
- Try: `./gradlew build --refresh-dependencies`

**Issue**: Kotlin compilation error
- Ensure Kotlin plugin is installed
- Check Kotlin version matches (1.9.20)
- Clean and rebuild: `./gradlew clean build`

### Runtime Issues

**Issue**: Permission denied
- Grant microphone permission in app
- Or: Settings → Apps → Guitar Tuner → Permissions → Microphone

**Issue**: No sound detected
- Verify device microphone works
- Check if another app is using microphone
- Test with louder input

**Issue**: App crashes
- Check logcat: **View → Tool Windows → Logcat**
- Look for stack traces
- Verify device is API 24+

## Testing

### Manual Testing

```bash
# Install and run
./gradlew installDebug

# View logs
adb logcat | grep GuitarTuner
```

### Automated Testing (when implemented)

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## Project Structure

```
guitar-tuner/
├── app/                    # Main application module
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Kotlin source files
│   │       ├── res/        # Resources
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts    # App-level build configuration
├── gradle/                 # Gradle wrapper
├── build.gradle.kts        # Root build configuration
├── settings.gradle.kts     # Project settings
└── local.properties        # Local SDK path (not in git)
```

## Dependencies

Key dependencies are managed in `app/build.gradle.kts`:

```kotlin
dependencies {
    // Audio Processing
    implementation("be.tarsos.dsp:core:2.5")
    implementation("be.tarsos.dsp:jvm:2.5")
    
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
}
```

## Build Variants

- **debug**: Development build with debugging symbols
- **release**: Optimized production build with ProGuard

## Advanced Configuration

### ProGuard Rules

Edit `app/proguard-rules.pro` for custom ProGuard configuration.

Current rules keep TarsosDSP classes:
```proguard
-keep class be.tarsos.dsp.** { *; }
```

### Build Performance

Improve build speed in `gradle.properties`:

```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

## Continuous Integration

GitHub Actions workflow example (create `.github/workflows/build.yml`):

```yaml
name: Android CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
    - name: Build with Gradle
      run: ./gradlew build
```

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [TarsosDSP GitHub](https://github.com/JorenSix/TarsosDSP)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

## Getting Help

- Check [TESTING.md](TESTING.md) for common issues
- Review Android Studio logcat output
- Open an issue on GitHub with details

## Next Steps

After successful build:
1. Test on physical device
2. Try all tuning modes
3. Test string selection feature
4. Verify in different environments
5. Report bugs or suggest features

---

Happy Building!
