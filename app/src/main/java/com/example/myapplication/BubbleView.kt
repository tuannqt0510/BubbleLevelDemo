package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

class BubbleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr), SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var xPosition by mutableFloatStateOf(0f)
    private var yPosition by mutableFloatStateOf(0f)

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    @Composable
    override fun Content() {
        BubbleLevelUI(xPosition, yPosition)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = event.values[0]
            val y = event.values[1]

            // Điều chỉnh giá trị để bubble di chuyển hợp lý
            xPosition = x * 10 // Tăng giá trị để dễ thấy sự di chuyển
            yPosition = y * 10
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Không cần xử lý
    }

    @Composable
    fun BubbleLevelUI(xPosition: Float, yPosition: Float) {

        val context = LocalContext.current

        Canvas(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Giới hạn bubble trong phạm vi của màn hình
                    val limitedX = min(max(xPosition, -canvasWidth / 2), canvasWidth / 2)
                    val limitedY = min(max(yPosition, -canvasHeight / 2), canvasHeight / 2)

                    val bitmap =
                        Bitmap
                            .createScaledBitmap(
                                BitmapFactory
                                    .decodeResource(resources, R.drawable.background_center),
                                500,
                                500,
                                false
                            )
                            .asImageBitmap()


                    drawImage(bitmap, Offset(0f, height / 2f))

                    val bubbleCenterBitmap = Bitmap
                        .createScaledBitmap(
                            BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_bubble_center
                            ),
                            50,
                            150,
                            false
                        )
                        .asImageBitmap()
                    drawImage(
                        image = bubbleCenterBitmap,
                        Offset(
                            bitmap.width / 2 + xPosition,
                            height / 2f + yPosition
                        )
                    )
                }
            }) {

        }
    }
}


fun Dp.toPx(drawScope: DrawScope): Float {
    return with(drawScope) { this@toPx.toPx() }
}