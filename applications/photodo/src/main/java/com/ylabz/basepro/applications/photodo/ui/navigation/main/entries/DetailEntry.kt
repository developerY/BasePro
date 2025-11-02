package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.core.ui.FabAction
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent
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
    detailKey: PhotoDoNavKeys.TaskListDetailKey,
    backStack: NavBackStack<NavKey>,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabStateMenu?) -> Unit
) {
// NAV_LOG: Log rendering of TaskListDetailKey entry
    Log.d(TAG, "Displaying content for TaskListDetailKey (listId=${detailKey.listId})")
    val viewModel: PhotoDoDetailViewModel = hiltViewModel()
    LaunchedEffect(detailKey.listId) {
        Log.d(
            TAG,
            "TaskListDetailKey LaunchedEffect triggered. Loading list with id: ${detailKey.listId}"
        )
        viewModel.loadList(detailKey.listId)
    }

    // This effect now correctly sets the FAB to "Add Item" instead of null.
    LaunchedEffect(Unit) {
        setTopBar {
            TopAppBar(
                title = { Text("List Details from DetailEntry.kt") },
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

        // ### WHY & WHAT ###
        // This sets the FAB state when the detail screen is visible.
        // The text is "Add Item" and the action calls the new onEvent
        // function in the PhotoDoDetailViewModel. This fixes the bug where
        // the FAB would disappear on this screen.

        // The FAB is a single "Add Item" button on this screen.
        // ** FAB LOGIC UPDATED **
        setFabState(
            FabStateMenu.Menu(
                mainButtonAction = FabAction(
                    text = "DetailEntry.kt",
                    icon = Icons.Default.Add,
                    onClick = {
                        Log.d(TAG, "Main button clicked in DetailEntry")
                    }
                ),
                items = listOf(
                    FabAction(
                        text = "Item from DetailEntry.kt",
                        icon = Icons.Default.Add,
                        onClick = {
                            Log.d(TAG, "Item button clicked in DetailEntry")
                            viewModel.onEvent(PhotoDoDetailEvent.OnAddPhotoClicked) }
                    )
                )
            )
        )
    }
    Box(modifier = Modifier.fillMaxSize().background(Color.Magenta)) {
        Column {
            Text("Source: DetailEntry.kt")
            PhotoDoDetailUiRoute(viewModel = viewModel)
        }
    }

}