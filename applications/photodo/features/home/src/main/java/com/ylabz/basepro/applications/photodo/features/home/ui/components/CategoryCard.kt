package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    // 1. DYNAMIC GRADIENT: Generate a unique pastel gradient based on the name
    val gradientBrush = remember(category.name) {
        val hash = category.name.hashCode()
        val color1 = Color(0xFFE0F7FA) // Cyan 50 (Base)
        val color2 = when (hash % 4) {
            0 -> Color(0xFFB2EBF2) // Cyan 100
            1 -> Color(0xFFE1BEE7) // Purple 100
            2 -> Color(0xFFFFF9C4) // Yellow 100
            else -> Color(0xFFDCEDC8) // Light Green 100
        }
        Brush.linearGradient(listOf(color1, color2))
    }

    // Animate the border/container slightly when selected
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "BorderColor"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },
        shape = MaterialTheme.shapes.extraLarge,
        // We use a basic container color but overlay the gradient below
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        extracted(gradientBrush)

            Spacer(modifier = Modifier.width(16.dp))

            // 3. PROMINENT TYPOGRAPHY & CONTENT
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Description
                if (!category.description.isNullOrBlank()) {
                    Text(
                        text = category.description?: "No Discription",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 4. VISUAL METADATA (Task Count Placeholder)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "0 Tasks", // Placeholder for now
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 5. ACTION ACCESS (Edit & Delete)
            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { onEvent(HomeEvent.OnDeleteCategoryClicked(category)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = { onEvent(HomeEvent.OnEditCategoryClicked(category)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

@Composable
private fun extracted(gradientBrush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradientBrush) // Apply the unique gradient
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 2. BIG ICON: A visual anchor for the category
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), // Semi-transparent
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
}