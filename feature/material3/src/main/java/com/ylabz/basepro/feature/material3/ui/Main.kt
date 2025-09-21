package com.ylabz.basepro.feature.material3.ui

// Animation imports
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateContentSize // For Modifier

// Common imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Explicit imports for the example composables
import com.ylabz.basepro.feature.material3.ui.ButtonGroupExample
import com.ylabz.basepro.feature.material3.ui.ExpressiveLoadingIndicatorExample
import com.ylabz.basepro.feature.material3.ui.FabMenuExample
import com.ylabz.basepro.feature.material3.ui.MorphingIconButtonExample
import com.ylabz.basepro.feature.material3.ui.SplitButtonExample
import com.ylabz.basepro.feature.material3.ui.WavyProgressIndicatorExample

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold, TopAppBar, BottomAppBar, FAB
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
                        Icon(Icons.Filled.Add, contentDescription = "Add")
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
                modifier = Modifier.padding(bottom = 12.dp) // Increased bottom padding
            )
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveCardExample() {
    var isSelected by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val cardOptions = listOf("Save as Draft", "Export as PDF", "Delete")
    var selectedOptionText by remember { mutableStateOf("Save") } // To display selected option
    var showMenu by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.surfaceVariant,
        label = "CardContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                      else MaterialTheme.colorScheme.onSurfaceVariant, // Use onSurfaceVariant for better contrast
        label = "CardContentColor"
    )
    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "ExpandIconRotation")

    Card(
        onClick = { isSelected = !isSelected },
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .animateContentSize() // Animate size changes smoothly
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dinner club",
                    style = MaterialTheme.typography.titleMedium, // Adjusted style
                    color = contentColor
                )
                Row {
                    Box { // Wrapper for DropdownMenu positioning
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "More options",
                                tint = contentColor
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            cardOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOptionText = option
                                        showMenu = false
                                        // TODO: Handle $option click
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            modifier = Modifier.rotate(iconRotation),
                            tint = contentColor
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "More details about the dinner club: \nJoin us every Friday for a delightful culinary experience. \nSelected option: $selectedOptionText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                    // You can add more complex content here
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 1200) // Increased height for better view
@Composable
fun Material3ShowcaseScreenPreview() {
    MaterialTheme { // Ensure MaterialTheme is applied for the preview
        Material3ShowcaseScreen()
    }
}
