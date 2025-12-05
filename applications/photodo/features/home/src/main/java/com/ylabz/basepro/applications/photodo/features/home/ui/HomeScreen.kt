package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.home.ui.components.CategoryList
import com.ylabz.basepro.applications.photodo.features.home.ui.components.TaskList
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    navigationEffects: Flow<HomeNavigationEffect> // Pass the side-effect flow
    //onCategorySelected: (Long) -> Unit, // <-- ADD THIS PARAMETER
    // ### WHAT: This parameter was added to accept the "setter" function from MainScreen.
    // ### WHY: This allows HomeScreen to tell MainScreen which FAB to display.
    // setFabState: (FabStateMenu?) -> Unit, // <-- IT'S A PARAMETER PASSED TO THE FUNCTION
    // onCategorySelected: (Long) -> Unit, // <-- ADD THIS PARAMETER
    // ### WHAT: This parameter was added to accept the "setter" in MainScreen.
    // ### WHY: This allows HomeScreen to tell MainScreen which FAB to display.
    // setFabState: (FabStateMenu?) -> Unit, // <-- IT'S A PARAMETER PASSED TO THE FUNCTION
) {


    Log.d("HomeScreen", "On HomeScreen recomposing.")
    if (uiState.isExpandedScreen) {

        // val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
        val navigator = rememberListDetailPaneScaffoldNavigator()
        val scope = rememberCoroutineScope() // 3. Get a coroutine scope

        // --- ADAPTIVE NAVIGATION SYNC ---
        // Automatically navigate to the Detail pane when a category is selected.
        // This ensures the UI stays in sync with your ViewModel state.
        // --- EFFECT HANDLING ---
        // Listen for "Go To Detail" commands from the ViewModel
        LaunchedEffect(navigationEffects) {
            navigationEffects.collect { effect ->
                when (effect) {
                    is HomeNavigationEffect.NavigateToDetailPane -> {
                        // Navigate to the detail pane (right side)
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        // Note: If you need to navigate to a NEW screen (Screen C),
                        // you would trigger a callback to the MainActivity here.
                    }
                }
            }
        }

        // --- SYNC STATE ---
        // Ensure navigator aligns with selected category
        LaunchedEffect(uiState.selectedCategory) {
            if (uiState.selectedCategory != null) {
                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
            }
        }

        ListDetailPaneScaffold(
            modifier = modifier, // The modifier for the whole screen goes on the scaffold
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                // 1. EXPRESSIVE MOTION: Wrap in AnimatedPane
                // This gives you the beautiful M3 entry/exit animations automatically.
                AnimatedPane {
                    CategoryList(
                        uiState = uiState,
                        onEvent = onEvent,
                        isExpandedScreen = true,
                    )
                }
            },
            detailPane = {
                // This pane will now become visible and show the correct task list
                // because the uiState was updated by the onEvent call above.
                // 2. EXPRESSIVE MOTION: Wrap in AnimatedPane
                AnimatedPane {
                    // This pane shows the "Task List" for the selected category
                    TaskList(
                        category = uiState.selectedCategory,
                        taskLists = uiState.taskListsForSelectedCategory,
                        // This is for clicking items in the right-hand pane, which is already correct
                        // This onSelectList is for when a user clicks a task *within* this list,
                        // which correctly triggers the navigation to the 3rd pane.
                        // Pure Event Passing
                        onSelectList = { listId ->
                            onEvent(HomeEvent.OnTaskListSelected(listId))
                        }
                    )
                }
            }
        )
    }else {

        // --- PHONE / FOLDED LAYOUT (1 Pane) ---
        // Just show the Categories.
        // If you click a category, the Global Nav (HomeEntry) pushes the Task List screen.
        // This prevents the "Hidden Categories" bug.
        CategoryList(
            modifier = modifier.fillMaxSize(),
            uiState = uiState,
            onEvent = onEvent,
            isExpandedScreen = false
        )
    }

    // ### NEW LOGIC: Context-Aware FAB ###
    // This LaunchedEffect observes the selectedCategory. If it changes, the
    // effect will re-run, updating the FAB to match the current context.

    // ### WHAT: This is the new logic block for the FAB.
    // ### WHY: This code runs automatically whenever `uiState.selectedCategory` changes.
    // It checks if a category is selected and calls `setFabState` with the correct
    // FAB configuration ("Add Category" or "Add List"), making the FAB context-aware.

    /* LaunchedEffect(uiState.selectedCategory) {
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
    }*/
}
