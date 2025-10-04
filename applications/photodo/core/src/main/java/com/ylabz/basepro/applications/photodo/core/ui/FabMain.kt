package com.ylabz.basepro.applications.photodo.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
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
    var isFabMenuExpanded by remember { mutableStateOf(true) }

    when (fabStateMenu) {
        is FabStateMenu.Single -> {
            ExtendedFloatingActionButton(
                onClick = {
                    isFabMenuExpanded = false // Ensure menu is closed
                    fabStateMenu.action.onClick()
                },
                text = { Text(fabStateMenu.action.text) },
                icon = { Icon(fabStateMenu.action.icon, contentDescription = fabStateMenu.action.text) }
            )
        }
        is FabStateMenu.Menu -> {
            // If the state is Menu, draw the FloatingActionButtonMenu.
            FloatingActionButtonMenu(
                expanded = isFabMenuExpanded,
                // The main button that is always visible.
                button = {
                    ExtendedFloatingActionButton(
                        text = { Text(fabStateMenu.mainButtonAction.text) },
                        icon = { Icon(fabStateMenu.mainButtonAction.icon, contentDescription = fabStateMenu.mainButtonAction.text) },
                        onClick = {
                            // If the menu has items, clicking the main button toggles the menu.
                            // If the menu is already open, it performs the primary action.
                            if (fabStateMenu.items.isNotEmpty()) {
                                if (isFabMenuExpanded) {
                                    fabStateMenu.mainButtonAction.onClick()
                                    isFabMenuExpanded = false
                                } else {
                                    isFabMenuExpanded = true
                                }
                            } else {
                                // If there are no menu items, it's just a regular button.
                                fabStateMenu.mainButtonAction.onClick()
                            }
                        }
                    )
                }
            ) { // This is the `content` block for the menu items.
                // Loop through the list of secondary actions and create a Small FAB for each.
                fabStateMenu.items.forEach { item ->
                    SmallFloatingActionButton(
                        onClick = {
                            item.onClick()
                            isFabMenuExpanded = false // Close the menu after an item is clicked.
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(item.icon, contentDescription = item.text)
                    }
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