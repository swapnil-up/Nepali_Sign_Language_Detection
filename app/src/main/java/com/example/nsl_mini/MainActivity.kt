package com.example.nsl_mini

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.TextureView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.io.IOException

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
    private var isSwitchingCamera = false
    private val switchCameraDebounceTime = 1000L  // 1 second debounce time
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var profileImageView: ImageView

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val READ_STORAGE_PERMISSION_REQUEST_CODE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val CAPTURE_IMAGE_REQUEST = 3
    }

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

        // Set up profile image view click listener
        val headerView = navView.getHeaderView(0)
        profileImageView = headerView.findViewById(R.id.profileImageView)
        profileImageView.setOnClickListener {
            val options = arrayOf("Choose from Gallery", "Capture Photo")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (checkPermissionForReadExternalStorage()) {
                            openImageSelector()
                        } else {
                            requestPermissionForReadExternalStorage()
                        }
                    }
                    1 -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            openCamera()
                        } else {
                            requestPermissionForCamera()
                        }
                    }
                }
            }
            builder.show()
        }


        // Set up backspace button click listener
        backspaceButton.setOnClickListener {
            synchronized(this) {
                if (cumulativeResult.isNotEmpty()) {
                    cumulativeResult.deleteCharAt(cumulativeResult.length - 1)
                    lastResultTextView.text = "Last Detected Result: ${cumulativeResult.toString()}"
                }
            }
        }

        // Set up switch camera button click listener with debouncing
        switchCameraButton.setOnClickListener {
            Log.d("MainActivity", "Switch camera button clicked")
            if (!isSwitchingCamera) {
                isSwitchingCamera = true
                switchCameraButton.isEnabled = false
                Log.d("MainActivity", "Switching camera started")
                cameraHelper.switchCamera {
                    Log.d("MainActivity", "Switching camera completed")
                    isSwitchingCamera = false
                    switchCameraButton.isEnabled = true
                }
                // Re-enable the button after debounce time
                handler.postDelayed({
                    switchCameraButton.isEnabled = true
                }, switchCameraDebounceTime)
            } else {
                Log.d("MainActivity", "Camera is already switching")
            }
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

                R.id.nav_upload_and_learn -> {
                    Log.d("MainActivity", "Upload and Learn selected")
                    val intent = Intent(this, PhotoModelActivity::class.java)
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            setupCamera(textureView)
        }
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }

    private fun requestPermissionForCamera() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    private fun checkPermissionForReadExternalStorage(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForReadExternalStorage() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri: Uri? = data?.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                        profileImageView.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
                CAPTURE_IMAGE_REQUEST -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    profileImageView.setImageBitmap(photo)
                }
            }
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
                Log.d("MainActivity", "SurfaceTexture available")
                cameraHelper = CameraHelper(this@MainActivity, textureView) { bitmap ->
                    gestureRecognizerHelper.recognizeAsync(bitmap, System.currentTimeMillis())
                }
                cameraHelper.startCamera()
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                // Handle the change in size if needed
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                Log.d("MainActivity", "SurfaceTexture destroyed")
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                // Update the texture if needed
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraHelper.isInitialized) {
            cameraHelper.stopCamera()
        }
    }
}
