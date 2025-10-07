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
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenu(fabStateMenu: FabStateMenu?) {
    // This is the SINGLE source of truth for the menu's state.
    // It is initialized to `false` so the menu always starts closed.
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // This is the key to "forcing" it closed. This effect will run
    // whenever the fabStateMenu object itself changes (e.g., navigating to a new screen).
    // It guarantees that the menu will reset to a closed state on navigation.
    LaunchedEffect(fabStateMenu) {
        isFabMenuExpanded = false
    }

    when (fabStateMenu) {


        is FabStateMenu.Menu -> {
            // The duplicate state declaration has been removed from here.
            FloatingActionButtonMenu(
                expanded = isFabMenuExpanded,
                // --- Main Button ---
                button = {
                    FloatingActionButton(
                        // The button's only job is to toggle the single state variable.
                        onClick = { isFabMenuExpanded = !isFabMenuExpanded }
                    ) {
                        // Animate the icon from a '+' to an 'x' when expanded.
                        val rotation by animateFloatAsState(
                            targetValue = if (isFabMenuExpanded) 45f else 0f,
                            animationSpec = tween(durationMillis = 200),
                            label = "FabRotationAnimation"
                        )
                        Icon(
                            imageVector = fabStateMenu.mainButtonAction.icon,
                            contentDescription = "Open menu",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            ) { // --- Menu Items Content ---
                fabStateMenu.items.forEach { item ->
                    ExtendedFloatingActionButton(
                        onClick = {
                            item.onClick()
                            isFabMenuExpanded = false // Close the menu after clicking.
                        },
                        text = { Text(item.text) },
                        icon = { Icon(item.icon, contentDescription = item.text) },
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