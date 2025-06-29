package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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

/**
 * A single card showing an icon, a value, and a label.
 */
/**
 * A single stat card with an icon, a value, and a label.
 */
@Composable
fun StatCard(
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant, // Default to a theme color
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp),  // adjust as needed
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            // Use theme color for the card's background
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                tint = tint, // This will use the passed 'tint' or the default theme color
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                // Use theme color for the primary text
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                // Use theme color for the secondary text
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun StatCardPreview() {
    // Dummy data for preview
    val dummyIcon = Icons.Filled.Info // Replace with actual icon if available
    // For preview, let's explicitly use a theme color if we want to see it,
    // otherwise it will use the default from the StatCard's signature.
    val dummyIconTint = MaterialTheme.colorScheme.primary
    val dummyLabel = "Dummy Label"
    val dummyValue = "123"

    // Create a horizontal gradient brush for the background for preview purposes
    // In a real app, the screen background would likely also come from the theme
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.surfaceBright)
    )

    Column(modifier = Modifier.background(gradientBrush)) {
        StatCard(icon = dummyIcon, tint = dummyIconTint, label = dummyLabel, value = dummyValue)
    }
}

@Preview
@Composable
fun StatCardPreviewGray() {
    // Dummy data for preview
    val dummyIcon = Icons.Filled.Info // Replace with actual icon if available
    val dummyLabel = "Dummy Label"
    val dummyValue = "123"

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.surfaceBright)
    )

    Column(modifier = Modifier.background(gradientBrush)) {
        // Here, the default tint (MaterialTheme.colorScheme.onSurfaceVariant) will be used
        StatCard(icon = dummyIcon,label = dummyLabel, value = dummyValue)
    }
}

@Preview
@Composable
fun StatCardPreviewRed() {
    // Dummy data for preview
    val dummyIcon = Icons.Filled.Info // Replace with actual icon if available
    val dummyIconTint = Color.Red // Keep this specific for this preview to test explicit tinting
    val dummyLabel = "Dummy Label"
    val dummyValue = "123"

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.surfaceBright)
    )

    Column(modifier = Modifier.background(gradientBrush)) {
        StatCard(icon = dummyIcon, tint = dummyIconTint, label = dummyLabel, value = dummyValue)
    }
}