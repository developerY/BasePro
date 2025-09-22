package com.ylabz.basepro.applications.photodo.ui.navigation.main

// import androidx.compose.material.icons.filled.DeleteSweep // No longer directly used here
// import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy // Not directly used in detail entry
// import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy // Not used currently
// androidx.navigation3.runtime.getValue is not needed here as 'detailKey' is the NavKey itself
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
import androidx.compose.material3.TopAppBarDefaults // New import
import androidx.compose.material3.rememberTopAppBarState // New import
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll // New import
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
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
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()) // New

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // New
        topBar = {
            TopBarForCurrentRoute(
                topLevelBackStack = topLevelBackStack,
                photoDoListViewModel = photoDoListViewModel,
                onNavigateBack = { topLevelBackStack.removeLastOrNull() },
                scrollBehavior = scrollBehavior // New
            )
        },
        bottomBar = {
            HomeBottomBar(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { topLevelBackStack.switchTopLevel(it) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    photoDoListViewModel.onEvent(PhotoDoListEvent.OnAddTaskClicked)
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
            // The content of NavDisplay's entries will need to be scrollable
            // for the scroll behavior to work. This usually means ensuring
            // composables like PhotoDoHomeUiRoute, PhotoDoListUiRoute, etc.,
            // contain a LazyColumn or similar scrollable container.
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
                    PhotoDoHomeUiRoute(navTo = {})
                    /* PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            Log.d("PhotoDoApp", "HomeFeedKey: Navigating to detail with ID string: '$id'")
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoNavKeys.PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoNavKeys.PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoNavKeys.PhotoDoDetailKey(id))
                            }
                        },
                        onEvent = photoDoListViewModel::onEvent,
                        viewModel = photoDoListViewModel
                    )*/
                    /*PhotoDoHomeUiRoute(navTo = { destinationKey ->
                        // Example of how PhotoDoHomeUiRoute might navigate to detail
                        // This part depends on how navTo is implemented in PhotoDoHomeUiRoute
                        if (destinationKey is PhotoDoNavKeys.PhotoDoDetailKey) {
                            Log.d(
                                "PhotoDoApp",
                                "Home navigating to detail with ID: '${destinationKey.photoId}'"
                            )
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
                    })*/
                }

                entry<PhotoDoNavKeys.PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane() // Example if using ListDetailSceneStrategy
                ) { detailKey -> // 'detailKey' IS the PhotoDoNavKeys.PhotoDoDetailKey object
                    Log.d(
                        "PhotoDoApp",
                        "Detail Entry Scope: ID from detailKey.photoId: '${detailKey.photoId}'"
                    )
                    val detailViewModel: PhotoDoDetailViewModel = hiltViewModel()

                    // Call loadPhoto when detailKey.photoId is available or changes
                    LaunchedEffect(detailKey.photoId) {
                        Log.d(
                            "PhotoDoApp",
                            "Detail LaunchedEffect: Calling loadPhoto with ID: '${detailKey.photoId}'"
                        )
                        detailViewModel.loadPhoto(detailKey.photoId)
                    }

                    PhotoDoDetailUiRoute(
                        modifier = Modifier.fillMaxSize(), // Typically fill an area
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
                {
                    PhotoDoListUiRoute(
                        modifier = Modifier,
                        onItemClick = { id ->
                            Log.d(
                                "PhotoDoApp",
                                "PhotoListKey: Navigating to detail with ID string: '$id'"
                            )
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
