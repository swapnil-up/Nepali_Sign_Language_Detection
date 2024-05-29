package com.example.nsl_mini

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.TextureView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class MainActivity : AppCompatActivity() {
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper
    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            setupGestureRecognizer()
        }
    }

    private fun setupGestureRecognizer() {
        try {
            gestureRecognizerHelper = GestureRecognizerHelper(this).apply {
                setupGestureRecognizer("gesturerecognizer.task")
            }

            val textureView = findViewById<TextureView>(R.id.textureView)
            cameraHelper = CameraHelper(this, textureView)
            cameraHelper.startCamera()
            cameraHelper.setCameraListener(object : CameraHelper.CameraListener {
                override fun onFrame(bitmap: Bitmap) {
                    val mpImage = BitmapImageBuilder(bitmap).build()
                    val frameTime = SystemClock.uptimeMillis()
                    gestureRecognizerHelper.recognizeAsync(mpImage, frameTime)
                }
            })

            cameraHelper.startCamera()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error during setupGestureRecognizer", e)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupGestureRecognizer()
        } else {
            Log.e("MainActivity", "Camera permission denied")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }

    private fun displayResults(result: GestureRecognizerResult) {
        val formattedResult = GestureRecognizerResultsAdapter().formatResults(result)
        Log.d("GestureResults", formattedResult)
        findViewById<TextView>(R.id.resultTextView).text = formattedResult
    }
}
