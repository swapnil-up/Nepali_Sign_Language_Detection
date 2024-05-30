package com.example.nsl_mini

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class GestureRecognizerHelper(
    private val context: Context,
    private val resultListener: (String, List<NormalizedLandmark>?) -> Unit)

{
    private var gestureRecognizer: GestureRecognizer? = null
    private val confidenceThreshold = 0.65

    fun setupGestureRecognizer(modelAssetPath: String) {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelAssetPath)
            .build()

        val options = GestureRecognizer.GestureRecognizerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener(this::returnLivestreamResult)
            .setErrorListener(this::returnLivestreamError)
            .setMinHandDetectionConfidence(confidenceThreshold.toFloat())
            .setMinHandPresenceConfidence(confidenceThreshold.toFloat())
            .setMinTrackingConfidence(confidenceThreshold.toFloat())
            .build()

        gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
    }

    private fun returnLivestreamResult(result: GestureRecognizerResult, image: MPImage) {
        val stringBuilder = StringBuilder()

        result.gestures().forEach { gestureList ->
            gestureList
                .filter { it.score() >= confidenceThreshold } // Filter gestures by confidence
                .forEach { gesture ->
                    stringBuilder.append("${gesture.categoryName()}\n")
                }
        }
        // Extract hand landmarks from the result
        val handLandmarks = result.landmarks().flatten()
        resultListener(stringBuilder.toString().trim(), handLandmarks)
    }

    private fun returnLivestreamError(error: Exception) {
        Log.e("GestureRecognizerHelper", "Error in gesture recognizer", error)
    }

    fun recognizeAsync(bitmap: Bitmap, frameTime: Long) {
        val mpImage = BitmapImageBuilder(bitmap).build()
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }
}
