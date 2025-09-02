package com.ylabz.basepro.applications.bike.ui.navigation.main

//import androidx.compose.ui.tooling.preview.Preview
// Import the R class from your 'ashbike' module.
// The exact package will depend on your module's namespace defined in its build.gradle.kts
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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ylabz.basepro.applications.bike.R
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.R as CoreUiR

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
    unsyncedRidesCount: Int,
    showSettingsProfileAlert: Boolean
) {
    val tabs = listOf(
        TabInfo(
            navigationKey = "Home",
            titleResId = CoreUiR.string.navigation_home,
            selectedIcon = Icons.TwoTone.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        TabInfo(
            navigationKey = "Ride",
            titleResId = R.string.bike_bottom_nav_ride,
            selectedIcon = Icons.AutoMirrored.TwoTone.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            hasNews = false,
            badgeCount = unsyncedRidesCount
        ),
        TabInfo(
            navigationKey = "Settings",
            titleResId = CoreUiR.string.action_settings,
            selectedIcon = Icons.TwoTone.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = showSettingsProfileAlert
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItemIndex =
        remember(currentRoute, tabs) { // Ensure tabs is a key if it could change
            tabs.indexOfFirst { tabInfo ->
                // Helper to get the base route for a navigation key
                val tabBaseRoute = when (tabInfo.navigationKey) {
                    "Home" -> BikeScreen.HomeBikeScreen.route
                    "Ride" -> BikeScreen.TripBikeScreen.route
                    "Settings" -> BikeScreen.SettingsBikeScreen.route
                    else -> null
                }

                if (tabBaseRoute == null) return@indexOfFirst false

                // For settings, check if the current route starts with the settings base route
                // to account for arguments. For others, an exact match is fine.
                if (tabInfo.navigationKey == "Settings") {
                    currentRoute?.startsWith(tabBaseRoute) == true
                } else {
                    currentRoute == tabBaseRoute
                }
            }
                .let { if (it != -1) it else 0 } // Default to the first tab (Home) if no match or currentRoute is null
        }

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEachIndexed { index, tabInfo ->
            val displayTitle = stringResource(id = tabInfo.titleResId)

            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    // Navigate to the tab's route
                    // The selection will update reactively due to currentRoute changes
                    navigateTo(tabInfo.navigationKey, navController = navController)
                },
                label = {
                    Text(text = displayTitle)
                },
                alwaysShowLabel = false,
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
                            imageVector = if (selectedItemIndex == index) { // Use derived selectedItemIndex
                                tabInfo.selectedIcon
                            } else tabInfo.unselectedIcon,
                            contentDescription = displayTitle
                        )
                    }
                }
            )
        }
    }
}

private fun navigateTo(navigationKey: String, navController: NavHostController) {
    val route = when (navigationKey) {
        "Home" -> BikeScreen.HomeBikeScreen.route
        "Ride" -> BikeScreen.TripBikeScreen.route
        "Settings" -> BikeScreen.SettingsBikeScreen.route // Navigates to base settings route
        else -> BikeScreen.HomeBikeScreen.route
    }

    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
/*
@Preview
@Composable
fun BikeHomeBottomBarPreview() {
    val navController = rememberNavController()
    HomeBottomBar(
        navController = navController,
        unsyncedRidesCount = 3,
        showSettingsProfileAlert = true
    )
}
*/