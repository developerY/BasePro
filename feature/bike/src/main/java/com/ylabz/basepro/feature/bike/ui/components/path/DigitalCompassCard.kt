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



@Composable
fun DigitalCompassCard(
    headingDegrees: Float,
    modifier: Modifier = Modifier
) {
    val direction = getCardinalDirection(headingDegrees)
    Card(
        modifier = modifier
            .width(80.dp)
            .height(60.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90CAF9).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show heading in degrees, e.g. "28° NE"
            Text(
                text = "${headingDegrees.toInt()}° $direction",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = Color.Black
            )
        }
    }
}

/**
 * Convert heading (0–360) to an 8-direction label (N, NE, E, SE, S, SW, W, NW).
 */
fun getCardinalDirection(headingDegrees: Float): String {
    val normalized = (headingDegrees % 360 + 360) % 360
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


@Preview
@Composable
fun DigitalCompassCardPreview() {
    val headingDegrees = 45f
    DigitalCompassCard(headingDegrees)
}
