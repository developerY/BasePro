package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.path.unused.DigitalCompassCard

@Composable
fun SpeedometerWithCompassOverlay(
    modifier: Modifier = Modifier,
    currentSpeed: Float,
    heading: Float,
    maxSpeed: Float = 60f,
    contentColor: Color
) {
    Box(modifier = modifier) {
        // 1) The main speedometer
        FancySpeedometer(
            currentSpeed = currentSpeed,
            maxSpeed = maxSpeed,
            modifier = Modifier.fillMaxSize(),
            contentColor = contentColor
        )

        // 2) Overlay the digital compass, now positioned below the speed text
        Box(
            modifier = Modifier
                .align(Alignment.Center) // First, align the container to the center
                .offset(y = 77.dp)       // Then, push it down with a vertical offset
        ) {
            DigitalCompassCard(
                headingDegrees = heading,
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp)
            )
        }
    }
}
/*
@Preview
@Composable
fun SpeedometerWithCompassOverlayPreview() {
    SpeedometerWithCompassOverlay(
        currentSpeed = 30f,
        heading = 45f,
        maxSpeed = 100f,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}
*/