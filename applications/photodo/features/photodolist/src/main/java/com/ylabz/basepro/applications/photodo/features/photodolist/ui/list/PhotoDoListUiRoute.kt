package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * The route for displaying the list of tasks for a given project.
 * This composable is stateless and driven by the provided ViewModel.
 */
@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    onTaskClick: (Long) -> Unit,
    onEvent: (PhotoDoListEvent) -> Unit, // Allows parent to send events
    // The ViewModel is now a required parameter.
    viewModel: PhotoDoListViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoListUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoListUiState.Success -> {
            LazyColumn(modifier = modifier) {
                items(state.tasks) { task ->
                    PhotoDoTaskCard(
                        task = task,
                        onItemClick = { onTaskClick(task.taskId) },
                        onDeleteClick = { onEvent(PhotoDoListEvent.OnDeleteTaskClicked(task.taskId)) },
                    )
                }
            }
        }
        is PhotoDoListUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}
