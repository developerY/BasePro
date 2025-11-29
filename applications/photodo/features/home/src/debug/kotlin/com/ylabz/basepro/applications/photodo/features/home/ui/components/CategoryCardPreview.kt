package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
// Adjust this import based on your actual Theme location

@Preview(showBackground = true, name = "Category Card (Folded/Phone)")
@Composable
fun CategoryCardPreview() {
    val sampleCategory = CategoryEntity(
        categoryId = 1L,
        name = "Personal",
        description = "Things I need to do for myself."
    )

    val selectedCategory = CategoryEntity(
        categoryId = 2L,
        name = "Work",
        description = "High priority office tasks."
    )

    // PhotoDoTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // Unselected State
            CategoryCard(
                category = sampleCategory,
                isSelected = false,
                onClick = {},
                onEvent = {},
                onDeleteClick = {},
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected State
            CategoryCard(
                category = selectedCategory,
                isSelected = true,
                onClick = {},
                onEvent = {},
                onDeleteClick = {}
            )
        }
    // }
}

@Preview(showBackground = true, name = "Category List Item (Unfolded/Tablet)")
@Composable
fun CategoryListItemPreview() {
    val sampleCategory = CategoryEntity(
        categoryId = 1L,
        name = "Groceries",
        description = "Weekly shopping list"
    )

    val selectedCategory = CategoryEntity(
        categoryId = 2L,
        name = "Ideas",
        description = "Random thoughts"
    )

    // PhotoDoTheme {
    Column(modifier = Modifier.padding(16.dp)) {
        // Unselected State
        CategoryListItem(
            category = sampleCategory,
            isSelected = false,
            onClick = {},
            onDeleteClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selected State
        CategoryListItem(
            category = selectedCategory,
            isSelected = true,
            onClick = {},
            onDeleteClick = {}
        )
    }
    // }
}