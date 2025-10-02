package com.ylabz.basepro.applications.photodo.db.model

/**
 * Represents a single category that contains multiple task lists.
 * This is the domain model used in the UI and feature layers.
 *
 * @property categoryId The unique identifier for the category.
 * @property name The name of the category (e.g., "Family", "Work").
 * @property description An optional description for the category.
 */
data class Category(
    val categoryId: Long,
    val name: String,
    val description: String?
)