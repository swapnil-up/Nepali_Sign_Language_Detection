package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class QuizCompletedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_completed)
        setupDrawer()

    }
    override fun onBackPressed() {
        // Navigate to MainActivity explicitly
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Call super to handle default back button behavior
        super.onBackPressed()
    }
}
