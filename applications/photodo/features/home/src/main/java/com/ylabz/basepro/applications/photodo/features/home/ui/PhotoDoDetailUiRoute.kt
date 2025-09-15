package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PhotoDoDetailUiRoute(
    modifier: Modifier = Modifier,
    photoDoIdForcingRecomposition: String, // Added this parameter
    viewModel: PhotoDoDetailViewModel = hiltViewModel() // ViewModel is now correctly fetched here
) {
    // It's good practice to also pass the key or relevant parts of it to the ViewModel
    // if it needs to re-trigger loading based on this specific ID, especially if the
    // ViewModel instance might be reused or if init{} logic isn't re-run reliably.
    // However, SavedStateHandle *should* get the latest arguments if hiltViewModel()
    // is re-evaluated due to the route's key changing.

    // We can use LaunchedEffect to explicitly tell the ViewModel to load data for the new ID
    // when photoDoIdForcingRecomposition changes. This is more robust if the ViewModel
    // instance is reused across navigations to the same route type.
    LaunchedEffect(photoDoIdForcingRecomposition) {
        // This approach requires the ViewModel to have a method to load/reload data for an ID.
        // Assuming your ViewModel's init {} block handles loading via SavedStateHandle,
        // and if hiltViewModel() gives a fresh ViewModel when the NavBackStackEntry changes,
        // this LaunchedEffect might primarily be for cases where the same ViewModel instance is kept.
        // For now, we rely on the init{} block and SavedStateHandle being correct upon ViewModel creation.
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoDetailUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoDetailUiState.Success -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(text = "Details for Task: ${state.task.name}")
                    Text(text = "Task ID from Route Param: $photoDoIdForcingRecomposition") // Display the passed ID
                    Text(text = "Task ID from State: ${state.task.id}") // Display ID from loaded task
                    // This is where you will build the UI to show the photos
                }
            }
        }
        is PhotoDoDetailUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}
