package com.example.nsl_mini

import android.content.Context
import android.util.Log
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.vision.core.RunningMode

class GestureRecognizerHelper(private val context: Context) {
    private var gestureRecognizer: GestureRecognizer? = null

    fun setupGestureRecognizer(
        modelAssetPath: String,
        minHandDetectionConfidence: Float = 0.5f,
        minHandPresenceConfidence: Float = 0.5f,
        minTrackingConfidence: Float = 0.5f
    ) {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelAssetPath)
                .build()

            val options = GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(minHandDetectionConfidence)
                .setMinTrackingConfidence(minTrackingConfidence)
                .setMinHandPresenceConfidence(minHandPresenceConfidence)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .build()

            gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e("GestureRecognizerHelper", "Error setting up gesture recognizer", e)
        }
    }


    private fun returnLivestreamResult(result: GestureRecognizerResult, inputImage: MPImage) {
        // Handle gesture recognition results here
    }

    private fun returnLivestreamError(error: Exception) {
        // Handle errors here
    }

    fun recognizeAsync(image: MPImage, frameTime: Long) {
        gestureRecognizer?.recognizeAsync(image, frameTime)
    }
}
