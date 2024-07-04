package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class AnyQuestionActivity : BaseActivity() {

    private lateinit var questionImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_any_question)
        setupDrawer()

        // Initialize views
        questionImageView = findViewById(R.id.questionImageView)

        // Example: Load an image into the ImageView
        val imageResId = R.drawable.any
        questionImageView.setImageResource(imageResId)
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
