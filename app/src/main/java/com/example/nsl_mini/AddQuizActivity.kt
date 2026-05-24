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
import java.io.IOException
import java.util.UUID

class AddQuizActivity : BaseActivity() {

    private lateinit var imageView: ImageView
    private lateinit var option1EditText: EditText
    private lateinit var option2EditText: EditText
    private lateinit var option3EditText: EditText
    private lateinit var option4EditText: EditText
    private lateinit var correctAnswerEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var selectImageButton: Button
    private var imageUri: Uri? = null
    private lateinit var storage: QuizStorage

    private var editingQuizId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)

        storage = LocalQuizStorage(this)

        imageView = findViewById(R.id.imageView)
        option1EditText = findViewById(R.id.option1EditText)
        option2EditText = findViewById(R.id.option2EditText)
        option3EditText = findViewById(R.id.option3EditText)
        option4EditText = findViewById(R.id.option4EditText)
        correctAnswerEditText = findViewById(R.id.correctAnswerEditText)
        uploadButton = findViewById(R.id.uploadButton)
        selectImageButton = findViewById(R.id.selectImageButton)

        selectImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        uploadButton.setOnClickListener {
            saveQuizQuestion()
        }

        editingQuizId = intent.getStringExtra("quizId")
        editingQuizId?.let { id ->
            val quiz = storage.loadQuiz(id)
            if (quiz != null) {
                Glide.with(this).load(quiz.imageUrl).into(imageView)
                option1EditText.setText(quiz.options.getOrElse(0) { "" })
                option2EditText.setText(quiz.options.getOrElse(1) { "" })
                option3EditText.setText(quiz.options.getOrElse(2) { "" })
                option4EditText.setText(quiz.options.getOrElse(3) { "" })
                correctAnswerEditText.setText(quiz.correctAnswer)
            }
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

    private fun saveQuizQuestion() {
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
            val quizId = editingQuizId ?: UUID.randomUUID().toString()
            var imagePath = intent.getStringExtra("imageUrl") ?: ""

            if (imageUri != null) {
                val savedPath = LocalQuizStorage.saveImageToInternalStorage(this, imageUri!!, quizId)
                if (savedPath != null) {
                    imagePath = savedPath
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                    return
                }
            } else if (editingQuizId != null) {
                val existing = storage.loadQuiz(quizId)
                if (existing != null) {
                    imagePath = existing.imageUrl ?: ""
                }
            }

            val quiz = Quiz(
                id = quizId,
                imageUrl = imagePath,
                options = options,
                correctAnswer = correctAnswer
            )
            storage.saveQuiz(quiz)

            Toast.makeText(this, "Quiz question saved", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please fill in all options and correct answer", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, ViewQuizzesActivity::class.java)
        startActivity(intent)
        finish()
    }
}
