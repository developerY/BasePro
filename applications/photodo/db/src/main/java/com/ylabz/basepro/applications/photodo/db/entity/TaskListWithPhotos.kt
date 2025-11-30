package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Holds the Task List, its Photos, AND its Checklist Items.
 */
/**
 * A data class that represents the one-to-many relationship between a TaskList and its Photos.
 *
 * This class is used by Room to fetch a task list and all of its associated photos at once.
 *
 * @property taskList The parent [TaskListEntity].
 * @property photos The list of child [PhotoEntity] objects.
 */

/**
 * Represents a Task List with its associated photos and checklist items.
 *
 * @property taskList The TaskListEntity representing the Task List.
 */
data class TaskListWithPhotos(
    @Embedded
    val taskList: TaskListEntity,

    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    val photos: List<PhotoEntity> = emptyList(),

    // --- ADD THIS ---
    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    val items: List<TaskItemEntity> = emptyList()
)