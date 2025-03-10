package com.ylabz.basepro.feature.bike.ui.components.path

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.ui.tooling.preview.Preview

fun getCardinalDirection(headingDegrees: Float): String {
    // We'll divide 360° into 8 segments of 45° each.
    // heading 0° is N, 45° is NE, 90° is E, etc.
    val normalized = (headingDegrees % 360 + 360) % 360 // ensure 0..359
    return when {
        normalized < 22.5f -> "N"
        normalized < 67.5f -> "NE"
        normalized < 112.5f -> "E"
        normalized < 157.5f -> "SE"
        normalized < 202.5f -> "S"
        normalized < 247.5f -> "SW"
        normalized < 292.5f -> "W"
        normalized < 337.5f -> "NW"
        else -> "N"
    }
}


@Composable
fun DigitalCompassCard(
    headingDegrees: Float,
    modifier: Modifier = Modifier
) {
    val direction = getCardinalDirection(headingDegrees)
    Card(
        modifier = modifier
            .width(120.dp)
            .height(80.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Optionally, show a small compass icon
            Icon(
                imageVector = Icons.Filled.Explore,
                contentDescription = "Compass",
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Show heading in degrees, e.g. "28°"
            Text(
                text = "${headingDegrees.toInt()}° $direction",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun DigitalCompassCardPreview() {
    val headingDegrees = 45f
    DigitalCompassCard(headingDegrees)
}
