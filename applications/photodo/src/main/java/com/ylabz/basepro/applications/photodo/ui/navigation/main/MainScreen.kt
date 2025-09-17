package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log // Added for logging
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
// import androidx.compose.material.icons.filled.DeleteSweep // No longer directly used here
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
// import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy // Not directly used in detail entry
// import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy // Not used currently
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Added import
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
// androidx.navigation3.runtime.getValue is not needed here as 'detailKey' is the NavKey itself
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailViewModel
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey) }
    // val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopBarForCurrentRoute(
                topLevelBackStack = topLevelBackStack,
                photoDoListViewModel = photoDoListViewModel,
                onNavigateBack = { topLevelBackStack.removeLastOrNull()} // Added onNavigateBack
            )
        },
        bottomBar = {
            HomeBottomBar(
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
            // sceneStrategy = listDetailStrategy,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryProvider = entryProvider {
                entry<PhotoDoNavKeys.HomeFeedKey>
                {
                    PhotoDoHomeUiRoute(navTo = { destinationKey ->
                        // Example of how PhotoDoHomeUiRoute might navigate to detail
                        // This part depends on how navTo is implemented in PhotoDoHomeUiRoute
                        if (destinationKey is PhotoDoNavKeys.PhotoDoDetailKey) {
                            Log.d("PhotoDoApp", "Home navigating to detail with ID: '${destinationKey.photoId}'")
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoNavKeys.PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(destinationKey)
                            } else {
                                topLevelBackStack.add(destinationKey)
                            }
                        } else {
                            // Handle other navigations from home if necessary
                            // For example, if HomeFeedKey itself can be a destination from home:
                            // if (destinationKey == PhotoDoNavKeys.HomeFeedKey) { /* ... */ }
                        }
                    })
                }

                entry<PhotoDoNavKeys.PhotoDoDetailKey>(
                    // metadata = ListDetailSceneStrategy.detailPane() // Example if using ListDetailSceneStrategy
                ) { detailKey -> // 'detailKey' IS the PhotoDoNavKeys.PhotoDoDetailKey object
                    Log.d("PhotoDoApp", "Detail Entry Scope: ID from detailKey.photoId: '${detailKey.photoId}'")
                    val detailViewModel: PhotoDoDetailViewModel = hiltViewModel()

                    // Call loadPhoto when detailKey.photoId is available or changes
                    LaunchedEffect(detailKey.photoId) {
                        Log.d("PhotoDoApp", "Detail LaunchedEffect: Calling loadPhoto with ID: '${detailKey.photoId}'")
                        detailViewModel.loadPhoto(detailKey.photoId)
                    }

                    PhotoDoDetailUiRoute(
                        modifier = Modifier.fillMaxSize(), // Typically fill an area
                        viewModel = detailViewModel
                    )
                }

                entry<PhotoDoNavKeys.PhotoDolListKey> {
                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            Log.d("PhotoDoApp", "PhotoListKey: Navigating to detail with ID string: '$id'")
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoNavKeys.PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoNavKeys.PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoNavKeys.PhotoDoDetailKey(id))
                            }
                        },
                        onEvent = photoDoListViewModel::onEvent,
                        viewModel = photoDoListViewModel
                    )
                }

                entry<PhotoDoNavKeys.SettingsKey> {
                    PhotoDoSettingsUiRoute(
                        modifier = Modifier
                    )
                }
            }
        )
    }
}
