# AGENTS.md — Nepali Sign Language Detection

## Project Overview

Android (Kotlin) app that recognizes Nepali Sign Language hand gestures via the phone camera using MediaPipe. Fully offline — no Firebase, no backend, no login required. Package name: `com.example.nsl_mini`. Min SDK 24, target SDK 34.

## Key Files

| File | Purpose |
|---|---|
| `app/build.gradle.kts` | Dependencies: MediaPipe, Material, Glide, RecyclerView |
| `app/src/main/AndroidManifest.xml` | Permissions (CAMERA, INTERNET), 12 activities registered |
| `app/src/main/assets/gesture_recognizer.task` | MediaPipe gesture model |
| `app/src/main/java/com/example/nsl_mini/LocalQuizStorage.kt` | SharedPreferences-backed quiz persistence |

## Build & Run

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=~/Android/Sdk

# Build
./gradlew assembleDebug

# Build + Install
./gradlew installDebug

# Sideload
adb install app/build/outputs/apk/debug/app-debug.apk
```

Gradle 8.2 is used via wrapper. JDK 17+ required.

## Architecture

- **BaseActivity** — base class with navigation drawer (Home, Learn, Upload & Learn, Play Quiz, Admin Panel)
- **MainActivity** — main gesture recognition screen (TextureView + Camera2 + MediaPipe)
- **CameraHelper** — wraps Camera2 API, provides bitmap frames for recognition
- **GestureRecognitionHelper** — wraps MediaPipe `GestureRecognizer` in LIVE_STREAM mode, returns recognized gesture names and hand landmarks
- **LandmarkOverlayView** — custom View drawing hand landmarks on top of camera feed
- **LearnActivity** / **ConsonantsActivity** / **VowelsActivity** / **NumbersActivity** — browse sign references with images
- **PhotoModelActivity** — pick a photo from gallery and run gesture recognition on it
- **AddQuizActivity** — admin panel for creating quiz questions (image + 4 options + correct answer)
- **ViewQuizzesActivity** — list all quizzes with edit/delete
- **PlayQuizActivity** — play through quizzes (multiple choice)
- **QuizCompletedActivity** — shows score after quiz
- **LocalQuizStorage** — persists quizzes to SharedPreferences as JSON array; quiz images saved to internal storage
- **MyApp** (Application.kt) — minimal, no initialization needed

## Key Technical Details

- MediaPipe model runs at `RunningMode.LIVE_STREAM` with confidence threshold `0.65`
- Camera uses deprecated `createCaptureSession` (Camera2 API). Upgrade candidate.
- `onBackPressed()` is overridden in many activities (deprecated). `OnBackInvokedCallback` is the modern alternative.
- Quiz images are copied from gallery URI to `context.filesDir/quiz_images/{id}.jpg` for persistence
- No internet is required after the initial APK install

## Navigation Flow

```
SplashActivity → MainActivity (gesture camera)
                  ├── LearnActivity → Consonants / Vowels / Numbers
                  ├── PhotoModelActivity (upload & recognize)
                  ├── PlayQuizActivity → QuizCompletedActivity
                  ├── AnyQuestionActivity
                  └── AddQuizActivity (Admin Panel)
                       └── ViewQuizzesActivity
```

## Data Models

```kotlin
data class Quiz(
    var id: String? = null,
    var imageUrl: String? = null,     // File path or content URI
    var options: List<String> = listOf(),
    var correctAnswer: String? = null
)
```

## Quiz Storage

- **Key**: `local_quizzes` SharedPreferences file
- **Storage key**: `quizzes` → JSON array string
- **Images**: `/data/data/com.example.nsl_mini/files/quiz_images/{id}.jpg`
- **Data structure**: Each quiz serialized to JSON with `id`, `imagePath`, `options` (array), `correctAnswer`

## Deleted Files (Firebase removal)

The following were removed to make the app fully offline:
- `google-services.json`, `LoginActivity`, `SignupActivity`, `UserProfileActivity`
- `AdminActivity`, `UserAdapter`, `UserData`, `BaseActivityAdmin`
- Firebase dependencies from build.gradle.kts
- Firebase layouts (`activity_login`, `activity_signup`, `activity_user_profile`, `activity_admin`)
- `nav_menu_admin.xml`, `toolbaradmin.xml`, `nav_header_admin.xml`

## Conventions

- Kotlin DSL for Gradle
- XML layouts with `activity_*` / `item_*` / `nav_*` naming
- Glide for image loading
- All activities use `drawer_layout`, `toolbarUser`, `nav_view` as standard nav drawer IDs

## CLI Shortcuts (from project root)

```bash
# One-liner build + install
ANDROID_HOME=~/Android/Sdk ./gradlew installDebug

# Just build
ANDROID_HOME=~/Android/Sdk ./gradlew assembleDebug

# Clean then build
ANDROID_HOME=~/Android/Sdk ./gradlew clean assembleDebug
```
