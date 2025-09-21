package com.ylabz.basepro.feature.material3.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenuExample() {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }

    // Handle back press to collapse the menu
    BackHandler(enabled = isMenuExpanded) {
        isMenuExpanded = false
    }

    FloatingActionButtonMenu(
        expanded = isMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isMenuExpanded,
                onCheckedChange = { checked -> isMenuExpanded = checked }
            ) {
                val iconImage = if (isMenuExpanded) Icons.Filled.Close else Icons.Filled.Add
                Icon(imageVector = iconImage, contentDescription = "Toggle Menu")
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                isMenuExpanded = false // Collapse menu on item click
                /* TODO: Handle Photo action */
            },
            icon = { Icon(Icons.Filled.PhotoCamera, "Take Photo") },
            text = { Text("Photo") }
        )
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

@Preview(showBackground = true)
@Composable
private fun FabMenuExamplePreview() {
    MaterialTheme {
        FabMenuExample()
    }
}
