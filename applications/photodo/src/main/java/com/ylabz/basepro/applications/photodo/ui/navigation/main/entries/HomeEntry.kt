package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/HomeEntry.kt

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeViewModel
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabAction
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenViewModel

private const val TAG = "HomeEntry"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEntry(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    backStack: NavBackStack<NavKey>,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit, // Use correct FabState
    onCategorySelected: (Long) -> Unit,
    onEvent: (HomeEvent) -> Unit,
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
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        setTopBar {
            TopAppBar(title = { Text("PhotoDo") })//, scrollBehavior = it)
        }

        // --- THIS IS THE FIX ---
        // The main button's onClick is now empty. Its only job is to toggle.
        // The "Add Item" action is moved into the `items` list.
        setFabState(
            FabState.Menu(
                mainButtonAction = FabAction(
                    text = "Add",
                    icon = Icons.Default.Add,
                    onClick = {
                        // Main button's only job is to open/close the menu.
                        // This click is handled inside FabMain.
                        Log.d(TAG, "Main FAB clicked to toggle menu.")
                    }
                ),
                items = listOf(
                    // "Add Item" is now the first item in the menu
                    FabAction(
                        text = "Add Item",
                        icon = Icons.Default.Add, // You can change this icon
                        onClick = { onEvent(HomeEvent.OnAddCategoryClicked)}
                    ),
                    FabAction(
                        text = "Add List",
                        icon = Icons.Default.List,
                        onClick = { onEvent(HomeEvent.OnAddListClicked)}
                    ),
                    FabAction(
                        text = "Add Category",
                        icon = Icons.Default.Category,
                        onClick = { onEvent(HomeEvent.OnAddCategoryClicked)}
                    )
                )
            )
        )
        // --- END OF FIX ---
    }


    // In MainScreen.kt -> AppContent()
    PhotoDoHomeUiRoute(
        modifier = modifier,
        navTo = { listId ->
            Log.d(
                TAG,
                "Step3: Navigating from Task List Item to Detail Screen with listId: $listId"
            )

            // 1. Create the key for the final detail screen (Pane 3).
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())

            // 2. Add it to the back stack. The adaptive strategy handles the rest.
            backStack.add(detailKey)
        },
        homeViewModel = homeViewModel,
        uiState = uiState,
        // ### THIS IS THE SOLUTION ###
        // The `onCategorySelected` lambda is already available here as a
        // parameter of the `AppContent` function. You just need to pass it down.
        // This allows the HomeScreen to notify the MainScreen whenever a new
        // category is selected, keeping the "Tasks" tab in sync.
        onCategorySelected = onCategorySelected,
        onEvent = onEvent,
        // setFabState = setFabState // <-- Pass the FAB setter down
        // setFabState = setFabState
    )
}


// Old
// --- BOTTOM SHEET FLOW: Step 2 ---
// This LaunchedEffect acts as a listener for global UI events from the MainScreenViewModel.
// It's key to decoupling the FAB click from the direct action, allowing any screen to
/* request that the bottom sheet be shown.
LaunchedEffect(Unit) {
    mainScreenViewModel.events.collectLatest { event : MainScreenEventOrig ->
        when (event) {

            // --- BOTTOM SHEET FLOW: Step 3 ---
            // When a 'RequestAddCategory' event is received, this is the code that runs.
            is MainScreenEventOrig.RequestAddCategory -> {
                Log.d(TAG, "Listener heard RequestAddCategory. Telling HomeViewModel to show sheet.")

                // --- BOTTOM SHEET FLOW: Step 4 ---
                // It tells the local HomeViewModel to handle the event. The HomeViewModel will
                // then update its own state (HomeUiState) to signal that the
                // bottom sheet should be displayed. The PhotoDoHomeUiRoute is observing
                // this state and will show the sheet accordingly when the state changes.
                homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
            }
            // When it's asked to save the data, tell the HomeViewModel
            is MainScreenEventOrig.AddCategory -> {
                Log.d(TAG, "Listener heard AddCategory. Telling HomeViewModel to show sheet.")
                homeViewModel.onEvent(HomeEvent.OnSaveCategory(event.categoryName))
                // homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked(event.categoryName))
                // homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
            }
            /*is MainScreenEvent.AddList -> {
                ///homeViewModel.onEvent(HomeEvent.OnAddListClicked)
            }*/
            // It ignores the AddItem event, as another screen handles that.
            else -> {
                Log.d(TAG, "Listener heard something happened and we missed it.")

            }
        }
    }
}*/

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
}
}*/

/*
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
 */