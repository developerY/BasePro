package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.entries.DetailEntry
import com.ylabz.basepro.applications.photodo.ui.navigation.main.entries.HomeEntry
import com.ylabz.basepro.applications.photodo.ui.navigation.main.entries.ListEntry
import com.ylabz.basepro.applications.photodo.ui.navigation.main.entries.SettingsEntry

private const val TAG = "PhotoDoNavGraph"

/**
 * The core navigation graph for the PhotoDo application.
 *
 * This composable is responsible for defining all possible screens (destinations)
 * and handling the transitions between them using the NavDisplay component from Navigation 3.
 * It's designed to be adaptive, changing its layout based on screen size.
 *
 * @param modifier The modifier to be applied to the NavDisplay container.
 * @param backStack The mutable list of navigation keys representing the current navigation history.
 * @param sceneStrategy The adaptive strategy (e.g., ListDetailSceneStrategy) that determines
 * how list and detail panes are displayed on different screen sizes.
 * @param scrollBehavior A TopAppBarScrollBehavior to coordinate scrolling between the top app bar and the content.
 * @param setTopBar A lambda function to hoist the composable for the top app bar up to the parent Scaffold.
 * @param setFabState A lambda function to hoist the state of the Floating Action Button up to the parent Scaffold.
 * @param onCategorySelected A callback invoked when a user selects a category, allowing the parent to remember it.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotoDoNavGraph(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>,
    isExpandedScreen: Boolean, // <-- ADD THIS
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit,
    onCategorySelected: (Long) -> Unit, // Callback to update the remembered category ID
    onEvent: (MainScreenEvent) -> Unit,
    // REMOVE the updateCurrentTopLevelKey parameter, it's not needed here
    // updateCurrentTopLevelKey: (NavKey) -> Unit
) {
    // NAV_LOG: Log AppContent recomposition
    Log.d(TAG, "AppContent recomposing. Backstack size: ${backStack.size}")

    NavDisplay(
        backStack = backStack,
        onBack = {
            // --- NEW LOGGING ADDED HERE ---
            Log.d(TAG, "onBack invoked. Backstack count BEFORE action: ${backStack.size}")
            val currentStack = backStack.joinToString { it::class.simpleName ?: "Unknown" }
            Log.d(TAG, "Current backstack contents: [$currentStack]")
            // --- END OF NEW LOGGING ---

            // This custom logic ensures a more intuitive navigation experience.
            val currentKey = backStack.lastOrNull()

            // Special case: If we are at the root of a task list (e.g., navigated from the bottom bar),
            // pressing back should take us to the home feed, not exit the app.
            val isAtRootTaskList = currentKey is PhotoDoNavKeys.TaskListKey && backStack.size == 1
            Log.d(TAG, "onBack invoked. Current backstack: $currentKey")
            if (isAtRootTaskList) {
                Log.d(TAG, "Back action: At a root task list, replacing with HomeFeedKey.")

                // --- THIS IS THE FIX ---
                // Use clear() and add() to correctly reset the stack
                backStack.clear()
                backStack.add(PhotoDoNavKeys.HomeFeedKey)
                // --- END OF FIX ---

            } else {
                Log.d(TAG, "Back action: Performing default 'removeLastOrNull'.")
                backStack.removeLastOrNull()
            }

            // --- NEW LOGGING ADDED HERE ---
            Log.d(TAG, "Backstack count AFTER action: ${backStack.size}")
            if (backStack.isEmpty()) {
                Log.d(TAG, "Backstack is now empty. App will exit on next back press if not handled by system.")
            }
            // --- END OF NEW LOGGING ---

        },
        sceneStrategy = sceneStrategy,
        modifier = modifier,


        /**
         * Defines the complete navigation graph of the application.
         * The `entryProvider` block maps each unique `NavKey` to its corresponding
         * Composable content and adaptive layout metadata.
         */
        entryProvider = entryProvider {
            val isExpandedScreen = isExpandedScreen

            /**
             * =================================================================
             * Entry: Home Feed Screen (`PhotoDoNavKeys.HomeFeedKey`)
             * =================================================================
             * This is the primary "list" pane and the starting destination of the app.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.listPane`: Marks this as a list view. On large screens,
             * it appears as the first of three panes.
             * - `detailPlaceholder`: A placeholder shown in the detail area on large screens
             * when no item from this list has been selected yet. On smaller screens, this has no effect.
             */
            entry<PhotoDoNavKeys.HomeFeedKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                        { Text("Select a category") }
                    }
                ))
            {
                HomeEntry(
                    isExpandedScreen = isExpandedScreen, // <-- Use the correct parameter
                    backStack = backStack,
                    setTopBar = setTopBar,
                    setFabState = setFabState,
                    onCategorySelected = onCategorySelected,
                    onEvent = onEvent
                )
            }


            /**
             * =================================================================
             * Entry: Task List Screen (`PhotoDoNavKeys.TaskListKey`)
             * =================================================================
             * This is the second "list" pane, showing items within a selected category.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.listPane`: On compact screens, it replaces the home screen.
             * On expanded screens, it appears in the second pane.
             * - `detailPlaceholder`: A placeholder for the third pane.
             */
            entry<PhotoDoNavKeys.TaskListKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                        { Text("Select a list to see details") }
                    }
                ))
            { listKey ->
                ListEntry(
                    isExpandedScreen = isExpandedScreen,
                    listKey = listKey,
                    backStack = backStack,
                    scrollBehavior = scrollBehavior,
                    setTopBar = setTopBar,
                    setFabState = setFabState,
                    onCategorySelected = onCategorySelected,
                    onEvent = onEvent
                )
            }



            /**
             * =================================================================
             * Entry: Task Detail Screen (`PhotoDoNavKeys.TaskListDetailKey`)
             * =================================================================
             * This is the final "detail" pane, showing the contents of a single task list.
             *
             * Adaptive Behavior:
             * - `ListDetailSceneStrategy.detailPane`: Marks this as a detail view.
             * On compact screens, it covers the entire screen. On expanded screens,
             * it appears in the third pane on the right.
             */
            entry<PhotoDoNavKeys.TaskListDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                // Call the extracted composable, passing in the necessary state.
                Column() {
                    Text (" PhotoDoNavGraph.kt --- Fab needs to be set here")
                    DetailEntry(
                        modifier = Modifier, // <-- FIX #9: Remove the modifier to prevent double padding
                        isExpandedScreen = isExpandedScreen,
                        detailKey = detailKey,
                        backStack = backStack,
                        scrollBehavior = scrollBehavior,
                        setTopBar = setTopBar,
                        setFabState = setFabState
                    )
                }
            }

            /**
             * =================================================================
             * Entry: Settings Screen (`PhotoDoNavKeys.SettingsKey`)
             * =================================================================
             * A standard, full-screen destination that is not part of the list-detail flow.
             */
            entry<PhotoDoNavKeys.SettingsKey> {
                SettingsEntry(
                    modifier = Modifier, // <-- FIX #10: Remove the modifier
                    scrollBehavior = scrollBehavior,
                    setTopBar = setTopBar,
                    // setFabState = setFabState,
                )
            }
        }
    )
}

/*fun <T : Any> MutableList<T>.replace(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}*/
