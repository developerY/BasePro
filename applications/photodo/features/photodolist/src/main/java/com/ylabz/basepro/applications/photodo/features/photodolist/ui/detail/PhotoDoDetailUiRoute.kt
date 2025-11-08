package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components.CameraScreen
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components.DetailCard

@Composable
fun PhotoDoDetailUiRoute(
    modifier: Modifier = Modifier,
    viewModel: PhotoDoDetailViewModel = hiltViewModel(),
    // We will handle navigation from the NavGraph, so onBack is not needed here
    // onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    // --- ADDED STATE ---
    // State to control whether the camera UI is shown
    var showCamera by remember { mutableStateOf(false) }

    // --- ADDED LOGIC ---
    if (showCamera) {
        CameraScreen(
            modifier = modifier,
            onSavePhoto = { uri ->
                // Send the event to the ViewModel
                viewModel.onEvent(PhotoDoDetailEvent.AddPhoto(uri.toString()))
                // Hide the camera
                showCamera = false
            },
            onBack = {
                // Hide the camera
                showCamera = false
            }
        )
    } else {
        // --- This is your existing logic ---
        when (uiState) {
            is PhotoDoDetailUiState.Loading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PhotoDoDetailUiState.Error -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.message}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is PhotoDoDetailUiState.Success -> {
                DetailCard(
                    modifier = modifier,
                    state = uiState,
                    onCameraClick = {
                        // --- ADDED LOGIC ---
                        // Show the camera when the button is clicked
                        showCamera = true
                    }
                )
            }
        }
    }
}