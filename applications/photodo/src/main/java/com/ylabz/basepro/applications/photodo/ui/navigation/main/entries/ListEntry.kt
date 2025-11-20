package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/ListEntry.kt

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabAction
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreenEvent

private const val TAG = "ListEntry"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListEntry(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    listKey: PhotoDoNavKeys.TaskListKey,
    backStack: NavBackStack<NavKey>,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onEvent: (MainScreenEvent) -> Unit,
) {
    // NAV_LOG: Log rendering of TaskListKey entry
    Log.d(TAG, "Displaying content for TaskListKey (categoryId=${listKey.categoryId})")

    val viewModel: PhotoDoListViewModel = hiltViewModel()

    LaunchedEffect(listKey.categoryId) {
        Log.d(TAG, "TaskListKey LaunchedEffect triggered. Loading category with id: ${listKey.categoryId}")
        onCategorySelected(listKey.categoryId) // Also update when loading a list directly -- Inform the parent
        viewModel.loadCategory(listKey.categoryId)
    }

    /*LaunchedEffect(listKey.categoryId) {
        onCategorySelected(listKey.categoryId);
        listViewModel.loadCategory(listKey.categoryId)
    }*/


    // ### WHY & WHAT ###
    // Same reason as above. We wrap these state updates in a LaunchedEffect
    // to prevent the infinite recomposition loop when this screen is shown.
    LaunchedEffect(Unit) {
        setTopBar {
            LargeTopAppBar(
                title = { Text("Task Lists from ListEntry.kt") },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(PhotoDoListEvent.OnDeleteAllTaskListsClicked) }) {
                        Icon(
                            Icons.Filled.DeleteSweep,
                            contentDescription = "Delete All Lists from ListEntry.kt"
                        )
                    }
                }
            )
        }
        //setFabState(FabState("Add List") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) })
        // THIS IS THE NEW SPLIT FAB LOGIC
        setFabState(
            FabState.Single(
                action = FabAction(
                    text = "Add List",
                    icon = Icons.Default.Add,
                    onClick = { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) }
                )
            )

            /*FabState.Split(
                primaryText = "Add Item",
                primaryIcon = Icons.Default.Add, // Or a more specific item icon
                primaryOnClick = { /* TODO: Add item to a default/selected list */ },
                secondaryText = "Add List",
                secondaryIcon = Icons.Default.Add, // Or a more specific list icon
                secondaryOnClick = { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) }
            )*/
        )
    }

// ** FAB LOGIC UPDATED **
LaunchedEffect(backStack.lastOrNull()) {
        val isDetailVisible = backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey

        setFabState(
            FabState.Menu(
                mainButtonAction = FabAction(
                    text = "ListEntry.kt", // Icon-only FAB
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = {}
                ),
                items = listOfNotNull(
                    FabAction(
                        "List from ListEntry.kt",
                        Icons.AutoMirrored.Filled.NoteAdd
                    ) {
                        viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)
                    },
                    if (isDetailVisible) FabAction(
                        "Item from ListEntry.kt",
                        Icons.Default.Add
                    ) {
                        Log.d(TAG, "Add Item from FAB clicked (requires Detail ViewModel)")
                    } else null
                )
            )
        )
    }
 /*
    LaunchedEffect(Unit) {
        setTopBar {
            TopAppBar(
                title = { Text("Category Lists") }, // You can make this dynamic
                scrollBehavior = it,
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
        setFabState(
            FabState.Menu(
                mainButtonAction = FabAction(
                    text = "Add",
                    icon = Icons.Default.Add,
                    onClick = { } // Main button only toggles
                ),
                items = listOf(
                    FabAction(
                        text = "Add Item",
                        icon = Icons.Default.Add,
                        onClick = { onEvent(MainScreenEvent.OnAddItemClicked) }
                    ),
                    FabAction(
                        text = "Add List",
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = { onEvent(MainScreenEvent.OnAddListClicked) }
                    )
                )
            )
        )
    }*/

    // UI
    Column {
        Text("Source: ListEntry.kt")
        PhotoDoListUiRoute(
            onTaskClick = { listId ->
                // NAV_LOG: Log navigation from TaskList to Detail
                Log.d(TAG, "TaskList onTaskClick triggered. ListId: $listId")

                // --- THIS IS THE FIX ---
                // We must pass the listId as a Long, not a String
                // 1. Create the key
                val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId = listId.toString())
                Log.d(TAG, " -> Calling backStack.add with TaskListDetailKey($listId)")

                // --- THIS IS THE FIX ---
                // If the current top element is *any* Detail Key, remove it (i.e., replace it).
                if (backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey) {
                    Log.d(TAG, "Replacing existing Detail Key with new key: $listId")
                    backStack.removeLastOrNull()
                }
                Log.d(TAG, " -> Calling backStack.add with TaskListDetailKey($listId)")
                backStack.add(detailKey)

                // --- END FIX ---
            },
            /*onTaskClick = { listId ->
                val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
                backStack.add(detailKey)
            },*/
            onEvent = viewModel::onEvent,
            viewModel = viewModel
        )
    }



}