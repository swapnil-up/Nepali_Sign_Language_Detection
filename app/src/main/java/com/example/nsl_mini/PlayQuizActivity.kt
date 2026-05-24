package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide

class PlayQuizActivity : BaseActivity() {
    private lateinit var imageView: ImageView
    private lateinit var optionButtons: List<Button>
    private lateinit var quizList: MutableList<Quiz>
    private var currentQuizIndex = 0
    private var correctAnswers = 0
    private lateinit var storage: QuizStorage
    private var hasShownCompletion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_quiz)

        storage = LocalQuizStorage(this)

        imageView = findViewById(R.id.imageView)
        optionButtons = listOf(
            findViewById(R.id.optionButton1),
            findViewById(R.id.optionButton2),
            findViewById(R.id.optionButton3),
            findViewById(R.id.optionButton4)
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PlayQuizActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        loadQuizzes()
    }

    private fun loadQuizzes() {
        quizList = storage.loadAllQuizzes().toMutableList()
        currentQuizIndex = 0
        correctAnswers = 0
        hasShownCompletion = false

        if (quizList.isEmpty()) {
            Toast.makeText(this, "No quizzes available. Add some from Admin Panel!", Toast.LENGTH_LONG).show()
            return
        }

        showNextQuiz()
    }

    private fun showNextQuiz() {
        if (currentQuizIndex < quizList.size) {
            val quiz = quizList[currentQuizIndex]
            Glide.with(this).load(quiz.imageUrl).into(imageView)

            optionButtons.forEachIndexed { index, button ->
                button.text = quiz.options.getOrElse(index) { "" }
                button.setOnClickListener {
                    checkAnswer(button.text.toString())
                }
                button.isEnabled = true
            }
        } else {
            if (!hasShownCompletion) {
                hasShownCompletion = true
                val intent = Intent(this, QuizCompletedActivity::class.java)
                intent.putExtra("correctAnswers", correctAnswers)
                intent.putExtra("totalQuestions", quizList.size)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkAnswer(selectedOption: String) {
        val correctAnswer = quizList[currentQuizIndex].correctAnswer

        optionButtons.forEach { it.isEnabled = false }

        if (selectedOption == correctAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            correctAnswers++
        } else {
            Toast.makeText(this, "Incorrect! The correct answer is $correctAnswer", Toast.LENGTH_SHORT).show()
        }
        currentQuizIndex++

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            showNextQuiz()
        }, 1000)
    }

}
