package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent

@Composable
fun CategoryCard(
    category: CategoryEntity,
    isSelected: Boolean,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerLow,
        label = "CardColor"
    )
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurface

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = animatedContainerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Label,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) contentColor else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
                if (category.description?.isNotBlank() == true) {
                    Text(
                        text = category.description ?: "no description",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            // 3. Actions (Edit & Delete)
            Row {
                IconButton(onClick = { onEvent(HomeEvent.OnEditCategoryClicked(category)) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = contentColor
                    )
                }
                IconButton(onClick = { onEvent(HomeEvent.OnDeleteCategoryClicked(category)) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = if (isSelected) contentColor else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}