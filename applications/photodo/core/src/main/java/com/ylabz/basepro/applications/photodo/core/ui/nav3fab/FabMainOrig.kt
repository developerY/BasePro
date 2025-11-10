package com.ylabz.basepro.applications.photodo.core.ui.nav3fab

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
import com.ylabz.basepro.applications.photodo.core.ui.FabStateOrig


private val TAG = "FabMain "



// In MainScreen.kt
@OptIn(ExperimentalMaterial3ExpressiveApi::class) // Add this annotation
@Composable
fun FabMainOrig(fabStateOrig: FabStateOrig?) {
    // A state to control the menu expansion, hoisted here
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    when (fabStateOrig) {
        is FabStateOrig.Single -> {
            ExtendedFloatingActionButton(
                onClick = fabStateOrig.onClick,
                text = { Text("${fabStateOrig.text} -- FabMain Single") },
                icon = { Icon(fabStateOrig.icon, contentDescription = fabStateOrig.text) }
            )
        }
        is FabStateOrig.Split -> {
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
                                fabStateOrig.primaryOnClick()
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
                        fabStateOrig.secondaryOnClick()
                        isFabMenuExpanded = false // Close menu after action
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.Create, contentDescription = "Add Category")
                }
            }
        }
        is FabStateOrig.Hidden, null -> {
            // Render nothing
        }
    }
}

/**
 * Preview --- Starts Here
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun FabMainOrigPreview() {
    val fabStateOrig = FabStateOrig.Split(
        primaryText = "Add List",
        primaryIcon = Icons.Default.Add,
        primaryOnClick = {},
        secondaryText = "Add Category",
        secondaryIcon = Icons.Default.Create,
        secondaryOnClick = {}
    )
    FabMainOrig(fabStateOrig)
}