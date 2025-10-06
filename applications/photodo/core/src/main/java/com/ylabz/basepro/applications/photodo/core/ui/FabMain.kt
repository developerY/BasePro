package com.ylabz.basepro.applications.photodo.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview

// In MainScreen.kt
@OptIn(ExperimentalMaterial3ExpressiveApi::class) // Add this annotation
@Composable
fun FabMain(fabState: FabState?) {
    // A state to control the menu expansion, hoisted here
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    when (fabState) {
        is FabState.Single -> {
            ExtendedFloatingActionButton(
                onClick = fabState.onClick,
                text = { Text(fabState.text) },
                icon = { Icon(fabState.icon, contentDescription = fabState.text) }
            )
        }
        is FabState.Split -> {
            FloatingActionButtonMenu(
                expanded = isFabMenuExpanded,
                // The main button that toggles the menu
                button = {
                    ExtendedFloatingActionButton(
                        text = { Text("Add List") },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add List") },
                        onClick = {
                            // If the menu is open, the main button performs the primary action.
                            // If closed, it opens the menu.
                            if (isFabMenuExpanded) {
                                fabState.primaryOnClick()
                                // isFabMenuGCC-Status-Body = ""
                            }
                            isFabMenuExpanded = !isFabMenuExpanded
                        }
                    )
                }
            ) {
                // This is the secondary menu item that appears when expanded
                SmallFloatingActionButton(
                    onClick = {
                        fabState.secondaryOnClick()
                        isFabMenuExpanded = false // Close menu after action
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Create, contentDescription = "Add Category")
                }
            }
        }
        is FabState.Hidden, null -> {
            // Render nothing
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class) // Add this annotation
@Composable
fun FabMenu(fabStateMenu: FabStateMenu?) {
    // This state now lives here and controls the expansion of the menu.
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    when (fabStateMenu) {
        /*is FabStateMenu.Single -> {
            ExtendedFloatingActionButton(
                onClick = {
                    isFabMenuExpanded = false // Ensure menu is closed
                    fabStateMenu.action.onClick()
                },
                text = { Text(fabStateMenu.action.text) },
                icon = { Icon(fabStateMenu.action.icon, contentDescription = fabStateMenu.action.text) }
            )
        }*/
        is FabStateMenu.Menu -> {
            // State to control whether the menu is open or closed.
            var isFabMenuExpanded by remember { mutableStateOf(false) }

            // This is a custom composable that likely handles the layout and animation
            // of the expanding menu.
            FloatingActionButtonMenu(
                expanded = isFabMenuExpanded,
                // --- Main Button ---
                // Changed to a standard FloatingActionButton to show only the icon.
                button = {
                    FloatingActionButton(
                        // The main button's only job is to toggle the menu's expanded state.
                        onClick = { isFabMenuExpanded = !isFabMenuExpanded }
                    ) {
                        // Animate the icon rotation from a '+' to an 'x' when expanded.
                        val rotation by animateFloatAsState(
                            targetValue = if (isFabMenuExpanded) 45f else 0f,
                            animationSpec = tween(durationMillis = 200)
                        )
                        Icon(
                            imageVector = fabStateMenu.mainButtonAction.icon, // This should be Icons.Default.Add
                            contentDescription = "Open menu",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            ) { // --- Menu Items Content ---
                // Iterate through the list of actions defined in the current screen's state.
                fabStateMenu.items.forEach { item ->
                    // Using ExtendedFloatingActionButton for items that have both text and an icon.
                    ExtendedFloatingActionButton(
                        onClick = {
                            item.onClick() // Execute the specific action for this item.
                            isFabMenuExpanded = false // Close the menu after clicking an item.
                        },
                        text = { Text(item.text) },
                        icon = { Icon(item.icon, contentDescription = item.text) },
                        // Use a secondary color to distinguish menu items from the main button.
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }
        is FabStateMenu.Hidden, null -> {
            // Render nothing.
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun FabMainPreview() {
    val fabState = FabState.Split(
        primaryText = "Add List",
        primaryIcon = Icons.Default.Add,
        primaryOnClick = {},
        secondaryText = "Add Category",
        secondaryIcon = Icons.Default.Create,
        secondaryOnClick = {}
    )
    FabMain(fabState)
}

@Preview
@Composable
fun FabOrigPreview() {
    val fabState = FabState.Single(
        text = "Add Item",
        icon = Icons.Default.Add,
        onClick = {}
    )
    FabOrig(fabState)
}


@Composable
private fun FabOrig(fabState: FabState?) {
    when (fabState) {
        is FabState.Single -> {
            ExtendedFloatingActionButton(
                onClick = fabState.onClick,
                text = { Text(fabState.text) },
                icon = { Icon(fabState.icon, contentDescription = null) }
            )
        }
        is FabState.Split -> {
            // We are calling the new SplitButtonFab composable here
            SplitButtonFab(fabState = fabState)
        }
        is FabState.Hidden, null -> {
            // Do nothing to show no FAB
        }
    }
}