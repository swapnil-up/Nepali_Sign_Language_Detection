<div align="center">

# Gesture \u0917\u0941\u0930\u0941 — Nepali Sign Language Detection

An Android app that uses **MediaPipe** to recognize Nepali Sign Language gestures in real time via your phone camera — consonants, vowels, and numbers. Also includes a local quiz system for practice.

**No backend needed.** Everything runs on-device with no account required.

</div>

## Features

- **Real-time gesture recognition** — point your camera at a hand sign and see the Devanagari character appear on screen
- **Consonants, Vowels, Numbers** — all 36 consonants, 13 vowels, and 0–9 with reference images
- **Upload & Recognize** — pick a photo from your gallery and run gesture recognition on it
- **Local Quiz System** — create quizzes with images and play them offline (Admin Panel → Add Quiz)
- **100% offline** — no Firebase, no login, no internet required after install

## Quick Start

```bash
# Prerequisites: JDK 17+ and Android SDK (platform 34+, build-tools 34+)

# Clone
git clone https://github.com/swapnil-up/NSL_mini
cd NSL_mini

# Build debug APK
ANDROID_HOME=~/Android/Sdk ./gradlew assembleDebug

# Install on connected device (USB debugging enabled)
ANDROID_HOME=~/Android/Sdk adb install app/build/outputs/apk/debug/app-debug.apk
```

The APK is ~85MB (MediaPipe model included).

## Requirements

- **JDK 17+**
- **Android SDK** with platform `android-34` or later and build-tools 34+
- **Android device** running Android 7.0 (API 24) or newer with a camera

## CLI Reference

| Command | What it does |
|---|---|
| `ANDROID_HOME=~/Android/Sdk ./gradlew assembleDebug` | Build debug APK |
| `ANDROID_HOME=~/Android/Sdk ./gradlew installDebug` | Build + install on connected device |
| `adb install app/build/outputs/apk/debug/app-debug.apk` | Sideload existing APK |
| `ANDROID_HOME=~/Android/Sdk ./gradlew clean assembleDebug` | Clean + rebuild |

## Project Structure

```
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   ├── assets/
│   │   ├── gesture_recognizer.task
│   │   └── gesture_recognizer1.task   # MediaPipe gesture models
│   ├── java/com/example/nsl_mini/
│   │   ├── MainActivity.kt            # Camera + gesture recognition
│   │   ├── CameraHelper.kt            # Camera2 wrapper
│   │   ├── GestureRecognitionHelper.kt# MediaPipe wrapper
│   │   ├── LandmarkOverlayView.kt     # Hand landmark overlay
│   │   ├── LearnActivity.kt           # Browse consonants/vowels/numbers
│   │   ├── ConsonantsActivity.kt
│   │   ├── VowelsActivity.kt
│   │   ├── NumbersActivity.kt
│   │   ├── SplashActivity.kt
│   │   ├── AddQuizActivity.kt         # Create/edit quiz questions (local)
│   │   ├── ViewQuizzesActivity.kt     # List/manage quizzes (local)
│   │   ├── PlayQuizActivity.kt        # Play through quizzes
│   │   ├── QuizCompletedActivity.kt   # Score screen
│   │   ├── AnyQuestionActivity.kt
│   │   ├── PhotoModelActivity.kt      # Recognize gestures from gallery
│   │   ├── LocalQuizStorage.kt        # SharedPreferences-backed quiz storage
│   │   ├── Quiz.kt                    # Data model
│   │   ├── QuizzesAdapter.kt
│   │   ├── GestureRecognizerResultsAdapter.kt
│   │   ├── BaseActivity.kt            # Base with nav drawer
│   │   ├── GestureRecognizerResultsAdapter.kt
│   │   └── Application.kt
│   └── res/
│       ├── drawable/                  # Sign reference images + UI assets
│       ├── layout/                    # Layout XML files
│       ├── menu/nav_menu.xml          # Navigation drawer items
│       ├── raw/splash_video.mp4       # Splash screen video
│       └── values/                    # Colors, strings, themes
├── build.gradle.kts
└── proguard-rules.pro

gradle/
└── wrapper/
    └── gradle-wrapper.properties      # Gradle 8.2
```

## Navigation

```
SplashScreen → Gesture Camera (MainActivity)
                ├── Learn → Consonants / Vowels / Numbers
                ├── Upload & Learn (recognize from gallery)
                ├── Play Quiz
                ├── ? (Any Question)
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
| Build | Gradle 8.2 / AGP 8.2.1 |
