package com.ylabz.basepro.applications.photodo.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val name: String,
    val description: String? = null,
    // --- ADD THIS ---
    val imageUri: String? = null
)
