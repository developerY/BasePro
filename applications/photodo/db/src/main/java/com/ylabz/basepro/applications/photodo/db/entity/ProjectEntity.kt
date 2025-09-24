package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val projectId: Long = 0,
    val name: String,
    val description: String? = null
)