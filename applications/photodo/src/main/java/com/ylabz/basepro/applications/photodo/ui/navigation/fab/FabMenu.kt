package com.ylabz.basepro.applications.photodo.ui.navigation.fab

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FabMenu(fabState: FabState) {
    // Use a key to ensure the composable recomposes when the state type changes
    key(fabState) {
        when (fabState) {
            is FabState.Hidden -> {
                // Don't display anything
            }
            is FabState.Single -> {
                ExtendedFloatingActionButton(
                    text = { Text(fabState.action.text) },
                    icon = { Icon(fabState.action.icon, contentDescription = null) },
                    onClick = fabState.action.onClick,
                    expanded = true
                )
            }
            is FabState.Menu -> {
                var isMenuExpanded by remember { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated visibility for the menu items
                    AnimatedVisibility(
                        visible = isMenuExpanded,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            fabState.items.forEach { action ->
                                SmallFloatingActionButton(
                                    onClick = {
                                        action.onClick()
                                        isMenuExpanded = false
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(action.icon, contentDescription = action.text)
                                }
                            }
                        }
                    }

                    // Main FAB
                    FloatingActionButton(
                        onClick = {
                            // If there are menu items, toggle the menu. Otherwise, execute the main action.
                            if (fabState.items.isNotEmpty()) {
                                isMenuExpanded = !isMenuExpanded
                            } else {
                                fabState.mainButtonAction.onClick()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = fabState.mainButtonAction.icon,
                            contentDescription = fabState.mainButtonAction.text
                        )
                    }
                }
            }
        }
    }
}