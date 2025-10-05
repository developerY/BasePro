package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.core.ui.FabAction
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeViewModel
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailViewModel
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

private const val TAG = "PhotoDoNavGraph"

/**
 * The core navigation graph for the PhotoDo application.
 *
 * This composable is responsible for defining all possible screens (destinations)
 * and handling the transitions between them using the NavDisplay component from Navigation 3.
 * It's designed to be adaptive, changing its layout based on screen size.
 *
 * @param modifier The modifier to be applied to the NavDisplay container.
 * @param backStack The mutable list of navigation keys representing the current navigation history.
 * @param sceneStrategy The adaptive strategy (e.g., ListDetailSceneStrategy) that determines
 * how list and detail panes are displayed on different screen sizes.
 * @param scrollBehavior A TopAppBarScrollBehavior to coordinate scrolling between the top app bar and the content.
 * @param setTopBar A lambda function to hoist the composable for the top app bar up to the parent Scaffold.
 * @param setFabState A lambda function to hoist the state of the Floating Action Button up to the parent Scaffold.
 * @param onCategorySelected A callback invoked when a user selects a category, allowing the parent to remember it.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoDoNavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabStateMenu?) -> Unit,
    onCategorySelected: (Long) -> Unit // Callback to update the remembered category ID
    // REMOVE the updateCurrentTopLevelKey parameter, it's not needed here
    // updateCurrentTopLevelKey: (NavKey) -> Unit
) {
    // NAV_LOG: Log AppContent recomposition
    Log.d(TAG, "AppContent recomposing. Backstack size: ${backStack.size}")

    NavDisplay(
        backStack = backStack,
        onBack = {
            // --- NEW LOGGING ADDED HERE ---
            Log.d(TAG, "onBack invoked. Backstack count BEFORE action: ${backStack.size}")
            val currentStack = backStack.joinToString { it::class.simpleName ?: "Unknown" }
            Log.d(TAG, "Current backstack contents: [$currentStack]")
            // --- END OF NEW LOGGING ---

            // This custom logic ensures a more intuitive navigation experience.
            val currentKey = backStack.lastOrNull()

            // Special case: If we are at the root of a task list (e.g., navigated from the bottom bar),
            // pressing back should take us to the home feed, not exit the app.
            val isAtRootTaskList = currentKey is PhotoDoNavKeys.TaskListKey && backStack.size == 1
            Log.d(TAG, "onBack invoked. Current backstack: $currentKey")
            if (isAtRootTaskList) {
                Log.d(TAG, "Back action: At a root task list, replacing with HomeFeedKey.")
                backStack.replace(PhotoDoNavKeys.HomeFeedKey)
            } else {
                Log.d(TAG, "Back action: Performing default 'removeLastOrNull'.")
                backStack.removeLastOrNull()
            }

            // --- NEW LOGGING ADDED HERE ---
            Log.d(TAG, "Backstack count AFTER action: ${backStack.size}")
            if (backStack.isEmpty()) {
                Log.d(TAG, "Backstack is now empty. App will exit on next back press if not handled by system.")
            }
            // --- END OF NEW LOGGING ---

        },
        sceneStrategy = sceneStrategy,
        modifier = modifier,


        /**
         * Defines the complete navigation graph of the application.
         * The `entryProvider` block maps each unique `NavKey` to its corresponding
         * Composable content and adaptive layout metadata.
         */
        entryProvider = entryProvider {
            val isExpandedScreen = true

            /**
             * =================================================================
             * Entry: Home Feed Screen (`PhotoDoNavKeys.HomeFeedKey`)
             * =================================================================
             * This is the primary "list" pane and the starting destination of the app.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.listPane`: Marks this as a list view. On large screens,
             * it appears as the first of three panes.
             * - `detailPlaceholder`: A placeholder shown in the detail area on large screens
             * when no item from this list has been selected yet. On smaller screens, this has no effect.
             */
            entry<PhotoDoNavKeys.HomeFeedKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                        { Text("Select a category") }
                    }
                ))
            {
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


                /**
                 * Side Effect for UI State Updates:
                 * `LaunchedEffect` is crucial here. It runs its block of code only when its keys change.
                 * This prevents an infinite recomposition loop that would occur if `setTopBar` or
                 * `setFabState` were called directly in the composable body, as they modify the
                 * state of the parent (`MainScreen`).
                 */
                LaunchedEffect(
                    backStack.size,
                    isExpandedScreen,
                    homeViewModel.uiState.collectAsState().value
                ) {
                    val currentUiState = homeViewModel.uiState.value
                    val isCategorySelected =
                        currentUiState is HomeUiState.Success && currentUiState.selectedCategory != null
                    val isListSelected = backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey

                    // --- FAB Logic for Expanded Screens (e.g., Tablets) ---
                    if (isExpandedScreen) {
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
                                        onClick = {
                                            Log.d(TAG, "Add Category from Global FAB Clicked")
                                            homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
                                        }
                                    ),
                                    // Action to add to Column 2 (List) - Only if a category is selected
                                    if (isCategorySelected) FabAction(
                                        text = "List",
                                        icon = Icons.AutoMirrored.Filled.NoteAdd,
                                        onClick = {
                                            Log.d(TAG, "Add List from Global FAB Clicked")
                                            /*homeViewModel.onEvent(HomeEvent.OnAddTaskListClicked)*/
                                        }
                                    ) else null,
                                    // Action to add to Column 3 (Item) - Only if a list is selected
                                    if (isListSelected) FabAction(
                                        text = "Item",
                                        icon = Icons.Default.Add,
                                        onClick = {
                                            // We need to find the Detail ViewModel to send the event
                                            // This is an advanced use case, for now we log it.
                                            Log.d(TAG, "Add Item from Global FAB Clicked")
                                        }
                                    ) else null
                                )
                            )
                        )
                    // --- FAB Logic for Compact Screens (e.g., Phones) ---
                    } else {
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
                    // This function is ONLY for navigating from a Task List item to the Detail screen.
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


            /**
             * =================================================================
             * Entry: Task List Screen (`PhotoDoNavKeys.TaskListKey`)
             * =================================================================
             * This is the second "list" pane, showing items within a selected category.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.listPane`: On compact screens, it replaces the home screen.
             * On expanded screens, it appears in the second pane.
             * - `detailPlaceholder`: A placeholder for the third pane.
             */
            entry<PhotoDoNavKeys.TaskListKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                        { Text("Select a list to see details") }
                    }
                ))
            { listKey ->
                // NAV_LOG: Log rendering of TaskListKey entry
                Log.d(TAG, "Displaying content for TaskListKey (categoryId=${listKey.categoryId})")
                val viewModel: PhotoDoListViewModel = hiltViewModel()
                LaunchedEffect(listKey.categoryId) {
                    Log.d(TAG, "TaskListKey LaunchedEffect triggered. Loading category with id: ${listKey.categoryId}")
                    onCategorySelected(listKey.categoryId) // Also update when loading a list directly
                    viewModel.loadCategory(listKey.categoryId)
                }

                /*LaunchedEffect(listKey.categoryId) {
                    onCategorySelected(listKey.categoryId);
                    listViewModel.loadCategory(listKey.categoryId)
                }*/


                // ### WHY & WHAT ###
                // Same reason as above. We wrap these state updates in a LaunchedEffect
                // to prevent the infinite recomposition loop when this screen is shown.
                LaunchedEffect(Unit) {
                    setTopBar {
                        LargeTopAppBar(
                            title = { Text("Task Lists with (+)") },
                            scrollBehavior = scrollBehavior,
                            actions = {
                                IconButton(onClick = { viewModel.onEvent(PhotoDoListEvent.OnDeleteAllTaskListsClicked) }) {
                                    Icon(
                                        Icons.Filled.DeleteSweep,
                                        contentDescription = "Delete All Lists"
                                    )
                                }
                            }
                        )
                    }
                    //setFabState(FabState("Add List") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) })
                    // THIS IS THE NEW SPLIT FAB LOGIC
                    /*setFabState(
                        FabStateMenu.Single(
                            action = FabAction(
                                text = "Add List",
                                icon = Icons.Default.Add,
                                onClick = { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) }
                            )
                        )

                        /*FabState.Split(
                            primaryText = "Add Item",
                            primaryIcon = Icons.Default.Add, // Or a more specific item icon
                            primaryOnClick = { /* TODO: Add item to a default/selected list */ },
                            secondaryText = "Add List",
                            secondaryIcon = Icons.Default.Add, // Or a more specific list icon
                            secondaryOnClick = { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) }
                        )*/
                    )*/
                }

                LaunchedEffect(backStack.size, isExpandedScreen) {
                    val isDetailVisible = backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey

                    if (isExpandedScreen) {
                        // On tablets, the Tasks tab always offers to add a List or an Item
                        setFabState(
                            FabStateMenu.Menu(
                                mainButtonAction = FabAction(
                                    "Add",
                                    Icons.Default.Add,
                                    onClick = { Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen") }
                                ),
                                items = listOfNotNull(
                                    FabAction(
                                        "List",
                                        Icons.AutoMirrored.Filled.NoteAdd
                                    ) {
                                        Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen")
                                        /*listViewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)*/
                                    },
                                    if (isDetailVisible) FabAction(
                                        "Item",
                                        Icons.Default.Add
                                    ) {
                                        Log.d(TAG, "Add Item from Global FAB Clicked -- Closed Screen")
                                        /*detailViewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked)*/
                                    } else null
                                )
                            ))
                    } else {
                        // On phones, show the most specific action
                        if (isDetailVisible) {
                            setFabState(
                                FabStateMenu.Single(
                                    FabAction(
                                        "Add Item",
                                        Icons.Default.Add
                                    ) {
                                        Log.d(TAG, "Add Item from Global FAB Clicked -- Closed Screen")
                                        /*detailViewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked)*/
                                    })
                            )
                        } else {
                            setFabState(
                                FabStateMenu.Single(
                                    FabAction(
                                        "Add List",
                                        Icons.Default.Add
                                    ) {
                                        Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen")
                                        /*listViewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)*/
                                    })
                            )
                        }
                    }
                }

                PhotoDoListUiRoute(
                    onTaskClick = { listId ->
                        // NAV_LOG: Log navigation from TaskList to Detail
                        Log.d(TAG, "TaskList onTaskClick triggered. ListId: $listId")
                        val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
                        Log.d(TAG, " -> Calling backStack.add with TaskListDetailKey($listId)")
                        backStack.add(detailKey)
                    },
                    /*onTaskClick = { listId ->
                        val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
                        backStack.add(detailKey)
                    },*/
                    onEvent = viewModel::onEvent,
                    viewModel = viewModel
                )
            }



            /**
             * =================================================================
             * Entry: Task Detail Screen (`PhotoDoNavKeys.TaskListDetailKey`)
             * =================================================================
             * This is the final "detail" pane, showing the contents of a single task list.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.detailPane`: Marks this as a detail view.
             * On compact screens, it covers the entire screen. On expanded screens,
             * it appears in the third pane on the right.
             */
            entry<PhotoDoNavKeys.TaskListDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                // NAV_LOG: Log rendering of TaskListDetailKey entry
                Log.d(TAG, "Displaying content for TaskListDetailKey (listId=${detailKey.listId})")
                val viewModel: PhotoDoDetailViewModel = hiltViewModel()
                LaunchedEffect(detailKey.listId) {
                    Log.d(
                        TAG,
                        "TaskListDetailKey LaunchedEffect triggered. Loading list with id: ${detailKey.listId}"
                    )
                    viewModel.loadList(detailKey.listId)
                }

                // This effect now correctly sets the FAB to "Add Item" instead of null.
                LaunchedEffect(Unit) {
                    setTopBar {
                        TopAppBar(
                            title = { Text("List Details") },
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                IconButton(onClick = { backStack.removeLastOrNull() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }

                    // ### WHY & WHAT ###
                    // This sets the FAB state when the detail screen is visible.
                    // The text is "Add Item" and the action calls the new onEvent
                    // function in the PhotoDoDetailViewModel. This fixes the bug where
                    // the FAB would disappear on this screen.

                    // The FAB is a single "Add Item" button on this screen.
                    setFabState(
                        FabStateMenu.Single(
                            action = FabAction(
                                text = "Add Item -- but we need to show state",
                                icon = Icons.Default.Add,
                                onClick = { viewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked) }
                            )
                        ))
                }
                PhotoDoDetailUiRoute(viewModel = viewModel)
            }

            /**
             * =================================================================
             * Entry: Settings Screen (`PhotoDoNavKeys.SettingsKey`)
             * =================================================================
             * A standard, full-screen destination that is not part of the list-detail flow.
             */
            entry<PhotoDoNavKeys.SettingsKey> {
                // NAV_LOG: Log rendering of SettingsKey entry
                Log.d(TAG, "Displaying content for SettingsKey")
                val viewModel: SettingsViewModel = hiltViewModel()
                setTopBar {
                    LargeTopAppBar(
                        title = { Text("Settings") },
                        scrollBehavior = scrollBehavior
                    )
                }
                setFabState(FabStateMenu.Hidden)

                SettingsUiRoute(
                    modifier = Modifier,
                    navTo = {},
                    viewModel = viewModel,
                    initialCardKeyToExpand = null
                )
            }
        }
    )
}

fun <T : Any> MutableList<T>.replace(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}
