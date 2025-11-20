package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

// Import the new sealed interface
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Details
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.DetailLoadState
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabAction
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState


private const val TAG = "DetailEntry"
/**
 * The composable content for the PhotoDoNavKeys.TaskListDetailKey navigation entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEntry(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    detailKey: PhotoDoNavKeys.TaskListDetailKey,
    backStack: NavBackStack<NavKey>,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    setFabState: (FabState?) -> Unit
) {
    Log.d(TAG, "Displaying content for TaskListDetailKey (listId=${detailKey.listId})")

    val viewModel: PhotoDoDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // This LaunchedEffect triggers the ViewModel to load the data.
    // This is safe to call in both single-pane and two-pane modes.
    LaunchedEffect(detailKey.listId) {
        Log.d(TAG, "DetailEntry LaunchedEffect: Loading list ${detailKey.listId}")
        viewModel.loadTaskDetails(detailKey.listId.toLong())
    }

    // Set the TopBar and FAB state
    LaunchedEffect(uiState, scrollBehavior) {
        setTopBar {
            TopAppBar(
                // Set the title based on the new load state
                title = {
                    val title = when (val loadState = uiState.loadState) {
                        is DetailLoadState.Success -> loadState.taskListWithPhotos.taskList.name
                        is DetailLoadState.Error -> "Error"
                        DetailLoadState.Loading -> "Loading..."
                    }
                    Text(title)
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                // --- ADDED: DELETE BUTTON ACTIONS SLOT ---
                actions = {
                    IconButton(onClick = {
                        // Trigger the delete event in the ViewModel
                        viewModel.onEvent(PhotoDoDetailEvent.OnDeleteTaskListClicked)
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Task List",
                            tint = MaterialTheme.colorScheme.error // Best practice for destructive actions
                        )
                    }
                }
                // --- END ADDED ---
            )
        }
        setFabState(
            FabState.Menu(
                mainButtonAction = FabAction(
                    text = "ListEntry.kt", // Icon-only FAB
                    icon = Icons.Default.Details,
                    onClick = {
                        Log.d(TAG, "Add from ListEntry.kt FAB clicked")
                    }
                ),
                items = listOfNotNull(
                    FabAction(
                        "Item from DetailEntry.kt",
                        Icons.Default.Add
                    ) {
                        Log.d(TAG, "Add Item from FAB clicked (requires Detail ViewModel)")
                    },
                    FabAction(
                            text = "Add Photo",
                    icon = Icons.Default.AddAPhoto,
                    onClick = { viewModel.onEvent(PhotoDoDetailEvent.OnCameraClick) }
                )
                    /*
                    FabAction(
                            "List from ListEntry.kt",
                            Icons.AutoMirrored.Filled.NoteAdd
                        ) {
                            //viewModel.onEvent(PhotoDoListEvent.OnAddTaskListClicked)
                        },
                    */
                )
            )
        )
        // No global FAB for this screen
        /*setFabState(
            FabState.Single(
                action = FabAction(
                    text = "Add List",
                    icon = Icons.Default.Add,
                    onClick = {  }
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

    // Pass the full state down to the route
    PhotoDoDetailUiRoute(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = { backStack.removeLastOrNull() }
    )
}