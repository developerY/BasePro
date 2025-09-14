package com.ylabz.basepro.applications.photodo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
// import androidx.compose.runtime.getValue // Removed to avoid conflict, using the specific Nav3 one below
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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


/**
 * A helper class to manage the back stack for each top-level destination in the bottom navigation bar.
 * This ensures that each tab maintains its own navigation history.
 */
class TopLevelBackStack<T : NavKey>(private val startKey: T) {

    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    val backStack = mutableStateListOf<T>(startKey)

    private fun updateBackStack() {
        backStack.clear()
        val currentStack = topLevelBackStacks[topLevelKey] ?: emptyList()

        if (topLevelKey == startKey) {
            backStack.addAll(currentStack)
        } else {
            val startStack = topLevelBackStacks[startKey] ?: emptyList()
            backStack.addAll(startStack + currentStack)
        }
    }

    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val currentStack = topLevelBackStacks[topLevelKey] ?: return

        if (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        } else if (topLevelKey != startKey) {
            topLevelKey = startKey
        }
        updateBackStack()
    }

    fun replaceLast(key: T) {
        val currentStack = topLevelBackStacks[topLevelKey]
        if (currentStack != null && currentStack.isNotEmpty()) {
            currentStack[currentStack.lastIndex] = key
            updateBackStack()
        }
    }
}

/**
 * The Bottom Navigation Bar composable, designed for Navigation 3.
 * It is stateless and driven by the [topLevelBackStack].
 */
@Composable
fun HomeBottomBarNav3( 
    topLevelBackStack: TopLevelBackStack<NavKey>,
    onNavigate: (NavKey) -> Unit
) {
    val bottomNavItems = listOf(
        HomeFeedKey,
        PhotoListKey,
        SettingsKey
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            val title = when (item) {
                is HomeFeedKey -> item.title
                is PhotoListKey -> item.title
                is SettingsKey -> item.title
                else -> "N/A"
            }
            val icon = when (item) {
                is HomeFeedKey -> item.icon
                is PhotoListKey -> item.icon
                is SettingsKey -> item.icon
                else -> Icons.Default.Home 
            }

            val selected = topLevelBackStack.topLevelKey == item
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
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
                        onItemClick = { id ->  // Corrected to onItemClick
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
                    //val navKey = backStackEntry.getValue<PhotoDoDetailKey>()
                    // Now calls your actual PhotoDoDetailUiRoute from its module
                    PhotoDoDetailUiRoute(
                        modifier = Modifier,
                        photoId = "the id"//navKey.photoDoId,
                    )
                }

                entry<PhotoListKey> {
                    PhotoDoListUiRoute(
                        modifier = Modifier, 
                        onItemClick = { id -> // Corrected to onItemClick
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
