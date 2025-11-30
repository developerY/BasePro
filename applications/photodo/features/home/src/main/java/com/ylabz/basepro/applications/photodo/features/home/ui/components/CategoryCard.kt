package com.ylabz.basepro.applications.photodo.features.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: CategoryEntity,
    isSelected: Boolean,
    taskLists: List<TaskListEntity> = emptyList(),
    onEvent: (HomeEvent) -> Unit,
    onTaskListClick: (Long) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Auto-collapse if selection is lost
    LaunchedEffect(isSelected) {
        if (!isSelected) isExpanded = false
    }

    // --- CALCULATE METADATA ---
    val totalLists = taskLists.size
    val activeCount = taskLists.count { it.status != "Done" }
    val hasHighPriority = taskLists.any { it.priority > 0 }
    // --------------------------

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(durationMillis = 300),
        label = "CardBackground"
    )

    val coverGradient = remember(category.name) {
        val hash = category.name.hashCode()
        val hue = (hash % 360).toFloat()
        val color1 = Color.hsv(hue, 0.6f, 0.8f)
        val color2 = Color.hsv((hue + 40) % 360, 0.5f, 0.9f)
        Brush.verticalGradient(listOf(color1, color2))
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEvent(HomeEvent.OnCategorySelected(category.categoryId)) },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().height(130.dp)
            ) {
                // 1. COVER IMAGE
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                        .background(coverGradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (category.imageUri != null) {
                        AsyncImage(
                            model = category.imageUri,
                            contentDescription = "Category Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // 2. CONTENT
                Column(
                    modifier = Modifier.weight(1f).padding(12.dp)
                ) {
                    // Title Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        // Priority Indicator
                        if (hasHighPriority) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = "High Priority Items",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!category.description.isNullOrBlank()) {
                        Text(
                            text = category.description ?: "No description",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // --- NEW: METADATA ROW ---
                    // Only show if we have data (selected) or if we want to show "0 lists"
                    if (isSelected || totalLists > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BadgeInfo(
                                text = "$totalLists Lists",
                                color = MaterialTheme.colorScheme.primaryContainer,
                                onColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (activeCount > 0) {
                                BadgeInfo(
                                    text = "$activeCount Active",
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    onColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                    // -------------------------
                }
            }

            // ... (Actions Row and Accordion remain the same as previous code) ...
            // I'll include the Actions Row here for completeness of the 'Column' block

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (!isSelected) onEvent(HomeEvent.OnCategorySelected(category.categoryId))
                        isExpanded = !isExpanded
                    }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onEvent(HomeEvent.OnEditCategoryClicked(category)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { onEvent(HomeEvent.OnDeleteCategoryClicked(category)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }


            // ... (Accordion Section remains the same) ...
            AnimatedVisibility(visible = isSelected && isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Task Lists", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (taskLists.isEmpty()) {
                        Text("No lists yet.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        taskLists.forEach { taskList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTaskListClick(taskList.listId) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (taskList.status == "Done") Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = if (taskList.status == "Done") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(taskList.name, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.weight(1f))
                                if (taskList.priority > 0) {
                                    Icon(Icons.Default.PriorityHigh, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeInfo(text: String, color: Color, onColor: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = onColor
        )
    }
}
