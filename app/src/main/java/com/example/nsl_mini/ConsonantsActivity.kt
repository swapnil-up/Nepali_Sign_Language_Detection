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
        R.drawable.ka71,
        R.drawable.kha3,
        R.drawable.ga5,
        R.drawable.gha6,
        R.drawable.nga6,
        R.drawable.cha6,
        R.drawable.chha7,
        R.drawable.ja7,
        R.drawable.jha5,
        R.drawable.yan6,
        R.drawable.ta4,
        R.drawable.tha7,
        R.drawable.da7,
        R.drawable.dha6,
        R.drawable.ada4,
        R.drawable.taa4,
        R.drawable.tha6,
        R.drawable.daa4,
        R.drawable.dhha5,
        R.drawable.na4,
        R.drawable.pa4,
        R.drawable.fa5,
        R.drawable.ba6,
        R.drawable.bha6,
        R.drawable.ma3,
        R.drawable.yan6,
        R.drawable.ra2,
        R.drawable.la6,
        R.drawable.wa2,
        R.drawable.sha2,
        R.drawable.shaa4,
        R.drawable.sa3,
        R.drawable.ha5,
        R.drawable.ksha47,
        R.drawable.taa4,
        R.drawable.gya5,
        // Add the rest of the consonants here
    )

    private val consonantSigns = arrayOf(
        R.drawable.consonant_ka,
        R.drawable.consonant_kha,
        R.drawable.consonant_ga,
        R.drawable.consonant_gha,
        R.drawable.consonant_kna,
        R.drawable.consonant_chaa,
        R.drawable.cchhaa,
        R.drawable.consonant_ja,
        R.drawable.consonant_jha,
        R.drawable.consonant_ya,
        R.drawable.thhaa,
        R.drawable.consonant_thaa,
        R.drawable.consonant_da,
        R.drawable.consonant_dha,
        R.drawable.consonant_adha,
        R.drawable.consonant_ta,
        R.drawable.consonant_tha,
        R.drawable.consonant_thaaa,
        R.drawable.consonant_dhaa,
        R.drawable.consonant_na,
        R.drawable.consonant_pa,
        R.drawable.consonant_pha,
        R.drawable.consonant_ba,
        R.drawable.consonant_bha,
        R.drawable.consonant_ma,
        R.drawable.yyaa,
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
