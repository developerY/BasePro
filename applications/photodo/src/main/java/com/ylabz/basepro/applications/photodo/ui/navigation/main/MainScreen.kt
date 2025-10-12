package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.ylabz.basepro.applications.photodo.core.ui.FabMenu
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.core.ui.MainScreenEvent
import com.ylabz.basepro.applications.photodo.ui.navigation.NavKeySaver
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

private const val TAG = "MainScreen"

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3Api::class
)
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
    var lastSelectedCategoryId by rememberSaveable { mutableLongStateOf(1L) }

    // NAV_LOG: Log recomposition and state values
    Log.d(TAG, "MainScreen recomposing -> isExpanded: $isExpandedScreen, topLevelKey: ${currentTopLevelKey::class.simpleName}, lastSelectedCategoryId: $lastSelectedCategoryId")

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabStateMenu? by remember { mutableStateOf(null) }

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    val onNavigate: (NavKey) -> Unit = { navKey ->
        // NAV_LOG: Log top-level tab navigation click
        Log.d(TAG, "NAVIGATION -- onNavigate triggered with navKey: ${navKey::class.simpleName}")

        // When navigating via BottomBar/Rail, if the target is the List tab,
        // use the last selected category ID instead of the hardcoded one.
        val keyToNavigate = if (navKey is PhotoDoNavKeys.TaskListKey) {
            Log.d(TAG, "NAVIGATION -START-  -> List tab clicked. Overriding to last selected categoryId: $lastSelectedCategoryId")
            PhotoDoNavKeys.TaskListKey(lastSelectedCategoryId)
        } else {
            Log.d(TAG, "NAVIGATION --  -> Tab is not TaskListKey, using original key.")
            navKey
        }

        if (currentTopLevelKey::class != keyToNavigate::class) {
            Log.d(TAG, "NAVIGATION --  -> Switching top-level tab from ${currentTopLevelKey::class.simpleName} to ${keyToNavigate::class.simpleName}")
            currentTopLevelKey = keyToNavigate
            backStack.replaceAll(keyToNavigate) // Clear history when switching tabs
        } else {
            Log.d(TAG, "NAVIGATION --  -> Already on top-level tab ${keyToNavigate::class.simpleName}. No change."
            )
        }
        // NAV_LOG: Log navigation
        Log.d(TAG, "NAVIGATION -DONE- onNavigate triggered with navKey: ${navKey::class.simpleName}")
    }

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

    val appContent = @Composable { modifier: Modifier ->
        key(backStackKey) {
            PhotoDoNavGraph(
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
                },
                // Pass down lambdas that post the correct event
                onAddCategoryClicked = { mainScreenViewModel.postEvent(MainScreenEvent.ShowAddCategorySheet) },
                onAddListClicked = { mainScreenViewModel.postEvent(MainScreenEvent.AddList) },
                onAddItemClicked = { mainScreenViewModel.postEvent(MainScreenEvent.AddItem) }
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
        // **Expanded Layout (Tablets/Desktops): Use a Navigation Rail**
        // A Row is used to place the navigation rail and the main content side-by-side.
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate)
            // The main content area takes up the remaining space.
            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = topBar,
                floatingActionButton = { FabMenu(fabState) }
            ) { padding ->
                appContent(Modifier.padding(padding))
            }
        }
    } else {
        // **Compact Layout (Phones): Use a Bottom Navigation Bar**
        // A standard Scaffold is used, which is ideal for smaller screens.
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            // The bottom bar is the primary navigation method on compact screens.
            bottomBar = {
                HomeBottomBar(
                    currentTopLevelKey = currentTopLevelKey,
                    onNavigate = onNavigate
                )
            },
            floatingActionButton = { FabMenu(fabState) }
        ) { padding ->
            appContent(Modifier.padding(padding))
        }
    }
}

// Helper extension functions

fun <T : Any> MutableList<T>.replaceAll(item: T) {
    clear(); add(item)
}

/**
 * This version removes the local state management for the bottom sheet and relies
 * on the PhotoDoNavGraph to pass down the correct click handlers to the HomeEntry.
// When the state is true, show the bottom sheet.
LaunchedEffect(Unit) {
mainScreenViewModel.events.collect { event: MainScreenEvent ->
when (event) {
is MainScreenEvent.ShowAddCategorySheet -> {
showAddCategorySheet = true
}
else -> Unit}}}

if (showAddCategorySheet) {
AddCategorySheet(
onAddCategory = { categoryName ->
// When the user saves, post the event to add the category
mainScreenViewModel.postEvent(MainScreenEvent.AddCategory(categoryName))
showAddCategorySheet = false
},
onDismiss = { showAddCategorySheet = false }
)
}*/
