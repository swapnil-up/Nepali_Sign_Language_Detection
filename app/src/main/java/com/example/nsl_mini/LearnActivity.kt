package com.example.nsl_mini

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LearnActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        val cardVowels = findViewById<CardView>(R.id.card_vowels)
        val cardConsonants = findViewById<CardView>(R.id.card_consonants)

        cardVowels.setOnClickListener {
            startPager(
                intArrayOf(
                    R.drawable.vowel_a, R.drawable.vowel_aa, R.drawable.vowel_e,
                    R.drawable.vowel_ee, R.drawable.vowel_u, R.drawable.vowel_uu,
                    R.drawable.vowel_ri, R.drawable.vowel_ya, R.drawable.vowel_yai,
                    R.drawable.vowel_wo, R.drawable.vowel_wau, R.drawable.vowel_aam,
                    R.drawable.vowel_aha
                ),
                intArrayOf(
                    R.drawable.vowelsign_a, R.drawable.vowelsign_aa, R.drawable.vowelsign_e,
                    R.drawable.vowelsign_ee, R.drawable.vowelsign_u, R.drawable.vowelsign_uu,
                    R.drawable.vowelsign_ri, R.drawable.vowelsign_ya, R.drawable.vowelsign_yai,
                    R.drawable.vowelsign_wo, R.drawable.vowelsign_wau, R.drawable.vowelsign_aam,
                    R.drawable.vowelsign_aha
                )
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@LearnActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        cardConsonants.setOnClickListener {
            startPager(
                intArrayOf(
                    R.drawable.ka71, R.drawable.kha3, R.drawable.ga5,
                    R.drawable.gha6, R.drawable.nga6, R.drawable.cha6,
                    R.drawable.chha7, R.drawable.ja7, R.drawable.jha5,
                    R.drawable.yan6, R.drawable.ta4, R.drawable.tha7,
                    R.drawable.da7, R.drawable.dha6, R.drawable.ada4,
                    R.drawable.taa4, R.drawable.tha6, R.drawable.daa4,
                    R.drawable.dhha5, R.drawable.na4, R.drawable.pa4,
                    R.drawable.fa5, R.drawable.ba6, R.drawable.bha6,
                    R.drawable.ma3, R.drawable.ya3, R.drawable.ra2,
                    R.drawable.la6, R.drawable.wa2, R.drawable.sha2,
                    R.drawable.shaa4, R.drawable.sa3, R.drawable.ha5,
                    R.drawable.ksha47, R.drawable.taa4, R.drawable.gya5
                ),
                intArrayOf(
                    R.drawable.consonant_ka, R.drawable.consonant_kha, R.drawable.consonant_ga,
                    R.drawable.consonant_gha, R.drawable.consonant_kna, R.drawable.consonant_chaa,
                    R.drawable.cchhaa, R.drawable.consonant_ja, R.drawable.consonant_jha,
                    R.drawable.consonant_ya, R.drawable.thhaa, R.drawable.consonant_thaa,
                    R.drawable.consonant_da, R.drawable.consonant_dha, R.drawable.consonant_adha,
                    R.drawable.consonant_ta, R.drawable.consonant_tha, R.drawable.consonant_thaaa,
                    R.drawable.consonant_dhaa, R.drawable.consonant_na, R.drawable.consonant_pa,
                    R.drawable.consonant_pha, R.drawable.consonant_ba, R.drawable.consonant_bha,
                    R.drawable.consonant_ma, R.drawable.yyaa, R.drawable.consonant_ra,
                    R.drawable.consonant_la, R.drawable.consonant_va, R.drawable.consonant_s,
                    R.drawable.consonant_ss, R.drawable.consonant_sa, R.drawable.consonant_ha,
                    R.drawable.consonant_chya, R.drawable.consonant_tra, R.drawable.consonant_gya
                )
            )
        }
    }

    private fun startPager(images: IntArray, signs: IntArray) {
        val intent = Intent(this, PagerActivity::class.java)
        intent.putExtra(PagerActivity.EXTRA_IMAGES, images)
        intent.putExtra(PagerActivity.EXTRA_SIGNS, signs)
        startActivity(intent)
    }

}
