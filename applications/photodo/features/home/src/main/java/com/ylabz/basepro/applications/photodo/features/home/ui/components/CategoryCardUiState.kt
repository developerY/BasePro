package com.ylabz.basepro.applications.photodo.features.home.ui.components

import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

// --- 1. DEFINE UI STATE ---
data class CategoryCardUiState(
    val category: CategoryEntity,
    val isSelected: Boolean,
    val taskLists: List<TaskListEntity> = emptyList()
)