package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack // Typealias for SnapshotStateList<NavKey>
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay // The NavDisplay composable

// AppBottomNavItem sealed class remains the same
sealed class AppBottomNavItem(
    val title: String,
    val icon: ImageVector,
    val navKey: NavKey
) {
    data object Home : AppBottomNavItem("Home", Icons.Filled.Home, HomeKey)
    data object Feed : AppBottomNavItem("Feed", Icons.Filled.Favorite, FeedKey)
    data object Profile : AppBottomNavItem("Profile", Icons.Filled.Person, ProfileKey)
}

@Composable
fun Nav3Main(modifier: Modifier = Modifier) {
    // NavBackStack is a SnapshotStateList<NavKey>
    val backStack: NavBackStack = rememberNavBackStack(HomeKey)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            val currentKey = backStack.lastOrNull()

            AppBottomBar(
                currentKey = currentKey,
                onNavigate = { newNavKey ->
                    // Logic for bottom navigation - directly manipulates the backStack
                    if (newNavKey == currentKey) return@AppBottomBar // Already on this key

                    if (newNavKey == HomeKey) {
                        backStack.clear()
                        backStack.add(HomeKey)
                    } else {
                        // Ensure HomeKey is root, remove newKey if it exists elsewhere, then add to top
                        if (backStack.isEmpty() || backStack.first() != HomeKey) {
                            backStack.clear()
                            backStack.add(HomeKey)
                        }
                        backStack.remove(newNavKey) // Remove if it exists to bring to top
                        backStack.add(newNavKey)
                    }
                }
            )
        }
    ) { innerPadding ->
        // Call NavDisplay with the correct parameter names from its signature
        NavDisplay(
            backStack = backStack, // Pass our NavBackStack here (it's a List<NavKey>)
            modifier = Modifier.padding(innerPadding),
            entryProvider = appEntryProvider, // Pass our entryProvider
            onBack = { count -> // Handle back requests from NavDisplay
                // Pop 'count' items from our backStack.
                // Ensure we don't pop the last item if it's HomeKey, unless count makes it necessary.
                repeat(count) {
                    if (backStack.size > 1) { // Always allow popping if more than one item
                        backStack.removeAt(backStack.lastIndex)
                    } else if (backStack.size == 1 && backStack.first() != HomeKey) {
                        // If only one item and it's not Home, allow popping it (stack becomes empty)
                        backStack.removeAt(backStack.lastIndex)
                    }
                    // If stack becomes empty after popping (e.g., non-Home single item popped),
                    // reset to HomeKey to ensure NavDisplay doesn't get an empty backstack,
                    // as NavDisplay requires backStack.isNotEmpty().
                    if (backStack.isEmpty()) {
                        backStack.add(HomeKey)
                    }
                }
            }
            // Other NavDisplay parameters like contentAlignment, transitions, etc.,
            // will use their default values as defined in the NavDisplay signature.
        )
    }
}

@Composable
private fun AppBottomBar(
    currentKey: NavKey?,
    onNavigate: (NavKey) -> Unit
) {
    val items = listOf(
        AppBottomNavItem.Home,
        AppBottomNavItem.Feed,
        AppBottomNavItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentKey == item.navKey,
                onClick = {
                    onNavigate(item.navKey)
                }
            )
        }
    }
}
