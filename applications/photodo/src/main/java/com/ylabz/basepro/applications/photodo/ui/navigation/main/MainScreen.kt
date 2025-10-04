package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.core.ui.FabAction
import com.ylabz.basepro.applications.photodo.core.ui.FabMenu
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
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
import com.ylabz.basepro.applications.photodo.ui.navigation.NavKeySaver
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    //NOTE: **Top-Level Navigation State**
    // (`currentTopLevelKey`): This tracks which main section of the app the user is in (e.g., "Home," "Tasks," or "Settings")
    // The currently selected top-level tab. `rememberSaveable` ensures this state survives process death.
    var currentTopLevelKey: NavKey by rememberSaveable(stateSaver = NavKeySaver) {
        mutableStateOf(PhotoDoNavKeys.HomeFeedKey)
    }
    // NOTE: **Bottom Navigation State**
    // The actual navigation history for the NavDisplay.
    val backStack = rememberNavBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey)
    // NOTE: **Adaptive Navigation State**
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    // Remember the last category ID the user interacted with. Default to 1L since we know it has data.
    var lastSelectedCategoryId by rememberSaveable { mutableStateOf(1L) }

    // NAV_LOG: Log recomposition and state values
    Log.d(TAG, "MainScreen recomposing -> isExpanded: $isExpandedScreen, topLevelKey: ${currentTopLevelKey::class.simpleName}, lastSelectedCategoryId: $lastSelectedCategoryId")

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabStateMenu? by remember { mutableStateOf(null) }

    val onNavigate: (NavKey) -> Unit = { navKey ->
        // NAV_LOG: Log top-level tab navigation click
        Log.d(TAG, "onNavigate triggered with navKey: ${navKey::class.simpleName}")

        // When navigating via BottomBar/Rail, if the target is the List tab,
        // use the last selected category ID instead of the hardcoded one.
        val keyToNavigate = if (navKey is PhotoDoNavKeys.TaskListKey) {
            Log.d(TAG, " -> List tab clicked. Overriding to last selected categoryId: $lastSelectedCategoryId")
            PhotoDoNavKeys.TaskListKey(lastSelectedCategoryId)
        } else {
            Log.d(TAG, " -> Tab is not TaskListKey, using original key.")
            navKey
        }

        if (currentTopLevelKey::class != keyToNavigate::class) {
            Log.d(TAG, " -> Switching top-level tab from ${currentTopLevelKey::class.simpleName} to ${keyToNavigate::class.simpleName}")
            currentTopLevelKey = keyToNavigate
            backStack.replaceAll(keyToNavigate) // Clear history when switching tabs
        } else {
            Log.d(TAG, " -> Already on top-level tab ${keyToNavigate::class.simpleName}. No change.")
        }
    }

    // A key that forces recomposition when the back stack changes.
    // CORRECTED KEY: This now uses derivedStateOf to be state-aware.
    val backStackKey by remember { derivedStateOf {
        backStack.joinToString("-") { navKey ->
            when (navKey) {
                is PhotoDoNavKeys.TaskListKey -> "TaskList(${navKey.categoryId})"
                else -> navKey.javaClass.simpleName
            }
        }
    } }

    // NAV_LOG: Log the current back stack state before rendering AppContent
    Log.d(TAG, "BackStack state before AppContent: $backStackKey")

    val appContent = @Composable { modifier: Modifier ->
        key(backStackKey) {
            AppContent(
                modifier = modifier,
                backStack = backStack,
                sceneStrategy = listDetailStrategy,
                scrollBehavior = scrollBehavior,
                setTopBar = { topBar = it },
                // THIS IS WHERE THE FUNCTION IS CREATED AND PASSED DOWN
                setFabState = { newFabState -> fabState = newFabState },
                // setFabState = { fabState = it },
                onCategorySelected = { categoryId ->
                    // NAV_LOG: Log when the last selected category ID is updated
                    Log.d(TAG, "onCategorySelected callback triggered. Updating lastSelectedCategoryId to: $categoryId")
                    lastSelectedCategoryId = categoryId
                }
            )
        }
    }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate)
            Scaffold(
                modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = topBar,
                floatingActionButton = { FabMenu(fabState) }
            ) { padding ->
                appContent(Modifier.padding(padding))
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = { HomeBottomBar(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate) },
            floatingActionButton = { FabMenu(fabState) }
        ) { padding ->
            appContent(Modifier.padding(padding))
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
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

            // Your custom back navigation logic
            val currentKey = backStack.lastOrNull()
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
         * Entry Provider: Defines the structure of the navigation graph.
         */
        entryProvider = entryProvider {


            /**
             * First Tab: Home
             * Defines the content for the 'Home' screen, which is the root of our navigation.
             * This entry corresponds to the `PhotoDoNavKeys.HomeFeedKey`.
             *
             * Behavior in Adaptive Layouts:
             * - `metadata = ListDetailSceneStrategy.listPane(...)`: This designates the home screen
             * as a "list" pane in a list-detail layout. On large screens (unfolded), it will occupy
             * the first pane on the left.
             *
             * - `detailPlaceholder`: When this list pane is visible on a large screen but no detail
             * pane is active, this placeholder composable is shown in the detail area, prompting
             * the user to make a selection. On small screens (folded), this has no effect.
             */
            entry<PhotoDoNavKeys.HomeFeedKey>(metadata = ListDetailSceneStrategy.listPane(
                detailPlaceholder = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                    { Text("Select a category") }
                }
            ))
            {
                // NAV_LOG: Log rendering of HomeFeedKey entry
                Log.d(TAG, "Displaying content for HomeFeedKey")


                /**
                 * NOTE: Hilt ViewModel Injection
                 * By placing the hiltViewModel() call inside the trailing lambda of an entry,
                 * the Navigation 3 library automatically ensures that the ViewModel's lifecycle is
                 * tied to that specific destination on the backstack.
                 */
                val homeViewModel: HomeViewModel = hiltViewModel()


                // ### WHY THIS CHANGE IS NEEDED ###
                // Calling setTopBar and setFabState directly inside the composable body
                // updates state in the parent (MainScreen), causing an immediate request
                // to redraw everything. This creates an infinite loop.
                //
                // ### WHAT THIS CODE DOES ###
                // By wrapping these calls in a LaunchedEffect(Unit), we tell Compose to run
                // this block of code only ONCE when this screen (HomeFeedKey) first appears.
                // This breaks the loop by treating the UI update as a one-time "side effect"
                // of navigation, not part of the regular drawing process.

                // The Home screen FAB logic remains in HomeScreen, so we only set the TopBar here.
                LaunchedEffect(Unit) {
                    setTopBar { LargeTopAppBar(title = { Text("PhotoDo Home") }, scrollBehavior = scrollBehavior) }
                    // setFabState(FabState("Add Category") { homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked) })
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
                    setFabState = setFabState // <-- Pass the FAB setter down

                )
            }


            /**
             * Second Tab: Task Lists
             * Defines the content when a user navigates to a list of tasks within a specific category.
             * This entry corresponds to the `PhotoDoNavKeys.TaskListKey(categoryId)`.
             *
             * Behavior in Adaptive Layouts:
             * - `metadata = ListDetailSceneStrategy.listPane(...)`: Similar to the home screen, this is also
             * a "list" pane. When navigating from home on a folded device, this screen replaces the home
             * screen. On an unfolded device, this content typically appears in the second pane, replacing
             * the initial placeholder.
             *
             * - `detailPlaceholder`: This provides a placeholder for the third pane, prompting the user
             * to select a specific task list to see its details.
             */
            entry<PhotoDoNavKeys.TaskListKey>(metadata = ListDetailSceneStrategy.listPane(
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

                // ### WHY & WHAT ###
                // Same reason as above. We wrap these state updates in a LaunchedEffect
                // to prevent the infinite recomposition loop when this screen is shown.
                LaunchedEffect(Unit) {
                    setTopBar {
                        LargeTopAppBar(
                            title = { Text("Task Lists") },
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
                    setFabState(
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
                    )
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
             * Detailed Screen: Task Details (Not A Tab)
             * Defines the content for the final detail screen, showing the photo tasks within a task list.
             * This entry corresponds to the `PhotoDoNavKeys.TaskListDetailKey(listId)`.
             *
             * Behavior in Adaptive Layouts:
             * - `metadata = ListDetailSceneStrategy.detailPane()`: This marks the screen as a "detail" pane.
             * - On a folded device, it will cover the entire screen.
             * - On an unfolded device, it will appear in the final (third) pane on the right, completing
             * the three-pane layout. The `listDetailStrategy` automatically handles showing or hiding
             * the previous panes based on screen size and navigation history.
             */
            entry<PhotoDoNavKeys.TaskListDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                // NAV_LOG: Log rendering of TaskListDetailKey entry
                Log.d(TAG, "Displaying content for TaskListDetailKey (listId=${detailKey.listId})")
                val viewModel: PhotoDoDetailViewModel = hiltViewModel()
                LaunchedEffect(detailKey.listId) {
                    Log.d(TAG, "TaskListDetailKey LaunchedEffect triggered. Loading list with id: ${detailKey.listId}")
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
                    setFabState(FabStateMenu.Single(
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
             * Third Tab: Settings
             * Defines the content for the Settings screen.
             * This entry corresponds to the `PhotoDoNavKeys.SettingsKey`.
             *
             * Behavior in Adaptive Layouts:
             * - This entry has no special metadata, so it behaves as a standard, full-screen destination.
             * When navigated to, it will replace all existing panes on both folded and unfolded screens.
             * This is appropriate for top-level destinations like Settings that are not part of the
             * list-detail flow.
             */
            entry<PhotoDoNavKeys.SettingsKey> {
                // NAV_LOG: Log rendering of SettingsKey entry
                Log.d(TAG, "Displaying content for SettingsKey")
                val viewModel: SettingsViewModel = hiltViewModel()
                setTopBar { LargeTopAppBar(title = { Text("Settings") }, scrollBehavior = scrollBehavior) }
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

// Helper extension functions
fun <T : Any> MutableList<T>.replace(item: T) { if (isNotEmpty()) this[lastIndex] = item else add(item) }
fun <T : Any> MutableList<T>.replaceAll(item: T) { clear(); add(item) }
