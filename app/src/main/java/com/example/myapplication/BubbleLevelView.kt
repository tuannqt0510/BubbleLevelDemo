package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.math.sqrt

class BubbleLevelView(context: Context, attrs: AttributeSet) : View(context, attrs),
    SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var xPos = 0f
    private var yPos = 0f
    private var bubbleRadius = 30f
    private var targetXPos = 0f
    private var targetYPos = 0f
    private var isBackgroundDraw = false

    private val bubblePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val levelPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val rulerBackgroundPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        canvas.save()
        canvas.translate(-width / 8, 0f)

        //background center
        drawCircularLevel(canvas, width, height)

        // Draw the horizontal and vertical rulers
        drawRulers(canvas, width, height)

        // Draw the bubble in the circular level
        drawBubble(canvas)
        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Adjust bubble radius based on new size
        bubbleRadius =
            min(w, h) / 30f  // Adjust the divisor as needed to change bubble size proportion

        invalidate() // Redraw the view with the new bubble size
    }

    private fun drawCircularLevel(canvas: Canvas, width: Float, height: Float) {
        val centerX = width / 2
        val centerY = height / 2
        val radius = min(width, height) / 3

        // Draw the concentric circles
        for (i in 1..3) {
            canvas.drawCircle(centerX, centerY, i * radius / 3, levelPaint)
        }

        // Draw cross lines
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, levelPaint)
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, levelPaint)

        val bitmapBubbleBackground = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.background_center),
            (radius * 2).toInt(),
            (radius * 2).toInt(),
            false
        )

        canvas.drawBitmap(bitmapBubbleBackground, centerX - radius, centerY - radius, levelPaint)
    }

    private fun drawBubble(canvas: Canvas) {
        val rulerSize = min(width, height) // Diameter of the circular level
        val rulerThickness = width / 4.5f
        val radius = min(width, height) / 3

        val bitmapBubbleCenter = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_bubble_center
            ), rulerSize / 10, rulerSize / 15, false
        )

            canvas.drawBitmap(
                bitmapBubbleCenter,
                xPos - bitmapBubbleCenter.width / 2,
                yPos - bitmapBubbleCenter.height / 2,
                bubblePaint
            )
        val hrX = width / 8
        val hrY = 0f
        // Map bubble position for the horizontal ruler (same as circular level's X position)
        val bubbleXInRuler = hrX + (xPos - (width / 2 - rulerSize / 2))
        // Center vertically in the ruler
        val bubbleYCenterInRuler = hrY + rulerThickness / 2

        // Draw the bubble in the horizontal ruler
        val bitmapBubbleHorizontal = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_bubble_horizontal
            ), (rulerSize / 6).toInt(), (rulerSize / 18).toInt(), false
        )

        canvas.drawBitmap(
            bitmapBubbleHorizontal,
            bubbleXInRuler - bitmapBubbleHorizontal.width / 2,
            bubbleYCenterInRuler - bitmapBubbleHorizontal.height / 2,
            bubblePaint
        )

        val vrX = width - rulerThickness / 2
        val vrY = height / 2 - rulerSize / 2
        // Map bubble position for the vertical ruler (same as circular level's Y position)
        val bubbleYInRuler = vrY + (yPos - (height / 2 - rulerSize / 2))
        // Center horizontally in the ruler
        val bubbleXCenterInRuler = vrX + rulerThickness / 2

        // Draw the bubble in the vertical ruler
        val bitmapBubbleVertical = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources, R.drawable.ic_bubble_vertical
            ), (rulerSize / 18).toInt(), (rulerSize / 6).toInt(), false
        )

        canvas.drawBitmap(
            bitmapBubbleVertical,
            bubbleXCenterInRuler - bitmapBubbleVertical.width / 2,
            bubbleYInRuler - bitmapBubbleVertical.height / 2,
            bubblePaint
        )

    }

    private fun drawRulers(canvas: Canvas, width: Float, height: Float) {
        // Calculate the diameter of the circular level
        val radius = (min(width, height) / 3)
        val rulerSize = min(width, height) // Diameter of the circular level

        val rulerThickness = width / 4.5f

        // Draw horizontal ruler background
        val hrX = width / 8
        val hrY = 0f
        val bitmapRulerHorizontalBackground = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.bg_ruler_horizontal),
            rulerSize.toInt(),
            rulerThickness.toInt(),
            false
        )
        canvas.drawBitmap(bitmapRulerHorizontalBackground, hrX, hrY, rulerBackgroundPaint)

        // Draw vertical ruler background
        val vrX = width - rulerThickness / 2
        val vrY = height / 2 - rulerSize / 2
        val bitmapRulerVerticalBackground = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.bg_ruler_vertical),
            rulerThickness.toInt(),
            rulerSize.toInt(),
            false
        )
        canvas.drawBitmap(bitmapRulerVerticalBackground, vrX, vrY, rulerBackgroundPaint)

        Log.e("TAG", "drawRulers: $isBackgroundDraw")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]

            // Adjust x-axis and y-axis for bubble movement
            val radius = min(width, height) / 3

            // Update target positions using low-pass filter to smooth the movement
            val alpha = 0.15f  // Low-pass filter coefficient for smoothness
            targetXPos = width / 2 - x * radius / 10
            targetYPos = height / 2 + y * radius / 10

            // Update current positions gradually towards the target positions for water-like effect
            xPos += (targetXPos - xPos) * alpha
            yPos += (targetYPos - yPos) * alpha

            // Ensure the bubble is within the bounds of the circular level
            val distanceFromCenter =
                sqrt((xPos - width / 2) * (xPos - width / 2) + (yPos - height / 2) * (yPos - height / 2))
            if (distanceFromCenter > radius) {
                val scale = radius / distanceFromCenter
                xPos = (xPos - width / 2) * scale + width / 2
                yPos = (yPos - height / 2) * scale + height / 2
            }

            invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // No action required
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
    }
}