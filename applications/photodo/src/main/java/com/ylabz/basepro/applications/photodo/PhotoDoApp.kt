package com.ylabz.basepro.applications.photodo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
import com.ylabz.basepro.applications.photodo.ui.navigation.HomeFeedKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoDetailKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoListKey
import com.ylabz.basepro.applications.photodo.ui.navigation.SettingsKey
import com.ylabz.basepro.applications.photodo.ui.navigation.main.HomeBottomBar

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
            val selected = topLevelBackStack.topLevelKey == item
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
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
    // The TopLevelBackStack manages the navigation history for each tab.
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(HomeFeedKey) }

    // This scene strategy automatically handles list-detail layouts on larger screens (like foldables).
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
            entryProvider = entryProvider { 
                // Define the screen for the Home tab
                entry<HomeFeedKey> {
                    PhotoDoHomeUiRoute(
                        modifier = Modifier, 
                        onNavigateToSettings = { 
                            topLevelBackStack.switchTopLevel(SettingsKey)
                        }
                    )
                }

                // Define the screen for the List tab, marking it as the "list" part of a list-detail view
                entry<PhotoListKey>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    PhotoDoListUiRoute(
                        modifier = Modifier, 
                        navToItemDetail = { id -> 
                            val last = topLevelBackStack.backStack.lastOrNull()
                            if (last is PhotoDoDetailKey) {
                                topLevelBackStack.replaceLast(PhotoDoDetailKey(id))
                            } else {
                                topLevelBackStack.add(PhotoDoDetailKey(id))
                            }
                        }
                    )
                }

                // Define the screen for the Detail view, marking it as the "detail" part
                entry<PhotoDoDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { backStackEntry ->
                    // val navKey = backStackEntry.getValue<PhotoDoDetailKey>() // Corrected to use getValue()
                    // Here you will create and call your detail screen.
                    // You will need to create this `PhotoDoDetailUiRoute` composable.
                    // Example: PhotoDoDetailUiRoute(photoId = navKey.photoDoId, modifier = Modifier)
                    Text("Detail Screen for ID:") // Updated to use navKey //  ${navKey.photoDoId}
                }

                // Define the screen for the Settings tab
                entry<SettingsKey> {
                    PhotoDoSettingsUiRoute(
                        modifier = Modifier 
                    )
                }
            }
        )
    }
}
