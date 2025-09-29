package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

@Composable
fun HomeBottomBar(
    currentTopLevelKey: NavKey,
    onNavigate: (NavKey) -> Unit
) {
    val bottomNavItems = listOf<BottomBarItem>(
        PhotoDoNavKeys.HomeFeedKey,
        PhotoDoNavKeys.TaskListKey(categoryId = 0L),
        PhotoDoNavKeys.SettingsKey
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            val navKeyItem = item as NavKey
            val selected = currentTopLevelKey::class == navKeyItem::class
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(navKeyItem) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}
