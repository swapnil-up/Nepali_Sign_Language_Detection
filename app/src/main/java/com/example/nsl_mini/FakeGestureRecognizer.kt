package com.example.nsl_mini

import android.graphics.Bitmap

class FakeGestureRecognizer(
    private val result: String = "test_gesture",
    private val landmarks: List<HandLandmark> = emptyList(),
) : GestureRecognizer {

    var lastFrameTime: Long = 0
    var wasSetupCalled = false

    override fun setup(modelAssetPath: String) {
        wasSetupCalled = true
    }

    override fun recognizeAsync(bitmap: Bitmap, frameTime: Long) {
        lastFrameTime = frameTime
    }

    fun buildResult(): Pair<String, List<HandLandmark>> = result to landmarks
}
