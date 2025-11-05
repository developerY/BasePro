package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.CategoryList
import com.ylabz.basepro.applications.photodo.features.home.ui.components.TaskList
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    // This is for navigating from a Task in the list to the Task Detail (3rd pane)
    onSelectList: (Long) -> Unit,
    onCategorySelected: (Long) -> Unit, // <-- ADD THIS PARAMETER
    // ### WHAT: This parameter was added to accept the "setter" function from MainScreen.
    // ### WHY: This allows HomeScreen to tell MainScreen which FAB to display.
    // setFabState: (FabStateMenu?) -> Unit, // <-- IT'S A PARAMETER PASSED TO THE FUNCTION
) {
    // ### NEW LOGIC: Context-Aware FAB ###
    // This LaunchedEffect observes the selectedCategory. If it changes, the
    // effect will re-run, updating the FAB to match the current context.

    // ### WHAT: This is the new logic block for the FAB.
    // ### WHY: This code runs automatically whenever `uiState.selectedCategory` changes.
    // It checks if a category is selected and calls `setFabState` with the correct
    // FAB configuration ("Add Category" or "Add List"), making the FAB context-aware.

    LaunchedEffect(uiState.selectedCategory) {
        if (uiState.selectedCategory == null) {
            // Context: No category is selected.
            // Action: The FAB should add a new category.
            // Context: No category is selected.
            // Use the correct `FabState.Single` constructor and provide the icon.
            Log.d("HomeScreen", "Selected category: ${uiState.selectedCategory}")
            Log.d("FabLifecycle", "HomeScreen: In LaunchedEffect, selectedCategory is null. Preparing to set FAB state.") // BREADCRUMB 1

            /* setFabState(
                FabStateMenu.Single(
                    action = FabAction(
                        text = "Add Category",
                        icon = Icons.Default.Add,
                        onClick = {
                            Log.d("HomeScreen", "Selected category: ${uiState.selectedCategory}")
                            onEvent(HomeEvent.OnAddCategoryClicked)
                        }
                    )
                )
            ) */
        } else {
            /* setFabState(
            // Context: A category IS selected.
            // Action: The FAB should add a new list to that category.
            // ### FIX ###
            // Use the correct `FabState.Single` constructor and provide the icon.
            //Log.d("HomeScreen", "Selected category: ${uiState.selectedCategory.categoryId}")
            FabStateMenu.Menu(
                mainButtonAction = FabAction(
                    text = "Add List -- HomeScreen",
                    icon = Icons.Default.Add,
                    onClick = {
                        Log.d("HomeScreen", "Selected category: ${uiState.selectedCategory.categoryId}")
                        onEvent(HomeEvent.OnAddCategoryClicked)
                    }
                ),
                items = listOf(
                    FabAction(
                        text = "Add Category -- HomeScreen",
                        icon = Icons.Default.Create,
                        onClick = {
                            Log.d("HomeScreen", "Selected category: ${uiState.selectedCategory.categoryId}")
                            onEvent(HomeEvent.OnAddCategoryClicked)
                        }
                    )
                )
            )
            )*/
        }
    }


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
                    onEvent(HomeEvent.OnCategorySelected(category.categoryId))

                    // This calls the lambda to update the GLOBAL state in MainScreen
                    onCategorySelected(category.categoryId) // <-- CALL THE LAMBDA

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
