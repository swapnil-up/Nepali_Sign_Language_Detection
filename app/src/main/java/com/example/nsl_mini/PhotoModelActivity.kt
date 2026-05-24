package com.example.nsl_mini

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream

class PhotoModelActivity : BaseActivity() {

    private lateinit var selectImageButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                processSelectedImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photomodel)

        selectImageButton = findViewById(R.id.selectImageButton)
        selectedImageView = findViewById(R.id.selectedImageView)
        resultTextView = findViewById(R.id.resultTextViewStatic)

        gestureRecognizerHelper = GestureRecognizerHelper(this) { result, _ ->
            resultTextView.text = result
        }
        gestureRecognizerHelper.setup(GestureResultFormatter.GESTURE_MODEL_FILE)

        if (!gestureRecognizerHelper.isModelLoaded) {
            resultTextView.text = gestureRecognizerHelper.modelError ?: "Failed to load model"
            resultTextView.setTextColor(android.graphics.Color.RED)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PhotoModelActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Check and request permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
    }

    private fun processSelectedImage(imageUri: Uri) {
        try {
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream)

            // Scale down the image to avoid memory issues
            val scaledBitmap = scaleBitmap(selectedImage)

            selectedImageView.setImageBitmap(scaledBitmap)

            // Process the selected image using gesture recognizer
            scaledBitmap?.let {
                gestureRecognizerHelper.recognizeAsync(it, System.currentTimeMillis())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Scale down the bitmap to avoid memory issues
    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val maxWidth = 1024
        val maxHeight = 1024
        val ratio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        var width = maxWidth
        var height = (width / ratio).toInt()

        if (height > maxHeight) {
            height = maxHeight
            width = (height * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, do your task here
        } else {
            // Permission denied, handle accordingly
        }
    }
}
