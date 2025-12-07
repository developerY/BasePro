package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

// --- MOCK DATA ---
private val mockCategory = CategoryEntity(
    categoryId = 1L,
    name = "Kitchen Renovation",
    description = "Cabinets, flooring, and paint selection for the new look.",
    imageUri = null // Using null to trigger the placeholder icon
)


val sampleTasks = listOf(
    TaskListEntity(
        listId = 101L,
        categoryId = 2L,
        name = "Q4 Planning",
        status = "To-Do",
        priority = 1
    ),
    TaskListEntity(
        listId = 102L,
        categoryId = 2L,
        name = "Expense Report",
        status = "Done",
        priority = 0
    )
)
private val mockTaskLists = listOf(
    TaskListEntity(listId = 1L, categoryId = 2L,name = "Buy Paint", status = "Active", priority = 1), // High Priority
    TaskListEntity(listId = 2L, categoryId = 2L,name = "Sand Walls", status = "Active", priority = 0),
    TaskListEntity(listId = 3L, categoryId = 2L,name = "Install Handles", status = "Done", priority = 0)
)

// --- PREVIEW ---

@Preview(showBackground = true, name = "1. Selected State")
@Composable
fun PreviewCategoryCardSelected() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CategoryCard(
                uiState = CategoryCardUiState(
                    category = mockCategory,
                    isSelected = true,
                    taskLists = mockTaskLists
                ),
                onEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "2. Unselected State")
@Composable
fun PreviewCategoryCardUnselected() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            CategoryCard(
                uiState = CategoryCardUiState(
                    category = mockCategory.copy(name = "Groceries", description = "Weekly run"),
                    isSelected = false,
                    taskLists = emptyList()
                ),
                onEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "3. Expanded State Logic Check")
@Composable
fun PreviewColumnDisplay() {
    // This preview stacks them to see how they look in a list context
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CategoryCard(
                uiState = CategoryCardUiState(
                    category = mockCategory,
                    isSelected = true,
                    taskLists = mockTaskLists
                ),
                onEvent = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            CategoryCard(
                uiState = CategoryCardUiState(
                    category = mockCategory.copy(categoryId = 2L, name = "Backyard"),
                    isSelected = false,
                    taskLists = emptyList()
                ),
                onEvent = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F, name = "4. Dark Mode")
@Composable
fun PreviewCategoryCardDark() {
    MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme()) {
        Surface(modifier = Modifier.padding(16.dp)) {
            CategoryCard(
                uiState = CategoryCardUiState(
                    category = mockCategory,
                    isSelected = true,
                    taskLists = mockTaskLists
                ),
                onEvent = {}
            )
        }
    }
}