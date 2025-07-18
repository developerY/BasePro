package com.zoewave.basepro.applications.rxdigita.ui.navigation.main

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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
// Removed HiltViewModel and TripsViewModel imports as they are no longer needed here
// import androidx.hilt.navigation.compose.hiltViewModel
// import com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel
// import androidx.compose.runtime.collectAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.ylabz.basepro.core.ui.RxDigitaScreen

@Composable
fun HomeBottomBar(
    navController: NavHostController,
    //showSettingsProfileAlert: Boolean // New parameter
) {

    // val unsyncedRidesCount by tripsViewModel.unsyncedRidesCount.collectAsState() // Removed

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.TwoTone.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Ride",
            selectedIcon = Icons.AutoMirrored.TwoTone.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Settings", // Category -> Cat
            selectedIcon = Icons.TwoTone.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false //showSettingsProfileAlert, // Use the new parameter
        ),
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        contentColor = Color.Blue
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                //colors = NavigationBarItemColors(),
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navigateTo(item.title, navController = navController)
                },
                label = {
                    Text(text = item.title)
                },
                alwaysShowLabel = false,
                icon = {
                    BadgedBox(
                        badge = {
                            // Updated condition: show badge if badgeCount is greater than 0
                            if (item.badgeCount != null && item.badgeCount > 0) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) { // Existing logic for other types of badges (e.g., "Settings")
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

private fun navigateTo(tabTitle: String, navController: NavHostController) {
    val route = when (tabTitle) {
        "Home"-> RxDigitaScreen.HomeRxDigitaScreen.route
        "Ride" -> RxDigitaScreen.TripRxDigitaScreen.route
        "Settings" -> RxDigitaScreen.SettingsRxDigitaScreen.route
        else -> RxDigitaScreen.HomeRxDigitaScreen.route // Default fallback
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
