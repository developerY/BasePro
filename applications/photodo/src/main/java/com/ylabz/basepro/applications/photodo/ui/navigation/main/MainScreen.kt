package com.ylabz.basepro.applications.photodo.ui.navigation.main

// --- THIS IS THE CORRECT, REAL IMPORT ---
// --- END ---
import FabMain
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.ylabz.basepro.applications.photodo.ui.navigation.NavKeySaver
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.components.debug.DebugStackUi
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.components.AddListSheet
import com.ylabz.basepro.core.ui.components.AddCategoryBottomSheet
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

enum class TopLevelDestination(val key: NavKey) {
    HOME(PhotoDoNavKeys.HomeFeedKey),
    TASK_LIST(PhotoDoNavKeys.TaskListKey(0)),
    SETTINGS(PhotoDoNavKeys.SettingsKey)
}

fun NavBackStack<NavKey>.isTopLevelDestinationInBackStack(topLevelDestinations: List<NavKey>): Boolean {
    return this.any { entry: NavKey -> topLevelDestinations.any { it::class == entry::class } }
}

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    // 1. Get the one and only STATEFUL ViewModel
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    scrollBehavior: TopAppBarScrollBehavior
) {
    // 0 Activity
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact


    // 1. COLLECT THE STATE from the single source of truth
    val uiState by mainScreenViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()


    // --- Navigation State ---
    // NOTE: **Bottom Navigation State**
    // The actual navigation history for the NavDisplay.
    val backStack = rememberNavBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey)
    // NOTE: **Adaptive Navigation State**
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    val currentListId by remember {
        derivedStateOf {
            backStack.filterIsInstance<PhotoDoNavKeys.TaskListDetailKey>()
                .lastOrNull()?.listId
        }
    }

    /*
    val backStack = rememberNavBackStack(PhotoDoNavKeys.HomeFeedKey)
    // 2. Use the *real* function from the 'adaptive-navigation3' library
    val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()
    */


    // --- Bottom Sheet State ---
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = uiState.currentSheet != BottomSheetType.NONE


    // val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    // --- Top Bar State ---
    var topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) by remember { mutableStateOf({}) }
    val setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit = { topBar = it }
    // -- Fix this
    //var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    //var fabState: FabStateMenu? by remember { mutableStateOf(null) }

    // 1. Define the state for the FAB. It's nullable.
    var currentFabState by remember { mutableStateOf<FabState?>(null) }


    // --- Back Stack Management ---

    //NOTE: **Top-Level Navigation State**
    // (`currentTopLevelKey`): This tracks which main section of the app the user is in (e.g., "Home," "Tasks," or "Settings")
    // The currently selected top-level tab. `rememberSaveable` ensures this state survives process death.
    var currentTopLevelKey: NavKey by rememberSaveable(stateSaver = NavKeySaver) {
        mutableStateOf(PhotoDoNavKeys.HomeFeedKey)
    }

    /*var currentTopLevelKey: NavKey by remember(backStack) {
        mutableStateOf(
            backStack.lastOrNull { entry: NavKey ->
                TopLevelDestination.entries.any { dest -> dest.key::class == entry::class }
            } ?: PhotoDoNavKeys.HomeFeedKey
        )
    }*/

    // 1. Observe the categories from the HomeViewModel (you'll need to expose them or the HomeUiState)
    // Ideally, MainScreenViewModel should hold this "global" state, but for now, let's assume
    // you can check if the ID is valid.

    // A simpler fix for now: Make the default nullable and handle the null case.
    // var lastSelectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }

    // Remember the last category ID the user interacted with. Default to 1L since we know it has data.
    // --- Remembered State for "Add List" ---
    // var lastSelectedCategoryId by rememberSaveable { mutableLongStateOf(1L) }

    // --- Navigation Handler ---
    // =================================================================
    // START OF FIX
    // =================================================================
    val onNavigate: (NavKey) -> Unit = { navKey ->
        // NAV_LOG: Log top-level tab navigation click
        Log.d(TAG, "NAVIGATION -- onNavigate triggered with navKey: ${navKey::class.simpleName}")

        // When navigating via BottomBar/Rail, if the target is the List tab,
        // use the last selected category ID instead of the hardcoded one.
        val keyToNavigate = if (navKey is PhotoDoNavKeys.TaskListKey) {
            if (uiState.lastSelectedCategoryId != null) {
                Log.d(TAG, "NAVIGATION -START-  -> List tab clicked. Overriding to last selected categoryId: ${uiState.lastSelectedCategoryId}")
                // Read from the uiState instead of the local variable
                // CHECK: Do we have a valid category ID?
                PhotoDoNavKeys.TaskListKey(uiState.lastSelectedCategoryId!!)
            } else {
                Log.d(TAG, "NAVIGATION --  -> List tab clicked. Using default categoryId: null")
                // try to set it to first category if you can
                // NOTE: We need to add this code as not sure how to this ...
                PhotoDoNavKeys.TaskListKey(null)
            }
        } else {
            Log.d(TAG, "NAVIGATION --  -> Tab is not TaskListKey, using original key.")
            navKey
        }

        // --- THIS IS THE FIX ---
        // Check if we are *already* at the root of the stack with this *exact* key.
        // backStack.firstOrNull() checks the current root.
        val isAlreadyAtRoot = backStack.firstOrNull() == keyToNavigate && backStack.size == 1

        if (!isAlreadyAtRoot) {
            // This block now correctly handles:
            // 1. Switching to a new tab.
            // 2. Popping to the root of the *current* tab (e.g., from a detail screen).
            // 3. Switching between two keys of the *same* class (e.g., TaskListKey(2) -> TaskListKey(5)).
            Log.d(TAG, "NAVIGATION --  -> Navigating to ${keyToNavigate::class.simpleName}. Replacing stack.")
            currentTopLevelKey = keyToNavigate
            backStack.replaceAll(keyToNavigate) // Clear history when switching tabs or popping to root
        } else {
            // We are already at the root of the correct tab, so do nothing.
            Log.d(TAG, "NAVIGATION --  -> Already on top-level tab ${keyToNavigate::class.simpleName} at root. No change.")
        }
        // --- END OF FIX ---

        // NAV_LOG: Log navigation
        Log.d(TAG, "NAVIGATION -DONE- onNavigate triggered with navKey: ${navKey::class.simpleName}")
    }
    // =================================================================
    // END OF FIX
    // =================================================================

    // A key that forces recomposition when the back stack changes.
    // CORRECTED KEY: This now uses derivedStateOf to be state-aware.
    val backStackKey by remember {
        derivedStateOf {
            backStack.joinToString("-") { navKey ->
                Log.d(TAG, "NAVIGATION -RECOMPOSITION- onNavigate triggered with navKey: ${navKey::class.simpleName}")
                when (navKey) {
                    is PhotoDoNavKeys.TaskListKey -> "TaskList(${navKey.categoryId})"
                    else -> navKey.javaClass.simpleName
                }
            }
        }
    }

    // NAV_LOG: Log the current back stack state before rendering AppContent
    Log.d(TAG, "BackStack state before AppContent: $backStackKey")

    // --- 4. Define the app content ONCE ---
    // This is the NavGraph that contains all the screens
    val appContent = @Composable { modifier: Modifier ->
        key(backStackKey) {
            PhotoDoNavGraph(
                modifier = modifier,
                backStack = backStack,
                sceneStrategy = listDetailStrategy,
                isExpandedScreen = isExpandedScreen,
                scrollBehavior = scrollBehavior,
                setTopBar = { topBar = { it(scrollBehavior) } },
                // THIS IS WHERE THE FUNCTION IS CREATED AND PASSED DOWN
                setFabState = { newFabState -> currentFabState = newFabState },
                // setFabState = { fabState = it },
                onCategorySelected = { categoryId ->
                    // NAV_LOG: Log when the last selected category ID is updated
                    Log.d(TAG, "onCategorySelected callback triggered. Updating lastSelectedCategoryId to: $categoryId")
                    mainScreenViewModel.onEvent(MainScreenEvent.OnCategorySelected(categoryId))                },
                // Pass the ACTIONS down.
                onEvent = mainScreenViewModel::onEvent

            )
        }
    }

    /**
     *Explanation of the Comments:
     * Main Block Comment: This provides a high-level overview of what the entire if/else statement is for, explaining that it's the central piece of the adaptive UI.
     * Expanded Layout Comment: This comment specifically describes the "if" part of the logic, explaining why a Row and HomeNavigationRail are used for larger screens.
     * Compact Layout Comment: This describes the "else" block, clarifying that a standard Scaffold with a HomeBottomBar is the appropriate choice for smaller, vertical screens like phones.
     * These comments should make the code much easier for other developers (and your future self!) to understand at a glance.
     */

    /**
     * ---------------------------------------------------------------------------------
     * ADAPTIVE UI SCAFFOLDING
     * ---------------------------------------------------------------------------------
     * This logic block is the core of the app's adaptive layout. It checks the
     * 'isExpandedScreen' boolean to determine the device's screen size category
     * (calculated using Material 3's WindowSizeClass).
     *
     * - For EXPANDED screens (like tablets or desktops), it creates a side-by-side
     * layout using a Row, placing a NavigationRail on the left and the main
     * app content on the right.
     *
     * - For COMPACT screens (like most phones in portrait mode), it uses a standard
     * Scaffold with a HomeBottomBar at the bottom for navigation.
     *
     * This approach ensures the UI is optimized for different form factors, providing
     * an intuitive user experience on any device.
     * ---------------------------------------------------------------------------------
     */
    if (isExpandedScreen) {
    // **Expanded Layout: Show Navigation Rail**
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(
                currentTopLevelKey = currentTopLevelKey,
                onNavigate = onNavigate
                /* onNavigate = { key ->
                    if (key::class != currentTopLevelKey::class) {
                        currentTopLevelKey = key
                        backStack.replace(key)
                    }
                }*/
            )
            Scaffold(
                // **Expanded Layout: Show Bottom Bar**
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = { topBar(scrollBehavior) },
                floatingActionButton = {
                    // 7. Render the FAB from the VM state
                    FabMain(fabState = currentFabState)
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    DebugStackUi(
                        backStackKey = backStackKey,
                        categoryId = uiState.lastSelectedCategoryId,
                        currentListId = currentListId,
                        currentFabState = currentFabState
                    )
                    appContent(Modifier.padding(padding))
                }
            }
        }
    } else {
// **Compact Layout: Show Bottom Bar**
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { topBar(scrollBehavior) },
            bottomBar = {
                Column {
                    HomeBottomBar(
                        currentTopLevelKey = currentTopLevelKey,
                        onNavigate = onNavigate
                        /*onNavigate = { key ->
                        if (key::class != currentTopLevelKey::class) {
                            currentTopLevelKey = key
                            backStack.replaceTop(key)
                        }
                    }*/
                    )
                    // Add the new collapsible debug UI
                    DebugStackUi(
                        backStackKey = backStackKey,
                        categoryId = uiState.lastSelectedCategoryId,
                        currentListId = currentListId,
                        currentFabState = currentFabState
                    )
                }
            },
            floatingActionButton = {
                // 7. Render the FAB from the VM state
                FabMain(fabState = currentFabState)
            }
        ) { padding ->
            appContent(Modifier.padding(padding))
        }
    }
