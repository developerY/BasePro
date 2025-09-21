package com.ylabz.basepro.feature.material3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column // Added for alignment in Preview if needed
import androidx.compose.foundation.layout.Spacer // Added for spacing menu items
import androidx.compose.foundation.layout.height // Added for spacing menu items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme // For Preview
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment // Added for alignment in Preview if needed
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenuExample() {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    // Handle back press to collapse the menu
    BackHandler(enabled = isMenuExpanded) {
        isMenuExpanded = false
    }

    // Animate the rotation of the main FAB icon
    val fabIconRotation by animateFloatAsState(
        targetValue = if (isMenuExpanded) 45f else 0f,
        label = "FabIconRotation"
    )

    FloatingActionButtonMenu(
        expanded = isMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isMenuExpanded,
                onCheckedChange = { checked -> isMenuExpanded = checked }
            ) {
                // Use a single icon that rotates (e.g., Add) or switch between Add/Close
                // For a rotating effect on a single 'Add' icon:
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Toggle Menu",
                    modifier = Modifier.rotate(fabIconRotation)
                )
                // If you prefer switching icons (Add/Close) as before, 
                // you can remove the rotation and use:
                // val iconImage = if (isMenuExpanded) Icons.Filled.Close else Icons.Filled.Add
                // Icon(imageVector = iconImage, contentDescription = "Toggle Menu")
            }
        }
    ) {
        // Animate visibility of menu items
        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            FloatingActionButtonMenuItem(
                onClick = {
                    isMenuExpanded = false // Collapse menu on item click
                    /* TODO: Handle Photo action */
                },
                icon = { Icon(Icons.Filled.PhotoCamera, "Take Photo") },
                text = { Text("Photo") }
            )
        }

        // Add a small spacer if both items are visible and animated separately
        // If animating them as a block, this might not be needed or could be part of the block's layout
        if (isMenuExpanded) { // Only show spacer when menu is expanded
            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            FloatingActionButtonMenuItem(
                onClick = {
                    isMenuExpanded = false // Collapse menu on item click
                    /* TODO: Handle Video action */
                },
                icon = { Icon(Icons.Filled.Videocam, "Record Video") },
                text = { Text("Video") }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FabMenuExamplePreview() {
    MaterialTheme {
        // Column to better position the FAB in preview if it's at the edge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FabMenuExample()
        }
    }
}
