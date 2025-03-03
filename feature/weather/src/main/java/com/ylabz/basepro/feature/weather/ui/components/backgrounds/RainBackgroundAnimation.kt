package com.ylabz.basepro.feature.weather.ui.components.backgrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun RainBackgroundAnimation(
    modifier: Modifier = Modifier
) {
    // For background simulation, assume default dimensions.
    // In practice, this Canvas will be sized by its parent (e.g., fillMaxSize())
    val defaultScreenWidth = 600f
    val defaultScreenHeight = 200f

    // Create a list of raindrops; adjust the count for desired density.
    val raindrops = remember {
        List(150) {
            Raindrop(
                screenWidth = defaultScreenWidth,
                screenHeight = defaultScreenHeight,
                sizeRange = 2f..6f,
                speedRange = 3f..7f
            )
        }
    }

    // Continuously update raindrop positions
    LaunchedEffect(Unit) {
        while (true) {
            raindrops.forEach { drop ->
                drop.move()
                // Reset the drop if it falls well beyond the visible area.
                if (drop.y > defaultScreenHeight * 3) {
                    drop.resetPosition(defaultScreenHeight)
                    drop.startSplash()
                }
            }
            delay(16L) // Roughly 60 frames per second
        }
    }

    // Draw the raindrops on a Canvas
    Canvas(modifier = modifier.fillMaxSize()) {
        raindrops.forEach { drop ->
            if (drop.isSplashing) {
                // Draw a small splash circle at the bottom
                drawCircle(
                    color = Color(0x9E0288D1),
                    radius = drop.splashRadius / 2,
                    center = Offset(drop.x, size.height - drop.splashRadius)
                )
            } else {
                // Draw a falling rain line
                drawLine(
                    color = Color(0x900288D1),
                    start = Offset(drop.x, drop.y),
                    end = Offset(drop.x, drop.y + drop.size * 4),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Preview
@Composable
fun RainBackgroundAnimationPreview() {
    RainBackgroundAnimation()
}
