package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.applications.photodo.db.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    // The onEvent lambda is now passed to the composable
    onEvent: (PhotoDoListEvent) -> Unit,
    viewModel: PhotoDoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoListUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoListUiState.Success -> {
            LazyColumn(modifier = modifier) {
                items(state.photoItems) { task ->
                    TaskRow(
                        task = task,
                        onItemClick = { onItemClick(task.id.toString()) },
                        // Pass the delete event up
                        onDeleteClick = { onEvent(PhotoDoListEvent.OnDeleteTaskClicked(task)) }
                    )
                }
            }
        }
        is PhotoDoListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message)
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskEntity,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = task.name)
            Text(text = "Created: ${task.startTime.toFormattedDate()}")
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
        }
    }
}

private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return format.format(date)
}