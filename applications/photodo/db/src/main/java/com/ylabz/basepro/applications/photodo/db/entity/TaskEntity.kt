package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val projectId: Long,
    var name: String,
    var notes: String? = null,
    var status: String = "To-Do", // "To-Do" or "Done"
    var priority: Int = 0, // 0 for normal, 1 for high
    val creationDate: Long = System.currentTimeMillis(),
    var dueDate: Long? = null
)