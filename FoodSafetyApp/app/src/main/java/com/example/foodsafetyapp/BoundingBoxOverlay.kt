package com.example.foodsafetyapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BoundingBoxOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val detections = mutableListOf<Detection>()
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        style = Paint.Style.FILL
    }
    private val boxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    data class Detection(
        val bounds: RectF,
        val label: String,
        val confidence: Float
    )

    fun setDetections(newDetections: List<Detection>) {
        detections.clear()
        detections.addAll(newDetections)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        detections.forEach { detection ->
            // Draw bounding box
            canvas.drawRect(detection.bounds, boxPaint)

            // Draw label background
            textPaint.color = Color.BLACK
            val textHeight = textPaint.descent() - textPaint.ascent()
            canvas.drawRect(
                detection.bounds.left,
                detection.bounds.top - textHeight,
                detection.bounds.left + textPaint.measureText(detection.label),
                detection.bounds.top,
                textPaint
            )

            // Draw label text
            textPaint.color = Color.WHITE
            canvas.drawText(
                "${detection.label} (${"%.1f".format(detection.confidence * 100)}%)",
                detection.bounds.left,
                detection.bounds.top - textPaint.descent(),
                textPaint
            )
        }
    }

    // Add this to your BoundingBoxOverlay class
    fun drawDebugInfo(canvas: Canvas, matrix: Matrix) {
        val debugPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        // Draw image boundaries
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), debugPaint)

        // Draw matrix info
        val values = FloatArray(9)
        matrix.getValues(values)
        canvas.drawText("Matrix: ${values.contentToString()}", 20f, 100f, textPaint)
    }
}