// --- END OF YOUR CORRECT LAYOUT LOGIC ---

// --- END OF YOUR CORRECT LAYOUT LOGIC ---
// G. SHOW BOTTOM SHEETS based on the collected state
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
            },
            sheetState = modalSheetState
        ) {
            when (uiState.currentSheet) {
                BottomSheetType.ADD_CATEGORY -> AddCategoryBottomSheet(
                    onDismiss = {
                        mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                    },
                    onSaveCategory = { name: String, description: String ->
                        mainScreenViewModel.onEvent(
                            MainScreenEvent.OnSaveCategory(
                                name,
                                description
                            )
                        )
                        mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                    }
                )

                // --- UPDATED CALL SITE ---
                BottomSheetType.ADD_LIST -> {
                    AddListSheet(
                        state = uiState,
                        onEvent = mainScreenViewModel::onEvent
                    )
                }
                // -------------------------

                /*BottomSheetType.ADD_LIST -> AddListBottomSheet(
                    onDismiss = { mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed) },
                    onSaveList = { title: String, description: String ->
                        // --- NEW LOGIC ---
                        // Get the current category ID from the ViewModel's state
                        val currentCategoryId = uiState.lastSelectedCategoryId

                        // 1. Send the save event with all necessary data
                        if (currentCategoryId != null) {
                            // YES: Unwrap it (smart cast to Long) and send the event
                            mainScreenViewModel.onEvent(
                                MainScreenEvent.OnSaveList(
                                    title = title,
                                    description = description,
                                    categoryId = currentCategoryId
                                )
                            )
                        } else {
                            // NO: Do not send the event. Show an error or log it.
                            Log.e("MainScreen", "Cannot save list. No category selected.")

                            // 2. Dismiss the sheet (or use the onDismiss handler)
                            mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                            // --- END NEW LOGIC ---
                        }
                    }
                )*/

                BottomSheetType.ADD_ITEM -> AddItemBottomSheet(
                    onAddClick = {
                        Log.d(TAG, "Adding Item (Photo) - Not yet implemented")
                        scope.launch { modalSheetState.hide() }.invokeOnCompletion {
                            mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
                        }
                    }
                )

                BottomSheetType.NONE -> {}
            }
        }
    }
}



@Composable
fun AddItemBottomSheet(onAddClick: () -> Unit) {
    Text("Add Item Bottom Sheet")
}
// Helper extension functions

fun <T : Any> MutableList<T>.replaceAll(item: T) {
    clear(); add(item)
}
fun <T : Any> MutableList<T>.replaceTop(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}