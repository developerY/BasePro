package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding // Added for ButtonDefaults.IconSpacing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit // Example icon for menu item
import androidx.compose.material.icons.filled.Save // Example icon for main button
import androidx.compose.material.icons.filled.Share // Example icon for menu item
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // Added for IconSpacing
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme // For Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SplitButtonExample(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    // Define items for the dropdown menu
    val menuItems = listOf(
        "Edit" to Icons.Filled.Edit,
        "Share" to Icons.Filled.Share
        // Add more items as needed
    )

    Box(modifier = modifier) {
        Row {
            // Primary action button
            Button(onClick = { /* TODO: Handle primary action (e.g., Save) */ }) {
                Icon(
                    Icons.Filled.Save,
                    contentDescription = "Save",
                    modifier = Modifier.padding(end = ButtonDefaults.IconSpacing) // Standard spacing
                )
                Text("Save")
            }
            // Secondary action button (to open dropdown)
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "More options")
            }
        }

        // DropdownMenu anchored to the Box, which contains the Row
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
            // You can use Modifier.align(Alignment.TopEnd) on the DropdownMenu
            // if you want to align it more precisely relative to the IconButton,
            // but the default positioning within the Box often works well.
        ) {
            menuItems.forEach { (label, icon) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        /* TODO: Handle action for $label */
                    },
                    leadingIcon = {
                        Icon(icon, contentDescription = label)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplitButtonExamplePreview() {
    MaterialTheme {
        SplitButtonExample()
    }
}
