package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Use AutoMirrored for LTR/RTL
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
// import androidx.compose.runtime.State // No longer needed for currentKey
// import androidx.compose.runtime.collectAsState // No longer needed for currentKey
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys // Assuming your NavKeys are here
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack // For current key access
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarForCurrentRoute(
    topLevelBackStack: TopLevelBackStack<NavKey>, // To get the current key
    photoDoListViewModel: PhotoDoListViewModel,  // For actions
    onNavigateBack: () -> Unit                   // For back navigation from detail
) {
    // topLevelBackStack.backStack is a SnapshotStateList, which is directly observable by Compose.
    // We can get the last element directly. Recomposition will occur if the list changes.
    val currentKey = topLevelBackStack.backStack.lastOrNull()

    when (currentKey) {
        is PhotoDoNavKeys.HomeFeedKey -> PhotoDoAppTopBar(
            title = "PhotoDo Home",
            showDeleteAll = true,
            photoDoListViewModel = photoDoListViewModel
        )
        is PhotoDoNavKeys.PhotoListKey -> PhotoDoAppTopBar(
            title = "Photo List",
            showDeleteAll = true,
            photoDoListViewModel = photoDoListViewModel
        )
        is PhotoDoNavKeys.PhotoDoDetailKey -> PhotoDoDetailTopBar(
            title = "Task Details", // You might want to pass the actual task name/ID here later
            onNavigateBack = onNavigateBack
        )
        is PhotoDoNavKeys.SettingsKey -> PhotoDoAppTopBar(
            title = "Settings",
            showDeleteAll = false, // No delete all on settings screen
            photoDoListViewModel = photoDoListViewModel // Not strictly needed if showDeleteAll is false
        )
        else -> PhotoDoAppTopBar(
            title = "PhotoDo", // Default title
            showDeleteAll = false,
            photoDoListViewModel = photoDoListViewModel
        ) // Fallback, though ideally all top-level keys should be handled
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoAppTopBar(
    title: String,
    showDeleteAll: Boolean,
    photoDoListViewModel: PhotoDoListViewModel // Needed for the action
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            if (showDeleteAll) {
                IconButton(onClick = {
                    photoDoListViewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked)
                }) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Tasks")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoDetailTopBar(
    title: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}
