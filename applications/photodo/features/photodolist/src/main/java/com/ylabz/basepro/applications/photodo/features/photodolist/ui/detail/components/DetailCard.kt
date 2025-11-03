package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailUiState

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    state: PhotoDoDetailUiState.Success,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section for Task Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.List,
                    contentDescription = "Task Name",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = state.taskListWithPhotos.taskList.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Section for Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Task Status",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Status: ${state.taskListWithPhotos.taskList.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            // Section for Photos
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium
            )
            
            // A simple horizontal list for photos.
            // Replace the placeholder with your actual image loading component.
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(5) { // Placeholder for 5 photos
                    PhotoPlaceholder()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailCardPreview() {
    val sampleTaskList = TaskListEntity(
        listId = 1,
        categoryId = 1,
        name = "Sample Task",
        status = "In Progress"
    )
    val samplePhotos = listOf(
        PhotoEntity(photoId = 1, listId = 1, uri = ""),
        PhotoEntity(photoId = 2, listId = 1, uri = ""),
        PhotoEntity(photoId = 3, listId = 1, uri = "")
    )
    val sampleState = PhotoDoDetailUiState.Success(
        taskListWithPhotos = TaskListWithPhotos(
            taskList = sampleTaskList,
            photos = samplePhotos
        )
    )
    DetailCard(state = sampleState)
}

@Preview(showBackground = true)
@Composable
fun PhotoPlaceholderPreview() {
    PhotoPlaceholder()
}

@Composable
fun PhotoPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Photo,
            contentDescription = "Photo Placeholder",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp)
        )
    }
}

