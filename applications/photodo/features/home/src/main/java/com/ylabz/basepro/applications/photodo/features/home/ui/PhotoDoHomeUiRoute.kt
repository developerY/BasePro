package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.AddCategoryBottomSheet


private const val TAG = "PhotoDoHomeUiRoute"

@Composable
fun PhotoDoHomeUiRoute(
    modifier: Modifier = Modifier,
    // The navigation lambda now expects a Long (the categoryId)
    uiState: HomeUiState, // Receives state from HomeEntry
    navTo: (Long) -> Unit,
    // setFabState: (FabStateMenu?) -> Unit = {}, // <-- ADD THIS PARAMETER
    onCategoryClick: (Long) -> Unit,
    homeViewModel: HomeViewModel, // -- do not use new hiltViewModel
    isExpandedScreen: Boolean,
    // --- FIX: The signature should never includes the mainScreenViewModel ---
) {
    // --- ADD THIS HASHCODE LOG ---
    Log.d("ViewModelInstance", "PhotoDoHomeUiRoute is OBSERVING hashCode: ${homeViewModel.hashCode()}")

    Log.d(TAG, "Entered PhotoDoHomeUiRoute composable") // <-- BREADCRUMB 1

    val onEvent: (HomeEvent) -> Unit = { event ->
        homeViewModel.onEvent(event)
        if (event is HomeEvent.OnCategorySelected) {
            onCategoryClick(event.categoryId)
        }
    }

    when (uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Text("PhotoDoHomeUI Holding the HomeScreen")
                HomeScreen(
                    uiState = uiState,
                    onEvent = homeViewModel::onEvent,
                    onSelectList = { taskID ->
                        Log.d(
                            "PhotoDoHomeUiRoute",
                            "STEP2: Navigating to TaskList with categoryId: $taskID"
                        )
                        navTo(taskID)
                    },
                    isExpandedScreen = isExpandedScreen,
                    // onCategorySelected = onCategorySelected, // <-- PASS THE LAMBDA DOWN
                    // This handles the "Add" button on the empty screen
                    /*onAddList = {
                    state.selectedCategory?.let { category ->
                        viewModel.onEvent(HomeEvent.OnAddTaskListClicked(category.categoryId))
                    }
                },*/
                    modifier = modifier,
                    // setFabState = setFabState
                )
                // --- THIS IS THE UI LOGIC ---
                // When the ViewModel's state flag is true, show the sheet.
                if (uiState.isAddingCategory) {

                    // --- UPDATED CALL SITE ---
                    // Now follows the pattern: Pass State + Event Handler
                    AddCategoryBottomSheet(
                        uiState = uiState,
                        onEvent = onEvent
                    )
                    // -------------------------

                    // --- UPDATED SHEET WIRING ---
                    /* AddCategoryBottomSheet(
                        // 1. Pass the category to edit (null if adding new)
                        categoryToEdit = uiState.categoryToEdit,

                        // 2. Simple Add (for the top part of your sheet)
                        onAddCategory = { categoryName ->
                            onEvent(HomeEvent.OnSaveCategory(categoryName, "New Category"))
                        },

                        // 3. Dismiss
                        onDismiss = {
                            onEvent(HomeEvent.OnDismissAddCategory)
                        },

                        // 4. Save New (Detailed)
                        onSaveCategory = { name, description ->
                            onEvent(HomeEvent.OnSaveCategory(name, description))
                        },

                        // 5. Update Existing
                        onUpdateCategory = { category ->
                            onEvent(HomeEvent.OnUpdateCategory(category))
                        }
                    ) */
                    // ----------------------------
                }
            }
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${uiState.message}")
            }
        }
    }
}
