package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack

/**
 * The Bottom Navigation Bar composable, designed for Navigation 3.
 * It is stateless and driven by the [topLevelBackStack].
 */
@Composable
fun HomeBottomBar(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    onNavigate: (NavKey) -> Unit
) {
    val bottomNavItems = listOf<BottomBarItem>(
        PhotoDoNavKeys.HomeFeedKey, 
        PhotoDoNavKeys.TaskListKey(categoryId = 0L), // Placeholder, projectId doesn't matter for selection
        PhotoDoNavKeys.SettingsKey
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            val title = item.title
            val icon = item.icon

            // Compare by the *class* of the NavKey, not the instance.
            // This ensures that any PhotoDolListKey (regardless of projectId) selects the correct tab.
            val selected = topLevelBackStack.topLevelKey::class == (item as NavKey)::class
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item as NavKey) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title
                    )
                },
                label = { Text(title) }
            )
        }
    }
}
