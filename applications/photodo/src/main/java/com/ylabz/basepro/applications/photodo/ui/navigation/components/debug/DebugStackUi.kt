package com.ylabz.basepro.applications.photodo.ui.navigation.components.debug

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState

/**
 * A collapsible UI for displaying the current NavBackStack.
 */
@Composable
fun DebugStackUi(
    backStackKey: String,
    categoryId: Long,
    currentListId: String?,
    currentFabState: FabState?
) {
    // State to hold if the debug panel is expanded or not
    var isDebugExpanded by rememberSaveable { mutableStateOf(false) }
    var isDebugFABExpanded by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
    ) {
        // Clickable header to toggle the expanded state
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDebugExpanded = !isDebugExpanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDebugExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isDebugExpanded) "Collapse Debug" else "Expand Debug",
                tint = Color.White
            )
            Text(
                text = " DEBUG STACK (Tap to toggle)",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )
        }

        // Conditionally show the stack trace with an animation
        AnimatedVisibility(visible = isDebugExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "STACK: $backStackKey",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                // --- NEW INFO ---
                Text(
                    text = "CURRENT CATEGORY: $categoryId",
                    color = Color.Yellow, // Distinct color for category
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "CURRENT LIST ID: ${currentListId ?: "None"}",
                    color = Color.Cyan, // Distinct color for list
                    style = MaterialTheme.typography.bodySmall
                )
                // --- ADD THIS ---
                AnimatedVisibility(visible = isDebugFABExpanded) {
                    Text(
                        text = "FAB STATE: ${currentFabState.toString()?: "Null"}",
                        color = Color.Magenta, // Distinct color for FAB
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                // --- END ADD ---
            }
        }
    }
}