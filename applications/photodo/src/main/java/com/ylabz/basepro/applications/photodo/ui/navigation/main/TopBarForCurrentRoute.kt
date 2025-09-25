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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // ViewModel-specific hilt import
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarForCurrentRoute(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    // photoDoListViewModel: PhotoDoListViewModel, // REMOVED parameter
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val currentKey = topLevelBackStack.backStack.lastOrNull()

    when (currentKey) {
        is PhotoDoNavKeys.HomeFeedKey -> {
            val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
            PhotoDoAppTopBar(
                title = "PhotoDo Home",
                showDeleteAll = true,
                photoDoListViewModel = photoDoListViewModel,
                scrollBehavior = scrollBehavior
            )
        }
        is PhotoDoNavKeys.PhotoDolListKey -> {
            val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
            PhotoDoAppTopBar(
                title = "Photo List", // Consider dynamic title with project name: "Project: ${currentKey.projectId}"
                showDeleteAll = true,
                photoDoListViewModel = photoDoListViewModel,
                scrollBehavior = scrollBehavior
            )
        }
        is PhotoDoNavKeys.PhotoDoDetailKey -> PhotoDoDetailTopBar(
            title = "Task Details",
            onNavigateBack = onNavigateBack,
            scrollBehavior = scrollBehavior
        )
        is PhotoDoNavKeys.SettingsKey -> {
            // Even if showDeleteAll is false, PhotoDoAppTopBar expects the ViewModel.
            // Hilt will provide a ViewModel scoped to the SettingsKey destination.
            val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
            PhotoDoAppTopBar(
                title = "Settings",
                showDeleteAll = false,
                photoDoListViewModel = photoDoListViewModel,
                scrollBehavior = scrollBehavior
            )
        }
        else -> {
            val photoDoListViewModel: PhotoDoListViewModel = hiltViewModel()
            PhotoDoAppTopBar(
                title = "PhotoDo",
                showDeleteAll = false,
                photoDoListViewModel = photoDoListViewModel,
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
    photoDoListViewModel: PhotoDoListViewModel, // Stays non-null
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(title) },
        actions = {
            if (showDeleteAll) {
                IconButton(onClick = {
                    photoDoListViewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked) // Make sure this event is still relevant/handled
                }) {
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
