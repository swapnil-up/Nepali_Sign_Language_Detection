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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.InputStream

class PhotoModelActivity : BaseActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectImageButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var gestureRecognizerHelper: GestureRecognizerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photomodel)
        setupDrawer()

        selectImageButton = findViewById(R.id.selectImageButton)
        selectedImageView = findViewById(R.id.selectedImageView)
        resultTextView = findViewById(R.id.resultTextViewStatic)

        // Initialize GestureRecognizerHelper
        gestureRecognizerHelper = GestureRecognizerHelper(this) { result, landmarks ->
            // Display the result in resultTextView
            resultTextView.text = result
        }
        gestureRecognizerHelper.setupGestureRecognizer("gesture_recognizer_vowel.task") // Replace with your model path

        selectImageButton.setOnClickListener {
            openGallery()
        }

        // Check and request permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                processSelectedImage(it)
            }
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
