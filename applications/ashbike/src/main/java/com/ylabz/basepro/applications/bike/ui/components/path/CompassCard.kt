package com.ylabz.basepro.applications.bike.ui.components.path

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassCard(
    headingDegrees: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CompassDial(headingDegrees = headingDegrees)
        }
    }
}

@Composable
fun CompassDial(
    headingDegrees: Float,
    modifier: Modifier = Modifier
) {
    // We'll draw a circle background, cardinal directions, and a rotating arrow
    Canvas(modifier = modifier.fillMaxSize()) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 1) Draw circle background
        drawCircle(
            color = Color.White,
            radius = radius,
            center = center
        )

        // 2) Draw cardinal directions (N, E, S, W)
        // We'll place them at 0°, 90°, 180°, 270° from the top
        // For brevity, just show N and S
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Helper function to draw text at an angle
        fun drawTextAtAngle(label: String, angleDeg: Float) {
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val x = center.x + cos(angleRad).toFloat() * (radius - 30f)
            val y = center.y + sin(angleRad).toFloat() * (radius - 30f) + 15f // offset to center text
            drawContext.canvas.nativeCanvas.drawText(label, x, y, textPaint)
        }

        // N (0°)
        drawTextAtAngle("N", -90f)
        // E (90°)
        drawTextAtAngle("E", 0f)
        // S (180°)
        drawTextAtAngle("S", 90f)
        // W (270°)
        drawTextAtAngle("W", 180f)

        // 3) Draw the arrow that rotates with heading
        // We'll treat 0° heading as North, so we rotate from -90
        val arrowAngleRad = Math.toRadians(headingDegrees.toDouble() - 90)
        val arrowLength = radius * 0.7f
        val arrowEnd = Offset(
            x = center.x + cos(arrowAngleRad).toFloat() * arrowLength,
            y = center.y + sin(arrowAngleRad).toFloat() * arrowLength
        )

        // Draw arrow line
        drawLine(
            color = Color.Red,
            start = center,
            end = arrowEnd,
            strokeWidth = 6f
        )
        // Draw arrow head (simple approach: small line on each side)
        val headSize = 10f
        val leftAngle = arrowAngleRad + Math.toRadians(150.0)
        val rightAngle = arrowAngleRad - Math.toRadians(150.0)
        val leftX = arrowEnd.x + headSize * cos(leftAngle).toFloat()
        val leftY = arrowEnd.y + headSize * sin(leftAngle).toFloat()
        val rightX = arrowEnd.x + headSize * cos(rightAngle).toFloat()
        val rightY = arrowEnd.y + headSize * sin(rightAngle).toFloat()

        drawLine(
            color = Color.Red,
            start = arrowEnd,
            end = Offset(leftX, leftY),
            strokeWidth = 6f
        )
        drawLine(
            color = Color.Red,
            start = arrowEnd,
            end = Offset(rightX, rightY),
            strokeWidth = 6f
        )
    }
}

@Preview
@Composable
fun CompassCardPreview() {
    val heading = 45f
    CompassCard(headingDegrees = heading)
}
