package com.example.nsl_mini

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.TextureView
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var cameraHelper: CameraHelper
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var landmarkOverlayView: LandmarkOverlayView
    private lateinit var resultTextView: TextView
    private lateinit var lastResultTextView: TextView
    private lateinit var backspaceButton: Button
    private lateinit var switchCameraButton: Button

    private var cumulativeResult = StringBuilder()
    private var lastDetectedLetter: String? = null

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val textureView = findViewById<TextureView>(R.id.textureView)
        landmarkOverlayView = findViewById(R.id.landmarkOverlayView)
        resultTextView = findViewById(R.id.resultTextView)
        lastResultTextView = findViewById(R.id.lastResultTextView)
        backspaceButton = findViewById(R.id.backspaceButton)
        switchCameraButton = findViewById(R.id.switchCameraButton)

        // Set up backspace button click listener
        backspaceButton.setOnClickListener {
            synchronized(this) {
                if (cumulativeResult.isNotEmpty()) {
                    cumulativeResult.deleteCharAt(cumulativeResult.length - 1)
                    lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                }
            }
        }

        // Set up switch camera button click listener
        switchCameraButton.setOnClickListener {
            cameraHelper.switchCamera()
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCamera() {
        val textureView = findViewById<TextureView>(R.id.textureView)

        gestureRecognizerHelper = GestureRecognizerHelper(this) { result, landmarks ->
            runOnUiThread {
                resultTextView.text = result
                landmarkOverlayView.setLandmarks(landmarks)

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
