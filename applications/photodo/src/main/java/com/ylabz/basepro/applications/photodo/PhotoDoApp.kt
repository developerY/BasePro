package com.ylabz.basepro.applications.photodo

import android.util.Log // Added for logging
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
// androidx.navigation3.runtime.getValue is not needed here as 'detailKey' is the NavKey itself
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.HomeFeedKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoDetailKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoListKey
import com.ylabz.basepro.applications.photodo.ui.navigation.SettingsKey
import com.ylabz.basepro.applications.photodo.ui.navigation.components.HomeBottomBarNav3
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoDoApp() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PhotoDo") },
                actions = {
                    IconButton(onClick = {
                        photoDoListViewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked)
                    }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Tasks")
                    }
                }
            )
        },
        bottomBar = {
            HomeBottomBarNav3(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { topLevelBackStack.switchTopLevel(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                photoDoListViewModel.onEvent(PhotoDoListEvent.OnAddTaskClicked)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
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
                entry<HomeFeedKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Select an item to see details")
                            }
                        }
                    )
                ) {
                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            Log.d("PhotoDoApp", "HomeFeedKey: Navigating to detail with ID string: '$id'")
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoDetailKey(id))
                            }
                        },
                        onEvent = photoDoListViewModel::onEvent,
                        viewModel = photoDoListViewModel
                    )
                }

                entry<PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { detailKey -> // 'detailKey' IS the PhotoDoDetailKey object
                    Log.d("PhotoDoApp", "Detail Entry: ID from detailKey.photoDoId: '${detailKey.photoDoId}'")
                    PhotoDoDetailUiRoute(
                        modifier = Modifier,
                    )
                }

                entry<PhotoListKey> {
                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            Log.d("PhotoDoApp", "PhotoListKey: Navigating to detail with ID string: '$id'")
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoDetailKey(id))
                            }
                        },
                        onEvent = photoDoListViewModel::onEvent,
                        viewModel = photoDoListViewModel
                    )
                }

                entry<SettingsKey> {
                    PhotoDoSettingsUiRoute(
                        modifier = Modifier
                    )
                }
            }
        )
    }
}