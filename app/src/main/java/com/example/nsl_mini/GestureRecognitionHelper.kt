package com.example.nsl_mini

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer as MpGestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GestureRecognizerHelper(
    private val context: Context,
    private val resultListener: (String, List<HandLandmark>?) -> Unit) : GestureRecognizer {

    private var gestureRecognizer: MpGestureRecognizer? = null
    private var modelAssetPath: String = ""

    var confidenceThreshold: Float = 0.65f
        set(value) {
            field = value
            recreateRecognizer()
        }

    var lastConfidence: Float = 0f
        private set

    var isModelLoaded: Boolean = false
        private set

    var modelError: String? = null
        private set

    override fun setup(modelAssetPath: String) {
        this.modelAssetPath = modelAssetPath
        createRecognizer()
    }

    private fun createRecognizer() {
        gestureRecognizer?.close()
        isModelLoaded = false
        modelError = null
        if (modelAssetPath.isEmpty()) {
            modelError = "Model path is empty"
            return
        }

        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelAssetPath)
                .build()

            val options = MpGestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener(this::returnLivestreamResult)
                .setErrorListener(this::returnLivestreamError)
                .setMinHandDetectionConfidence(confidenceThreshold.toFloat())
                .setMinHandPresenceConfidence(confidenceThreshold.toFloat())
                .setMinTrackingConfidence(confidenceThreshold.toFloat())
                .build()

            gestureRecognizer = MpGestureRecognizer.createFromOptions(context, options)
            isModelLoaded = true
        } catch (e: Exception) {
            modelError = "Failed to load model: ${e.localizedMessage}"
            Log.e("GestureRecognizerHelper", modelError!!, e)
        }
    }

    private fun recreateRecognizer() {
        if (gestureRecognizer != null) {
            createRecognizer()
        }
    }

    private fun returnLivestreamResult(result: GestureRecognizerResult, image: MPImage) {
        val stringBuilder = StringBuilder()
        lastConfidence = 0f

        result.gestures().forEach { gestureList ->
            gestureList.forEach { gesture ->
                if (gesture.score() > lastConfidence) {
                    lastConfidence = gesture.score()
                }
                if (gesture.score() >= confidenceThreshold) {
                    stringBuilder.append("${gesture.categoryName()}\n")
                }
            }
        }
        val handLandmarks = result.landmarks().flatten().map {
            HandLandmark(x = it.x(), y = it.y(), z = it.z())
        }
        resultListener(stringBuilder.toString().trim(), handLandmarks)
    }

    private fun returnLivestreamError(error: Exception) {
        Log.e("GestureRecognizerHelper", "Error in gesture recognizer", error)
    }

    override fun recognizeAsync(bitmap: Bitmap, frameTime: Long) {
        val mpImage = BitmapImageBuilder(bitmap).build()
        gestureRecognizer?.recognizeAsync(mpImage, frameTime)
    }
}
