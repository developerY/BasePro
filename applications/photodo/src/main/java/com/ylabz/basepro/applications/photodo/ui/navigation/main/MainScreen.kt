package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem
import com.ylabz.basepro.applications.photodo.ui.navigation.NavKeySaver
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

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

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabState? by remember { mutableStateOf(null) }

    val onNavigate: (NavKey) -> Unit = { newKey ->
        if (currentTopLevelKey::class != newKey::class) {
            currentTopLevelKey = newKey
            backStack.replaceAll(newKey) // Clear history when switching tabs
        }
    }

    // A key that forces recomposition when the back stack changes, fixing the detail navigation bug.
    val backStackKey = backStack.joinToString { (it as? PhotoDoNavKeys)?.javaClass?.simpleName ?: "Detail" }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            HomeNavigationRail(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate)
            Scaffold(
                modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = topBar,
                floatingActionButton = { Fab(fabState) }
            ) { padding ->
                key(backStackKey) {
                    AppContent(
                        modifier = Modifier.padding(padding),
                        backStack = backStack,
                        sceneStrategy = listDetailStrategy,
                        setTopBar = { topBar = it },
                        setFabState = { fabState = it },
                        updateCurrentTopLevelKey = { currentTopLevelKey = it }
                    )
                }
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = { HomeBottomBar(currentTopLevelKey = currentTopLevelKey, onNavigate = onNavigate) },
            floatingActionButton = { Fab(fabState) }
        ) { padding ->
            key(backStackKey) {
                AppContent(
                    modifier = Modifier.padding(padding),
                    backStack = backStack,
                    sceneStrategy = listDetailStrategy,
                    setTopBar = { topBar = it },
                    setFabState = { fabState = it },
                    updateCurrentTopLevelKey = { currentTopLevelKey = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit,
    updateCurrentTopLevelKey: (NavKey) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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
                        val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
                        updateCurrentTopLevelKey(listKey) // Update the selected tab
                        backStack.replace(listKey)      // Navigate to the new screen
                    },
                    viewModel = homeViewModel
                )
            }
            entry<PhotoDoNavKeys.TaskListKey>(metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select a list to see details") } })) { listKey ->
                val viewModel: PhotoDoListViewModel = hiltViewModel()
                LaunchedEffect(listKey.categoryId) { viewModel.loadCategory(listKey.categoryId) }

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
                        val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
                        backStack.add(detailKey)
                    },
                    onEvent = viewModel::onEvent,
                    viewModel = viewModel
                )
            }
            entry<PhotoDoNavKeys.TaskListDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                val viewModel: PhotoDoDetailViewModel = hiltViewModel()
                LaunchedEffect(detailKey.listId) { viewModel.loadList(detailKey.listId) }

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

@Composable
fun HomeNavigationRail(currentTopLevelKey: NavKey, onNavigate: (NavKey) -> Unit) {
    val bottomNavItems = listOf<BottomBarItem>(
        PhotoDoNavKeys.HomeFeedKey,
        PhotoDoNavKeys.TaskListKey(categoryId = 0L),
        PhotoDoNavKeys.SettingsKey
    )

    NavigationRail {
        bottomNavItems.forEach { item ->
            val navKeyItem = item as NavKey
            val selected = currentTopLevelKey::class == navKeyItem::class
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(navKeyItem) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

// Helper extension functions
fun <T : Any> MutableList<T>.replace(item: T) { if (isNotEmpty()) this[lastIndex] = item else add(item) }
fun <T : Any> MutableList<T>.replaceAll(item: T) { clear(); add(item) }