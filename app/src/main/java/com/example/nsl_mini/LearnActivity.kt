package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LearnActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        setupDrawer()

        val cardVowels = findViewById<CardView>(R.id.card_vowels)
        val cardConsonants = findViewById<CardView>(R.id.card_consonants)
        val cardNumbers = findViewById<CardView>(R.id.card_numbers)

        cardVowels.setOnClickListener {
            val intent = Intent(this, VowelsActivity::class.java)
            startActivity(intent)
        }

        cardConsonants.setOnClickListener {
            val intent = Intent(this, ConsonantsActivity::class.java)
            startActivity(intent)
        }

        cardNumbers.setOnClickListener {
            val intent = Intent(this, NumbersActivity::class.java)
            startActivity(intent)
        }
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
