package com.example.nsl_mini

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.app.AlertDialog
import android.widget.LinearLayout
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.graphics.SurfaceTexture
import android.widget.HorizontalScrollView
import java.util.Locale

class MainActivity : BaseActivity() {

    private lateinit var cameraHelper: CameraHelper
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var landmarkOverlayView: LandmarkOverlayView
    private lateinit var resultTextView: TextView
    private lateinit var lastResultTextView: TextView
    private lateinit var backspaceButton: Button
    private lateinit var switchCameraButton: Button
    private lateinit var clearButton: Button
    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var targetCharacterText: TextView
    private lateinit var practiceFeedbackText: TextView
    private lateinit var practiceProgressText: TextView
    private lateinit var practiceHintText: TextView
    private lateinit var practiceModeButton: Button
    private lateinit var speakButton: Button
    private var tts: TextToSpeech? = null

    private var cumulativeResult = StringBuilder()
    private var lastDetectedLetter: String? = null
    private var isSwitchingCamera = false
    private val switchCameraDebounceTime = 1000L
    private val handler = Handler(Looper.getMainLooper())
    private var isPracticeMode = false
    private var practiceIndex = 0
    private var practiceCorrectCount = 0
    private var isAdvancing = false
    private var practiceCharsShuffled: MutableList<String> = mutableListOf()

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textureView = findViewById<TextureView>(R.id.textureView)
        lastResultTextView = findViewById(R.id.lastResultTextView)
        lastResultTextView.isSelected = true
        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        landmarkOverlayView = findViewById(R.id.landmarkOverlayView)
        resultTextView = findViewById(R.id.resultTextView)
        backspaceButton = findViewById(R.id.backspaceButton)
        switchCameraButton = findViewById(R.id.switchCameraButton)
        clearButton = findViewById(R.id.clearButton)
        targetCharacterText = findViewById(R.id.targetCharacterText)
        practiceFeedbackText = findViewById(R.id.practiceFeedbackText)
        practiceProgressText = findViewById(R.id.practiceProgressText)
        practiceHintText = findViewById(R.id.practiceHintText)
        practiceModeButton = findViewById(R.id.practiceModeButton)
        speakButton = findViewById(R.id.speakButton)

        backspaceButton.setOnClickListener {
            if (isPracticeMode) {
                skipPracticeChar()
            } else {
                synchronized(this) {
                    if (cumulativeResult.isNotEmpty()) {
                        val compound = GestureResultFormatter.compoundCharacters.firstOrNull { cumulativeResult.endsWith(it) }
                        if (compound != null) {
                            cumulativeResult.delete(cumulativeResult.length - compound.length, cumulativeResult.length)
                        } else if (cumulativeResult.endsWith("\u0905:")) {
                            cumulativeResult.delete(cumulativeResult.length - 2, cumulativeResult.length)
                        } else {
                            cumulativeResult.deleteCharAt(cumulativeResult.length - 1)
                        }
                        lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                        scrollToEnd()
                    }
                }
            }
        }

        clearButton.setOnClickListener {
            if (isPracticeMode) {
                stopPractice()
            } else {
                synchronized(this) {
                    cumulativeResult.clear()
                    lastResultTextView.text = "Last Detected Result: "
                    scrollToEnd()
                }
            }
        }

        practiceModeButton.setOnClickListener {
            togglePractice()
        }

