package com.example.nsl_mini

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NumbersActivity : AppCompatActivity() {
    private lateinit var numberImage: ImageView
    private lateinit var signImage: ImageView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private val numbers = arrayOf(
        R.drawable.number_sunya,
        R.drawable.number_ek,
        R.drawable.number_dvi,
        R.drawable.number_tin,
        R.drawable.number_car,
        R.drawable.number_pac,
        R.drawable.number_chah,
        R.drawable.number_sat,
        R.drawable.number_ath,
        R.drawable.number_nau
    )

    private val signs = arrayOf(
        R.drawable.sign_0, R.drawable.sign_1, R.drawable.sign_2,
        R.drawable.sign_3, R.drawable.sign_4, R.drawable.sign_5,
        R.drawable.sign_6, R.drawable.sign_7, R.drawable.sign_8,
        R.drawable.sign_9
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers)

        numberImage = findViewById(R.id.number_image)
        signImage = findViewById(R.id.sign_image)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)

        updateImages()

        prevButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateImages()
            }
        }

        nextButton.setOnClickListener {
            if (currentIndex < numbers.size - 1) {
                currentIndex++
                updateImages()
            }
        }
    }

    private fun updateImages() {
        numberImage.setImageResource(numbers[currentIndex])
        signImage.setImageResource(signs[currentIndex])
    }
}
