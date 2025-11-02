package com.ylabz.basepro.applications.photodo.core.ui

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview



private val TAG = "FabMain "



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
                text = { Text("${fabState.text} -- FabMain Single") },
                icon = { Icon(fabState.icon, contentDescription = fabState.text) }
            )
        }
        is FabState.Split -> {
            FloatingActionButtonMenu(
                expanded = isFabMenuExpanded,
                // The main button that toggles the menu
                button = {
                    ExtendedFloatingActionButton(
                        text = { Text("Add List -- FabMain Split") },
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
                        onClick = {
                            // If there are items, the main button toggles the menu
                            if (fabStateMenu.items.isNotEmpty()) {
                                Log.d(TAG, "Main FAB clicked. Toggling menu.")
                                isFabMenuExpanded = !isFabMenuExpanded
                            } else {
                                // If there are NO items, the main button performs its own action
                                // This is for cases where you use FabStateMenu.Menu with an empty item list.
                                Log.d(TAG, "Main FAB clicked. Executing mainButtonAction.")
                                fabStateMenu.mainButtonAction.onClick()
                            }
                        }
                    ) {
                        // Icon animation logic (unchanged)
                        val rotation by animateFloatAsState(
                            targetValue = if (isFabMenuExpanded) 45f else 0f,
                            animationSpec = tween(durationMillis = 200),
                            label = "FabRotation"
                        )
                        Icon(
                            imageVector = fabStateMenu.mainButtonAction.icon,
                            contentDescription = "Open menu",
                            modifier = Modifier.rotate(rotation),
                            tint = Color.Green// LocalContentColor.current, -- used for debug
                        )
                    }
                }
            ) { // --- Menu Items Content ---
                // This is the code you care about. It will now work because the menu can open.
                fabStateMenu.items.forEach { item ->
                    ExtendedFloatingActionButton(
                        onClick = {
                            Log.d(TAG, " FabStateMenu.items Got the click ${item.text}")
                            item.onClick() // <-- THIS WILL NOW CALL YOUR VIEWMODEL
                            isFabMenuExpanded = false // Close the menu after clicking.
                        },
                        text = { Text("${item.text} -- FabMenu foreach") },
                        icon = { Icon(item.icon, contentDescription = item.text) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }

        // NOT USED
        // This handles a simple, single-action button
        is FabStateMenu.Single -> {
            FloatingActionButton(
                onClick = {
                    // THIS LOG WILL NOW APPEAR for single buttons
                    // OUR UI NEVER USES THIS !!!
                    Log.d(TAG, "Should never see this -- single FAB clicked: '${fabStateMenu.action.text}'.")
                    Log.d(TAG, "Single FAB clicked: '${fabStateMenu.action.text}'.")
                    fabStateMenu.action.onClick() // <-- THIS WILL NOW CALL YOUR VIEWMODEL
                }
            ) {
                Icon(imageVector = fabStateMenu.action.icon, contentDescription = fabStateMenu.action.text)
            }
        }

        is FabStateMenu.Hidden, null -> {
            // Render nothing.
        }
    }
}


/**
 * Preview --- Starts Here
 */
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