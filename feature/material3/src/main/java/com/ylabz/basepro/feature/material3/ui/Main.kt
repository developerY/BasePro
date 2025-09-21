package com.ylabz.basepro.feature.material3.ui

// Common imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Assume these composables are defined in other files in this package:
// import com.ylabz.basepro.feature.material3.ui.WavyProgressIndicatorExample
// import com.ylabz.basepro.feature.material3.ui.ButtonGroupExample
// import com.ylabz.basepro.feature.material3.ui.FabMenuExample
// import com.ylabz.basepro.feature.material3.ui.MorphingIconButtonExample
// import com.ylabz.basepro.feature.material3.ui.ExpressiveLoadingIndicatorExample
// import com.ylabz.basepro.feature.material3.ui.SplitButtonExample

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold, TopAppBar
@Composable
fun Material3ShowcaseScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Material 3 Components Showcase") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { ShowcaseSection(title = "Wavy Progress Indicator") { WavyProgressIndicatorExample() } }
            item { ShowcaseSection(title = "Expressive Card") { ExpressiveCardExample() } }
            item { ShowcaseSection(title = "Button Group") { ButtonGroupExample() } }
            item { ShowcaseSection(title = "FAB Menu") { FabMenuExample() } }
            item { ShowcaseSection(title = "Morphing Icon Button") { MorphingIconButtonExample() } }
            item { ShowcaseSection(title = "Expressive Loading Indicator") { ExpressiveLoadingIndicatorExample(
                selectedOption = "Default"
            ) } }
            item { ShowcaseSection(title = "Expressive Loading Indicator") { ExpressiveLoadingIndicatorExample(
                selectedOption = "Contained"
            ) } }
            item { ShowcaseSection(title = "Split Button") { SplitButtonExample() } }
            // Add other examples here if available
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                content()
            }
        }
    }
}

// Definition for ExpressiveCardExample (originally from your Main.kt)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ExpressiveCardExample() {
    var expanded by remember { mutableStateOf(false) } // This state is not used in the current snippet
    val options = listOf("Save as Draft", "Export as PDF", "Delete") // This state is not used
    var selectedOption by remember { mutableStateOf("Save") } // This state is not used
    var isSelected by remember { mutableStateOf(false) }

    Card( // This is the root of ExpressiveCardExample
        onClick = { isSelected = !isSelected }, // Made the card clickable to toggle selection
        colors = CardDefaults.cardColors(
            containerColor =
            if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Added padding to internal content
            Text(
                text = "Dinner club",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
            )
            // You might want to add more content or interactions here
            // based on the 'expanded', 'options', 'selectedOption' variables if they were intended for use.
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 1000)
@Composable
fun Material3ShowcaseScreenPreview() {
    MaterialTheme { // Ensure MaterialTheme is applied for the preview
        Material3ShowcaseScreen()
    }
}
