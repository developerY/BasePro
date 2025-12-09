package com.ylabz.basepro.applications.photodo.ui.navigation.main

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.components.debug.DebugStackUi
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.components.AddCategoryBottomSheet
import com.ylabz.basepro.applications.photodo.ui.navigation.main.components.AddListSheet
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
    val backStack = rememberNavBackStack(PhotoDoNavKeys.HomeFeedKey)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    val currentListId by remember {
        derivedStateOf {
            backStack.filterIsInstance<PhotoDoNavKeys.TaskListDetailKey>()
                .lastOrNull()?.listId
        }
    }

    // --- Bottom Sheet State ---
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = uiState.currentSheet != BottomSheetType.NONE

    // --- Top Bar & FAB State ---
    var topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) by remember { mutableStateOf({}) }
    // 1. Define the state for the FAB. It's nullable.
    var currentFabState by remember { mutableStateOf<FabState?>(null) }

    // --- Back Stack Management ---
    // --- FIX: SMART TAB SELECTION ---
    val currentTopLevelKey by remember {
        derivedStateOf {
            val top = backStack.lastOrNull()
            val root = backStack.firstOrNull() ?: PhotoDoNavKeys.HomeFeedKey

            // Helper to find matching tab
            fun findTab(key: NavKey?): NavKey? {
                if (key == null) return null
                return TopLevelDestination.entries.find { dest ->
                    dest.key::class == key::class
                }?.key
            }

            // If looking at Task List (any category), highlight Tasks Tab
            findTab(top) ?: findTab(root) ?: PhotoDoNavKeys.HomeFeedKey
        }
    }

    // --- Navigation Handler ---
    val onNavigate: (NavKey) -> Unit = { navKey ->
        Log.d(TAG, "NAVIGATION -- onNavigate triggered with navKey: ${navKey::class.simpleName}")

        val keyToNavigate: NavKey = if (navKey is PhotoDoNavKeys.TaskListKey) {
            if (uiState.lastSelectedCategoryId != null) {
                PhotoDoNavKeys.TaskListKey(uiState.lastSelectedCategoryId!!)
            } else {
                PhotoDoNavKeys.TaskListKey(null)
            }
        } else {
            navKey
        }

        val isSameTab = currentTopLevelKey::class == keyToNavigate::class

        if (!isSameTab) {
            Log.d(TAG, "Switching Tabs: ${currentTopLevelKey::class.simpleName} -> ${keyToNavigate::class.simpleName}")
            backStack.clear()
            backStack.add(keyToNavigate)
        } else {
            if (backStack.size > 1) {
                Log.d(TAG, "Reseeting Tab to Root.")
                backStack.subList(1, backStack.size).clear()
            }
        }
    }

    // A key that forces recomposition when the back stack changes.
    val backStackKey by remember {
        derivedStateOf {
            backStack.joinToString("-") { navKey ->
                when (navKey) {
                    is PhotoDoNavKeys.TaskListKey -> "TaskList(${navKey.categoryId})"
                    else -> navKey.javaClass.simpleName
                }
            }
        }
    }

    // --- FIX: RESET STACK ON FOLD For Testing ---
    LaunchedEffect(isExpandedScreen) {
        if (!isExpandedScreen) {
            Log.d(TAG, "Fold Detected. Current Stack Size: ${backStack.size}")
            if (backStack.size > 1) {
                Log.d(TAG, "Resetting stack to Root (Home).")
                backStack.subList(1, backStack.size).clear()
            }
        }
    }

    Log.d(TAG, "BackStack state before AppContent: $backStackKey")

    // --- RENDER CONTENT ---
    // We pass the "NavGraph" as a lambda to the stateless content
    MainScreenContent(
        isExpandedScreen = isExpandedScreen,
        uiState = uiState,
        currentTopLevelKey = currentTopLevelKey,
        currentFabState = currentFabState,
        backStackKey = backStackKey,
        currentListId = currentListId,
        scrollBehavior = scrollBehavior,
        topBar = topBar,
        onNavigate = onNavigate,
        onEvent = mainScreenViewModel::onEvent,
        content = { modifier ->
            // The actual Navigation Graph
            key(backStackKey) {
                if (backStack.isNotEmpty()) {
                    PhotoDoNavGraph(
                        modifier = modifier,
                        backStack = backStack,
                        sceneStrategy = listDetailStrategy,
                        isExpandedScreen = isExpandedScreen,
                        scrollBehavior = scrollBehavior,
                        setTopBar = { topBar = { it(scrollBehavior) } },
                        setFabState = { newFabState -> currentFabState = newFabState },
                        onCategorySelected = { categoryId ->
                            Log.d(TAG, "onCategorySelected callback triggered. Updating lastSelectedCategoryId to: $categoryId")
                            mainScreenViewModel.onEvent(MainScreenEvent.OnCategorySelected(categoryId))
                        },
                        onEvent = mainScreenViewModel::onEvent
                    )
                }
            }
        }
    )

    // --- BOTTOM SHEETS ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                mainScreenViewModel.onEvent(MainScreenEvent.OnBottomSheetDismissed)
            },
            sheetState = modalSheetState
        ) {
            when (uiState.currentSheet) {
                BottomSheetType.ADD_CATEGORY -> {
                    AddCategoryBottomSheet(
                        uiState = uiState,
                        onEvent = mainScreenViewModel::onEvent
                    )
                }
                BottomSheetType.ADD_LIST -> {
                    AddListSheet(
                        state = uiState,
                        onEvent = mainScreenViewModel::onEvent
                    )
                }
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

/**
 * ---------------------------------------------------------------------------------
 * STATELESS COMPOSABLE: MainScreenContent
 * ---------------------------------------------------------------------------------
 * This extracted composable handles the ADAPTIVE UI SCAFFOLDING layout.
 * It separates the layout logic (Row vs Column) from the ViewModel and State logic.
 *
 * This allows for:
 * 1. Previews (by passing mock state).
 * 2. Easier testing.
 * 3. Fixing layout bugs (like double padding) in one place.
 * ---------------------------------------------------------------------------------
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    isExpandedScreen: Boolean,
    uiState: MainScreenUiState, // Ensure this matches your actual State class name
    currentTopLevelKey: NavKey,
    currentFabState: FabState?,
    backStackKey: String,
    currentListId: String?,
    scrollBehavior: TopAppBarScrollBehavior,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit,
    onNavigate: (NavKey) -> Unit,
    onEvent: (MainScreenEvent) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    if (isExpandedScreen) {
        // **Expanded Layout: Show Navigation Rail**
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(
                currentTopLevelKey = currentTopLevelKey,
                onNavigate = onNavigate
            )
            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = { topBar(scrollBehavior) },
                floatingActionButton = {
                    FabMain(fabState = currentFabState)
                }
            ) { padding ->
                Column(
                    // 1. This padding handles the TopBar offset correctly
                    modifier = Modifier.padding(padding)
                ) {
                    DebugStackUi(
                        backStackKey = backStackKey,
                        categoryId = uiState.lastSelectedCategoryId,
                        currentListId = currentListId,
                        currentFabState = currentFabState
                    )

                    // --- FIX: NO PADDING HERE ---
                    // The parent Column is already padded. passing Modifier.fillMaxSize()
                    // ensures the content fills the remaining space without adding
                    // a second layer of padding.
                    content(Modifier.fillMaxSize())
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
                FabMain(fabState = currentFabState)
            }
        ) { padding ->
            // In compact mode, we DO need to pass the padding down, because
            // the content is a direct child of Scaffold, not wrapped in a padded Column.
            content(Modifier.padding(padding))
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