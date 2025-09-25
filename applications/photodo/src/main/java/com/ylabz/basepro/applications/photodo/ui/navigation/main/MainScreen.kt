package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailViewModel
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    // val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel() // REMOVED - will be scoped to NavDisplay entry

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarForCurrentRoute(
                topLevelBackStack = topLevelBackStack,
                // photoDoListViewModel will be handled by TopBarForCurrentRoute itself or removed if not needed globally
                onNavigateBack = { topLevelBackStack.removeLastOrNull() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            HomeBottomBar(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { key ->
                    val newKey = when (key) {
                        is PhotoDoNavKeys.PhotoDolListKey -> PhotoDoNavKeys.PhotoDolListKey(projectId = 1L) // Example: Default projectId
                        else -> key
                    }
                    topLevelBackStack.switchTopLevel(newKey)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // TODO: FAB action should be tied to the current active screen's ViewModel or via events.
                    // Example: If current route is PhotoDolListKey, get its ViewModel
                    // val currentRoute = topLevelBackStack.backStack.lastOrNull()
                    // if (currentRoute is PhotoDoNavKeys.PhotoDolListKey) {
                    // val listViewModel: PhotoDoListViewModel = hiltViewModel() // This would get a VM scoped to MainScreen's NavHost
                    // // A better approach: PhotoDoListUiRoute exposes an onAddTask event.
                    // }
                    Log.d("MainScreenFAB", "Add Task Clicked - further action needed based on current screen")
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Task Icon") },
                text = { Text("Add Task") }
            )
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
                        detailPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            )
                            { Text("Select an item to see details") }
                        })
                )
                {
                    PhotoDoHomeUiRoute(navTo = { destinationKey ->
                        // Example: Navigating from Home to a specific project's list
                        if (destinationKey is PhotoDoNavKeys.PhotoDolListKey) {
                             topLevelBackStack.add(PhotoDoNavKeys.PhotoDolListKey(projectId = destinationKey.projectId)) // Ensure projectId is passed
                        } else if (destinationKey is PhotoDoNavKeys.PhotoDoDetailKey) {
                            topLevelBackStack.add(destinationKey)
                        }
                        // Handle other potential navigations from home
                    })
                }

                entry<PhotoDoNavKeys.PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { detailKey ->
                    val detailViewModel: PhotoDoDetailViewModel = hiltViewModel()
                    LaunchedEffect(detailKey.photoId) {
                        // detailViewModel.loadPhoto(detailKey.photoId) // Assuming loadPhoto exists
                        Log.d("PhotoDoApp", "Detail LaunchedEffect: ID: '\${detailKey.photoId}'")
                    }
                    PhotoDoDetailUiRoute(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = detailViewModel
                    )
                }

                entry<PhotoDoNavKeys.PhotoDolListKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            )
                            { Text("Select an item to see details") }
                        }),
                )
                { listKey -> // listKey is PhotoDoNavKeys.PhotoDolListKey(projectId=...)
                    // ViewModel is instantiated here, scoped to this navigation destination
                    val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()

                    LaunchedEffect(listKey.projectId) {
                        Log.d("PhotoDoApp", "PhotoDolListKey active with projectId: \${listKey.projectId}")
                        // ViewModel's uiState will collect based on this projectId
                    }

                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onTaskClick = { taskId -> // taskId is Long
                            Log.d("PhotoDoApp", "PhotoListKey: Navigating to detail with Task ID string: '\$taskId'")
                            topLevelBackStack.add(PhotoDoNavKeys.PhotoDoDetailKey(photoId = taskId.toString())) // Convert Long to String
                        },
                        onEvent = photoDoListViewModel::onEvent,
                        viewModel = photoDoListViewModel
                    )
                }

                entry<PhotoDoNavKeys.SettingsKey> {
                    SettingsUiRoute(
                        modifier = Modifier,
                        navTo = {},
                        initialCardKeyToExpand = null
                    )
                }
            }
        )
    }
}
