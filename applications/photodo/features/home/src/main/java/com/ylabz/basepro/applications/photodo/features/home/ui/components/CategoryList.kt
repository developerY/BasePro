package com.ylabz.basepro.applications.photodo.features.home.ui.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeUiState

@Composable
fun CategoryList(
    uiState: HomeUiState.Success,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("CategoryList", "Recomposing with ${uiState.categories.size} categories")

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp), // Extra bottom padding for FAB
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Removed the debug "Source: CategoryList.kt" text for a cleaner look

        items(
            items = uiState.categories,
            key = { it.categoryId } // Important for performance and animation
        ) { category ->

            val isSelected = category.categoryId == uiState.selectedCategory?.categoryId

            // 1. ANIMATED COLOR: Smooth transition for selection state
            val animatedContainerColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerLow
                },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "CardContainerColor"
            )

            val contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },
                // 2. EXPRESSIVE SHAPE: Use ExtraLarge (28.dp) for that modern, round look
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = animatedContainerColor,
                    contentColor = contentColor
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (isSelected) 6.dp else 2.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Generous internal padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 3. LEADING ICON: Anchors the card visual
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) contentColor else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 4. TEXT CONTENT: Title and Description
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = contentColor
                        )
                        if (category.description?.isNotBlank() == true) {
                            Text(
                                text = category.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 5. TRAILING ACTION: Subtle delete button
                    IconButton(
                        onClick = {
                            onEvent(HomeEvent.OnDeleteCategoryClicked(category))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete ${category.name}",
                            // Keep delete red-ish even when selected, or match content theme?
                            // Matching content theme is usually cleaner, but Error color signals action.
                            // Let's use Error color but with alpha if unselected to not be too loud.
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}