package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.ylabz.basepro.feature.weather.ui.components.rain.randomInRange
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun RainVolumeCardWithWindAbbr(
    volume: Double,
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    val cardHeight = 200.dp
    val raindrops = remember { List(300) { RaindropOne(1200f, cardHeight.value, 2f..6f, 5f..10f) } }

    // Animate the raindrops in a loop
    LaunchedEffect(Unit) {
        while (true) {
            raindrops.forEach { raindrop ->
                raindrop.move()
                if (raindrop.y > cardHeight.value * 3) {
                    raindrop.resetPosition(cardHeight.value)
                    raindrop.startSplash()
                }
            }
            delay(16)  // ~60 FPS
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1) Draw falling raindrops behind everything
            Canvas(modifier = Modifier.fillMaxSize()) {
                raindrops.forEach { raindrop ->
                    if (raindrop.isSplashing) {
                        drawCircle(
                            color = Color(0x9E0288D1),
                            radius = raindrop.splashRadius / 2,
                            center = Offset(raindrop.x, size.height - raindrop.splashRadius)
                        )
                    } else {
                        drawLine(
                            color = Color(0x900288D1),
                            start = Offset(raindrop.x, raindrop.y),
                            end = Offset(raindrop.x, raindrop.y + raindrop.size * 4),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            // 2) Main text in the center (rain volume)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rain Volume",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${volume} mm",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.Black
                )
            }

            // 3) Wind dial in the bottom-left corner with an abbreviation (e.g., "WD")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDialOne(degree = windDegree.toFloat())
                // Optional small abbreviation on top of the dial
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(
                        text = "WD",  // Abbreviated label
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

data class RaindropOne(
    val screenWidth: Float,
    val screenHeight: Float,
    val sizeRange: ClosedFloatingPointRange<Float>,
    val speedRange: ClosedFloatingPointRange<Float>
) {
    private val random = Random(System.nanoTime())
    val size: Float = sizeRange.randomInRange()
    var speed: Float = speedRange.randomInRange()
    var x: Float = random.nextFloat() * screenWidth
    var y: Float = random.nextFloat() * screenHeight

    var isSplashing: Boolean by mutableStateOf(false)
    var splashRadius: Float by mutableStateOf(0f)
    private var splashProgress by mutableStateOf(0f)

    fun move() {
        if (isSplashing) {
            splashProgress += 0.1f
            splashRadius = (splashProgress * size * 3).coerceAtMost(size * 3)
            if (splashProgress >= 1f) {
                isSplashing = false
                splashProgress = 0f
                splashRadius = 0f
            }
        } else {
            y += speed
        }
    }

    fun resetPosition(height: Float) {
        y = -size
        x = random.nextFloat() * screenWidth
        speed = speedRange.randomInRange()
    }

    fun startSplash() {
        isSplashing = true
        splashProgress = 0f
    }
}

@Composable
fun WindDirectionDialOne(
    degree: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Light circle background
            drawCircle(
                color = Color(0xFFBBDEFB),
                center = center,
                radius = radius
            )

            // Markers every 45°
            for (i in 0..360 step 45) {
                val angleRad = Math.toRadians((i - 90).toDouble())
                val markerStart = Offset(
                    center.x + (radius * 0.85f * cos(angleRad)).toFloat(),
                    center.y + (radius * 0.85f * sin(angleRad)).toFloat()
                )
                val markerEnd = Offset(
                    center.x + (radius * cos(angleRad)).toFloat(),
                    center.y + (radius * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = Color.DarkGray,
                    start = markerStart,
                    end = markerEnd,
                    strokeWidth = 2f
                )
            }

            // Red arrow
            val arrowLength = radius * 0.6f
            val arrowAngleRad = Math.toRadians((degree - 90).toDouble())

            val tipX = center.x + (arrowLength * cos(arrowAngleRad)).toFloat()
            val tipY = center.y + (arrowLength * sin(arrowAngleRad)).toFloat()

            // Arrow shaft
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(tipX, tipY),
                strokeWidth = 4f
            )

            // Simple arrow head
            val headSize = 8f
            val leftAngle = arrowAngleRad + Math.toRadians(150.0)
            val rightAngle = arrowAngleRad - Math.toRadians(150.0)

            val leftX = tipX + headSize * cos(leftAngle).toFloat()
            val leftY = tipY + headSize * sin(leftAngle).toFloat()
            val rightX = tipX + headSize * cos(rightAngle).toFloat()
            val rightY = tipY + headSize * sin(rightAngle).toFloat()

            drawLine(
                color = Color.Red,
                start = Offset(tipX, tipY),
                end = Offset(leftX, leftY),
                strokeWidth = 4f
            )
            drawLine(
                color = Color.Red,
                start = Offset(tipX, tipY),
                end = Offset(rightX, rightY),
                strokeWidth = 4f
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun RainVolumeCardWithWindAbbrPreview() {
    RainVolumeCardWithWindAbbr(volume = 15.0, windDegree = 45)
}
*/
