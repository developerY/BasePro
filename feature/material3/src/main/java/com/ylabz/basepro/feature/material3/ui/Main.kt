package com.ylabz.basepro.feature.material3.ui

// Animation imports

// Common imports

// Explicit imports for the example composables
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme // Keep this for fallback or specific uses
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.material3.ui.theme.MaterialExpressiveTheme // Added custom theme

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold, TopAppBar, BottomAppBar, FAB
@Composable
fun Material3ShowcaseScreen(modifier: Modifier = Modifier) {
    MaterialExpressiveTheme { // Apply MaterialExpressiveTheme
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
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { /* TODO: Handle menu click */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                        IconButton(onClick = { /* TODO: Handle search click */ }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                        Spacer(Modifier.weight(1f, true)) // Pushes subsequent items to the end
                        IconButton(onClick = { /* TODO: Handle home click */ }) {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* TODO: Handle FAB click */ }
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
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
                item { ShowcaseSection(title = "Expressive Loading Indicator (Default)") { ExpressiveLoadingIndicatorExample(selectedOption = "Default") } }
                item { ShowcaseSection(title = "Expressive Loading Indicator (Contained)") { ExpressiveLoadingIndicatorExample(selectedOption = "Contained") } }
                item { ShowcaseSection(title = "Split Button") { SplitButtonExample() } }
            }
        }
    }
}

@Composable
fun FloatingActionButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.FloatingActionButton(onClick = onClick) {
        content()
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
                style = MaterialTheme.typography.titleLarge, // This will now use ExpressiveTypography.titleLarge from the theme
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 1200)
@Composable
fun Material3ShowcaseScreenPreview() {
    MaterialExpressiveTheme { // Ensure MaterialExpressiveTheme is applied for the preview
        Material3ShowcaseScreen()
    }
}