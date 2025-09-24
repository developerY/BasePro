package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A data class that represents the one-to-many relationship between a Task and its Photos.
 *
 * This class is used by Room to fetch a task and all of its associated photos at once.
 *
 * @property task The parent [TaskEntity].
 * @property photos The list of child [PhotoEntity] objects.
 */
data class TaskWithPhotos(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val photos: List<PhotoEntity>
)