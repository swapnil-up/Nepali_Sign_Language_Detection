package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class QuizCompletedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_completed)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 0)

        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "Score: $correctAnswers / $totalQuestions"

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@QuizCompletedActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}
