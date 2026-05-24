# AGENTS.md — Nepali Sign Language Detection

## Project Overview

Android (Kotlin) app that recognizes Nepali Sign Language hand gestures via the phone camera using MediaPipe. Fully offline — no Firebase, no backend, no login required. Package name: `com.example.nsl_mini`. Min SDK 24, target SDK 35.

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
| **MainActivity** | Camera + gesture recognition with MediaPipe LIVE_STREAM mode, practice mode, TTS, confidence overlay, adjustable threshold |
| **CameraHelper** | Camera2 wrapper, frame-skipped (every 3rd frame) to reduce GC pressure |
| **GestureRecognitionHelper** | Implements `GestureRecognizer`. Wraps MediaPipe, converts `NormalizedLandmark` → `HandLandmark`. Exposes `confidenceThreshold` (mutable, recreates recognizer on change) and `lastConfidence` |
| **GestureRecognizer** | Interface with `setup()` + `recognizeAsync()` — allows `FakeGestureRecognizer` for tests |
| **FakeGestureRecognizer** | Test double returning configured results |
| **HandLandmark** | `data class(x, y, z)` — decouples overlay from MediaPipe types |
| **LandmarkOverlayView** | Custom View drawing hand landmarks from `List<HandLandmark>` |
| **GestureResultFormatter** | `firstGesture()`, `GESTURE_MODEL_FILE`, `compoundCharacters`, `practiceCharacters`, `characterLabels`, `signImages` |
| **BaseActivity** | Base with nav drawer. `onNavItemSelected()` is open for subclass override |
| **LearnActivity** | 2 cards (vowels, consonants) → launches `PagerActivity` with `intArrayExtra` drawable arrays |
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

### New: `drawable/hint_chip.xml`

Rounded border background for the "Show sign" hint button in practice mode.

### Tests: `app/src/test/java/com/example/nsl_mini/`

| File | Coverage |
|---|---|
| `QuizStorageTest.kt` | CRUD, update, delete, empty, nonexistent (7 tests) |
| `GestureResultFormatterTest.kt` | firstGesture extraction, none/empty filtering, compound chars (5 tests) |
| `FakeGestureRecognizerTest.kt` | Setup tracking, configured result (2 tests) |

## Key Technical Details

- MediaPipe model: `gesture_recognizer1.task` in `assets/`, loaded via `GestureResultFormatter.GESTURE_MODEL_FILE`
- Confidence threshold: `0.65` (default, user-adjustable 0.50–0.95 via settings gear in result bar; changing it recreates the MediaPipe recognizer)
- Camera frame capture: every 3rd frame (frame-skipped, ~10fps) — reduces bitmap allocations by 66%
- Quiz images: copied from gallery URI to `context.filesDir/quiz_images/{id}.jpg`
- Compound characters for backspace: `GestureResultFormatter.compoundCharacters` (`क्ष`, `त्र`, `ज्ञ`, `अं`, `अः`)
- Practice characters: 47 Devanagari chars (11 vowels + 13 vowel modifiers + 33 consonants + 3 conjuncts) in `GestureResultFormatter.practiceCharacters`. No numbers — not in model training.
- Sign images mapped via `GestureResultFormatter.signImages: Map<String, Int>` for the "Show sign" hint dialog.
- Character labels transliterated via `GestureResultFormatter.characterLabels: Map<String, String>`.
- TTS uses Android `TextToSpeech` API with Nepali → Hindi → English fallback.
- No internet required after APK install

## Practice Mode

Toggle with the **Practice** button in the controls bar.

**Behavior**:
1. Shuffles all practice characters into a random sequence
2. Shows target character centered on camera feed (140sp)
3. User signs the character → app compares with target
4. **Correct**: green feedback, auto-advance after 1s
5. **Wrong**: shows "Got: X • Try again" in red
6. **Skip** button advances without penalty
7. **Show sign** tap opens an AlertDialog with the reference hand image
8. Completion shows score, auto-returns to free mode after 2.5s

**Animations**:
- Character advance: scale-down to 30% + fade out → text swap → scale-up + fade in (120ms/200ms)
- Correct answer: pulse scale to 125% then back to 100% (250ms total)
- Confidence text: color-coded green (>=80%), amber (>=threshold), red (<threshold), white (no hand)
- Result bar flash: brief primaryLight flash on each new detection (250ms)

