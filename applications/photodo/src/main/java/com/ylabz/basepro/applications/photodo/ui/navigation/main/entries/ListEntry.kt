package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/ListEntry.kt

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.core.ui.FabAction
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

private const val TAG = "ListEntry"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListEntry(
    isExpandedScreen: Boolean,
    listKey: PhotoDoNavKeys.TaskListKey,
    backStack: NavBackStack<NavKey>,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabStateMenu?) -> Unit,
    onCategorySelected: (Long) -> Unit
) {
    // NAV_LOG: Log rendering of TaskListKey entry
    Log.d(TAG, "Displaying content for TaskListKey (categoryId=${listKey.categoryId})")
    val viewModel: PhotoDoListViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    LaunchedEffect(listKey.categoryId) {
        Log.d(TAG, "TaskListKey LaunchedEffect triggered. Loading category with id: ${listKey.categoryId}")
        onCategorySelected(listKey.categoryId) // Also update when loading a list directly
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
                title = { Text("Task Lists with (+)") },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { viewModel.onEvent(PhotoDoListEvent.OnDeleteAllTaskListsClicked) }) {
                        Icon(
                            Icons.Filled.DeleteSweep,
                            contentDescription = "Delete All Lists"
                        )
                    }
                }
            )
        }
        //setFabState(FabState("Add List") { viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked) })
        // THIS IS THE NEW SPLIT FAB LOGIC
        /*setFabState(
            FabStateMenu.Single(
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
        )*/
    }

    LaunchedEffect(backStack.size, isExpandedScreen) {
        val isDetailVisible = backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey

        if (isExpandedScreen) {
            // On tablets, the Tasks tab always offers to add a List or an Item
            setFabState(
                FabStateMenu.Menu(
                    mainButtonAction = FabAction(
                        "Add",
                        Icons.Default.Add,
                        onClick = { Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen") }
                    ),
                    items = listOfNotNull(
                        FabAction(
                            "List",
                            Icons.AutoMirrored.Filled.NoteAdd
                        ) {
                            Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen")
                            /*listViewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)*/
                        },
                        if (isDetailVisible) FabAction(
                            "Item",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add Item from Global FAB Clicked -- Closed Screen")
                            /*detailViewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked)*/
                        } else null
                    )
                ))
        } else {
            // On phones, show the most specific action
            if (isDetailVisible) {
                setFabState(
                    FabStateMenu.Single(
                        FabAction(
                            "Add Item",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add Item from Global FAB Clicked -- Closed Screen")
                            /*detailViewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked)*/
                        })
                )
            } else {
                setFabState(
                    FabStateMenu.Single(
                        FabAction(
                            "Add List",
                            Icons.Default.Add
                        ) {
                            Log.d(TAG, "Add List from Global FAB Clicked -- Closed Screen")
                            /*listViewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)*/
                        })
                )
            }
        }
    }

    PhotoDoListUiRoute(
        onTaskClick = { listId ->
            // NAV_LOG: Log navigation from TaskList to Detail
            Log.d(TAG, "TaskList onTaskClick triggered. ListId: $listId")
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
            Log.d(TAG, " -> Calling backStack.add with TaskListDetailKey($listId)")
            backStack.add(detailKey)
        },
        /*onTaskClick = { listId ->
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
            backStack.add(detailKey)
        },*/
        onEvent = viewModel::onEvent,
        viewModel = viewModel
    )
}