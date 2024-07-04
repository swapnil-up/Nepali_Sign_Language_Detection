package com.example.nsl_mini

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class AddQuizActivity : BaseActivityAdmin() {

    private lateinit var imageView: ImageView
    private lateinit var option1EditText: EditText
    private lateinit var option2EditText: EditText
    private lateinit var option3EditText: EditText
    private lateinit var option4EditText: EditText
    private lateinit var correctAnswerEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    private var quizId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)
        setupDrawerAdmin()

        imageView = findViewById(R.id.imageView)
        option1EditText = findViewById(R.id.option1EditText)
        option2EditText = findViewById(R.id.option2EditText)
        option3EditText = findViewById(R.id.option3EditText)
        option4EditText = findViewById(R.id.option4EditText)
        correctAnswerEditText = findViewById(R.id.correctAnswerEditText)
        uploadButton = findViewById(R.id.uploadButton)
        selectImageButton = findViewById(R.id.selectImageButton)
        databaseReference = FirebaseDatabase.getInstance().reference.child("quizzes")
        storageReference = FirebaseStorage.getInstance().reference.child("quiz_images")

        selectImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        uploadButton.setOnClickListener {
            uploadQuizQuestion()
        }

        quizId = intent.getStringExtra("quizId")
        quizId?.let { id ->
            // Pre-fill the fields if it's an edit
            val imageUrl = intent.getStringExtra("imageUrl")
            val options = intent.getStringArrayListExtra("options")
            val correctAnswer = intent.getStringExtra("correctAnswer")

            Glide.with(this).load(imageUrl).into(imageView)
            option1EditText.setText(options?.get(0))
            option2EditText.setText(options?.get(1))
            option3EditText.setText(options?.get(2))
            option4EditText.setText(options?.get(3))
            correctAnswerEditText.setText(correctAnswer)
        }

        setEditTextFilters()
        setEditTextListeners()
    }

    private fun setEditTextFilters() {
        val lengthFilter = InputFilter.LengthFilter(20)
        option1EditText.filters = arrayOf(lengthFilter)
        option2EditText.filters = arrayOf(lengthFilter)
        option3EditText.filters = arrayOf(lengthFilter)
        option4EditText.filters = arrayOf(lengthFilter)
        correctAnswerEditText.filters = arrayOf(lengthFilter)
    }

    private fun setEditTextListeners() {
        option1EditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                option2EditText.requestFocus()
                true
            } else {
                false
            }
        }
        option2EditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                option3EditText.requestFocus()
                true
            } else {
                false
            }
        }
        option3EditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                option4EditText.requestFocus()
                true
            } else {
                false
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                imageUri = data.data
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun uploadQuizQuestion() {
        if (imageUri == null && quizId == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        val options = listOf(
            option1EditText.text.toString(),
            option2EditText.text.toString(),
            option3EditText.text.toString(),
            option4EditText.text.toString()
        )
        val correctAnswer = correctAnswerEditText.text.toString()

        if (options.distinct().size != options.size) {
            Toast.makeText(this, "Options must be unique", Toast.LENGTH_SHORT).show()
            return
        }

        if (!options.contains(correctAnswer)) {
            Toast.makeText(this, "Correct answer must match one of the options", Toast.LENGTH_SHORT).show()
            return
        }

        if (options.all { it.isNotEmpty() } && correctAnswer.isNotEmpty()) {
            if (imageUri != null) {
                // Upload image to Firebase Storage
                val imageFileName = UUID.randomUUID().toString()
                val imageRef = storageReference.child("$imageFileName.jpg")
                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            saveQuizData(imageUrl, options, correctAnswer)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Update existing quiz data without changing the image
                val imageUrl = intent.getStringExtra("imageUrl") ?: ""
                saveQuizData(imageUrl, options, correctAnswer)
            }
        } else {
            Toast.makeText(this, "Please fill in all options and correct answer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveQuizData(imageUrl: String, options: List<String>, correctAnswer: String) {
        val quizData = mapOf(
            "imageUrl" to imageUrl,
            "options" to options,
            "correctAnswer" to correctAnswer
        )
        if (quizId != null) {
            // Update existing quiz
            databaseReference.child(quizId!!).setValue(quizData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quiz question updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update quiz question", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Add new quiz
            val quizId = databaseReference.push().key ?: return
            databaseReference.child(quizId).setValue(quizData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quiz question added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add quiz question", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBackPressed() {
        // Navigate to MainActivity explicitly
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()

        // Call super to handle default back button behavior
        super.onBackPressed()
    }
}
