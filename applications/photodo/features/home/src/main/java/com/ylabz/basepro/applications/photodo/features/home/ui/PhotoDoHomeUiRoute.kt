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
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.features.home.ui.components.AddCategorySheet


private const val TAG = "PhotoDoHomeUiRoute"

@Composable
fun PhotoDoHomeUiRoute(
    modifier: Modifier = Modifier,
    // The navigation lambda now expects a Long (the categoryId)
    navTo: (Long) -> Unit,
    onCategorySelected: (Long) -> Unit, // <-- ADD THIS PARAMETER
    setFabState: (FabStateMenu?) -> Unit = {}, // <-- ADD THIS PARAMETER
     homeViewModel: HomeViewModel, // do not use hiltViewModel
) {

    Log.d(TAG, "Entered PhotoDoHomeUiRoute composable") // <-- BREADCRUMB 1


    val uiState by homeViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            HomeScreen(
                uiState = state,
                onEvent = homeViewModel::onEvent,
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
            // --- THIS IS THE UI LOGIC ---
            // When the ViewModel's state flag is true, show the sheet.
            if (state.isAddingCategory) {
                AddCategorySheet(
                    onAddCategory = { categoryName ->
                        // Log the event being sent to the ViewModel to save the category
                        Log.d(TAG, "onAddCategory called with name: '$categoryName'. Posting OnSaveCategory event.")
                        homeViewModel.onEvent(HomeEvent.OnSaveCategory(categoryName))
                    },
                    onDismiss = {
                        // Log the event being sent to the ViewModel to dismiss the sheet
                        Log.d(TAG, "onDismiss called. Posting OnDismissAddCategory event.")
                        homeViewModel.onEvent(HomeEvent.OnDismissAddCategory)
                    }
                )
            }


        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.message}")
            }
        }
    }
}
