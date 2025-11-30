package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components.CameraScreen
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components.DetailCard

@Composable
fun PhotoDoDetailUiRoute(
    modifier: Modifier = Modifier,
    uiState: PhotoDoDetailUiState, // Receive the new hybrid state
    onEvent: (PhotoDoDetailEvent) -> Unit,
    onBackClick: () -> Unit
) {
    // Top-level check for the camera
    if (uiState.showCamera) {
        CameraScreen(
            onSavePhoto = { uri ->
                onEvent(PhotoDoDetailEvent.OnPhotoSaved(uri))
            },
            onBack = {
                onEvent(PhotoDoDetailEvent.OnBackFromCamera)
            }
        )
    } else {
        // If not showing camera, check the load state
        when (val loadState = uiState.loadState) {
            is DetailLoadState.Loading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DetailLoadState.Error -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = loadState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is DetailLoadState.Success -> {
                // We pass the raw TaskListWithPhotos object to the DetailCard
                DetailCard(
                    modifier = modifier,
                    taskListWithPhotos = loadState.taskListWithPhotos, // <-- Pass the object
                    onEvent = onEvent,
                    // --- THIS WAS THE BUG ---
                    // onBackClick = onBackClick, // <-- Removed this line, DetailCard doesn't need it
                    // --- END OF BUG FIX ---
                    /*onCameraClick = {
                        onEvent(PhotoDoDetailEvent.OnCameraClick)
                    },
                    onDeletePhotoClick = { photoId ->
                        onEvent(PhotoDoDetailEvent.OnDeletePhoto(photoId))
                    }*/
                )
            }
        }
    }
}