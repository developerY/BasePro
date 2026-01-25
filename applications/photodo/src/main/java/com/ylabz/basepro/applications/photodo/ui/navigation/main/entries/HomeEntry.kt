package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/HomeEntry.kt

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeViewModel
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabAction
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenEvent
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenViewModel
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HomeEntry"

/**
 * The composable content for the PhotoDoNavKeys.HomeFeedKey navigation entry.
 * This is the main "home" screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEntry(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    backStack: NavBackStack<NavKey>,
    setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit, // Use correct FabState
    onCategorySelected: (Long) -> Unit,
    onEvent: (MainScreenEvent) -> Unit,
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

    // --- SAFE STATE EXTRACTION ---
    // Extract selectedCategory safely. If state is Loading/Error, this is null.
    // val selectedCategory = (uiState as? HomeUiState.Success)?.selectedCategory
    // -----------------------------

    // --- START OF FIX: MVI NAVIGATION BRIDGE ---
    // This LaunchedEffect is the only place we do stack manipulation.
    // NEW: Listens to the ACTUAL STATE (Handles clicks, deletes, and auto-selection)
    // --- START OF FIX: MVI NAVIGATION BRIDGE ---
    // This LaunchedEffect is the only place we do stack manipulation.

    // --- 1. CLICK NAVIGATION (For Phones & Tablets) ---
    // Triggers ONLY when user actively clicks a category card.
    // This is required for Phones to navigate, because we ignore auto-selection.
    LaunchedEffect(homeViewModel.categorySelectedEvent) {
        homeViewModel.categorySelectedEvent.collectLatest { categoryId ->
            Log.d(TAG, "Event: Category $categoryId clicked. Navigating.")

            // 1. Notify the MainScreen's ViewModel (which stores lastSelectedCategoryId)
            onCategorySelected(categoryId)

            // 2. TABLET LOGIC (Split Screen)
            // On a big screen, we DO want to update the right-hand pane immediately.
            if (isExpandedScreen) {
                Log.d(TAG, "Tablet: Updating detail pane for Category $categoryId")

                val newTaskListKey = PhotoDoNavKeys.HomeTaskListKey(categoryId)

                // Clear previous drill-downs to avoid stacking multiple detail panes
                if (backStack.size > 1) {
                    backStack.subList(1, backStack.size).clear()
                }

                // Show the new list on the right
                backStack.add(newTaskListKey)
            }

            // 3. PHONE LOGIC (Closed)
            // We do NOTHING here.
            // We removed the `if (!isExpandedScreen) { backStack.add(...) }` block.
            // Result: User stays on Home, Card highlights, no forced jump.
        }
    }
    // ----------------------
    // --- END OF FIX ---

    // --- 2. TABLET SYNC (Conditional) ---
    // This ensures that if you rotate to Tablet mode, the right pane appears.
    // It is DISABLED on phones to prevent "Auto-Jump" bugs.
    /*if (isExpandedScreen) {
        val selectedCategory = (uiState as? HomeUiState.Success)?.selectedCategory
        LaunchedEffect(selectedCategory) {
            if (selectedCategory != null) {
                Log.d(TAG, "TABLET SYNC: Showing Category ${selectedCategory.categoryId}")
                onCategorySelected(selectedCategory.categoryId)

                val newTaskListKey = PhotoDoNavKeys.TaskListKey(selectedCategory.categoryId)
                val currentTop = backStack.lastOrNull()

                if (currentTop != newTaskListKey) {
                    if (backStack.size > 1) {
                        backStack.subList(1, backStack.size).clear()
                    }
                    backStack.add(newTaskListKey)
                }
            }
        }
    }*/

    LaunchedEffect(Unit) {
        setTopBar {
            TopAppBar(
                title = { Text("PhotoDo") },
                navigationIcon = {
                    // FIX: Only show back button if we are drilled down AND on a phone
                    if (!isExpandedScreen && backStack.size > 1) {
                        IconButton(onClick = {
                            Log.d(TAG, "Back button clicked. Popping stack.")
                            backStack.removeLastOrNull()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Categories"
                            )
                        }
                    }
                }
            )
        }
            //  ---------------------------------


        // --- THIS IS THE FIX ---
        // The main button's onClick is now empty. Its only job is to toggle.
        // The "Add Item" action is moved into the `items` list.
        val currentState = uiState

        // 1. EMPTY STATE: No categories exist at all.
        if (currentState is HomeUiState.Success && currentState.categories.isEmpty()) {
            setFabState(
                FabState.Single(
                    action = FabAction(
                        text = "Add Category",
                        icon = Icons.Default.Add,
                        onClick = { onEvent(MainScreenEvent.OnAddCategoryClicked) }
                    )
                )
            )
        } else {
            // 2. NORMAL STATE: We have categories, show the Menu.

            // Dynamically build the menu items based on selection state
            val menuItems = mutableListOf<FabAction>()

            // CONDITIONAL: Only show "Add List" if a category is currently selected
            if (currentState is HomeUiState.Success && currentState.selectedCategory != null) {
                menuItems.add(
                    FabAction(
                        text = "Add List",
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = { onEvent(MainScreenEvent.OnAddListClicked) }
                    )
                )
            }

            // ALWAYS: "Add Category" is always an option in the menu
            menuItems.add(
                FabAction(
                    text = "Add Category",
                    icon = Icons.Default.Category,
                    onClick = { onEvent(MainScreenEvent.OnAddCategoryClicked) }
                )
            )

            setFabState(
                FabState.Menu(
                    mainButtonAction = FabAction(
                        text = "Add",
                        icon = Icons.Default.Home,
                        onClick = { Log.d(TAG, "Main FAB clicked to toggle menu.") }
                    ),
                    items = menuItems
                )
            )
        }
    }
    // --- END UPDATED FAB LOGIC ---


    // In MainScreen.kt -> AppContent()
    PhotoDoHomeUiRoute(
        modifier = modifier,
        homeViewModel = homeViewModel,
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        // viewModel = viewModel, // viewModel is hoisted, not passed down

        // The category click event starts inside PhotoDoHomeUiRoute's children,
        // goes up to PhotoDoHomeUiRoute, and then calls homeViewModel::onEvent.
        // The resulting navigation is then handled by the LaunchedEffect above.

        // --- COLUMN 1 CLICK HANDLER (Category Selection) ---
        /* --- COLUMN 1 CLICK HANDLER (Category Selection) ---
        onCategoryClick = { categoryId ->
            Log.d(TAG, "Category Clicked: $categoryId. Resetting drill-down.")

            onCategorySelected(categoryId)

            val newTaskListKey = PhotoDoNavKeys.TaskListKey(categoryId)

            if (backStack.size > 1) {
                backStack.subList(1, backStack.size).clear()
            }

            backStack.add(newTaskListKey)
        },*/
        // --- END COLUMN 1 FIX ---

        onCategoryClick = { categoryId ->
            // ... (Keep existing logic) ...
            // ONLY trigger the event. Do NOT manipulate backStack here.
            homeViewModel.onEvent(HomeEvent.OnCategorySelected(categoryId))
            /*if (!isExpandedScreen) {
                val newTaskListKey = PhotoDoNavKeys.TaskListKey(categoryId)
                if (backStack.size > 1) { backStack.subList(1, backStack.size).clear() }
                backStack.add(newTaskListKey)
            }*/
        },

        // --- IMPLEMENT NEW CALLBACK ---
        onEditCategory = { category ->
            // Bubble the event up to MainScreen, which owns the BottomSheet
            onEvent(MainScreenEvent.OnEditCategoryClicked(category))
        },
        // ------------------------------


        // --- START OF FIX: TASK LIST CLICK (Column 2 -> Column 3 Replacement) ---
        navTo = { listId ->
            Log.d(
                TAG,
                "Step3: Navigating from Task List Item to Detail Screen with listId: $listId"
            )

            backStack.lastOrNull()

            // 1. Create the key for the final detail screen (Pane 3).
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())

            // 2. Add it to the back stack. The adaptive strategy handles the rest.
            // --- START OF FIX: Universal Replacement/Add Logic ---
            if (backStack.lastOrNull() == detailKey) {
                // Do nothing: Already showing this detail screen.
                Log.d(TAG, "Skipping navigation: Already showing Detail for list $listId")
                return@PhotoDoHomeUiRoute
            }

            // If the current top element is *any* Detail Key, remove it (i.e., replace it).
            /*if (backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey) {
                Log.d(TAG, "Replacing existing Detail Key with new key: $listId")
                backStack.removeLastOrNull()
            }*/

            // Add the new detail key. This is a clean add or replacement.
            backStack.add(detailKey)
            // --- END OF FIX ---
        },
        /*navTo = { categoryId ->
            Log.d(
                TAG,
                "Step3: Navigating from Home to TaskList with categoryId: $categoryId"
            )
            // 1. Create the key for the task list screen.
            val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
            //val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
            // 2. Add it to the back stack.
            backStack.add(listKey)
        },*/
        // ### THIS IS THE SOLUTION ###
        // The `onCategorySelected` lambda is already available here as a
        // parameter of the `AppContent` function. You just need to pass it down.
        // This allows the HomeScreen to notify the MainScreen whenever a new
        // category is selected, keeping the "Tasks" tab in sync.
        // onCategorySelected = onCategorySelected,
        // onEvent = viewModel::onEvent,
        // onEvent = onEvent,
        // setFabState = setFabState // <-- Pass the FAB setter down
        // setFabState = setFabState
        /*
        onTaskListClick = { listId ->
            // --- THIS IS THE FIX ---
            // We must pass the listId as a Long, not a String
            backStack.add(PhotoDoNavKeys.TaskListDetailKey(listId = listId))
            // --- END OF FIX ---
        }
         */
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
                homeViewModel.onEvent(MainScreenEvent.OnAddCategoryClicked)
            }
            // When it's asked to save the data, tell the HomeViewModel
            is MainScreenEventOrig.AddCategory -> {
                Log.d(TAG, "Listener heard AddCategory. Telling HomeViewModel to show sheet.")
                homeViewModel.onEvent(MainScreenEvent.OnSaveCategory(event.categoryName))
                // homeViewModel.onEvent(MainScreenEvent.OnAddCategoryClicked(event.categoryName))
                // homeViewModel.onEvent(MainScreenEvent.OnAddCategoryClicked)
            }
            /*is MainScreenEvent.AddList -> {
                ///homeViewModel.onEvent(MainScreenEvent.OnAddListClicked)
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
                    homeViewModel.onEvent(MainScreenEvent.OnAddCategoryClicked)
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
                    //homeViewModel.onEvent(MainScreenEvent.OnAddTaskListClicked)
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