## Controls Bar

5 buttons, `IconButton` style (zero minWidth, zero padding) for compact icon buttons, `TonalButton` style for Practice:

| Button | Free Mode | Practice Mode |
|---|---|---|
| **Practice / Exit** (weight 3) | Enter practice mode | Exit to free mode |
| **Cam** (weight 1) | Flip camera | Flip camera |
| **Del** (weight 1) | Backspace last char | Skip target |
| **Clr** (weight 1) | Clear text | Stop practice |
| **Read** (weight 1) | TTS on accumulated text | TTS on target char |

## Confidence Overlay & Threshold Settings

- **Confidence display**: `confidenceText` TextView at top-left of the camera feed shows `Conf: 87%` (or `Conf: --` when no gesture passes threshold). `lastConfidence` tracks the max score across ALL gestures (unfiltered), so you see the raw confidence even when it's below threshold. Color-coded: green >= 80%, amber >= threshold, red < threshold, white for no hand.
- **Settings gear**: `ImageButton` in the result bar (right side) opens a dialog with a `SeekBar` (50%–95% range).
- Adjusting the threshold calls `GestureRecognitionHelper.confidenceThreshold` setter, which recreates the MediaPipe recognizer with the new confidence values.

## Navigation Flow

```
MainActivity (gesture camera + practice + TTS)
  ├── LearnActivity → PagerActivity (vowels / consonants)
  ├── PhotoModelActivity (upload & recognize)
  ├── PlayQuizActivity → QuizCompletedActivity
  └── AddQuizActivity (Admin Panel) → ViewQuizzesActivity
```

## Quiz Storage

- **SharedPreferences file**: `local_quizzes`
- **Key**: `quizzes` → JSON array string
- **Images**: `/data/data/com.example.nsl_mini/files/quiz_images/{id}.jpg`
- **Serialization**: `org.json.JSONArray`/`JSONObject` (no Gson dependency)

## Future Roadmap

See `llm/improvement-plan.md` for full plan. Priority order:

1. ~~Practice Mode~~ ✅ done
2. ~~Text-to-Speech~~ ✅ done
3. Better ML model training (expand character set)
4. ~~User-adjustable confidence slider~~ ✅ done
5. Dictionary / sentence smoothing
6. Save, copy & share detected text
7. ~~Visual feedback polish~~ ✅ done

## Deleted / Removed

- Firebase: `google-services.json`, all Firebase deps, `LoginActivity`, `SignupActivity`, `UserProfileActivity`, `AdminActivity`, `UserAdapter`, `UserData`, `BaseActivityAdmin`, Firebase layouts
- Dead code: `CameraSource.kt`, `GestureRecognizerResultsAdapter.kt`, `AnyQuestionActivity`, `getQuizCount()`
- Unused build deps: `dataBinding`, `circleimageview`, `kotlinx-coroutines-android`
- Unused manifest permissions: `RECORD_AUDIO`, `WRITE_EXTERNAL_STORAGE`
- Splash: `SplashActivity`, `activity_splash.xml`, `splash_video.mp4`
- Redundant pager activities: `ConsonantsActivity`, `VowelsActivity`, `NumbersActivity` + their 3 layouts
- Learn numbers card (`activity_learn.xml` Numbers card removed, digits removed from practice list)

## Tier 3 Cleanup (Done)

- Removed unused `INTERNET` permission from `AndroidManifest.xml`
- Upgraded `onBackPressed` → `OnBackPressedCallback` (AndroidX compat) in all 7 activities
- Upgraded `startActivityForResult` → `registerForActivityResult` in `PhotoModelActivity`
- Pinned MediaPipe to `0.10.18` instead of `latest.release`
- Added ProGuard rules (`proguard-rules.pro`) and enabled minification for release builds
- Added model loading error handling with user-visible error messages
- `CameraHelper.createCaptureSession` suppressed with `@Suppress("DEPRECATION")` — replacement `SessionConfiguration` requires API 28+

## Notes

- Nav drawer items handled via `BaseActivity.onNavItemSelected()` — subclass override supported
- Controls bar uses `IconButton` style (in themes.xml) for compact icon buttons; removes default Material minWidth and padding
