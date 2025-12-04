package com.ylabz.basepro.applications.photodo.features.home.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState

@Composable
fun CategoryList(
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Log.d("CategoryList", "Recomposing. Expanded: $isExpandedScreen")

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 88.dp // Space for FAB
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = uiState.categories,
            key = { it.categoryId }
        ) { category ->

            val isSelected = category.categoryId == uiState.selectedCategory?.categoryId
            val tasks = if (isSelected) uiState.taskListsForSelectedCategory else emptyList()

            if (isExpandedScreen) {
                // TABLET: Show clean List Items
                Column {
                    Text("List Item")
                    CategoryListItem(
                        category = category,
                        isSelected = isSelected,
                        onEvent = onEvent
                    )
                }
            } else {
                // PHONE: Show expressive Cards
                Column() {
                    Text("Cat Card")
                    CategoryCard(
                        // Construct the UI State object
                        uiState = CategoryCardUiState(
                            category = category,
                            isSelected = isSelected,
                            taskLists = tasks
                        ),
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}