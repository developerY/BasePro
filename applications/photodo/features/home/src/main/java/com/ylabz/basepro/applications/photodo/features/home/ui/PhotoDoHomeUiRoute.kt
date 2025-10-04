package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.applications.photodo.core.ui.FabState
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu

@Composable
fun PhotoDoHomeUiRoute(
    modifier: Modifier = Modifier,
    // The navigation lambda now expects a Long (the categoryId)
    navTo: (Long) -> Unit,
    onCategorySelected: (Long) -> Unit, // <-- ADD THIS PARAMETER
    setFabState: (FabStateMenu?) -> Unit, // <-- ADD THIS PARAMETER
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            HomeScreen(
                uiState = state,
                onEvent = viewModel::onEvent,
                // When a task list is selected, navigate using its categoryId
                onSelectList = { taskID ->
                    Log.d(
                        "PhotoDoHomeUiRoute",
                        "STEP2: Navigating to TaskList with categoryId: $taskID"
                    )
                    navTo(taskID)
                },
                onCategorySelected = onCategorySelected, // <-- PASS THE LAMBDA DOWN
                // This handles the "Add" button on the empty screen
                /*onAddList = {
                    state.selectedCategory?.let { category ->
                        viewModel.onEvent(HomeEvent.OnAddTaskListClicked(category.categoryId))
                    }
                },*/
                modifier = modifier,
                setFabState = setFabState
            )
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.message}")
            }
        }
    }
}
