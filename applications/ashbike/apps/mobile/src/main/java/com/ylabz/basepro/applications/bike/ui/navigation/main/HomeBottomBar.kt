package com.ylabz.basepro.applications.bike.ui.navigation.main

import android.util.Log
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
// import androidx.compose.material3.NavigationBarItemDefaults // No longer needed for custom colors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
// import androidx.compose.ui.graphics.Color // No longer needed for custom colors
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ylabz.basepro.applications.bike.R
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.AshBikeTabRoutes
import com.ylabz.basepro.core.ui.R as CoreUiR

private data class TabInfo(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@Composable
fun HomeBottomBar(
    currentNavController: NavHostController,
    onTabSelected: (route: String) -> Unit,
    unsyncedRidesCount: Int,
    showSettingsProfileAlert: Boolean
) {
    val tabs = remember(unsyncedRidesCount, showSettingsProfileAlert) { // Added keys here
        listOf(
            TabInfo(
                route = AshBikeTabRoutes.HOME_ROOT,
                titleResId = CoreUiR.string.navigation_home,
                selectedIcon = Icons.TwoTone.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNews = false,
                badgeCount = null // Home tab doesn't use badgeCount
            ),
            TabInfo(
                route = AshBikeTabRoutes.TRIPS_ROOT,
                titleResId = R.string.bike_bottom_nav_ride,
                selectedIcon = Icons.AutoMirrored.TwoTone.List,
                unselectedIcon = Icons.AutoMirrored.Outlined.List,
                hasNews = false, // Trips tab uses badgeCount, not hasNews for this scenario
                badgeCount = unsyncedRidesCount // Uses the current value
            ),
            TabInfo(
                route = AshBikeTabRoutes.SETTINGS_ROOT,
                titleResId = CoreUiR.string.action_settings,
                selectedIcon = Icons.TwoTone.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                hasNews = showSettingsProfileAlert, // Uses the current value
                badgeCount = null // Settings tab uses hasNews, not badgeCount for this scenario
            )
        )
    }

    val navBackStackEntry by currentNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // Log.d("HomeBottomBar", "Current Destination Route: ${currentDestination?.route}") // Optional: Keep for debugging

    val selectedItemIndex =
        remember(currentDestination, tabs) { // tabs is now a key too, ensuring this recalculates if tabs list object changes
            val index = tabs.indexOfFirst { tabInfo ->
                currentDestination?.hierarchy?.any { it.route == tabInfo.route } == true
            }
            val finalIndex = if (index != -1) index else 0
            // Log.d("HomeBottomBar", "Calculated selectedItemIndex: $finalIndex for destination: ${currentDestination?.route}") // Optional: Keep for debugging
            finalIndex
        }

    NavigationBar(
        // contentColor = MaterialTheme.colorScheme.primary // This is for the content of the NavigationBar itself, not items usually
    ) {
        tabs.forEachIndexed { index, tabInfo ->
            val displayTitle = stringResource(id = tabInfo.titleResId)
            val isSelected = selectedItemIndex == index
            // Log.d("HomeBottomBar", "TabItem '${tabInfo.route}' (index $index): isSelected = $isSelected (selectedItemIndex: $selectedItemIndex)") // Optional: Keep for debugging

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // Log.d("HomeBottomBar", "Tab clicked: ${tabInfo.route}") // Optional: Keep for debugging
                    onTabSelected(tabInfo.route)
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
                            imageVector = if (isSelected) {
                                tabInfo.selectedIcon
                            } else tabInfo.unselectedIcon,
                            contentDescription = displayTitle
                        )
                    }
                }
                // Removed explicit colors = NavigationBarItemDefaults.colors(...)
                // to use default Material 3 theme colors.
            )
        }
    }
}

/*
@Preview
@Composable
fun BikeHomeBottomBarPreview() {
    // Preview would need to be updated to correctly reflect theme colors
    // and provide NavHostController with a flow for currentDestination
    // val navController = rememberNavController()
    // HomeBottomBar(
    //     currentNavController = navController,
    //     onTabSelected = { route -> println("Tab selected: $route") },
    //     unsyncedRidesCount = 3,
    //     showSettingsProfileAlert = true
    // )
}
*/