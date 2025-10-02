package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.CategoryList


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState.Success,
    // The `onEvent` is no longer needed here for selection, but can be kept for other actions.
    onEvent: (HomeEvent) -> Unit,
    // This is for navigating from a Task in the list to the Task Detail (3rd pane)
    // This is the new navigation callback. It will be triggered when a category is clicked.
    onSelectList: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // This composable is now simplified to only show the category list.
    // The nested ListDetailPaneScaffold was removed as it conflicts with the primary navigation scaffold,
    // which was the root cause of the navigation loop.

    Log.d("HomeScreen", "On HomeScreen recomposing.")

    CategoryList(
        modifier = modifier,
        categories = uiState.categories,
        selectedCategory = uiState.selectedCategory,
        onCategoryClick = { category ->
            /* First, update the state for the detail view
            onEvent(HomeEvent.OnCategorySelected(category))
            // Then, trigger the navigation
            onSelectList(category.categoryId)*/

            // The old implementation used a nested scaffold.
            // The corrected implementation navigates via the main scaffold by simply invoking the callback.
            Log.d("HomeScreen", "Category clicked. Triggering navigation for categoryId: ${category.categoryId}")
            onSelectList(category.categoryId)
        }
    )
}
