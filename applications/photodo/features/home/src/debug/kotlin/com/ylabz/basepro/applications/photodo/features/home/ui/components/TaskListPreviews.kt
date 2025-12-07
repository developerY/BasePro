package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

// --- MOCK DATA ---
private val mockCategoryForList = CategoryEntity(
    categoryId = 1L,
    name = "Home Improvements",
    description = "Things to fix around the house",
    imageUri = null
)

private val mockTaskListsData = listOf(
    TaskListEntity(listId = 1L, categoryId = 2L,name = "Buy Paint", status = "Active", priority = 1), // High Priority
    TaskListEntity(listId = 2L, categoryId = 2L,name = "Sand Walls", status = "Active", priority = 0),
    TaskListEntity(listId = 3L, categoryId = 2L,name = "Install Handles", status = "Done", priority = 0)
)

// --- PREVIEWS ---

@Preview(showBackground = true, name = "1. Populated List")
@Composable
fun PreviewTaskListPopulated() {
    MaterialTheme {
        Surface {
            TaskList(
                category = mockCategoryForList,
                taskLists = mockTaskListsData,
                onSelectList = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "2. Empty State (No Lists)")
@Composable
fun PreviewTaskListEmpty() {
    MaterialTheme {
        Surface {
            TaskList(
                category = mockCategoryForList,
                taskLists = emptyList(), // Simulating empty DB result
                onSelectList = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "3. No Category Selected")
@Composable
fun PreviewTaskListNoCategory() {
    MaterialTheme {
        Surface {
            TaskList(
                category = null, // Simulating null selection
                taskLists = emptyList(),
                onSelectList = {}
            )
        }
    }
}