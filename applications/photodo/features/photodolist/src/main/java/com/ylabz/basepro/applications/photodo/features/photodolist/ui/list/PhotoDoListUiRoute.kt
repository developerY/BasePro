package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import android.util.Log
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

private const val TAG = "PhotoDoListUiRoute"

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
            Log.d(TAG, "Displaying Loading state")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoListUiState.Success -> {
            Log.d(TAG, "Displaying Success state with ${state.taskLists.size} items.")
            LazyColumn(modifier = modifier) {
                items(state.taskLists) { task ->
                    PhotoDoTaskCard(
                        task = task,
                        onItemClick = { onTaskClick(task.listId) },
                        onDeleteClick = { }//onEvent(PhotoDoListEvent.OnDeleteTaskClicked(task.taskId)) },
                    )
                }
            }
        }
        is PhotoDoListUiState.Error -> {
            Log.e(TAG, "Displaying Error state: ${state.message}")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}
