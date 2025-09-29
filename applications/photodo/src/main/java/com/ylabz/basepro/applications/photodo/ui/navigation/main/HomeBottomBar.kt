package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ylabz.basepro.applications.photodo.ui.navigation.BottomBarItem
import com.ylabz.basepro.applications.photodo.ui.navigation.PhotoDoNavKeys

@Composable
fun HomeBottomBarOrig(
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

// Hoisted list of navigation items to avoid duplication in Rail and BottomBar
private val bottomNavItems = listOf<BottomBarItem>(
    PhotoDoNavKeys.HomeFeedKey,
    PhotoDoNavKeys.TaskListKey(categoryId = 0L),
    PhotoDoNavKeys.SettingsKey
)


@Composable
fun HomeBottomBar(currentTopLevelKey: NavKey, onNavigate: (NavKey) -> Unit) {
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

@Composable
fun HomeNavigationRail(currentTopLevelKey: NavKey, onNavigate: (NavKey) -> Unit) {
    NavigationRail {
        bottomNavItems.forEach { item ->
            val navKeyItem = item as NavKey
            val selected = currentTopLevelKey::class == navKeyItem::class
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(navKeyItem) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

@Composable
fun HomeNavigationRailOrig(currentTopLevelKey: NavKey, onNavigate: (NavKey) -> Unit) {
    val bottomNavItems = listOf<BottomBarItem>(
        PhotoDoNavKeys.HomeFeedKey,
        PhotoDoNavKeys.TaskListKey(categoryId = 0L),
        PhotoDoNavKeys.SettingsKey
    )

    NavigationRail {
        bottomNavItems.forEach { item ->
            val navKeyItem = item as NavKey
            val selected = currentTopLevelKey::class == navKeyItem::class
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(navKeyItem) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}