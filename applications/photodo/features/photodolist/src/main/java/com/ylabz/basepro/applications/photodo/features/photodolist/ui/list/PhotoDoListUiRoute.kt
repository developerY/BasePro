package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

// import androidx.compose.foundation.clickable // No longer directly used here
// import androidx.compose.material.icons.Icons // No longer directly used here
// import androidx.compose.material.icons.filled.Delete // No longer directly used here
// import androidx.compose.material3.Icon // No longer directly used here
// import androidx.compose.material3.IconButton // No longer directly used here
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

// import com.ylabz.basepro.applications.photodo.db.TaskEntity // Already imported by PhotoDoTaskCard
// import java.text.SimpleDateFormat // Moved to PhotoDoTaskCard / or a common util
// import java.util.Date // Moved to PhotoDoTaskCard / or a common util
// import java.util.Locale // Moved to PhotoDoTaskCard / or a common util

@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
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
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Added padding for cards
                verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between cards
            ) {
                items(state.photoItems) { task ->
                    PhotoDoTaskCard( // Use the new PhotoDoTaskCard
                        task = task,
                        onItemClick = { onItemClick(task.id.toString()) },
                        onDeleteClick = { onEvent(PhotoDoListEvent.OnDeleteTaskClicked(task)) }
                        // modifier = Modifier.padding(bottom = 8.dp) // Alternative way to add space
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

// TaskRow composable is now removed, as its functionality is in PhotoDoTaskCard
// private fun Long.toFormattedDate(): String { ... } // Also removed, present in PhotoDoTaskCard (or move to common utils)
