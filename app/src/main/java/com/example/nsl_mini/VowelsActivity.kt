package com.example.nsl_mini

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class VowelsActivity : AppCompatActivity() {
    private lateinit var vowelImage: ImageView
    private lateinit var vowelSignImage: ImageView
    private lateinit var prevVowelButton: Button
    private lateinit var nextVowelButton: Button

    private val vowels = arrayOf(
        R.drawable.vowel_a,
        R.drawable.vowel_aa,
        R.drawable.vowel_e,
        R.drawable.vowel_ee,
        R.drawable.vowel_u,
        R.drawable.vowel_uu,
        R.drawable.vowel_ri,
        R.drawable.vowel_ya,
        R.drawable.vowel_yai,
        R.drawable.vowel_wo,
        R.drawable.vowel_wau,
        R.drawable.vowel_aam,
        R.drawable.vowel_aha,
    )

    private val vowelSigns = arrayOf(
        R.drawable.vowelsign_a,
        R.drawable.vowelsign_aa,
        R.drawable.vowelsign_e,
        R.drawable.vowelsign_ee,
        R.drawable.vowelsign_u,
        R.drawable.vowelsign_uu,
        R.drawable.vowelsign_ri,
        R.drawable.vowelsign_ya,
        R.drawable.vowelsign_yai,
        R.drawable.vowelsign_wo,
        R.drawable.vowelsign_wau,
        R.drawable.vowelsign_aam,
        R.drawable.vowelsign_aha,
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vowels)

        vowelImage = findViewById(R.id.vowel_image)
        vowelSignImage = findViewById(R.id.vowel_sign_image)
        prevVowelButton = findViewById(R.id.prev_vowel_button)
        nextVowelButton = findViewById(R.id.next_vowel_button)

        updateImages()

        prevVowelButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateImages()
            }
        }

        nextVowelButton.setOnClickListener {
            if (currentIndex < vowels.size - 1) {
                currentIndex++
                updateImages()
            }
        }
    }

    private fun updateImages() {
        vowelImage.setImageResource(vowels[currentIndex])
        vowelSignImage.setImageResource(vowelSigns[currentIndex])
    }
}
