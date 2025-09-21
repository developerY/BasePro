package com.ylabz.basepro.feature.material3.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement // Added for Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row       // Added for button layout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding // Added for Row padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme // Added for Preview
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WavyProgressIndicatorExample() {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "ProgressAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp) // Added some padding to the column
    ) {
        Text(
            "Linear Wavy Progress",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp)) // Adjusted spacing

        Text(
            "Circular Wavy Progress",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CircularWavyProgressIndicator(progress = { animatedProgress })

        Spacer(Modifier.height(32.dp)) // Adjusted spacing

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally) // Spaced buttons
        ) {
            OutlinedButton(
                onClick = { if (progress > 0f) progress = (progress - 0.1f).coerceAtLeast(0f) },
                modifier = Modifier.weight(1f) // Give buttons equal weight
            ) {
                Text("Decrease")
            }
            OutlinedButton(
                onClick = { progress = 0.1f },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
            OutlinedButton(
                onClick = { if (progress < 1f) progress = (progress + 0.1f).coerceAtMost(1f) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Increase")
            }
        }
        Text(
            text = "Current Progress: ${"%.2f".format(animatedProgress)}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
private fun WavyProgressIndicatorExamplePreview() { // Renamed for clarity
    MaterialTheme {
        WavyProgressIndicatorExample()
    }
}
