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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy // Added import
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ylabz.basepro.applications.bike.R
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.AshBikeTabRoutes
import com.ylabz.basepro.core.ui.R as CoreUiR

private data class TabInfo(
    val navigationKey: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@Composable
fun HomeBottomBar(
    currentNavController: NavHostController,
    onTabSelected: (navigationKey: String) -> Unit,
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

    val navBackStackEntry by currentNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // Keep this log for the raw current destination's route if helpful
    Log.d("HomeBottomBar", "Current Destination Route: ${currentDestination?.route}") 

    val selectedItemIndex =
        remember(currentDestination, tabs) { // Keyed by currentDestination now
            val index = tabs.indexOfFirst { tabInfo ->
                val tabBaseRoute = when (tabInfo.navigationKey) {
                    "Home" -> AshBikeTabRoutes.HOME_ROOT
                    "Ride" -> AshBikeTabRoutes.TRIPS_ROOT
                    "Settings" -> AshBikeTabRoutes.SETTINGS_ROOT
                    else -> null
                }
                // Check if the tabBaseRoute is part of the current destination's hierarchy
                currentDestination?.hierarchy?.any { it.route == tabBaseRoute } == true
            }
            val finalIndex = if (index != -1) index else 0
            Log.d("HomeBottomBar", "Calculated selectedItemIndex: $finalIndex for destination: ${currentDestination?.route}")
            finalIndex
        }

    NavigationBar(
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEachIndexed { index, tabInfo ->
            val displayTitle = stringResource(id = tabInfo.titleResId)
            val isSelected = selectedItemIndex == index
            Log.d("HomeBottomBar", "TabItem '${tabInfo.navigationKey}' (index $index): isSelected = $isSelected (selectedItemIndex: $selectedItemIndex)")

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    Log.d("HomeBottomBar", "Tab clicked: ${tabInfo.navigationKey}")
                    onTabSelected(tabInfo.navigationKey)
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
                },
                colors = NavigationBarItemDefaults.colors( // Diagnostic colors
                    selectedIconColor = Color.Red, 
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Red, 
                    unselectedTextColor = Color.Gray, 
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant 
                )
            )
        }
    }
}

/*
@Preview
@Composable
fun BikeHomeBottomBarPreview() {
    // Preview would need to be updated 
    // val navController = rememberNavController() 
    // HomeBottomBar(
    //     currentNavController = navController,
    //     onTabSelected = { key -> println("Tab selected: $key") }, 
    //     unsyncedRidesCount = 3,
    //     showSettingsProfileAlert = true
    // )
}
*/
