package com.example.pictureactions

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import com.google.android.material.slider.Slider

class CustomSlider(
    context: Context,
    attrs: AttributeSet? = null
) : Slider(context, attrs) {

    private val leftTrackPaint = Paint().apply {
        color = 0xFFFF0000.toInt() // Red for negative values
        isAntiAlias = true
    }

    private val rightTrackPaint = Paint().apply {
        color = 0xFF00FF00.toInt() // Green for positive values
        isAntiAlias = true
    }

    private val thumbRadius = 12f // Adjust based on your thumb size

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the actual track boundaries (not just padding)
        val trackLeft = trackSidePadding.toFloat()
        val trackRight = width - trackSidePadding.toFloat()
        val trackWidth = trackRight - trackLeft

        Log.d("LeftTrack", "trackLeft: $trackLeft")
        Log.d("RightTrack", "trackRight: $trackRight, width: $width, paddingRight: $paddingRight")

        // Zero position (middle of track)
        val centerX = trackLeft + trackWidth / 2f
        val trackTop = (height - trackHeight) / 2f
        val trackBottom = trackTop + trackHeight

        // Get thumb position (ensuring it's inside track bounds)
        val thumbX = trackLeft + ((value - valueFrom) / (valueTo - valueFrom)) * trackWidth

        // Ensure thumb doesn't exceed track bounds
        val clampedThumbX = Math.max(trackLeft, Math.min(thumbX, trackRight))

        // Attach track to thumb (trailing edge)
        val trackEndX = clampedThumbX - thumbRadius

        // Clip to prevent exceeding the track
        canvas.save()
        canvas.clipRect(trackLeft, trackTop, trackRight, trackBottom)

        // Draw left (Red) track up to the thumb's tail
        if (value < 0) {
            canvas.drawRect(centerX, trackTop, trackEndX, trackBottom, leftTrackPaint)
        }

        // Draw right (Green) track up to the thumb's tail
        if (value > 0) {
            canvas.drawRect(centerX, trackTop, trackEndX, trackBottom, rightTrackPaint)
        }

        canvas.restore()
    }

}
