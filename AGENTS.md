# AGENTS.md — Nepali Sign Language Detection

## Project Overview

Android (Kotlin) app that recognizes Nepali Sign Language hand gestures via the phone camera using MediaPipe. Fully offline — no Firebase, no backend, no login required. Package name: `com.example.nsl_mini`. Min SDK 24, target SDK 34.

## Build & Run

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=~/Android/Sdk

# Build
./gradlew assembleDebug

# Build + Install
./gradlew installDebug

# Unit tests
./gradlew test
```

Gradle 8.2 via wrapper. JDK 17+ required.

## Architecture

### Sources: `app/src/main/java/com/example/nsl_mini/`

| File | Role |
|---|---|
| **MainActivity** | Camera + gesture recognition with MediaPipe LIVE_STREAM mode |
| **CameraHelper** | Camera2 wrapper, frame-skipped (every 3rd frame) to reduce GC pressure |
| **GestureRecognitionHelper** | Implements `GestureRecognizer`. Wraps MediaPipe, converts `NormalizedLandmark` → `HandLandmark` |
| **GestureRecognizer** | Interface with `setup()` + `recognizeAsync()` — allows `FakeGestureRecognizer` for tests |
| **FakeGestureRecognizer** | Test double returning configured results |
| **HandLandmark** | `data class(x, y, z)` — decouples overlay from MediaPipe types |
| **LandmarkOverlayView** | Custom View drawing hand landmarks from `List<HandLandmark>` |
| **GestureResultFormatter** | `firstGesture()`, `GESTURE_MODEL_FILE`, `compoundCharacters` list |
| **BaseActivity** | Base with nav drawer. `onNavItemSelected()` is open for subclass override |
| **LearnActivity** | 3 cards → launches `PagerActivity` with `intArrayExtra` drawable arrays |
| **PagerActivity** | Parameterized pager (replaces Consonants/Vowels/NumbersActivity) |
| **PhotoModelActivity** | Pick gallery photo, run gesture recognition on it |
| **PlayQuizActivity** | Multiple-choice quiz via `QuizStorage`. Guards empty list |
| **QuizCompletedActivity** | Shows final score |
| **QuizStorage** | Interface: `saveQuiz`, `deleteQuiz`, `loadAllQuizzes`, `loadQuiz` |
| **LocalQuizStorage** | SharedPreferences-backed `QuizStorage` impl; image files in `filesDir/quiz_images/` |
| **InMemoryQuizStorage** | Pure-Kotlin `QuizStorage` for unit tests |
| **AddQuizActivity** | Admin: create/edit quiz (image + 4 options + answer) |
| **ViewQuizzesActivity** | List all quizzes with edit/delete |
| **QuizzesAdapter** | RecyclerView adapter for quiz list |
| **Quiz** | `data class(id, imageUrl, options, correctAnswer)` |
| **MyApp** | Minimal `Application` subclass, no init needed |

### Tests: `app/src/test/java/com/example/nsl_mini/`

| File | Coverage |
|---|---|
| `QuizStorageTest.kt` | CRUD, update, delete, empty, nonexistent (7 tests) |
| `GestureResultFormatterTest.kt` | firstGesture extraction, none/empty filtering, compound chars (5 tests) |
| `FakeGestureRecognizerTest.kt` | Setup tracking, configured result (2 tests) |

## Key Technical Details

- MediaPipe model: `gesture_recognizer1.task` in `assets/`, loaded via `GestureResultFormatter.GESTURE_MODEL_FILE`
- Confidence threshold: `0.65` (hardcoded in `GestureRecognitionHelper`)
- Camera frame capture: every 3rd frame (frame-skipped, ~10fps) — reduces bitmap allocations by 66%
- Quiz images: copied from gallery URI to `context.filesDir/quiz_images/{id}.jpg`
- Compound characters for backspace: `GestureResultFormatter.compoundCharacters` (`क्ष`, `त्र`, `ज्ञ`, `अं`, `अः`)
- No internet required after APK install

## Navigation Flow

```
MainActivity (gesture camera)
  ├── LearnActivity → PagerActivity (vowels / consonants / numbers)
  ├── PhotoModelActivity (upload & recognize)
  ├── PlayQuizActivity → QuizCompletedActivity
  └── AddQuizActivity (Admin Panel) → ViewQuizzesActivity
```

## Quiz Storage

- **SharedPreferences file**: `local_quizzes`
- **Key**: `quizzes` → JSON array string
- **Images**: `/data/data/com.example.nsl_mini/files/quiz_images/{id}.jpg`
- **Serialization**: `org.json.JSONArray`/`JSONObject` (no Gson dependency)

## Deleted / Removed

- Firebase: `google-services.json`, all Firebase deps, `LoginActivity`, `SignupActivity`, `UserProfileActivity`, `AdminActivity`, `UserAdapter`, `UserData`, `BaseActivityAdmin`, Firebase layouts
- Dead code: `CameraSource.kt`, `GestureRecognizerResultsAdapter.kt`, `AnyQuestionActivity`, `getQuizCount()`
- Unused build deps: `dataBinding`, `circleimageview`, `kotlinx-coroutines-android`
- Unused manifest permissions: `RECORD_AUDIO`, `WRITE_EXTERNAL_STORAGE`
- Splash: `SplashActivity`, `activity_splash.xml`, `splash_video.mp4`
- Redundant pager activities: `ConsonantsActivity`, `VowelsActivity`, `NumbersActivity` + their 3 layouts

## Notes

- Most activities override deprecated `onBackPressed()` — upgrade candidate to `OnBackInvokedCallback`
- `CameraHelper` uses deprecated `createCaptureSession` — upgrade candidate to `createCaptureSession` with `OutputConfiguration`
- `PhotoModelActivity` uses deprecated `startActivityForResult` — already has a modern `registerForActivityResult` example in `AddQuizActivity`
- Nav drawer items handled via `BaseActivity.onNavItemSelected()` — subclass override supported
