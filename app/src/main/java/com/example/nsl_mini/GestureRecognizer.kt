package com.example.nsl_mini

import android.graphics.Bitmap

interface GestureRecognizer {
    fun setup(modelAssetPath: String)
    fun recognizeAsync(bitmap: Bitmap, frameTime: Long)
}
