package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarForCurrentRoute(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val currentKey = topLevelBackStack.backStack.lastOrNull()

    when (currentKey) {
        is PhotoDoNavKeys.HomeFeedKey -> {
            // The ViewModel created here is scoped to the NavHost, which doesn't have a projectId.
            // We must provide one if the ViewModel is to be created here.
            // A better solution would be for the Home screen to have its own ViewModel.
            // For now, to prevent a crash, we can't create a PhotoDoListViewModel here if it expects a projectId.
            // Let's assume the delete action is not available on the generic home screen for now.
            PhotoDoAppTopBar(
                title = "PhotoDo Home",
                showDeleteAll = false, // Changed to false to avoid creating the ViewModel
                onDeleteAllClicked = {},
                scrollBehavior = scrollBehavior
            )
        }
        is PhotoDoNavKeys.PhotoDolListKey -> {
            // This is the correct place to get the ViewModel, as the NavKey has the projectId
            val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
            PhotoDoAppTopBar(
                title = "Photo List",
                showDeleteAll = true,
                onDeleteAllClicked = { photoDoListViewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked) },
                scrollBehavior = scrollBehavior
            )
        }
        is PhotoDoNavKeys.PhotoDoDetailKey -> PhotoDoDetailTopBar(
            title = "Task Details",
            onNavigateBack = onNavigateBack,
            scrollBehavior = scrollBehavior
        )
        is PhotoDoNavKeys.SettingsKey -> {
            PhotoDoAppTopBar(
                title = "Settings",
                showDeleteAll = false,
                onDeleteAllClicked = {},
                scrollBehavior = scrollBehavior
            )
        }
        else -> {
            PhotoDoAppTopBar(
                title = "PhotoDo",
                showDeleteAll = false,
                onDeleteAllClicked = {},
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoAppTopBar(
    title: String,
    showDeleteAll: Boolean,
    onDeleteAllClicked: () -> Unit, // Accept a lambda instead of the whole ViewModel
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(title) },
        actions = {
            if (showDeleteAll) {
                IconButton(onClick = onDeleteAllClicked) { // Use the passed lambda
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Tasks")
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoDetailTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}
