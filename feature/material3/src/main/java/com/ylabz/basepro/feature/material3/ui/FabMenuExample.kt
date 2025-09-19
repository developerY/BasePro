package com.ylabz.basepro.feature.material3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // Correctly imported
import androidx.compose.material.icons.filled.Close // Correctly imported
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview // Will be used by @Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview // Added for previewing
@Composable
fun FabMenuExample() {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    // Handle back press to collapse the menu
    BackHandler(enabled = isMenuExpanded) {
        isMenuExpanded = false
    }

    FloatingActionButtonMenu(
        expanded = isMenuExpanded,
        button = { // Main Toggle FAB goes into the button slot
            ToggleFloatingActionButton(
                checked = isMenuExpanded,
                onCheckedChange = { checked -> isMenuExpanded = checked }
            ) { // Trailing lambda for ToggleFloatingActionButton content (the icon)
                val iconImage = if (isMenuExpanded) Icons.Filled.Close else Icons.Filled.Add
                Icon(imageVector = iconImage, contentDescription = "Toggle Menu")
            }
        }
    ) { // Trailing lambda for FloatingActionButtonMenu content (the menu items)
        FloatingActionButtonMenuItem(
            onClick = {
                isMenuExpanded = false // Collapse menu on item click
                /* Action 1 */
            },
            icon = { Icon(Icons.Filled.PhotoCamera, "Take Photo") },
            text = { Text("Photo") } // Changed from 'label' to 'text'
        )
        FloatingActionButtonMenuItem(
            onClick = {
                isMenuExpanded = false // Collapse menu on item click
                /* Action 2 */
            },
            icon = { Icon(Icons.Filled.Videocam, "Record Video") },
            text = { Text("Video") } // Changed from 'label' to 'text'
        )
    }
}
