package com.example.nsl_mini

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class LandmarkOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.parseColor("#59b5df")
        strokeWidth = 4f // Adjust the width of the lines as needed
    }

    private var landmarks: List<NormalizedLandmark>? = null

    // Define the pairs of indices to connect landmarks
    private val connections = listOf(
        // Example pairs, adjust these to match the hand landmarks connections
        Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4), // Thumb
        Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8), // Index finger
        Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12), // Middle finger
        Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16), // Ring finger
        Pair(13, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20), // Pinky finger
        Pair(0, 17) // Palm
    )

    fun setLandmarks(landmarks: List<NormalizedLandmark>?) {
        this.landmarks = landmarks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        landmarks?.let { landmarkList ->
            // Ensure the landmark list is not empty and has enough points
            if (landmarkList.isNotEmpty()) {
                connections.forEach { (start, end) ->
                    // Ensure the indices are within the bounds of the landmark list
                    if (start < landmarkList.size && end < landmarkList.size) {
                        val startX = landmarkList[start].x() * width
                        val startY = landmarkList[start].y() * height
                        val endX = landmarkList[end].x() * width
                        val endY = landmarkList[end].y() * height
                        canvas.drawLine(startX, startY, endX, endY, paint)
                    }
                }
            }
        }
    }
}
