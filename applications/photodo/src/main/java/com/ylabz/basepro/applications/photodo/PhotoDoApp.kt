package com.ylabz.basepro.applications.photodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
// import androidx.compose.material.icons.Icons // Will be in HomeBottomBarNav3.kt
// import androidx.compose.material.icons.filled.Home // Will be in HomeBottomBarNav3.kt
// import androidx.compose.material3.Icon // Will be in HomeBottomBarNav3.kt
// import androidx.compose.material3.NavigationBar // Will be in HomeBottomBarNav3.kt
// import androidx.compose.material3.NavigationBarItem // Will be in HomeBottomBarNav3.kt
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text // Keep for placeholder in NavDisplay
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
// import androidx.compose.runtime.setValue // Not directly used in this file after refactor
// import androidx.compose.runtime.mutableStateListOf // Will be in TopLevelBackStack.kt
// import androidx.compose.runtime.mutableStateOf // Will be in TopLevelBackStack.kt
// import androidx.compose.runtime.snapshots.SnapshotStateList // Will be in TopLevelBackStack.kt
import androidx.compose.ui.Alignment // Keep for placeholder in NavDisplay
import androidx.compose.ui.Modifier
// import androidx.compose.ui.graphics.vector.ImageVector // Will be in HomeBottomBarNav3.kt & NavKeys.kt
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry // For entry<T>() function
import androidx.navigation3.runtime.entryProvider // For entryProvider builder
import androidx.navigation3.ui.NavDisplay
// Import for your actual PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoDetailUiRoute 
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.HomeFeedKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoDetailKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoListKey
import com.ylabz.basepro.applications.photodo.ui.navigation.SettingsKey
// Import for the moved TopLevelBackStack
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack
// Placeholder import for HomeBottomBarNav3, will be created next
import com.ylabz.basepro.applications.photodo.ui.navigation.components.HomeBottomBarNav3

// TopLevelBackStack class definition REMOVED

// HomeBottomBarNav3 composable definition REMOVED

/**
 * The main entry point for the PhotoDo application's UI.
 * This composable sets up the adaptive navigation, bottom bar, and the content area.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PhotoDoApp() {
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(HomeFeedKey) }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    Scaffold(
        bottomBar = {
            HomeBottomBarNav3(
                topLevelBackStack = topLevelBackStack,
                onNavigate = { topLevelBackStack.switchTopLevel(it) }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            sceneStrategy = listDetailStrategy,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryProvider = entryProvider { // Explicitly use the entryProvider builder
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
                        }
                    )
                }

                entry<PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { backStackEntry ->
                    // val navKey = backStackEntry.getValue<PhotoDoDetailKey>()
                    PhotoDoDetailUiRoute( 
                        modifier = Modifier,
                        photoId = "0" //navKey.photoDoId
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
                        }
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
