# Contributing to Guitar Tuner

Thank you for your interest in contributing to Guitar Tuner! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community and project

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When creating a bug report, include:

- **Device Information**: Model, manufacturer, Android version
- **App Version**: Found in app settings or build.gradle
- **Steps to Reproduce**: Clear, numbered steps
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Screenshots**: If applicable
- **Logs**: Any relevant logcat output

### Suggesting Enhancements

Enhancement suggestions are welcome! Please include:

- **Clear Description**: What feature you want and why
- **Use Case**: How would this feature be used?
- **Mockups**: Visual designs if applicable
- **Implementation Ideas**: Technical approach if you have one

### Pull Requests

1. **Fork the Repository**
   ```bash
   git clone https://github.com/yourusername/guitar-tuner.git
   ```

2. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Your Changes**
   - Follow the existing code style
   - Add comments for complex logic
   - Update documentation as needed

4. **Test Your Changes**
   - Build the app successfully
   - Test on physical device if possible
   - Verify no new lint errors

5. **Commit Your Changes**
   ```bash
   git commit -m "Add feature: your feature description"
   ```

6. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request**
   - Provide clear description of changes
   - Reference any related issues
   - Include screenshots if UI changes

## Development Setup

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11+
- Android SDK API 24+

### Building

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Code Style

This project follows standard Kotlin coding conventions:

- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable names
- Add KDoc comments for public APIs

## Project Structure

```
app/src/main/java/com/guitartuner/app/
├── audio/          # Audio capture and processing
├── data/           # Data models
├── engine/         # Business logic
├── ui/             # UI components and screens
└── viewmodel/      # State management
```

## Areas for Contribution

### High Priority

- Unit tests for TuningEngine
- UI tests with Compose Testing
- Accessibility improvements
- Performance optimizations

### Medium Priority

- Additional tuning modes
- Custom tuning creation
- Haptic feedback
- Tuning history

### Low Priority

- Theme customization
- Tablet layout optimization
- Additional language translations

## Questions?

Feel free to open an issue with the "question" label if you need clarification on anything.

## Recognition

Contributors will be recognized in the project README and release notes.

Thank you for contributing!

