package com.ylabz.basepro.applications.photodo.features.home.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

private const val TAG = "TaskList"


@Composable
fun TaskList(
    category: CategoryEntity?,
    taskLists: List<TaskListEntity>,
    onSelectList: (Long) -> Unit,
    // onAddList: () -> Unit, // Callback to add a new list
    modifier: Modifier = Modifier
) {
    // Log when the composable is recomposed, showing the state of the data
    Log.d(TAG, "Recomposing with category: ${category?.name ?: "null"}, task list count: ${taskLists.size}")

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFE0F7FA))) {
        Text("Source: TaskList.kt")
        if (category != null) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )


            // Check if the task list for the selected category is empty
            if (taskLists.isEmpty()) {
                // Log that the empty state is being shown
                Log.d(TAG, "No task lists found for category '${category.name}'. Showing empty state.")
                EmptyState(
                    message = "No lists found in ${category.name}. From TaskList.kt",
                    onAddClick = {}//onAddList
                )
            } else {
                Column() {
                    // Log that the grid is being displayed
                    Text("Source: TaskList.kt -- inside the else")
                    Log.d(TAG, "Displaying ${taskLists.size} task lists in a grid.")
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(taskLists) { taskList ->
                            TaskListItem(
                                taskList = taskList,
                                onClick = {
                                    Log.d(TAG, "STEP1: Clicked on task list: ${taskList.name}")
                                    onSelectList(taskList.listId)
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select a category to see your lists. From TaskList.kt")
            }
        }
    }
}

@Composable
fun InitialPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select a category to see your lists. From TaskList.kt")
    }
}

@Composable
fun EmptyState(message: String, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onAddClick) {
            Text("Add a new List from TaskList.kt")
        }
    }
}
