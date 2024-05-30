package com.example.nsl_mini

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.TextureView
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var cameraHelper: CameraHelper
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var resultTextView: TextView
    private lateinit var lastResultTextView: TextView
    private lateinit var backspaceButton: Button

    private var cumulativeResult = StringBuilder()
    private var lastDetectedLetter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textureView = findViewById<TextureView>(R.id.textureView)
        resultTextView = findViewById(R.id.resultTextView)
        lastResultTextView = findViewById(R.id.lastResultTextView)
        backspaceButton = findViewById(R.id.backspaceButton)


        backspaceButton.setOnClickListener {
            synchronized(this) {
                if (cumulativeResult.isNotEmpty()) {
                    cumulativeResult.deleteCharAt(cumulativeResult.length - 1)
                    lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                }
            }
        }

        gestureRecognizerHelper = GestureRecognizerHelper(this) { result ->
            runOnUiThread {
                resultTextView.text = result

                // Extract the current letter
                val currentLetter = result.split("\n").firstOrNull()?.trim()

                // Check if the current letter is valid and not equal to "none"
                if (currentLetter != null && currentLetter.lowercase() != "none") {
                    // Check if the current letter is different from the last detected letter
                    if (currentLetter != lastDetectedLetter) {
                        cumulativeResult.append(currentLetter)

                        // Update lastDetectedLetter
                        lastDetectedLetter = currentLetter

                        // Update lastResultTextView with cumulative result
                        lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                        Log.d("MainActivity", "New letter detected: $currentLetter. Updated cumulativeResult: ${cumulativeResult.toString()}")
                    } else {
                        Log.d("MainActivity", "Detected letter is the same as the last one: $currentLetter")
                    }
                } else {
                    Log.d("MainActivity", "Detected letter is invalid or 'none'")
                }
            }
        }
        gestureRecognizerHelper.setupGestureRecognizer("gesture_recognizer_vowel.task")

        cameraHelper = CameraHelper(this, textureView) { bitmap ->
            gestureRecognizerHelper.recognizeAsync(bitmap, System.currentTimeMillis())
        }
        cameraHelper.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }
}
