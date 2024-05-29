package com.example.nsl_mini

import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

object GestureRecognizerResultsAdapter {
    fun formatResults(result: GestureRecognizerResult): String {
        val stringBuilder = StringBuilder()

        result.gestures().forEach { gestureList ->
            gestureList.forEach { gesture ->
                stringBuilder.append("Gesture: ${gesture.categoryName()} - Confidence: ${gesture.score()}\n")
            }
        }

        return stringBuilder.toString()
    }
}
