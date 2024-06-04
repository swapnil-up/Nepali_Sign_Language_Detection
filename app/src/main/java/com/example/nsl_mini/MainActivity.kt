package com.example.nsl_mini

import android.Manifest
import android.content.Intent
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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.hardware.camera2.*
import androidx.appcompat.widget.Toolbar

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
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup navigation drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("MainActivity", "Home selected")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_learn -> {
                    Log.d("MainActivity", "Learn selected")
                    val intent = Intent(this, LearnActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    Log.d("MainActivity", "Logout selected")
                    logoutUser()
                }
            }
            true
        }

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            setupCamera(textureView)
        }
    }

    private fun logoutUser() {
        // Clear user session or perform any necessary logout operations
        // For example, clear shared preferences
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Navigate to LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val textureView = findViewById<TextureView>(R.id.textureView)
                setupCamera(textureView)
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCamera(textureView: TextureView) {
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

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                cameraHelper = CameraHelper(this@MainActivity, textureView) { bitmap ->
                    gestureRecognizerHelper.recognizeAsync(bitmap, System.currentTimeMillis())
                }
                cameraHelper.startCamera()
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                // Handle the change in size if needed
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                // Update the texture if needed
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.stopCamera()
    }
}
