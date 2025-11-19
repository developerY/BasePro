package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.unused

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp


@Composable
fun RotatedBatteryIndicator(
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    // Wrap the BatteryIndicator in a Box with a rotation modifier.
    Box(modifier = modifier.rotate(-90f)) {
        BatteryIndicator(batteryLevel = batteryLevel)
    }
}


@Composable
fun BatteryIndicator(
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    // Clamp battery level between 0 and 100
    val clampedLevel = batteryLevel.coerceIn(0, 100)
    // Convert to fraction (0f..1f)
    val fraction = clampedLevel / 100f
    // Interpolate color from Red (0%) to Green (100%)
    val batteryColor = lerp(Color.Red, Color.Green, fraction)

    Row(
        modifier = modifier
            .height(24.dp)
            .clipToBounds(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1) Battery Body (with border)
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .border(width = 2.dp, color = Color.Black, shape = RectangleShape)
        ) {
            // Fill the inside according to battery level
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(batteryColor)
            )
        }

        // 2) Small terminal on the right
        Spacer(modifier = Modifier.width(2.dp)) // small gap
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(12.dp)
                .background(Color.Black)
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun RotatedBatteryIndicatorPreview() {
    Column(Modifier.padding(16.dp)) {
        RotatedBatteryIndicator(batteryLevel = 50)
    }
}


//Preview
@Preview
@Composable
fun BatteryIndicatorPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BatteryIndicator(batteryLevel = 0)
            BatteryIndicator(batteryLevel = 25)
            BatteryIndicator(batteryLevel = 50)
            BatteryIndicator(batteryLevel = 75)
            BatteryIndicator(batteryLevel = 100)
        }
    }
}
*/