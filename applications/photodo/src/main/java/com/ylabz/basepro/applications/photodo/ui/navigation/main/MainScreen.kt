package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

// Data class to hold FAB configuration
private data class FabState(val text: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var fabState: FabState? by remember { mutableStateOf(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = topBar,
        bottomBar = {
            HomeBottomBar(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { key ->
                    val newKey = when (key) {
                        is PhotoDoNavKeys.PhotoDolListKey -> PhotoDoNavKeys.PhotoDolListKey(projectId = 1L)
                        else -> key
                    }
                    topLevelBackStack.switchTopLevel(newKey)
                }
            )
        },
        floatingActionButton = {
            // Render FAB based on fabState
            fabState?.let {
                ExtendedFloatingActionButton(
                    onClick = it.onClick,
                    icon = { Icon(Icons.Filled.Add, contentDescription = "${it.text} Icon") },
                    text = { Text(it.text) }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            sceneStrategy = listDetailStrategy,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryProvider = entryProvider {
                entry<PhotoDoNavKeys.HomeFeedKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select a category") } })
                )
                {
                    topBar = { LargeTopAppBar(title = { Text("PhotoDo Home") }, scrollBehavior = scrollBehavior) }
                    fabState = FabState("Add Category") { /* TODO: Add category action */ }

                    PhotoDoHomeUiRoute(
                        navTo = { projectId ->
                            topLevelBackStack.add(PhotoDoNavKeys.PhotoDolListKey(projectId.toLong()))
                        }
                    )
                }

                entry<PhotoDoNavKeys.PhotoDolListKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Select an item to see details") } })
                )
                { listKey ->
                    val viewModel: PhotoDoListViewModel = hiltViewModel()
                    LaunchedEffect(listKey.projectId) {
                        viewModel.loadProject(listKey.projectId)
                    }

                    topBar = { /* ... as before ... */ }
                    fabState = FabState("Add Item") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskClicked) }

                    topBar = {
                        LargeTopAppBar(
                            title = { Text("Photo List") },
                            scrollBehavior = scrollBehavior,
                            actions = {
                                IconButton(onClick = { viewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked) }) {
                                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Tasks")
                                }
                            }
                        )
                    }

                    PhotoDoListUiRoute(
                        onTaskClick = { taskId ->
                            topLevelBackStack.add(PhotoDoNavKeys.PhotoDoDetailKey(taskId.toString()))
                        },
                        onEvent = viewModel::onEvent,
                        viewModel = viewModel
                    )
                }

                entry<PhotoDoNavKeys.PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { detailKey ->
                    topBar = { /* ... as before ... */ }
                    fabState = null // Hide FAB on detail screen

                    topBar = {
                        TopAppBar(
                            title = { Text("Task Details") },
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                IconButton(onClick = { topLevelBackStack.removeLastOrNull() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                    PhotoDoDetailUiRoute()
                }

                entry<PhotoDoNavKeys.SettingsKey> {
                    topBar = { LargeTopAppBar(title = { Text("Settings") }, scrollBehavior = scrollBehavior) }
                    fabState = null // Hide FAB on settings screen

                    SettingsUiRoute(modifier = Modifier, navTo = {}, initialCardKeyToExpand = null)
                }
            }
        )
    }
}
