package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos

/**
 * This is the main detail card, refactored for a more expressive M3 look.
 * It NO LONGER CONTAINS A SCAFFOLD OR TOPAPPBAR.
 * It accepts the [TaskListWithPhotos] object directly.
 */
@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    taskListWithPhotos: TaskListWithPhotos,
    onCameraClick: () -> Unit,
    onDeletePhotoClick: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize() // Fill the available space given by the Scaffold
            .verticalScroll(rememberScrollState()) // Make the whole card scrollable
            // --- THIS IS THE FIX ---
            // Apply horizontal padding to the column.
            // The `modifier` already contains the TopAppBar padding.
            .padding(horizontal = 16.dp),
        // --- END OF FIX ---
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- THIS IS THE FIX ---
        // We add our own vertical padding inside the scrollable column
        // Spacer(Modifier.height(16.dp))
        // --- END OF FIX ---

        // --- Task Info Section ---
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = taskListWithPhotos.taskList.name,
                    style = MaterialTheme.typography.headlineMedium, // More expressive
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                if (taskListWithPhotos.taskList.notes?.isNotBlank() == true) {
                    Text(
                        text = taskListWithPhotos.taskList.notes!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(100.dp) // Give description a fixed height
                    )
                }
            }
        }

        Divider()

        // --- Photos Section ---
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Horizontal list of photos, starting with an "Add" button
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // 1. The "Add Photo" Card
            item {
                AddPhotoCard(
                    onClick = onCameraClick
                )
            }

            // 2. The list of photos
            items(taskListWithPhotos.photos, key = { it.photoId }) { photo ->
                PhotoItem(
                    photo = photo,
                    onDeleteClick = { onDeletePhotoClick(photo.photoId) }
                )
            }
        }

        // --- THIS IS THE FIX ---
        // Add bottom padding
        Spacer(Modifier.height(16.dp))
        // --- END OF FIX ---
    }
}

/**
 * An expressive card that invites the user to add a new photo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier
            .size(120.dp)
            .aspectRatio(1f), // Ensure it's square
        shape = MaterialTheme.shapes.large // Softer, more expressive corners
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Add Photo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

/**
 * Displays a single photo in a Card with a delete button.
 */
@Composable
fun PhotoItem(
    modifier: Modifier = Modifier,
    photo: PhotoEntity,
    onDeleteClick: () -> Unit
) {
    // Wrap the image in a Card for elevation and consistent styling
    Card(
        modifier = modifier
            .size(120.dp)
            .aspectRatio(1f), // Ensure it's square
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
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

            // Delete Button (stays the same, it's a good pattern)
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
        notes = "This is a sample description for the task. It could be quite long and wrap to multiple lines."
    )
    val sampleState = TaskListWithPhotos(
        taskList = sampleTaskList,
        photos = samplePhotos
    )

    // Note: This preview won't be in a Scaffold, so it will fill the whole screen.
    // This is correct as the Scaffold is handled by the calling route.
    DetailCard(
        modifier = Modifier.fillMaxSize(),
        taskListWithPhotos = sampleState,
        onCameraClick = {},
        onDeletePhotoClick = {}
    )
}