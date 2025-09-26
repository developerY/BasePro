package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A data class that represents the one-to-many relationship between a TaskList and its Photos.
 *
 * This class is used by Room to fetch a task list and all of its associated photos at once.
 *
 * @property taskList The parent [TaskListEntity].
 * @property photos The list of child [PhotoEntity] objects.
 */
data class TaskListWithPhotos(
    @Embedded
    val taskList: TaskListEntity,

    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    val photos: List<PhotoEntity>
)
