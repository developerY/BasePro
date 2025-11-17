package com.ylabz.basepro.applications.photodo.features.home.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState

@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
// 1. STATE: Receive the full Success state
    uiState: HomeUiState.Success,
    // 2. EVENTS: Receive the single event handler
    onEvent: (HomeEvent) -> Unit,
    navTo: (Long) -> Unit
) {
    // 3. Extract data from the state
    val categories = uiState.categories
    val selectedCategory = uiState.selectedCategory

    Log.d("CategoryList", "Recomposing with ${categories.size} categories")

    // 1. Use contentPadding for better spacing around the list
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Add a bit more space
    ) {
        item {
            Text("Source: CategoryList.kt")
        }
        items(categories) { category ->

            // 2. Use ElevatedCard for a more "expressive" Material 3 look
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    // Make the *card itself* clickable for selection
                    .clickable {
                        // 4. ACTION: Send the correct selection event
                        onEvent(HomeEvent.OnCategorySelected(category.categoryId))
                    },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (category.categoryId == selectedCategory?.categoryId) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                // 2. USE A ROW FOR TEXT + ICON
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Add padding, but less vertical for the icon button
                        .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f) // Text takes up available space
                    )

                    // 3. ADD THE DELETE ICON BUTTON
                    IconButton(onClick = {
                        Log.d("CategoryList", "Delete button clicked for ${category.name}")
                        onEvent(HomeEvent.OnDeleteCategoryClicked(category))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete ${category.name}",
                            // Use the error color for a destructive action
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

            }
        }
    }
}
