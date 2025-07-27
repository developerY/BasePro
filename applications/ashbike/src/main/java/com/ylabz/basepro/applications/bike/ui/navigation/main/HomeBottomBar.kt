package com.ylabz.basepro.applications.bike.ui.navigation.main

import android.R.id.tabs
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource // Added for localization
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
// Import the R class from your 'ashbike' module.
// The exact package will depend on your module's namespace defined in its build.gradle.kts
import com.ylabz.basepro.applications.bike.R
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.R as CoreUiR // Added import

// Local data class to hold tab information, including the navigation key and title resource ID
private data class TabInfo(
    val navigationKey: String,   // Non-localized key for routing (e.g., "Home")
    val titleResId: Int,         // Resource ID for the display title (e.g., R.string.bike_bottom_nav_home)
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@Composable
fun HomeBottomBar(
    navController: NavHostController,
    unsyncedRidesCount: Int, // Accept the count as a parameter
    showSettingsProfileAlert: Boolean // New parameter
) {
    // Define the tabs using the TabInfo data class
    val tabs = listOf(
        TabInfo(
            navigationKey = "Home", // This key is used by navigateTo
            titleResId = CoreUiR.string.navigation_home, // Localized display title - UPDATED
            selectedIcon = Icons.TwoTone.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        TabInfo(
            navigationKey = "Ride",
            titleResId = R.string.bike_bottom_nav_ride, // This one is ashbike specific, leave as is
            selectedIcon = Icons.AutoMirrored.TwoTone.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            hasNews = false,
            badgeCount = unsyncedRidesCount // Use the passed-in count
        ),
        TabInfo(
            navigationKey = "Settings",
            titleResId = CoreUiR.string.action_settings, // UPDATED
            selectedIcon = Icons.TwoTone.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = showSettingsProfileAlert
        )
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    // Consider making initial selection more robust by checking current route
    // against navController, if needed. For now, it defaults to the first item.

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.primary // Consider using MaterialTheme.colorScheme for theming
    ) {
        tabs.forEachIndexed { index, tabInfo ->
            // Get the localized display title using the resource ID
            val displayTitle = stringResource(id = tabInfo.titleResId)

            NavigationBarItem(
                //colors = NavigationBarItemColors(),
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    // Use the non-localized navigationKey for routing
                    navigateTo(tabInfo.navigationKey, navController = navController)
                },
                label = {
                    // Display the localized title
                    Text(text = displayTitle)
                },
                alwaysShowLabel = false, // Or true, based on your design preference
                icon = {
                    BadgedBox(
                        badge = {
                            if (tabInfo.badgeCount != null && tabInfo.badgeCount > 0) {
                                Badge {
                                    Text(text = tabInfo.badgeCount.toString())
                                }
                            } else if (tabInfo.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                tabInfo.selectedIcon
                            } else tabInfo.unselectedIcon,
                            // Use the localized title for accessibility
                            contentDescription = displayTitle
                        )
                    }
                }
            )
        }
    }
}

// This function remains UNCHANGED as it relies on the non-localized keys.
private fun navigateTo(tabTitle: String, navController: NavHostController) {
    val route = when (tabTitle) { // tabTitle will be "Home", "Ride", or "Settings"
        "Home"-> BikeScreen.HomeBikeScreen.route
        "Ride" -> BikeScreen.TripBikeScreen.route
        "Settings" -> BikeScreen.SettingsBikeScreen.route
        else -> BikeScreen.HomeBikeScreen.route // Default route
    }

    navController.navigate(route) {
        // Avoid multiple copies of the same destination
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid reloading the same destination if already on it
        launchSingleTop = true
        // Restore state when reselecting a previously selected tab
        restoreState = true
    }
}
