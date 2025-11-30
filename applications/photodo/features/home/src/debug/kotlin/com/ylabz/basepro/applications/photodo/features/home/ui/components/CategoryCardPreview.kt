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
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

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

    // Dummy data for the accordion
    val sampleTasks = listOf(
        TaskListEntity(
            listId = 101,
            categoryId = 2,
            name = "Q4 Planning",
            status = "To-Do",
            priority = 1
        ),
        TaskListEntity(
            listId = 102,
            categoryId = 2,
            name = "Expense Report",
            status = "Done",
            priority = 0
        )
    )

    //PhotoDoTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // Unselected State (Collapsed)
            CategoryCard(
                category = sampleCategory,
                isSelected = false,
                taskLists = emptyList(),
                onEvent = {},
                onTaskListClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected State (Expanded with Accordion)
            CategoryCard(
                category = selectedCategory,
                isSelected = true,
                taskLists = sampleTasks,
                onEvent = {},
                onTaskListClick = {}
            )
        }
    //}
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

    //PhotoDoTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // Unselected State
            CategoryListItem(
                category = sampleCategory,
                isSelected = false,
                onEvent = {}
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selected State
            CategoryListItem(
                category = selectedCategory,
                isSelected = true,
                onEvent = {}
            )
        }
    //}
}