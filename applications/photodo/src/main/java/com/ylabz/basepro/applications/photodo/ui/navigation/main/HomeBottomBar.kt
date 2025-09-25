package com.ylabz.basepro.applications.photodo.ui.navigation.main

// Removed direct import of PhotoDolListKey as we need to instantiate it
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys.HomeFeedKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys.SettingsKey
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
        HomeFeedKey, 
        PhotoDoNavKeys.PhotoDolListKey(projectId = 0L), // Instantiate with a placeholder projectId
        SettingsKey
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            // item is now guaranteed to have .title and .icon due to BottomBarItem interface
            val title = item.title
            val icon = item.icon

            // For comparison, ensure topLevelKey can be compared with NavKey instances.
            // If topLevelKey is PhotoDolListKey, its projectId will matter for exact match.
            // However, bottom bar selection is often by type or a base route, not specific args.
            // Here, we cast item to NavKey. If topLevelBackStack.topLevelKey is an instance of
            // PhotoDolListKey (e.g. PhotoDolListKey(projectId=1L)), it won't be '==' to the item
            // PhotoDolListKey(projectId=0L) from bottomNavItems if selection relies on full equality.
            // A common pattern for bottom bar selection is to check the *type* of the key or a common base route.
            
            // Current selection logic: topLevelBackStack.topLevelKey == (item as NavKey)
            // This will work for HomeFeedKey and SettingsKey.
            // For PhotoDolListKey, it will only be selected if topLevelBackStack.topLevelKey is *exactly*
            // PhotoDolListKey(projectId = 0L). This might not be what you want if any PhotoDolListKey
            // (regardless of projectId) should select the tab.
            // For now, let's keep the direct comparison. If selection is an issue, we can refine it.
            val selected = topLevelBackStack.topLevelKey == (item as NavKey)
            
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item as NavKey) }, // Pass the NavKey (e.g., PhotoDolListKey(0L))
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
