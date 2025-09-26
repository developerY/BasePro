package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_lists",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskListEntity(
    @PrimaryKey(autoGenerate = true)
    val listId: Long = 0,
    val categoryId: Long,
    var name: String,
    var notes: String? = null,
    var status: String = "To-Do", // "To-Do" or "Done"
    var priority: Int = 0, // 0 for normal, 1 for high
    val creationDate: Long = System.currentTimeMillis(),
    var dueDate: Long? = null
)
