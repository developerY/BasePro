package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiState

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    state: PhotoDoDetailUiState.Success,
    ) {
    Column(modifier = modifier) {
        Text(text = "Task: ${state.taskListWithPhotos.taskList.name}")
        Text(text = "Status: ${state.taskListWithPhotos.taskList.status}")
        // TODO: Add more details and a list of photos here
    }
}