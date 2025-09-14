package com.ylabz.basepro.applications.photodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PhotoDoApp() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    // Get an instance of the ViewModel here
    val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            HomeBottomBarNav3(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { topLevelBackStack.switchTopLevel(it) }
            )
        },
        // Add the FloatingActionButton to the Scaffold
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Send the event to the ViewModel when the FAB is clicked
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
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoDetailKey(id))
                            }
                        },
                        // Pass the viewModel instance to the route
                        viewModel = photoDoListViewModel
                    )
                }

                entry<PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { detailKey ->
                    PhotoDoDetailUiRoute(
                        modifier = Modifier,
                        photoId = detailKey.photoDoId
                    )
                }

                entry<PhotoListKey> {
                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoDetailKey(id))
                            }
                        },
                        // Pass the same viewModel instance here as well
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