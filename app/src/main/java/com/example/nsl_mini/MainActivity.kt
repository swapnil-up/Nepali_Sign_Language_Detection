package com.example.nsl_mini

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.TextureView

class MainActivity : AppCompatActivity() {
    private lateinit var cameraHelper: CameraHelper
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var resultTextView: TextView
    private lateinit var lastResultTextView: TextView

    private var cumulativeResult = StringBuilder()
    private var lastDetectedLetter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textureView = findViewById<TextureView>(R.id.textureView)
        resultTextView = findViewById(R.id.resultTextView)
        lastResultTextView = findViewById(R.id.lastResultTextView)

        gestureRecognizerHelper = GestureRecognizerHelper(this) { result ->
            runOnUiThread {
                resultTextView.text = result
                val currentLetter = result.split("\n").firstOrNull()?.trim()
                if (currentLetter != null && currentLetter.lowercase() != "none") {
                    if (currentLetter != lastDetectedLetter) {
                        // Append new result to cumulative result
                        cumulativeResult.append(currentLetter)

                        // Update lastDetectedLetter
                        lastDetectedLetter = currentLetter

                        // Update lastResultTextView with cumulative result
                        lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                    }
                }
            }
        }

        gestureRecognizerHelper.setupGestureRecognizer("gesture_recognizer.task")

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
