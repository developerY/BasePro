package com.ylabz.basepro.feature.nav3.ui.content.demo

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
// NavBackStack is not directly used here anymore, TopLevelBackStack manages it.
// import androidx.navigation3.runtime.NavBackStack
// rememberNavBackStack is not directly used here.
// import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.feature.nav3.ui.FeedKey
import com.ylabz.basepro.feature.nav3.ui.HomeKey
import com.ylabz.basepro.feature.nav3.ui.ProfileKey
import com.ylabz.basepro.feature.nav3.ui.createNav3EntryProvider

// AppBottomNavItem sealed class definition
sealed class AppBottomNavItem(
    val title: String,
    val icon: ImageVector,
    val navKey: NavKey // This should match one of the top-level NavKeys (HomeKey, FeedKey, ProfileKey)
) {
    data object Home : AppBottomNavItem("Home", Icons.Filled.Home, HomeKey)
    data object Feed : AppBottomNavItem("Feed", Icons.Filled.Favorite, FeedKey)
    data object Profile : AppBottomNavItem("Profile", Icons.Filled.Person, ProfileKey)
}

@Composable
fun NavBottomBar(modifier: Modifier = Modifier) {
    // Instantiate TopLevelBackStack, remembering it across recompositions.
    // HomeKey is the starting tab.
    val topLevelBackStack = remember { TopLevelBackStack<NavKey>(HomeKey) }

    // Create the entry provider using the function from Nav3Destinations
    // Pass the navigation actions from our TopLevelBackStack instance.
    // This will now be of type: (NavKey) -> NavEntry<NavKey>
    val appEntryProvider = remember(topLevelBackStack) {
        createNav3EntryProvider(
            onNavigate = topLevelBackStack::add, // Navigating to a new screen within the current tab
            onBack = topLevelBackStack::removeLast // Going back within the current tab
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppBottomBar(
                // The current top-level key determines the selected tab
                currentTopLevelKey = topLevelBackStack.topLevelKey,
                onNavigateToTopLevel = { newTopLevelKey ->
                    // Switch to a different top-level tab
                    topLevelBackStack.switchTopLevel(newTopLevelKey)
                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            // NavDisplay observes the combined backStack from TopLevelBackStack
            backStack = topLevelBackStack.backStack,
            modifier = Modifier.padding(innerPadding),
            entryProvider = appEntryProvider, // <<< SIMPLIFIED AND CORRECTED
            onBack = { count ->
                // Handle back requests from NavDisplay (e.g., system back press)
                // by telling TopLevelBackStack to pop 'count' items.
                topLevelBackStack.removeLast(count)
            }
        )
    }
}

@Composable
private fun AppBottomBar(
    currentTopLevelKey: NavKey,
    onNavigateToTopLevel: (NavKey) -> Unit
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
                // An item is selected if its navKey matches the current topLevelKey
                selected = currentTopLevelKey == item.navKey,
                onClick = {
                    onNavigateToTopLevel(item.navKey)
                }
            )
        }
    }
}
