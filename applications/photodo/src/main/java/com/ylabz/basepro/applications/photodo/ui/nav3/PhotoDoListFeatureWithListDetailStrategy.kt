package com.ylabz.basepro.applications.photodo.ui.nav3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.hilt.navigation.compose.hiltViewModel // If ViewModels are needed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListUiRoute // <--- IMPORT REVERTED
// import com.ylabz.basepro.applications.photodo.features.photodolist.ui.PhotoDoListViewModel // Assuming ViewModel name

// Placeholder for the detail screen Composable - we will define this properly later
@Composable
fun PhotoDoItemDetailScreen(itemId: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Detail Screen for Item: $itemId")
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PhotoDoListFeatureWithListDetailStrategy(modifier: Modifier = Modifier) {
    val listDetailBackStack = rememberNavBackStack<NavKey>(PhotoDoListContentKey)
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = listDetailBackStack,
        modifier = modifier.fillMaxSize(),
        onBack = { keysToRemove ->
            repeat(keysToRemove) {
                if (listDetailBackStack.size > 0) { // Should be > 0 or > 1 depending on desired behavior for first item
                    listDetailBackStack.removeLastOrNull()
                }
            }
        },
        sceneStrategy = listDetailSceneStrategy,
        entryProvider = entryProvider {
            entry<PhotoDoListContentKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        // Placeholder shown when no detail is selected or when list and detail are shown together
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select an item to see details")
                        }
                    }
                )
            ) {
                // val listViewModel = hiltViewModel<PhotoDoListViewModel>() // If needed
                PhotoDoListUiRoute( // <--- FUNCTION CALL REVERTED
                    modifier = Modifier, // <--- EXPLICITLY PASS Modifier
                    // viewModel = listViewModel, // Pass ViewModel if needed
                    onItemClick = { itemId -> // <--- PARAMETER REVERTED
                        listDetailBackStack.add(PhotoDoItemDetailKey(itemId = itemId))
                    }
                )
            }
            entry<PhotoDoItemDetailKey> { navKey ->
                // val detailViewModel = hiltViewModel<PhotoDoItemDetailViewModel>(navKey) // Example with Hilt and NavKey args
                PhotoDoItemDetailScreen(itemId = navKey.itemId)
            }
        }
    )
}
