package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.material3.adaptive.AnimatedPane
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.CategoryList
import com.ylabz.basepro.applications.photodo.features.home.ui.components.TaskList

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    onSelectList: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    ListDetailPaneScaffold(
        listPane = {
            AnimatedPane(modifier = modifier) {
                CategoryList(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategoryClick = { category ->
                        onEvent(HomeEvent.OnCategorySelected(category))
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane(modifier = modifier) {
                TaskList(
                    category = uiState.selectedCategory,
                    taskLists = uiState.taskListsForSelectedCategory,
                    onSelectList = onSelectList
                )
            }
        }
    )
}
