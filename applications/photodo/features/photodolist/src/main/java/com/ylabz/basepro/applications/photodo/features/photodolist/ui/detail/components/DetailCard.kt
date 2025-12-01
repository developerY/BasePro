package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    taskListWithPhotos: TaskListWithPhotos, // UI State for this component
    onEvent: (PhotoDoDetailEvent) -> Unit,  // Single Event Handler
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. HEADER INFO ---
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = taskListWithPhotos.taskList.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!taskListWithPhotos.taskList.notes.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = taskListWithPhotos.taskList.notes!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- 2. CHECKLIST ---
        Text(
            text = "Checklist",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (taskListWithPhotos.items.isEmpty()) {
                    Text(
                        text = "No items added.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    taskListWithPhotos.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { isChecked ->
                                    // Trigger Event
                                    onEvent(PhotoDoDetailEvent.OnItemCheckedChange(item, isChecked))
                                }
                            )
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )

                            // --- ADDED DELETE BUTTON ---
                            IconButton(
                                onClick = { onEvent(PhotoDoDetailEvent.OnDeleteItem(item)) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Item",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                            // ---------------------------


                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }

                IconButton(
                    onClick = { onEvent(PhotoDoDetailEvent.OnAddItemClicked) }, // Trigger Event
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Add, "Add Item")
                }
            }
        }

        // --- 3. PHOTOS ---
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            item {
                AddPhotoCard(onClick = { onEvent(PhotoDoDetailEvent.OnCameraClick) }) // Trigger Event
            }
            items(taskListWithPhotos.photos, key = { it.photoId }) { photo ->
                PhotoItem(
                    photo = photo,
                    onDeleteClick = {
                        // onEvent(PhotoDoDetailEvent.OnDeletePhotoClicked(photo.photoId)) // Trigger Event
                    }
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}