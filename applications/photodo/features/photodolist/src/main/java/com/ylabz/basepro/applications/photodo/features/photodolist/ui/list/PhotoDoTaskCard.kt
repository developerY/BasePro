package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function from PhotoDoListUiRoute, can be kept here or moved to a common util file
private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return format.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDoTaskCard(
    modifier: Modifier = Modifier,
    task: TaskEntity,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Using MaterialTheme colors for consistency
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    val iconRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "ExpandIconRotation")

    Card(
        onClick = onItemClick, // Main card click navigates to detail
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .animateContentSize() // Animate size changes smoothly
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Task Name on the left
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f) // Allow text to take available space
                )

                // Icons on the right
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box { // Wrapper for DropdownMenu positioning
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "More options",
                                tint = contentColor
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    onDeleteClick()
                                    showMenu = false
                                }
                            )
                            // Add other options like "Edit" here if needed
                        }
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            modifier = Modifier.rotate(iconRotation),
                            tint = contentColor
                        )
                    }
                }
            }

            // Task Creation Time - shown below the name
            Text(
                text = "Created: ${task.creationDate.toFormattedDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
                modifier = Modifier.padding(top = 4.dp)
            )


            // Content to show when expanded
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = task.name ?: "No description available.", // Assuming TaskEntity might have a description
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                    // You can add more complex content here like image previews, sub-tasks etc.
                }
            }
        }
    }
}
