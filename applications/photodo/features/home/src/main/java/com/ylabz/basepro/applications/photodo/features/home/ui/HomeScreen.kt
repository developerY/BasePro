package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.CategoryList
import com.ylabz.basepro.applications.photodo.features.home.ui.components.TaskList
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    // This is for navigating from a Task in the list to the Task Detail (3rd pane)
    onSelectList: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val scope = rememberCoroutineScope() // 3. Get a coroutine scope


    Log.d("HomeScreen", "On HomeScreen recomposing.")

    ListDetailPaneScaffold(
        modifier = modifier, // The modifier for the whole screen goes on the scaffold
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            CategoryList(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategoryClick = { category ->
                    /* First, update the state for the detail view
                    onEvent(HomeEvent.OnCategorySelected(category))
                    // Then, trigger the navigation
                    onSelectList(category.categoryId)*/
                    // This updates the state, so the detailPane knows what to show. This is correct.
                    onEvent(HomeEvent.OnCategorySelected(category))

                    // 2. Replace onSelectList with the navigator call.
                    // This tells the scaffold to show the detail pane.
                    // 4. Launch the navigation call in a coroutine
                    scope.launch {
                        Log.d("HomeScreen", "Navigating to detail pane")
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                }
            )
        },
        detailPane = {
            // This pane will now become visible and show the correct task list
            // because the uiState was updated by the onEvent call above.
            TaskList(
                category = uiState.selectedCategory,
                taskLists = uiState.taskListsForSelectedCategory,
                // This is for clicking items in the right-hand pane, which is already correct
                // This onSelectList is for when a user clicks a task *within* this list,
                // which correctly triggers the navigation to the 3rd pane.
                onSelectList = onSelectList
            )
        }
    )
}
