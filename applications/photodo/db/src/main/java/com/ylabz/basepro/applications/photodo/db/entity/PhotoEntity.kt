package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = TaskListEntity::class, // Corrected: Points to the new TaskListEntity
            parentColumns = ["listId"],      // Corrected: Points to the new primary key
            childColumns = ["listId"],       // Corrected: The foreign key column in this table
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val photoId: Long = 0,
    val listId: Long, // Corrected: Renamed from taskId to listId
    val uri: String,
    val caption: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
