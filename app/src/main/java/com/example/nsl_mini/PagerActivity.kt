package com.example.nsl_mini

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class PagerActivity : BaseActivity() {
    private lateinit var imageFirst: ImageView
    private lateinit var imageSecond: ImageView
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private val images: IntArray by lazy { intent.getIntArrayExtra(EXTRA_IMAGES) ?: intArrayOf() }
    private val signs: IntArray by lazy { intent.getIntArrayExtra(EXTRA_SIGNS) ?: intArrayOf() }
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

        imageFirst = findViewById(R.id.image_first)
        imageSecond = findViewById(R.id.image_second)
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
            if (currentIndex < images.size - 1) {
                currentIndex++
                updateImages()
            }
        }
    }

    private fun updateImages() {
        imageFirst.setImageResource(images[currentIndex])
        imageSecond.setImageResource(signs[currentIndex])
    }

    companion object {
        const val EXTRA_IMAGES = "images"
        const val EXTRA_SIGNS = "signs"
    }
}
