package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_items",
    foreignKeys = [
        ForeignKey(
            entity = TaskListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE // If List is deleted, delete its items
        )
    ]
)
data class TaskItemEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val listId: Long, // Foreign Key to the TaskList
    val text: String,
    val isChecked: Boolean = false
)