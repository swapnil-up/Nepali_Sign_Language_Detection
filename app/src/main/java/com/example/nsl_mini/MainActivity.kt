package com.example.nsl_mini

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.view.TextureView
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.graphics.SurfaceTexture
import android.widget.HorizontalScrollView

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

    private var cumulativeResult = StringBuilder()
    private var lastDetectedLetter: String? = null
    private var isSwitchingCamera = false
    private val switchCameraDebounceTime = 1000L
    private val handler = Handler(Looper.getMainLooper())

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

        backspaceButton.setOnClickListener {
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

        clearButton.setOnClickListener {
            synchronized(this) {
                cumulativeResult.clear()
                lastResultTextView.text = "Last Detected Result: "
                scrollToEnd()
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
                if (currentLetter != null && currentLetter != lastDetectedLetter) {
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
