package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function - consider moving to a common util file if not already done
// If you have a common date formatting utility, prefer using that.
private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return format.format(date)
}

@Composable
fun PhotoDoDetailUiRoute(
    modifier: Modifier = Modifier,
    viewModel: PhotoDoDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) { // Use Surface for background & theming
        when (val state = uiState) {
            is PhotoDoDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PhotoDoDetailUiState.Success -> {
                // Extracted details into a separate composable for clarity
                PhotoDoTaskDetailsView(task = state.task, modifier = Modifier.padding(16.dp))
            }
            is PhotoDoDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoDoTaskDetailsView(task: TaskEntity, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Increased spacing
    ) {
        // Task Name
        Text(
            text = task.name,
            style = MaterialTheme.typography.headlineMedium, // More prominent title
            color = MaterialTheme.colorScheme.primary
        )

        Divider() // Visual separation

        // Creation Time
        Text(
            text = "Created: ${task.startTime.toFormattedDate()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp)) // Extra space before description

        // Description
        Text(
            text = "Details:", // Label for description
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = task.name ?: "No description provided.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Extra space before photo section

        // Placeholder for Photo(s) - visually enhanced
        Text(
            text = "Photos:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Slightly larger placeholder
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium), // Themed background and shape
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Photo previews will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Future: Add action buttons like "Edit", "Add Photo", "Mark as complete"
        // e.g., using a Row with ButtonGroup or individual Buttons
    }
}
