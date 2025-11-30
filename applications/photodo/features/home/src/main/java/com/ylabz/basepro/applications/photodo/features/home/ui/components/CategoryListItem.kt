package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent
import kotlin.math.absoluteValue

@Composable
fun CategoryListItem(
    category: CategoryEntity,
    isSelected: Boolean,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Generate a deterministic "Random" Pastel Color based on the Category ID/Name
    val pastelColors = remember(category.categoryId) {
        val hash = category.name.hashCode().absoluteValue
        val hue = (hash % 360).toFloat()
        val saturation = 0.4f // Low saturation for "Soft" look
        val value = 0.95f     // High value for "Pastel" brightness

        val container = Color.hsv(hue, saturation, value)
        val content = Color.hsv(hue, 0.8f, 0.3f) // Darker shade of same hue for text

        Pair(container, content)
    }

    val (baseContainer, baseContent) = pastelColors

    // 2. Adjust based on selection state
    val containerColor = if (isSelected) {
        // If selected, maybe make it slightly more saturated or darker
        baseContainer.copy(alpha = 1f)
    } else {
        // If not selected, maybe lighter or just the base pastel
        baseContainer.copy(alpha = 0.6f)
    }

    val contentColor = if (isSelected) {
        baseContent
    } else {
        baseContent.copy(alpha = 0.8f)
    }

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },
        leadingContent = {},
        headlineContent = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = contentColor
            )
        },
        trailingContent = {},
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}



@Composable
fun TrainlingContentOne(
    modifier: Modifier = Modifier,
    contentColor: Color,
    category: CategoryEntity,
    isSelected: Boolean,
    onEvent: (HomeEvent) -> Unit,
) {
    Row {
        IconButton(onClick = { onEvent(HomeEvent.OnEditCategoryClicked(category)) }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = contentColor.copy(alpha = 0.8f)
            )
        }
        IconButton(onClick = { onEvent(HomeEvent.OnDeleteCategoryClicked(category)) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = if (isSelected) contentColor.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TrainlingContent(
    modifier: Modifier = Modifier,
    category: CategoryEntity,
    isSelected: Boolean,
    onEvent: (HomeEvent) -> Unit,
) {

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

}