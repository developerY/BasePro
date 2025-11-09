package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
// This is the import you correctly identified we would need
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos

/**
 * This is the main detail card, simplified to be read-only.
 * It NO LONGER CONTAINS A SCAFFOLD OR TOPAPPBAR.
 * It now accepts the [TaskListWithPhotos] object directly.
 */
@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    taskListWithPhotos: TaskListWithPhotos, // <-- Accepts the database model object
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDeletePhotoClick: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Task Title - Read directly from the object
        Text(
            text = taskListWithPhotos.taskList.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Task Description - Read directly from the object
        Text(
            text = taskListWithPhotos.taskList.notes ?: "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        // Photos Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Photos",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Horizontal list of photos
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Read photos directly from the object
            items(taskListWithPhotos.photos, key = { it.photoId }) { photo ->
                PhotoItem(
                    photo = photo,
                    onDeleteClick = { onDeletePhotoClick(photo.photoId) }
                )
            }
        }
    }
}

@Composable
fun PhotoItem(
    modifier: Modifier = Modifier,
    photo: PhotoEntity,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo.uri))
                .crossfade(true)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = photo.caption ?: "Task photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Photo",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailCardPreview() {
    val samplePhotos = listOf(
        PhotoEntity(photoId = 1, listId = 1, uri = ""),
        PhotoEntity(photoId = 2, listId = 1, uri = ""),
        PhotoEntity(photoId = 3, listId = 1, uri = "")
    )
    val sampleTaskList = TaskListEntity(
        listId = 1,
        categoryId = 1,
        name = "Sample Task Title",
        notes = "This is a sample description for the task."
    )
    val sampleState = TaskListWithPhotos(
        taskList = sampleTaskList,
        photos = samplePhotos
    )

    DetailCard(
        taskListWithPhotos = sampleState,
        onBackClick = {},
        onCameraClick = {},
        onDeletePhotoClick = {}
    )
}