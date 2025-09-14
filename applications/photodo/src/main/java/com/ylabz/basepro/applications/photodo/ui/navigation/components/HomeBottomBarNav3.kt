package com.ylabz.basepro.applications.photodo.ui.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home // Default icon example, ensure NavKeys provide their own
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector // For item.icon type
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.ui.navigation.HomeFeedKey
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoListKey
import com.ylabz.basepro.applications.photodo.ui.navigation.SettingsKey
import com.ylabz.basepro.applications.photodo.ui.navigation.util.TopLevelBackStack
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem // Import the interface


/**
 * The Bottom Navigation Bar composable, designed for Navigation 3.
 * It is stateless and driven by the [topLevelBackStack].
 */
@Composable
fun HomeBottomBarNav3(
    topLevelBackStack: TopLevelBackStack<NavKey>,
    onNavigate: (NavKey) -> Unit
) {
    val bottomNavItems = listOf<BottomBarItem>(
        HomeFeedKey, // HomeFeedKey now conforms to BottomBarItem
        PhotoListKey,
        SettingsKey
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            // item is now guaranteed to have .title and .icon due to BottomBarItem interface
            val title = item.title
            val icon = item.icon

            // We need to cast back to NavKey for comparison if topLevelKey is NavKey, 
            // or ensure topLevelKey is also BottomBarItem for direct comparison.
            // For now, assuming topLevelBackStack.topLevelKey can be compared with NavKey instances.
            val selected = topLevelBackStack.topLevelKey == (item as NavKey) 

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item as NavKey) }, // Pass the NavKey itself
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