        practiceHintText.setOnClickListener {
            if (isPracticeMode && practiceIndex < practiceCharsShuffled.size) {
                showHintDialog(practiceCharsShuffled[practiceIndex])
            }
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ne = tts?.setLanguage(Locale("ne"))
                if (ne == TextToSpeech.LANG_MISSING_DATA || ne == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val hi = tts?.setLanguage(Locale("hi"))
                    if (hi == TextToSpeech.LANG_MISSING_DATA || hi == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.language = Locale.ENGLISH
                    }
                }
            }
        }

        speakButton.setOnClickListener {
            if (isPracticeMode && practiceIndex < practiceCharsShuffled.size) {
                val target = practiceCharsShuffled[practiceIndex]
                tts?.speak(target, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                val text = cumulativeResult.toString()
                if (text.isNotEmpty()) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }

        switchCameraButton.setOnClickListener {
            if (!isSwitchingCamera) {
                isSwitchingCamera = true
                switchCameraButton.isEnabled = false
                cameraHelper.switchCamera {
                    isSwitchingCamera = false
                    switchCameraButton.isEnabled = true
                }
                handler.postDelayed({
                    switchCameraButton.isEnabled = true
                }, switchCameraDebounceTime)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupCamera(textureView)
        }
    }

    private fun setupCamera(textureView: TextureView) {
        gestureRecognizerHelper = GestureRecognizerHelper(this) { result, landmarks ->
            runOnUiThread {
                resultTextView.text = result
                landmarkOverlayView.setLandmarks(landmarks)
                val currentLetter = GestureResultFormatter.firstGesture(result)

                if (isPracticeMode) {
                    handlePracticeResult(currentLetter)
                } else if (currentLetter != null && currentLetter != lastDetectedLetter) {
                    cumulativeResult.append(currentLetter)
                    lastDetectedLetter = currentLetter
                    lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                    scrollToEnd()
                }
            }
        }
        gestureRecognizerHelper.setup(GestureResultFormatter.GESTURE_MODEL_FILE)

        val listener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                cameraHelper = CameraHelper(this@MainActivity, textureView) { bitmap ->
                    gestureRecognizerHelper.recognizeAsync(bitmap, System.currentTimeMillis())
                }
                cameraHelper.startCamera()
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean = true
            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }
        textureView.surfaceTextureListener = listener
    }

    private fun togglePractice() {
        if (isPracticeMode) stopPractice() else startPractice()
    }

    private fun startPractice() {
        isPracticeMode = true
        practiceIndex = 0
        practiceCorrectCount = 0
        isAdvancing = false
        practiceCharsShuffled = GestureResultFormatter.practiceCharacters.shuffled().toMutableList()

        practiceModeButton.text = "Exit"
        backspaceButton.text = "Skip"
        clearButton.text = "Stop"

        targetCharacterText.visibility = View.VISIBLE
        practiceFeedbackText.visibility = View.VISIBLE
        practiceProgressText.visibility = View.VISIBLE
        practiceHintText.visibility = View.VISIBLE

        lastDetectedLetter = null
        updatePracticeUI()
    }

    private fun stopPractice() {
        isPracticeMode = false
        isAdvancing = false

        practiceModeButton.text = "Practice"
        backspaceButton.text = "Del"
        clearButton.text = "Clr"
        targetCharacterText.visibility = View.GONE
        practiceFeedbackText.visibility = View.GONE
        practiceProgressText.visibility = View.GONE
        practiceHintText.visibility = View.GONE
        targetCharacterText.setTextColor(ContextCompat.getColor(this, R.color.white))
        practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.white))
        lastDetectedLetter = null
    }

    private fun skipPracticeChar() {
        if (!isPracticeMode || isAdvancing) return
        practiceIndex++
        if (practiceIndex >= practiceCharsShuffled.size) {
            showPracticeComplete()
            return
        }
        updatePracticeUI()
    }

    private fun updatePracticeUI() {
        if (practiceIndex >= practiceCharsShuffled.size) {
            showPracticeComplete()
            return
        }
        val target = practiceCharsShuffled[practiceIndex]
        targetCharacterText.text = target
        val label = GestureResultFormatter.characterLabels[target]
        practiceFeedbackText.text = if (label != null) "$target — $label" else target
        practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.white))
        practiceProgressText.text = "${practiceIndex + 1} / ${practiceCharsShuffled.size}  •  $practiceCorrectCount correct"
        lastDetectedLetter = null
    }

    private fun showPracticeComplete() {
        practiceFeedbackText.text = "All done! $practiceCorrectCount / ${practiceCharsShuffled.size} correct"
        practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.green))
        targetCharacterText.text = "\u2714"
        practiceProgressText.text = ""
        handler.postDelayed({
            stopPractice()
        }, 2500)
    }

    private fun handlePracticeResult(currentLetter: String?) {
        if (isAdvancing || practiceIndex >= practiceCharsShuffled.size) return

        val target = practiceCharsShuffled[practiceIndex]

        if (currentLetter == target && currentLetter != lastDetectedLetter) {
            isAdvancing = true
            practiceCorrectCount++
            practiceFeedbackText.text = "Correct! \u2714"
            practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.green))
            targetCharacterText.setTextColor(ContextCompat.getColor(this, R.color.green))
            lastDetectedLetter = currentLetter
            practiceProgressText.text = "${practiceIndex + 1} / ${practiceCharsShuffled.size}  •  $practiceCorrectCount correct"

            handler.postDelayed({
                targetCharacterText.setTextColor(ContextCompat.getColor(this, R.color.white))
                practiceIndex++
                isAdvancing = false
                updatePracticeUI()
            }, 1000)
        } else if (currentLetter != null && currentLetter != target) {
            practiceFeedbackText.text = "Got: $currentLetter  •  Try again"
            practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.error))
            lastDetectedLetter = currentLetter
        }
    }

    private fun showHintDialog(character: String) {
        val resId = GestureResultFormatter.signImages[character]
        if (resId == null) {
            practiceFeedbackText.text = "No hint available for this character"
            practiceFeedbackText.setTextColor(ContextCompat.getColor(this, R.color.accentLight))
            return
        }
        val imageView = ImageView(this).apply {
            setImageResource(resId)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(24, 24, 24, 24)
        }
        AlertDialog.Builder(this)
            .setTitle("Sign for $character")
            .setView(imageView)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun scrollToEnd() {
        horizontalScrollView.post {
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val textureView = findViewById<TextureView>(R.id.textureView)
            setupCamera(textureView)
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
        if (this::cameraHelper.isInitialized) {
            cameraHelper.stopCamera()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
