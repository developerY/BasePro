package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A single card showing an icon, a value, and a label.
 */
/**
 * A single stat card with an icon, a value, and a label.
 */
@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp),  // adjust as needed
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray)
            )
        }
    }
}

@Preview
@Composable
fun StatCardPreview() {
    // Dummy data for preview
    val dummyIcon = androidx.compose.material.icons.Icons.Filled.Info // Replace with actual icon if available
    val dummyLabel = "Dummy Label"
    val dummyValue = "123"

    // Create a horizontal gradient brush for the background
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color.Cyan, Color.Magenta)
    )

    Column(modifier = Modifier.background(gradientBrush)) {
        StatCard(icon = dummyIcon, label = dummyLabel, value = dummyValue)
    }
}
