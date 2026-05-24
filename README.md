<div align="center">

# Gesture गुरु — Nepali Sign Language Detection

An Android app that uses **MediaPipe** to recognize Nepali Sign Language gestures in real time via your phone camera — consonants and vowels. Also includes **practice mode**, **text-to-speech**, and a local quiz system.

**No backend needed.** Everything runs on-device with no account required.

</div>

## Features

- **Real-time gesture recognition** — point your camera at a hand sign and see the Devanagari character appear on screen
- **Practice Mode** — the app shows a target character; sign it correctly and get instant feedback. Characters shuffled each session. Tap "Show sign" to see a reference image.
- **Text-to-Speech** — press the **Read** button to hear your detected text spoken aloud (Nepali → Hindi → English fallback)
- **Consonants & Vowels** — reference images for all consonants and vowels
- **Upload & Recognize** — pick a photo from your gallery and run gesture recognition on it
- **Local Quiz System** — create, edit, and play quizzes offline (Admin Panel → Add Quiz / View Quizzes)
- **100% offline** — no Firebase, no login, no internet required after install

## Quick Start

```bash
# Prerequisites: JDK 17+ and Android SDK (platform 34+, build-tools 34+)

# Clone
git clone https://github.com/swapnil-up/NSL_mini
cd NSL_mini

# Build + install on connected device
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=~/Android/Sdk
./gradlew installDebug
```

The APK is ~85MB (MediaPipe model included).

## Requirements

- **JDK 17+**
- **Android SDK** with platform `android-34` or later and build-tools 34+
- **Android device** running Android 7.0 (API 24) or newer with a camera

## CLI Reference

| Command | What it does |
|---|---|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew installDebug` | Build + install on connected device |
| `adb install app/build/outputs/apk/debug/app-debug.apk` | Sideload existing APK |
| `./gradlew test` | Run unit tests |

## Controls

The bottom bar has 5 buttons:

| Button | Free Mode | Practice Mode |
|---|---|---|
| **Practice / Exit** | Toggle practice mode on | Exit practice mode |
| **Cam** | Flip front/back camera | Flip front/back camera |
| **Del** | Backspace last character | Skip current target |
| **Clr** | Clear all text | Stop practice session |
| **Read** | Speak accumulated text aloud | Speak target character aloud |

## Project Structure

```
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   ├── assets/
│   │   └── gesture_recognizer1.task       # MediaPipe gesture model
│   ├── java/com/example/nsl_mini/
│   │   ├── MainActivity.kt                # Camera + gesture + practice + TTS
│   │   ├── CameraHelper.kt                # Camera2 wrapper (frame-skipped)
│   │   ├── GestureRecognitionHelper.kt    # MediaPipe wrapper
│   │   ├── GestureRecognizer.kt           # Interface for recognizer
│   │   ├── FakeGestureRecognizer.kt       # Test double
│   │   ├── HandLandmark.kt                # Decoupled landmark data class
│   │   ├── LandmarkOverlayView.kt         # Hand landmark overlay
│   │   ├── GestureResultFormatter.kt      # Formatting, practice chars, sign images
│   │   ├── BaseActivity.kt               # Base with nav drawer
│   │   ├── LearnActivity.kt              # Browse consonants & vowels
│   │   ├── PagerActivity.kt              # Parameterized pager
│   │   ├── PhotoModelActivity.kt         # Recognize gestures from gallery
│   │   ├── PlayQuizActivity.kt           # Play through quizzes
│   │   ├── QuizCompletedActivity.kt      # Score screen
│   │   ├── Quiz.kt                       # Data model
│   │   ├── QuizStorage.kt               # Interface for quiz persistence
│   │   ├── LocalQuizStorage.kt           # SharedPreferences-backed impl
│   │   ├── InMemoryQuizStorage.kt        # In-memory impl for tests
│   │   ├── AddQuizActivity.kt            # Create/edit quiz questions
│   │   ├── ViewQuizzesActivity.kt        # List/manage quizzes
│   │   ├── QuizzesAdapter.kt
│   │   └── Application.kt
│   ├── res/
│   │   ├── drawable/                     # Sign reference images + UI assets
│   │   ├── drawable/hint_chip.xml        # Practice hint button background
│   │   ├── layout/                       # Layout XML files
│   │   ├── menu/nav_menu.xml             # Navigation drawer items
│   │   └── values/                       # Colors, strings, themes
│   └── test/java/com/example/nsl_mini/
│       ├── QuizStorageTest.kt
│       ├── GestureResultFormatterTest.kt
│       └── FakeGestureRecognizerTest.kt
├── build.gradle.kts
└── proguard-rules.pro

gradle/
└── wrapper/
    └── gradle-wrapper.properties         # Gradle 8.2
llm/
├── architecture-analysis.md              # Session notes (gitignored)
└── improvement-plan.md                   # Future roadmap (gitignored)
```

## Navigation

```
Gesture Camera (MainActivity)
  ├── Learn → Vowels / Consonants (parameterized PagerActivity)
  ├── Upload & Learn (PhotoModelActivity — recognize from gallery)
  ├── Play Quiz → Quiz Completed
  └── Admin Panel → Add Quiz / View Quizzes
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML layouts, Material Design, Navigation Drawer |
| Camera | Camera2 API via TextureView |
| ML Inference | MediaPipe Tasks Vision (GestureRecognizer) |
| Local Storage | SharedPreferences (quizzes), internal files (images) |
| Image Loading | Glide |
| Testing | JUnit 4 |
| Build | Gradle 8.2 / AGP 8.2.1 |
