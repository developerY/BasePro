package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar // New import
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior // New import
import androidx.compose.runtime.Composable
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListEvent
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.list.PhotoDoListViewModel
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarForCurrentRoute(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    photoDoListViewModel: PhotoDoListViewModel,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior // New parameter
) {
    val currentKey = topLevelBackStack.backStack.lastOrNull()

    when (currentKey) {
        is PhotoDoNavKeys.HomeFeedKey -> PhotoDoAppTopBar(
            title = "PhotoDo Home",
            showDeleteAll = true,
            photoDoListViewModel = photoDoListViewModel,
            scrollBehavior = scrollBehavior // Pass scrollBehavior
        )
        is PhotoDoNavKeys.PhotoDolListKey -> PhotoDoAppTopBar(
            title = "Photo List",
            showDeleteAll = true,
            photoDoListViewModel = photoDoListViewModel,
            scrollBehavior = scrollBehavior // Pass scrollBehavior
        )
        is PhotoDoNavKeys.PhotoDoDetailKey -> PhotoDoDetailTopBar(
            title = "Task Details",
            onNavigateBack = onNavigateBack,
            scrollBehavior = scrollBehavior // Pass scrollBehavior
        )
        is PhotoDoNavKeys.SettingsKey -> PhotoDoAppTopBar( // Settings could use a standard TopAppBar or LargeTopAppBar
            title = "Settings",
            showDeleteAll = false,
            photoDoListViewModel = photoDoListViewModel,
            scrollBehavior = scrollBehavior // Pass scrollBehavior
        )
        else -> PhotoDoAppTopBar(
            title = "PhotoDo",
            showDeleteAll = false,
            photoDoListViewModel = photoDoListViewModel,
            scrollBehavior = scrollBehavior // Pass scrollBehavior
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoAppTopBar(
    title: String,
    showDeleteAll: Boolean,
    photoDoListViewModel: PhotoDoListViewModel,
    scrollBehavior: TopAppBarScrollBehavior // New parameter
) {
    LargeTopAppBar( // Changed to LargeTopAppBar
        title = { Text(title) },
        actions = {
            if (showDeleteAll) {
                IconButton(onClick = {
                    photoDoListViewModel.onEvent(PhotoDoListEvent.OnDeleteAllTasksClicked)
                }) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Delete All Tasks")
                }
            }
        },
        scrollBehavior = scrollBehavior // Use scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoDoDetailTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior // New parameter
) {
    TopAppBar( // Stays as TopAppBar, but could be MediumTopAppBar
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior // Use scrollBehavior
    )
}
