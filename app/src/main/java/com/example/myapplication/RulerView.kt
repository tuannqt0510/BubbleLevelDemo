package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.min


class RulerView : View {

    private var paintCircle = Paint()
    private var paintMarker = Paint()
    private val paintFill = Paint()
    private var lastY = 0f

    private var gestureDetector: GestureDetector
    private var currentDegree = 90f // Current degree based on swipe

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        paintCircle.color = Color.BLACK
        paintCircle.strokeWidth = 4f
        paintCircle.style = Paint.Style.STROKE
        paintCircle.isAntiAlias = true

        paintMarker.color = Color.BLACK
        paintMarker.strokeWidth = 2f
        paintMarker.style = Paint.Style.STROKE
        paintMarker.isAntiAlias = true
        paintMarker.textSize = 20f

        paintFill.style = Paint.Style.FILL
        paintFill.color = Color.GRAY

        gestureDetector = GestureDetector(context, GestureListener { currentDegree ->
            // Update the view based on the current degree
            Log.d("TUANNQ", "CurrentDegree: $currentDegree")
            this.currentDegree = currentDegree
            invalidate()
        })

        // Make sure the view is interactive and receives touch events
        isFocusable = true
        isClickable = true
        isFocusableInTouchMode = true // Ensure the view is focusable in touch mode
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height
        val centerY = height / 2
        val radius = min(
            width * 3 / 4.0,
            height * 3 / 4.0
        ).toFloat() // Radius of the half-circle

        canvas.drawColor(Color.YELLOW)
        canvas.translate(0f, centerY.toFloat())


        val bounds = Rect()
        val text = "90"
        paintMarker.getTextBounds(text, 0, text.length, bounds)
        val textHeight = bounds.height()
        val space = height / 70f

        val oval = RectF(
            -radius + width / 20,
            -radius + width / 20,
            radius - width / 20,
            radius - width / 20
        )

        canvas.drawArc(
            oval,
            -90f,
            currentDegree,
            true,
            paintFill
        ) // Draw half-circle from -90 to 90 degrees

        canvas.save()
        canvas.rotate(currentDegree)
        canvas.drawLine(0f, -radius * 1.1f, 0f, -radius + centerY, paintCircle)

        canvas.drawCircle(0f, -radius * 1.1f, 10f, paintFill)

        val degreeText = "Angle: ${currentDegree.toInt()}°"
        canvas.drawText(
            degreeText,
            0f - paintMarker.measureText(degreeText) / 2,
            -radius * 1.2f,
            paintMarker
        )
        canvas.restore()


        // Draw the degree markers along the half-circle using rotation
        for (i in 0..180) {
            canvas.save() // Save the current state of the canvas

            // Rotate the canvas for each marker
            val angle: Float = i.toFloat()  // Calculate angle for each marker
            canvas.rotate(angle)

            // Adjust length of markers (longer markers at every 10 degrees, shorter otherwise)
            val markerLength = if ((i % 10 == 0)) width / 20 else width / 30

            // Draw the marker line
            canvas.drawLine(
                0f, -radius, 0f, -radius + markerLength, paintMarker
            )

            // Draw degree labels at each 10 degrees
            if (i % 10 == 0 && i != 0 && i != 180) {
                canvas.drawText(
                    i.toString(),
                    -paintMarker.measureText(i.toString()) / 2,
                    -radius + markerLength + space,
                    paintMarker
                )
                canvas.drawLine(
                    0f,
                    -radius + markerLength + textHeight + space,
                    0f,
                    -radius + markerLength + textHeight + 2f * space,
                    paintMarker
                )
                canvas.drawText(
                    (180 - i).toString(),
                    -paintMarker.measureText((180 - i).toString()) / 2,
                    -radius + markerLength + textHeight + 3 * space,
                    paintMarker
                )
                canvas.drawLine(
                    0f,
                    -markerLength.toFloat(),
                    0f,
                    -radius + markerLength + textHeight + 3.5f * space,
                    paintMarker
                )
            }

            canvas.restore() // Restore the canvas to the previous state
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pass the touch event to the GestureDetector
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y // Initialize the starting Y position
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val currentY = event.y // Get current Y position
                val deltaY: Float =
                    lastY - currentY // Calculate the difference from the last Y position

                // Update the degree based on the vertical swipe distance
                currentDegree -= deltaY / 10 // Adjust the divisor to control sensitivity

                // Cap the degree between 0 and 180
                if (currentDegree < 0) {
                    currentDegree = 0f
                } else if (currentDegree > 180) {
                    currentDegree = 180f
                }

                lastY = currentY // Update lastY for the next move
                invalidate() // Redraw the view
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return true
        }

        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private class GestureListener(val onResult: (Float) -> Unit) : SimpleOnGestureListener() {
        var currentDegree = 90f

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = e2.y - e1!!.y // Calculate vertical distance of the swipe
            val diffX = e2.x - e1.x // Calculate horizontal distance of the swipe

            if (abs(diffY.toDouble()) > abs(diffX.toDouble())) { // If swipe is primarily vertical
                if (abs(diffY.toDouble()) > SWIPE_THRESHOLD && abs(velocityY.toDouble()) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown() // Swipe down detected
                    } else {
                        onSwipeUp() // Swipe up detected
                    }
                    return true
                }
            }
            return false
        }

        private fun onSwipeUp() {
            currentDegree -= 1f // Increase degree for swipe up
            if (currentDegree < 0) currentDegree = 0f // Limit the degree to 180
            // Redraw the view
            onResult.invoke(currentDegree)
        }

        private fun onSwipeDown() {
            currentDegree += 1f // Decrease degree for swipe down
            if (currentDegree > 180) currentDegree = 180f // Limit the degree to 0
            // Redraw the view
            onResult.invoke(currentDegree)
        }

        companion object {
            private const val SWIPE_THRESHOLD = 50 // Minimum distance for a swipe to be considered
            private const val SWIPE_VELOCITY_THRESHOLD =
                50 // Minimum velocity for a swipe to be considered
        }
    }

}