package com.example.nsl_mini

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class LandmarkOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.parseColor("#59b5df")
        strokeWidth = 4f
        isAntiAlias = true
    }

    private var landmarks: List<HandLandmark>? = null

    fun setLandmarks(landmarks: List<HandLandmark>?) {
        this.landmarks = landmarks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        landmarks?.let { landmarkList ->
            landmarkList.forEach { landmark ->
                val landmarkPoint = PointF(landmark.x * width, landmark.y * height)
                canvas.drawCircle(landmarkPoint.x, landmarkPoint.y, 8f, paint)
            }

            drawConnections(canvas, landmarkList)
        }
    }

    private fun drawConnections(canvas: Canvas, landmarks: List<HandLandmark>) {
        val connections = listOf(
            Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
            Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
            Pair(5, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),
            Pair(9, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),
            Pair(13, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20),
            Pair(0, 17)
        )

        connections.forEach { (start, end) ->
            if (start < landmarks.size && end < landmarks.size) {
                val startPoint = PointF(landmarks[start].x * width, landmarks[start].y * height)
                val endPoint = PointF(landmarks[end].x * width, landmarks[end].y * height)
                canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint)
            }
        }
    }
}
