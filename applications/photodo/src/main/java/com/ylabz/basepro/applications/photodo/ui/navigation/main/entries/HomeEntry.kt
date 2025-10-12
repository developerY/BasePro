package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/HomeEntry.kt

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.core.ui.FabAction
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.core.ui.MainScreenEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeViewModel
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenViewModel
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HomeEntry"

@Composable
fun HomeEntry(
    isExpandedScreen: Boolean,
    backStack: NavBackStack<NavKey>,
    setFabState: (FabStateMenu?) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onAddCategoryClicked: () -> Unit,
    onAddListClicked: () -> Unit,
    onAddItemClicked: () -> Unit
) {
    // NAV_LOG: Log rendering of HomeFeedKey entry
    Log.d(TAG, "Displaying content for HomeFeedKey")

    /**
     * Hilt ViewModel Injection:
     * `hiltViewModel()` ties the ViewModel's lifecycle to this specific navigation entry.
     * It will be created when the user navigates to this screen and destroyed when they
     * navigate away from it.
     * the Navigation 3 library automatically ensures that the ViewModel's lifecycle is
     * tied to that specific destination on the backstack.
     */
    val homeViewModel: HomeViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    // --- THIS IS THE FIX ---
    // This block now LISTENS for events that this screen is responsible for.
    LaunchedEffect(Unit) {
        mainScreenViewModel.events.collectLatest { event : MainScreenEvent ->
            when (event) {

                // When it's asked to show the sheet, tell the HomeViewModel
                is MainScreenEvent.RequestAddCategory -> {
                    homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
                }
                // When it's asked to save the data, tell the HomeViewModel
                is MainScreenEvent.AddCategory -> {
                    homeViewModel.onEvent(HomeEvent.OnSaveCategory(event.categoryName))
                    // homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked(event.categoryName))
                    // homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
                }
                /*is MainScreenEvent.AddList -> {
                    ///homeViewModel.onEvent(HomeEvent.OnAddListClicked)
                }*/
                // It ignores the AddItem event, as another screen handles that.
                else -> {}
            }
        }
    }

    /**
     * Side Effect for UI State Updates:
     * `LaunchedEffect` is crucial here. It runs its block of code only when its keys change.
     * This prevents an infinite recomposition loop that would occur if `setTopBar` or
     * `setFabState` were called directly in the composable body, as they modify the
     * state of the parent (`MainScreen`).
     */
    LaunchedEffect(
        backStack.size,
        //isExpandedScreen,
        homeViewModel.uiState.collectAsState().value
    ) {
        val currentUiState = homeViewModel.uiState.value
        val isCategorySelected =
            currentUiState is HomeUiState.Success && currentUiState.selectedCategory != null
        val isListSelected = backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey

        // --- FAB Logic for Expanded Screens (e.g., Tablets) ---
        //if (isExpandedScreen) {
        // --- LOGIC FOR EXPANDED (OPEN) SCREENS ---
        setFabState(
            FabStateMenu.Menu(
                mainButtonAction = FabAction(
                    text = "Add Main Screen",
                    icon = Icons.Default.Add,
                    onClick = {
                        Log.d(TAG, "Main Button Pressed")
                    } // Main button just opens the menu
                ),
                // The menu items are conditional based on the user's selections.
                items = listOfNotNull(
                    // Action to add to Column 1 (Category) - Always available
                    FabAction(
                        text = "Category",
                        icon = Icons.Default.Create,
                        //Log.d(TAG, "Add Category from Global FAB Clicked")
                        onClick = onAddCategoryClicked
                    ),
                    // Action to add to Column 2 (List) - Only if a category is selected
                    if (isCategorySelected) FabAction(
                        text = "List",
                        icon = Icons.AutoMirrored.Filled.NoteAdd,
                        //Log.d(TAG, "Add List from Global FAB Clicked")
                        onClick = onAddListClicked
                    ) else null,
                    // Action to add to Column 3 (Item) - Only if a list is selected
                    if (isListSelected) FabAction(
                        text = "Item",
                        icon = Icons.Default.Add,
                        // --- THIS IS THE CORRECTED CODE ---
                        // We post an event to the shared ViewModel.
                        // This announces that the "Add Item" button was clicked.
                        // Log.d(TAG, "Add Item from Global FAB Clicked - Posting Event")
                        onClick = onAddItemClicked
                    ) else null
                )
            )
        )

        // --- END OF FAB Logic for Expanded Screens ---

        // --- FAB Logic for Compact Screens (e.g., Phones) ---
        /* } else {
            // --- LOGIC FOR COMPACT (CLOSED) SCREENS ---
            // The FAB action depends on the top-most screen
            // On smaller screens, the FAB shows the most specific, single action available.
            when (backStack.lastOrNull()) {
                is PhotoDoNavKeys.HomeFeedKey -> setFabState(
                    FabStateMenu.Single(
                        FabAction(
                            "Add Category",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add Category from Global FAB Clicked -- Closed Screen")
                            homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
                        })
                )

                is PhotoDoNavKeys.TaskListKey -> setFabState(
                    FabStateMenu.Single(
                        FabAction(
                            "Add List",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen")
                            /* This would need the list ViewModel */
                            //homeViewModel.onEvent(HomeEvent.OnAddTaskListClicked)
                        })
                )

                is PhotoDoNavKeys.TaskListDetailKey -> setFabState(
                    FabStateMenu.Single(
                        FabAction(
                            "Add Item",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add Item from Global FAB Clicked -- Closed Screen")
                            /* This would need the detail ViewModel */
                        })
                )

                else -> setFabState(FabStateMenu.Hidden)
            }
        }
        }*/
    }

    // In MainScreen.kt -> AppContent()
    PhotoDoHomeUiRoute(
        /*navTo = { categoryId ->
            /*val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
            updateCurrentTopLevelKey(listKey) // Update the selected tab
            backStack.replace(listKey)      // Navigate to the new screen*/
            // CORRECTED LOGIC:
            // 1. Do NOT update the top-level key here.
            // 2. ADD the new screen to the stack for forward navigation.
            Log.d(TAG, "Navigating from Home to TaskList with categoryId: $categoryId")
            onCategorySelected(categoryId) // Update the remembered category ID
            val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
            Log.d(TAG, " -> Calling backStack.add(TaskListKey($categoryId))")
            backStack.add(listKey)
        },*/
        // --- RelativeDateTimeFormatter.Direction.
        // It has been renamed to `onNavigateToDetail` for clarity.
        // Its job is to handle the click from a TaskList item and navigate to the detail screen.
        // This function is ONLY for navigating from a Task List item to the Detail screen.
        navTo = { listId ->
            Log.d(TAG, "Step3: Navigating from Task List Item to Detail Screen with listId: $listId")

            // 1. Create the key for the final detail screen (Pane 3).
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())

            // 2. Add it to the back stack. The adaptive strategy handles the rest.
            backStack.add(detailKey)
        },
        viewModel = homeViewModel,
        // ### THIS IS THE SOLUTION ###
        // The `onCategorySelected` lambda is already available here as a
        // parameter of the `AppContent` function. You just need to pass it down.
        // This allows the HomeScreen to notify the MainScreen whenever a new
        // category is selected, keeping the "Tasks" tab in sync.
        onCategorySelected = onCategorySelected,
        // setFabState = setFabState // <-- Pass the FAB setter down

    )
}