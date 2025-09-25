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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarForCurrentRoute(
                topLevelBackStack = topLevelBackStack,
                onNavigateBack = { topLevelBackStack.removeLastOrNull() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            HomeBottomBar(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { key ->
                    topLevelBackStack.switchTopLevel(key)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Implement add task functionality */ },
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
                            { Text("Select a category") }
                        })
                )
                {
                    PhotoDoHomeUiRoute(
                        navTo = { projectId ->
                            //topLevelBackStack.add(PhotoDoNavKeys.PhotoDolListKey(projectId))
                        }
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
                        onTaskClick = { taskId ->
                            Log.d("PhotoDoApp", "Navigating to detail with ID: '$taskId'")
                            topLevelBackStack.add(PhotoDoNavKeys.PhotoDoDetailKey(taskId.toString()))
                        },
                        onEvent = {},
                    )
                }

                entry<PhotoDoNavKeys.PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    PhotoDoDetailUiRoute()
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
