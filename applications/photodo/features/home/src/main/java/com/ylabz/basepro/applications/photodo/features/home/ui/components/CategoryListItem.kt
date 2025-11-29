package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent

@Composable
fun CategoryListItem(
    category: CategoryEntity,
    isSelected: Boolean,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },

        headlineContent = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        },
        trailingContent = {
            // Compact Actions for Tablet
            androidx.compose.foundation.layout.Row {
                IconButton(onClick = { onEvent(HomeEvent.OnEditCategoryClicked(category)) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { onEvent(HomeEvent.OnDeleteCategoryClicked(category)) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}