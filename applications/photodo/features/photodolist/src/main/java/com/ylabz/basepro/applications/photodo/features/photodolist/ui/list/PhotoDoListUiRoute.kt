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
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    onTaskClick: (Long) -> Unit,
    onEvent: (PhotoDoListEvent) -> Unit, // onEvent is still passed in, though not used directly here
    viewModel: PhotoDoListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoListUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoListUiState.Success -> {
            LazyColumn {
                items(state.tasks) { task ->
                    Text("Task: ${task.name}")
                    /*
                    fun PhotoDoTaskCard(
    task: TaskEntity,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
                     */

                    PhotoDoTaskCard(
                        task = task,
                        onItemClick = { onTaskClick(task.taskId) },
                        onDeleteClick = {},
                    )
                }

            }
        }
        is PhotoDoListUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}

// TaskRow composable is now removed, as its functionality is in PhotoDoTaskCard
// private fun Long.toFormattedDate(): String { ... } // Also removed, present in PhotoDoTaskCard (or move to common utils)
