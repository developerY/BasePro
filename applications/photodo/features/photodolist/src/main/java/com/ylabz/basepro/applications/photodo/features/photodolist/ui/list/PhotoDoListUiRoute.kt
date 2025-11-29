package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

private const val TAG = "PhotoDoListUiRoute"

/*
 * The route for displaying the list of tasks for a given project.
 * This composable is stateless and driven by the provided ViewModel.
 */
@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    viewModel: PhotoDoListViewModel = hiltViewModel(),
    onTaskClick: (Long) -> Unit,
    onEvent: (PhotoDoListEvent) -> Unit, // Allows parent to send events
    // The ViewModel is now a required parameter.
) {
    //val uiState by viewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PhotoDoListUiState.Loading -> {
            Log.d(TAG, "Displaying Loading state")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text("Source: PhotoDoListUiRoute.kt")
                    CircularProgressIndicator()
                }
            }
        }
        is PhotoDoListUiState.Success -> {
            Log.d(TAG, "Displaying Success state with ${state.taskLists.size} items.")
            Column {
                Text("Source: PhotoDoListUiRoute.kt")
                LazyColumn(modifier = modifier) {
                    items(state.taskLists) { task ->
                        PhotoDoTaskCard(
                            task = task,
                            onItemClick =
                                {
                                    Log.d(TAG, "onItemClick: ${task.listId} -- should show the detailed card")
                                    Log.d(TAG, "The onEvent callback is not working here")

                                    onTaskClick(task.listId)
                                },
                            onDeleteClick = { onEvent(PhotoDoListEvent.OnDeleteTaskListClicked(task.listId)) },
                        )
                    }
                }
            }
        }
        is PhotoDoListUiState.Error -> {
            Log.e(TAG, "Displaying Error state: ${state.message}")
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text("Source: PhotoDoListUiRoute.kt")
                    Text(text = state.message)
                }
            }
        }
    }
}

@Composable
internal fun PhotoDoListScreen(
    modifier: Modifier = Modifier,
    taskLists: List<TaskListEntity>,
    onTaskListClick: (Long) -> Unit,
    onDeleteTaskList: (TaskListEntity) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Task Lists",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskLists, key = { it.listId }) { taskList ->
                /*PhotoDoTaskCard(
                    taskList = taskList,
                    onClick = { onTaskListClick(taskList.listId) },
                    onDelete = { onDeleteTaskList(taskList) }
                )*/
            }
        }
    }
}

// --- Preview Helpers ---

/**
 * A mock implementation of the [PhotoDoRepo] for use in Composable Previews.
 * It returns predefined data, allowing for consistent and predictable UI previews.
 */
class MockPhotoDoRepo : PhotoDoRepo {
    override suspend fun insertCategory(category: CategoryEntity) {}
    override suspend fun deleteCategory(category: CategoryEntity) {}
    override fun getAllCategories(): Flow<List<CategoryEntity>> = flowOf(emptyList())
    override fun getCategoryById(categoryId: Long): Flow<CategoryEntity?> = flowOf(null)
    override suspend fun updateCategory(category: CategoryEntity) {}

    override suspend fun insertTaskList(taskList: TaskListEntity) {}
    override suspend fun deleteTaskList(taskList: TaskListEntity) {}
    override suspend fun deleteTaskListById(listId: Long) {}

    override fun getTaskListById(listId: Long): Flow<TaskListEntity?> = flowOf(null)
    override fun getTaskListsForCategory(categoryId: Long): Flow<List<TaskListEntity>> = flowOf(
        List(5) {
            TaskListEntity(
                listId = it.toLong(),
                categoryId = categoryId,
                name = "Task List #$it",
                notes = "Description for task #$it"
            )
        }
    )
    override suspend fun insertPhoto(photo: PhotoEntity) {}
    override suspend fun deletePhoto(photo: PhotoEntity) {}
    override fun getPhotosForTaskList(listId: Long): Flow<List<PhotoEntity>> = flowOf(emptyList())
    override fun getTaskListWithPhotos(listId: Long): Flow<TaskListWithPhotos?> = flowOf(null)
}

/**
 * Creates a [PhotoDoListViewModel] with a mock repository and a predefined initial state.
 * This is useful for creating consistent and predictable Previews.
 *
 * @param initialState The [PhotoDoListUiState] to initialize the ViewModel with.
 * @return A configured [PhotoDoListViewModel] instance.
 */
fun createMockViewModel(initialState: PhotoDoListUiState): PhotoDoListViewModel {
    val repo = MockPhotoDoRepo()
    return object : PhotoDoListViewModel(repo) {
        override val uiState: MutableStateFlow<PhotoDoListUiState> = MutableStateFlow(initialState)
    }
}


// --- Previews ---

@Preview(name = "Loading State")
@Composable
fun PhotoDoListUiRoutePreviewLoading() {
    val mockViewModel = createMockViewModel(PhotoDoListUiState.Loading)
    PhotoDoListUiRoute(
        onTaskClick = {},
        onEvent = {},
        viewModel = mockViewModel
    )
}

@Preview(name = "Success State")
@Composable
fun PhotoDoListUiRoutePreviewSuccess() {
    val mockTasks = List(5) {
        TaskListEntity(
            listId = it.toLong(),
            categoryId = 1L,
            name = "Task List #$it",
            notes = "Description for task #$it"
        )
    }
    val mockViewModel = createMockViewModel(PhotoDoListUiState.Success(mockTasks))
    PhotoDoListUiRoute(
        onTaskClick = {},
        onEvent = {},
        viewModel = mockViewModel
    )
}

@Preview(name = "Error State")
@Composable
fun PhotoDoListUiRoutePreviewError() {
    val mockViewModel = createMockViewModel(PhotoDoListUiState.Error("Failed to load task lists."))
    PhotoDoListUiRoute(
        onTaskClick = {},
        onEvent = {},
        viewModel = mockViewModel
    )
}