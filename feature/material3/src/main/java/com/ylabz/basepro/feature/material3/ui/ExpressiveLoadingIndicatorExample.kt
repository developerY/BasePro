package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLoadingIndicatorExample(
    modifier: Modifier = Modifier,
    selectedOption: String = "Default" // "Default" or "Contained"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (selectedOption) {
            "Default" -> {
                // Default LoadingIndicator
                LoadingIndicator()
            }
            "Contained" -> {
                // ContainedLoadingIndicator with custom color and shapes
                ContainedLoadingIndicator(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.onSecondaryContainer, // Note: using indicatorColor
                    polygons = listOf(
                        MaterialShapes.Cookie9Sided,
                        MaterialShapes.Pentagon,
                        MaterialShapes.SoftBurst
                    ),
                    progress = { 0.75f } // Example determinate progress
                )
            }
            else -> {
                // Fallback to default if option is unknown
                LoadingIndicator()
            }
        }
    }
}

@Preview(showBackground = true, name = "Default Loading Indicator")
@Composable
private fun ExpressiveLoadingIndicatorDefaultPreview() {
    MaterialTheme {
        ExpressiveLoadingIndicatorExample(selectedOption = "Default")
    }
}

@Preview(showBackground = true, name = "Contained Loading Indicator")
@Composable
private fun ExpressiveLoadingIndicatorContainedPreview() {
    MaterialTheme {
        ExpressiveLoadingIndicatorExample(selectedOption = "Contained")
    }
}
