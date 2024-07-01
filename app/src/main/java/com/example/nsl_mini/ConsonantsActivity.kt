package com.example.nsl_mini

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ConsonantsActivity : BaseActivity() {
    private lateinit var consonantImage: ImageView
    private lateinit var consonantSignImage: ImageView
    private lateinit var prevConsonantButton: Button
    private lateinit var nextConsonantButton: Button

    private val consonants = arrayOf(
        R.drawable.consonant_ka,
        R.drawable.consonant_kha,
        R.drawable.consonant_ga,
        R.drawable.consonant_gha,
        R.drawable.consonant_kna,
        R.drawable.consonant_chaa,
        R.drawable.consonant_ca,
        R.drawable.consonant_cha,
        R.drawable.consonant_ja,
        R.drawable.consonant_jha,
        R.drawable.consonant_ya,
        R.drawable.consonant_tha,
        R.drawable.consonant_thaa,
        R.drawable.consonant_da,
        R.drawable.consonant_dha,
        R.drawable.consonant_adha,
        R.drawable.consonant_ta,
        R.drawable.consonant_tha,
        R.drawable.consonant_thaa,
        R.drawable.consonant_thaaa,
        R.drawable.consonant_dhaa,
        R.drawable.consonant_na,
        R.drawable.consonant_pa,
        R.drawable.consonant_pha,
        R.drawable.consonant_ba,
        R.drawable.consonant_bha,
        R.drawable.consonant_ma,
        R.drawable.consonant_ya,
        R.drawable.consonant_ra,
        R.drawable.consonant_la,
        R.drawable.consonant_va,
        R.drawable.consonant_s,
        R.drawable.consonant_ss,
        R.drawable.consonant_sa,
        R.drawable.consonant_ha,
        R.drawable.consonant_chya,
        R.drawable.consonant_tra,
        R.drawable.consonant_gya,
        // Add the rest of the consonants here
    )

    private val consonantSigns = arrayOf(
        R.drawable.consonant_ka,
        R.drawable.consonant_kha,
        R.drawable.consonant_ga,
        R.drawable.consonant_gha,
        R.drawable.consonant_kna,
        R.drawable.consonant_chaa,
        R.drawable.consonant_ca,
        R.drawable.consonant_cha,
        R.drawable.consonant_ja,
        R.drawable.consonant_jha,
        R.drawable.consonant_ya,
        R.drawable.consonant_ta,
        R.drawable.consonant_tha,
        R.drawable.consonant_thaa,
        R.drawable.consonant_thaaa,
        R.drawable.consonant_da,
        R.drawable.consonant_dha,
        R.drawable.consonant_adha,
        R.drawable.consonant_dhaa,
        R.drawable.consonant_na,
        R.drawable.consonant_pa,
        R.drawable.consonant_pha,
        R.drawable.consonant_ba,
        R.drawable.consonant_bha,
        R.drawable.consonant_ma,
        R.drawable.consonant_ya,
        R.drawable.consonant_ra,
        R.drawable.consonant_la,
        R.drawable.consonant_va,
        R.drawable.consonant_s,
        R.drawable.consonant_ss,
        R.drawable.consonant_sa,
        R.drawable.consonant_ha,
        R.drawable.consonant_chya,
        R.drawable.consonant_tra,
        R.drawable.consonant_gya,


        // Add the rest of the consonant signs here
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consonants)
        setupDrawer()


        consonantImage = findViewById(R.id.consonant_image)
        consonantSignImage = findViewById(R.id.consonant_sign_image)
        prevConsonantButton = findViewById(R.id.prev_consonant_button)
        nextConsonantButton = findViewById(R.id.next_consonant_button)

        updateImages()

        prevConsonantButton.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateImages()
            }
        }

        nextConsonantButton.setOnClickListener {
            if (currentIndex < consonants.size - 1) {
                currentIndex++
                updateImages()
            }
        }
    }

    private fun updateImages() {
        consonantImage.setImageResource(consonants[currentIndex])
        consonantSignImage.setImageResource(consonantSigns[currentIndex])
    }
}
