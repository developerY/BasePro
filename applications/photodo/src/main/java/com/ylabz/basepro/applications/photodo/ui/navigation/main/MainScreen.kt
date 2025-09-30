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
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeViewModel
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
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
private data class FabState(val text: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    // The actual navigation history for the NavDisplay.
    val backStack = rememberNavBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey)

    // The currently selected top-level tab. `rememberSaveable` ensures this state survives process death.
    var currentTopLevelKey: NavKey by rememberSaveable(stateSaver = NavKeySaver) {
        mutableStateOf(PhotoDoNavKeys.HomeFeedKey)
    }
    // Remember the last category ID the user interacted with. Default to 1L since we know it has data.
    var lastSelectedCategoryId by rememberSaveable { mutableStateOf(1L) }

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabState? by remember { mutableStateOf(null) }

    val onNavigate: (NavKey) -> Unit = { navKey ->
        // When navigating via BottomBar/Rail, if the target is the List tab,
        // use the last selected category ID instead of the hardcoded one.
        val keyToNavigate = if (navKey is PhotoDoNavKeys.TaskListKey) {
            Log.d(TAG, "List tab clicked. Overriding to last selected categoryId: $lastSelectedCategoryId")
            PhotoDoNavKeys.TaskListKey(lastSelectedCategoryId)
        } else {
            navKey
        }

        if (currentTopLevelKey::class != keyToNavigate::class) {
            Log.d(TAG, "Top-level navigation to: ${keyToNavigate::class.simpleName}")
            currentTopLevelKey = keyToNavigate
            backStack.replaceAll(keyToNavigate) // Clear history when switching tabs
        }
    }

    // A key that forces recomposition when the back stack changes.
    val backStackKey = backStack.joinToString { (it as? PhotoDoNavKeys)?.javaClass?.simpleName ?: "Detail" }

    val appContent = @Composable { modifier: Modifier ->
        key(backStackKey) {
            AppContent(
                modifier = modifier,
                backStack = backStack,
                sceneStrategy = listDetailStrategy,
                scrollBehavior = scrollBehavior,
                setTopBar = { topBar = it },
                setFabState = { fabState = it },
                onCategorySelected = { categoryId ->
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
                floatingActionButton = { Fab(fabState) }
            ) { padding ->
                appContent(Modifier.padding(padding))
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = { HomeBottomBar(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate) },
            floatingActionButton = { Fab(fabState) }
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
    setFabState: (FabState?) -> Unit,
    onCategorySelected: (Long) -> Unit // Callback to update the remembered category ID
    // REMOVE the updateCurrentTopLevelKey parameter, it's not needed here
    // updateCurrentTopLevelKey: (NavKey) -> Unit
) {
    // val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = sceneStrategy,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<PhotoDoNavKeys.HomeFeedKey>(metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select a category") } })) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                setTopBar { LargeTopAppBar(title = { Text("PhotoDo Home") }, scrollBehavior = scrollBehavior) }
                setFabState(FabState("Add Category") { homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked) })

                PhotoDoHomeUiRoute(
                    navTo = { categoryId ->
                        /*val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
                        updateCurrentTopLevelKey(listKey) // Update the selected tab
                        backStack.replace(listKey)      // Navigate to the new screen*/
                        // CORRECTED LOGIC:
                        // 1. Do NOT update the top-level key here.
                        // 2. ADD the new screen to the stack for forward navigation.
                        Log.d(TAG, "Navigating from Home to TaskList with categoryId: $categoryId")
                        onCategorySelected(categoryId) // Update the remembered category ID
                        val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
                        backStack.add(listKey)
                    },
                    viewModel = homeViewModel
                )
            }
            entry<PhotoDoNavKeys.TaskListKey>(metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select a list to see details") } })) { listKey ->
                val viewModel: PhotoDoListViewModel = hiltViewModel()
                LaunchedEffect(listKey.categoryId) {
                    Log.d(TAG, "TaskListKey LaunchedEffect triggered. Loading category with id: ${listKey.categoryId}")
                    onCategorySelected(listKey.categoryId) // Also update when loading a list directly
                    viewModel.loadCategory(listKey.categoryId)
                }

                setFabState(FabState("Add List") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) })
                setTopBar {
                    LargeTopAppBar(
                        title = { Text("Task Lists") },
                        scrollBehavior = scrollBehavior,
                        actions = {
                            IconButton(onClick = { viewModel.onEvent(PhotoDoListEvent.OnDeleteAllTaskListsClicked) }) {
                                Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Lists")
                            }
                        }
                    )
                }

                PhotoDoListUiRoute(
                    onTaskClick = { listId ->
                        // This is already correct! You are correctly adding the detail
                        // key to the stack.
                        Log.d(TAG, "Navigating from TaskList to Detail with listId: $listId")
                        val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
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
            entry<PhotoDoNavKeys.TaskListDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                val viewModel: PhotoDoDetailViewModel = hiltViewModel()
                LaunchedEffect(detailKey.listId) {
                    Log.d(TAG, "TaskListDetailKey LaunchedEffect triggered. Loading list with id: ${detailKey.listId}")
                    viewModel.loadList(detailKey.listId)
                }

                setFabState(null)
                setTopBar {
                    TopAppBar(
                        title = { Text("List Details") },
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            IconButton(onClick = { backStack.removeLastOrNull() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
                PhotoDoDetailUiRoute(viewModel = viewModel)
            }
            entry<PhotoDoNavKeys.SettingsKey> {
                val viewModel: SettingsViewModel = hiltViewModel()
                setTopBar { LargeTopAppBar(title = { Text("Settings") }, scrollBehavior = scrollBehavior) }
                setFabState(null)

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

@Composable
private fun Fab(fabState: FabState?) {
    fabState?.let {
        ExtendedFloatingActionButton(
            onClick = it.onClick,
            icon = { Icon(Icons.Filled.Add, contentDescription = "${it.text} Icon") },
            text = { Text(it.text) }
        )
    }
}

// Helper extension functions
fun <T : Any> MutableList<T>.replace(item: T) { if (isNotEmpty()) this[lastIndex] = item else add(item) }
fun <T : Any> MutableList<T>.replaceAll(item: T) { clear(); add(item) }
