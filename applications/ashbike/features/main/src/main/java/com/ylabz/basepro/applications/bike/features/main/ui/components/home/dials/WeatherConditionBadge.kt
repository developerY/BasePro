package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A simple composable that displays the weather condition text
 * in a small styled container.
 */
@Composable
fun WeatherConditionBadge(
    conditionText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Text(
            text = conditionText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

//Preview
@Preview
@Composable
fun WeatherConditionBadgePreview() {
    val condition = "Sunny"
    WeatherConditionBadge(conditionText = condition)
}