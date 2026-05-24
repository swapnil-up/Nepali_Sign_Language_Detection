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

    private val landmarkPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 3f
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val palmPaint = Paint().apply {
        color = Color.parseColor("#80FFFFFF")
        strokeWidth = 5f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint().apply {
        color = Color.parseColor("#FF6B6B")
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val indexPaint = Paint().apply {
        color = Color.parseColor("#FFD93D")
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val middlePaint = Paint().apply {
        color = Color.parseColor("#45B7D1")
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val ringPaint = Paint().apply {
        color = Color.parseColor("#96CEB4")
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val pinkyPaint = Paint().apply {
        color = Color.parseColor("#DDA0DD")
        strokeWidth = 6f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val tipPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 2f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    private data class ConnectionGroup(
        val connections: List<Pair<Int, Int>>,
        val paint: Paint
    )

    private val groups = listOf(
        ConnectionGroup(
            listOf(Pair(0, 5), Pair(5, 9), Pair(9, 13), Pair(13, 17), Pair(0, 17)),
            palmPaint
        ),
        ConnectionGroup(
            listOf(Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4)),
            thumbPaint
        ),
        ConnectionGroup(
            listOf(Pair(5, 6), Pair(6, 7), Pair(7, 8)),
            indexPaint
        ),
        ConnectionGroup(
            listOf(Pair(9, 10), Pair(10, 11), Pair(11, 12)),
            middlePaint
        ),
        ConnectionGroup(
            listOf(Pair(13, 14), Pair(14, 15), Pair(15, 16)),
            ringPaint
        ),
        ConnectionGroup(
            listOf(Pair(17, 18), Pair(18, 19), Pair(19, 20)),
            pinkyPaint
        ),
    )

    private val tipIndices = setOf(4, 8, 12, 16, 20)

    private var landmarks: List<HandLandmark>? = null

    fun setLandmarks(landmarks: List<HandLandmark>?) {
        this.landmarks = landmarks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        landmarks?.let { landmarkList ->
            drawConnections(canvas, landmarkList)
            drawLandmarks(canvas, landmarkList)
        }
    }

    private fun drawConnections(canvas: Canvas, landmarks: List<HandLandmark>) {
        groups.forEach { group ->
            group.connections.forEach { (start, end) ->
                if (start < landmarks.size && end < landmarks.size) {
                    val startPoint = PointF(landmarks[start].x * width, landmarks[start].y * height)
                    val endPoint = PointF(landmarks[end].x * width, landmarks[end].y * height)
                    canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, group.paint)
                }
            }
        }
    }

    private fun drawLandmarks(canvas: Canvas, landmarks: List<HandLandmark>) {
        landmarks.forEachIndexed { index, landmark ->
            val point = PointF(landmark.x * width, landmark.y * height)
            val radius = if (index in tipIndices) 14f else 10f
            canvas.drawCircle(point.x, point.y, radius, landmarkPaint)
            canvas.drawCircle(point.x, point.y, radius, tipPaint)
        }
    }
}
