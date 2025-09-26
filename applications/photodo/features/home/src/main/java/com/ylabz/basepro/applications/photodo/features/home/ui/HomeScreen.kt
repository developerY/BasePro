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
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane(modifier = modifier) {
                // In unfolded mode, this is the master pane (the list of categories)
                CategoryList(
                    categories = uiState.projects,
                    selectedCategory = uiState.selectedProject,
                    onCategoryClick = { category ->
                        onEvent(HomeEvent.OnProjectSelected(category))
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane(modifier = modifier) {
                // In unfolded mode, this is the detail pane (the lists for the selected category)
                TaskList(
                    project = uiState.selectedProject,
                    tasks = uiState.tasksForSelectedProject,
                    onSelectList = onSelectList
                )
            }
        }
    )
}
