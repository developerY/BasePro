package com.ylabz.basepro.feature.material3.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Linear Wavy Progress Indicator
        LinearWavyProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(30.dp))
        // Circular Wavy Progress Indicator
        CircularWavyProgressIndicator(progress = { animatedProgress })
        Spacer(Modifier.height(30.dp))
        OutlinedButton(
            onClick = { if (progress < 1f) progress += 0.1f }
        ) {
            Text("Increase Progress")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun WavyProgressIndicatorExamplePreview() {
    WavyProgressIndicatorExample()
}