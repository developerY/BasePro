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
fun ExpressiveLoadingIndicatorExample(selectedOption: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedOption == "Default") { // Still keeping this as per original user code structure
            // Default LoadingIndicator
            LoadingIndicator()
        } else {
            // ContainedLoadingIndicator with custom color and shapes
            ContainedLoadingIndicator(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                // color = MaterialTheme.colorScheme.onSecondaryContainer, // REMOVED THIS LINE
                polygons = listOf(
                    MaterialShapes.Cookie9Sided,
                    MaterialShapes.Pentagon,
                    MaterialShapes.SoftBurst
                ),
                progress = { 0.5f },
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ExpressiveLoadingIndicatorExampleDefaultPreview() {
    ExpressiveLoadingIndicatorExample(selectedOption = "Default")
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ExpressiveLoadingIndicatorExampleContainedPreview() {
    ExpressiveLoadingIndicatorExample(selectedOption = "Contained")
}



