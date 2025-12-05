package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    taskListWithPhotos: TaskListWithPhotos,
    onEvent: (PhotoDoDetailEvent) -> Unit
) {
    val (taskList, photos) = taskListWithPhotos

    // --- 1. EXPRESSIVE STATE MANAGEMENT ---
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    // Spring Physics: Bouncy scale effect on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardScale"
    )

    // Shape Morph: Corners get rounder when pressed
    val cornerRadius by animateIntAsState(
        targetValue = if (isPressed) 32 else 24, // Morph from Large to Extra Large
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "CornerMorph"
    )

    // Color: Subtle tint shift
    val containerColor by animateColorAsState(
        targetValue = if (isPressed)
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surfaceContainer,
        label = "ColorShift"
    )

    // --------------------------------------

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale) // Apply physics scale
            .clickable(
                interactionSource = interactionSource,
                indication = null // Disable default ripple for cleaner physics feel
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                // Optional: Navigate to full edit screen or expand
            },
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPressed) 2.dp else 6.dp // Depress effect
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp) // Generous "Expressive" spacing
        ) {
            // --- HEADER ROW ---
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = taskList.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold, // Bolder is better
                            letterSpacing = (-0.5).sp // Tighter tracking for headlines
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // if (!taskList.name.isNullOrBlank()) {
                        Text(
                            text = "Description",//taskList.discription  ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    //}
                }

                // Action Button (Edit)
                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                        // onEvent(PhotoDoDetailEvent.OnEditList(taskList))
                    },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit List",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PROGRESS SECTION ---
            // Calculate progress (e.g., if you have a 'total' target, or just count photos)
            // Assuming simplified logic: 1 photo = 10% progress for visual demo,
            // or modify 'TaskListEntity' to have a 'targetPhotoCount'.
            val photoCount = photos.size
            val targetCount = 10 // Arbitrary target for visualization
            val progress = (photoCount / targetCount.toFloat()).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$photoCount Photos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Goal: $targetCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp) // Thicker, expressive bar
                    .clip(RoundedCornerShape(50)), // Pill shape
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- EXPRESSIVE ACTIONS ROW ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "Add Photo" Chip - High Emphasis
                ExpressiveActionButton(
                    text = "Add Photo",
                    icon = Icons.Default.Add,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = {}// onEvent(PhotoDoDetailEvent.OnPhotoSaved(taskList.listId)) }
                )

                // "View Gallery" Chip - Medium Emphasis
                ExpressiveActionButton(
                    text = "Gallery",
                    icon = Icons.Default.PhotoLibrary,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { /* Navigate to gallery view */ }
                )
            }
        }
    }
}

/**
 * A custom helper button that matches the Expressive style:
 * - Tall height (56dp)
 * - Fully rounded corners
 */
@Composable
private fun ExpressiveActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)) // Medium rounded
            .background(containerColor)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)//.LightImpact)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
        }
    }
}

/* Extension for convenient SP unit if not available
private val Int.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.sp(this)
private val Double.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.sp(this)*/