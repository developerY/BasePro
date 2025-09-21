package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A centralized screen to showcase all the Material 3 Expressive components.
 * This provides a "catalog" view, making it easy to see and interact with all
 * the examples in one place.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3ExpressiveShowcaseScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M3 Expressive Showcase") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ShowcaseSection(title = "Split Button") { SplitButtonExample() } }
            item { ShowcaseSection(title = "FAB Menu") { FabMenuExample() } }
            item { ShowcaseSection(title = "Button Group") { ButtonGroupExample() } }
            item { ShowcaseSection(title = "Morphing Icon Button") { MorphingIconButtonExample() } }
            item { ShowcaseSection(title = "Expressive Card") { ExpressiveCardExample() } }
            item { ShowcaseSection(title = "Loading Indicators") { LoadingIndicatorsSection() } }
            item { ShowcaseSection(title = "Flexible Bottom App Bar") { FlexibleBottomAppBarExample() } }
            item { ShowcaseSection(title = "Vertical Slider") { VerticalSliderExample() } }
        }
    }
}

/**
 * A wrapper composable to create a titled section for each component showcase.
 */
@Composable
private fun ShowcaseSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

/**
 * A dedicated section for the different loading indicators.
 */
@Composable
private fun LoadingIndicatorsSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("WavyProgressIndicator", style = MaterialTheme.typography.bodyLarge)
        WavyProgressIndicatorExample()
        Spacer(modifier = Modifier.height(8.dp))
        Text("Morphing LoadingIndicator", style = MaterialTheme.typography.bodyLarge)
        ExpressiveLoadingIndicatorExample()
    }
}

@Preview(showBackground = true)
@Composable
private fun Material3ExpressiveShowcaseScreenPreview() {

        Material3ExpressiveShowcaseScreen()

}