package com.ylabz.basepro.feature.camera.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.feature.camera.ui.components.SimpleCameraCaptureWithImagePreview

@Composable
fun CameraUIRoute(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: CamViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is CamUIState.Loading -> {
            LoadingScreen()
        }

        is CamUIState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(CamEvent.OnRetry)
            }
        }

        is CamUIState.Success -> {
            Column(modifier = modifier) {
                SimpleCameraCaptureWithImagePreview(
                    //modifier = modifier,
                    //data = uiState.data,
                    paddingValues = paddingValues,
                    onEvent = { event -> viewModel.onEvent(event) },
                    navTo = navTo
                )
            }
        }
    }
}

// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}