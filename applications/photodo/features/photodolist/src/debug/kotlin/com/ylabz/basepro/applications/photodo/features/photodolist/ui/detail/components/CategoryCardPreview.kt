package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskItemEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos

@Preview(showBackground = true, name = "Detail Card (Populated)")
@Composable
fun DetailCardPreview() {
    // 1. Mock Data
    val taskList = TaskListEntity(
        listId = 1L,
        categoryId = 1L,
        name = "Kitchen Renovation",
        notes = "Remember to check the measurements for the new cabinet.",
        status = "In Progress",
        priority = 1
    )

    val photos = listOf(
        PhotoEntity(photoId = 1L, listId = 1L, uri = "", caption = "Before"),
        PhotoEntity(photoId = 2L, listId = 1L, uri = "", caption = "After")
    )

    val items = listOf(
        TaskItemEntity(itemId = 1L, listId = 1L, text = "Buy paint", isChecked = true),
        TaskItemEntity(itemId = 2L, listId = 1L, text = "Sand the walls", isChecked = false),
        TaskItemEntity(itemId = 3L, listId = 1L, text = "Tape the trim", isChecked = false)
    )

    val sampleData = TaskListWithPhotos(
        taskList = taskList,
        photos = photos,
        items = items
    )

    // 2. Preview
    //PhotoDoTheme {
        DetailCard(
            modifier = Modifier.fillMaxSize(),
            taskListWithPhotos = sampleData,
            onEvent = { event -> }
        )
    //}
}

@Preview(showBackground = true, name = "Detail Card (Empty)")
@Composable
fun DetailCardEmptyPreview() {
    val taskList = TaskListEntity(
        listId = 2L,
        categoryId = 1L,
        name = "New Project",
        notes = null
    )

    val sampleData = TaskListWithPhotos(
        taskList = taskList,
        photos = emptyList(),
        items = emptyList()
    )

    //PhotoDoTheme {
        DetailCard(
            modifier = Modifier.fillMaxSize(),
            taskListWithPhotos = sampleData,
            onEvent = { event ->  } ,  // Single Event Handler
        )
    //}
}

@Preview(showBackground = true)
@Composable
fun DetailCardPreviewOrig() {
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
        onEvent = {}
    )
}