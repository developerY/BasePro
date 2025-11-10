package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ylabz.basepro.applications.photodo.core.ui.FabStateOrig
// Import the new sealed interface
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.DetailLoadState
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiRoute
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys


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
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabStateOrig?) -> Unit
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
                }
            )
        }
        // No global FAB for this screen
        setFabState(null)
    }

    // Pass the full state down to the route
    PhotoDoDetailUiRoute(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = { backStack.removeLastOrNull() }
    )
}