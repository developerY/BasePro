package com.ylabz.basepro.applications.photodo.ui.navigation.main

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
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
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

private data class FabState(val text: String, val onClick: () -> Unit)

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun MainScreen() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    // This is the only navigator you need for this pattern
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey) }
    // This strategy is what makes the adaptive logic work
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabState? by remember { mutableStateOf(null) }

    val onNavigate: (NavKey) -> Unit = { key ->
        if (topLevelBackStack.topLevelKey::class == key::class) {
            topLevelBackStack.popToRoot()
        } else {
            val newKey = when (key) {
                is PhotoDoNavKeys.TaskListKey -> PhotoDoNavKeys.TaskListKey(categoryId = 1L) // Default category
                else -> key
            }
            topLevelBackStack.switchTopLevel(newKey)
        }
    }

    val scaffoldContent: @Composable (Modifier) -> Unit = { modifier ->
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            sceneStrategy = listDetailStrategy,
            modifier = modifier,
            entryProvider = entryProvider {
                entry<PhotoDoNavKeys.HomeFeedKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { Text("Select a category") }
                        })
                ) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    topBar =
                        { LargeTopAppBar(title = { Text("PhotoDo Home") }, scrollBehavior = scrollBehavior) }
                    fabState =
                        FabState("Add Category") { homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked) }

                    PhotoDoHomeUiRoute(
                        navTo = { categoryId ->
                            // CORRECTED: Replace the current list (categories)
                            // with the new list (tasks).
                            val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
                            topLevelBackStack.switchTopLevel(listKey)
                            // topLevelBackStack.replaceStack(listKey) // Use replaceStack, not add
                        },
                        viewModel = homeViewModel
                    )
                }

                entry<PhotoDoNavKeys.TaskListKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { Text("Select a list to see details") }
                        })
                ) { listKey ->
                    val viewModel: PhotoDoListViewModel = hiltViewModel()
                    LaunchedEffect(listKey.categoryId) {
                        viewModel.loadCategory(listKey.categoryId)
                    }

                    fabState =
                        FabState("Add List") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) }

                    topBar = {
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
                            // CORRECT: Add the detail screen. The strategy will handle
                            // showing it in the second pane on large screens.
                            // topLevelBackStack.add(PhotoDoNavKeys.TaskListDetailKey(listId.toString()))
                            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
                            topLevelBackStack.add(detailKey) // Use add here
                        },
                        onEvent = viewModel::onEvent,
                        viewModel = viewModel
                    )
                }

                entry<PhotoDoNavKeys.TaskListDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { detailKey ->
                    val viewModel: PhotoDoDetailViewModel = hiltViewModel()
                    LaunchedEffect(detailKey.listId) {
                        viewModel.loadList(detailKey.listId)
                    }

                    fabState = null

                    topBar = {
                        TopAppBar(
                            title = { Text("List Details") },
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                IconButton(onClick = { topLevelBackStack.removeLastOrNull() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                    PhotoDoDetailUiRoute(viewModel = viewModel)
                }

                entry<PhotoDoNavKeys.SettingsKey> {
                    val viewModel: SettingsViewModel = hiltViewModel()
                    topBar =
                        { LargeTopAppBar(title = { Text("Settings") }, scrollBehavior = scrollBehavior) }
                    fabState = null

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

    if (isExpandedScreen) {
        Row(Modifier.fillMaxSize()) {
            HomeNavigationRail(
                topLevelBackStack = topLevelBackStack,
                onNavigate = onNavigate
            )
            Scaffold(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = topBar,
                floatingActionButton = {
                    fabState?.let {
                        ExtendedFloatingActionButton(
                            onClick = it.onClick,
                            icon = { Icon(Icons.Filled.Add, contentDescription = "${it.text} Icon") },
                            text = { Text(it.text) }
                        )
                    }
                }
            ) { innerPadding ->
                scaffoldContent(Modifier.padding(innerPadding))
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = {
                HomeBottomBar(
                    topLevelBackStack = topLevelBackStack,
                    onNavigate = onNavigate
                )
            },
            floatingActionButton = {
                fabState?.let {
                    ExtendedFloatingActionButton(
                        onClick = it.onClick,
                        icon = { Icon(Icons.Filled.Add, contentDescription = "${it.text} Icon") },
                        text = { Text(it.text) }
                    )
                }
            }
        ) { innerPadding ->
            scaffoldContent(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomeNavigationRail(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    onNavigate: (NavKey) -> Unit
) {
    val bottomNavItems = listOf<BottomBarItem>(
        PhotoDoNavKeys.HomeFeedKey,
        PhotoDoNavKeys.TaskListKey(categoryId = 0L), // Placeholder, projectId doesn't matter for selection
        PhotoDoNavKeys.SettingsKey
    )

    NavigationRail {
        bottomNavItems.forEach { item ->
            val title = item.title
            val icon = item.icon

            // Compare by the *class* of the NavKey, not the instance.
            // This ensures that any PhotoDolListKey (regardless of projectId) selects the correct tab.
            val selected = topLevelBackStack.topLevelKey::class == (item as NavKey)::class

            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(item as NavKey) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title
                    )
                },
                label = { Text(title) }
            )
        }
    }
}
