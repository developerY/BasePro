package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
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
        modifier = modifier, // The modifier for the whole screen goes on the scaffold
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            CategoryList(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategoryClick = { category ->
                    // First, update the state for the detail view
                    onEvent(HomeEvent.OnCategorySelected(category))
                    // Then, trigger the navigation
                    onSelectList(category.categoryId)
                }
            )
        },
        detailPane = {
            TaskList(
                category = uiState.selectedCategory,
                taskLists = uiState.taskListsForSelectedCategory,
                // This is for clicking items in the right-hand pane, which is already correct
                onSelectList = onSelectList
            )
        }
    )
}
