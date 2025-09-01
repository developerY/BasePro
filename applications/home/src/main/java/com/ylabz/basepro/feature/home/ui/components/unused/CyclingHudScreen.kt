package com.ylabz.basepro.feature.home.ui.components.unused

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// In a real Android XR app, this Composable would be rendered onto a surface
// that overlays the XR view (camera passthrough or virtual environment).
@Composable
fun CyclingHudScreen(
    modifier: Modifier = Modifier,
    speed: String,
    speedUnit: String,
    heartRate: String,
    navigationDistance: String,
    navigationInstruction: String,
    navigationIcon: ImageVector,
    elapsedTime: String,
    distanceTravelled: String
) {
    Box(modifier = modifier.fillMaxSize()) {
        // --- Navigation Display (Top Center) ---
        NavigationDisplay(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp), // Adjust padding for optimal HUD placement
            distance = navigationDistance,
            instruction = navigationInstruction,
            icon = navigationIcon
        )

        // --- Speed Display (Bottom Left) ---
        SpeedDisplay(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp),
            speed = speed,
            unit = speedUnit
        )

        // --- Heart Rate Display (Bottom Right) ---
        HeartRateDisplay(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            heartRate = heartRate
        )

        // --- Trip Stats (Top Left) ---
        TripStatsDisplay(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 24.dp),
            elapsedTime = elapsedTime,
            distanceTravelled = distanceTravelled
        )
    }
}

@Composable
fun HudElementCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // Using a Surface for subtle background, mimicking Material 3 card style.
    // In a HUD, this might be very low opacity or even just a conceptual grouping.
    // For true transparency against an XR background, this Surface might be omitted
    // or its color set to Color.Transparent, relying on text/icon shadows or outlines
    // if needed, which are harder to do simply in Compose for text.
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium, // Rounded corners
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), // Subtle, semi-transparent
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}


@Composable
fun NavigationDisplay(
    modifier: Modifier = Modifier,
    distance: String,
    instruction: String,
    icon: ImageVector
) {
    HudElementCard(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = "Navigation Direction",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary // Using primary color for accent
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = distance,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary // Accent color
        )
        Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SpeedDisplay(
    modifier: Modifier = Modifier,
    speed: String,
    unit: String
) {
    HudElementCard(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Speed, // Example icon
            contentDescription = "Speed",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.secondary // Different accent for variety
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = speed,
                fontSize = 48.sp, // Prominent speed display
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary // Accent color
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Bottom).padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun HeartRateDisplay(
    modifier: Modifier = Modifier,
    heartRate: String
) {
    HudElementCard(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Heart Rate",
            modifier = Modifier.size(32.dp),
            tint = Color(0xFFE91E63) // Custom accent color for heart rate
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = heartRate,
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE91E63) // Custom accent
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "bpm",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Bottom).padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
fun TripStatsDisplay(
    modifier: Modifier = Modifier,
    elapsedTime: String,
    distanceTravelled: String
) {
    HudElementCard(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.Timer, // Example icon
            contentDescription = "Trip Duration",
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = elapsedTime,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$distanceTravelled km", // Assuming km
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/*
@Preview(showBackground = true, backgroundColor = 0xFF000000) // Preview on black background
@Composable
fun CyclingHudScreenPreview() {
    // Make sure your project has a Material 3 Theme defined (e.g., AppTheme)
    // For this preview, wrapping with a basic MaterialTheme.
    MaterialTheme {
        CyclingHudScreen(
            speed = "28.5",
            speedUnit = "km/h",
            heartRate = "135",
            navigationDistance = "200m",
            navigationInstruction = "Main Street",
            navigationIcon = Icons.Filled.Navigation, // Example, could be more specific
            elapsedTime = "01:12:30",
            distanceTravelled = "25.7"
        )
    }
}

@Preview(showBackground = true, widthDp = 150)
@Composable
fun SpeedDisplayPreview() {
    MaterialTheme {
        SpeedDisplay(speed = "35.0", unit = "mph")
    }
}

@Preview(showBackground = true, widthDp = 150)
@Composable
fun HeartRateDisplayPreview() {
    MaterialTheme {
        HeartRateDisplay(heartRate = "128")
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun NavigationDisplayPreview() {
    MaterialTheme {
        NavigationDisplay(
            distance = "500ft",
            instruction = "Next Left",
            icon = Icons.Filled.ArrowUpward // Placeholder
        )
    }
}

 */