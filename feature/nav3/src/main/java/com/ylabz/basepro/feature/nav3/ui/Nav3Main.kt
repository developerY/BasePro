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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navigation

// Assuming this is defined elsewhere
sealed class NavDestinations(val route: String, val title: String, val icon: ImageVector) {
    data object Home : NavDestinations("home_route", "Home", Icons.Filled.Home)
    data object Feed : NavDestinations("feed_route", "Feed", Icons.Filled.Favorite)
    data object Profile : NavDestinations("profile_route", "Profile", Icons.Filled.Person)
}

@Composable
fun Nav3Main(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestinations.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestinations.Home.route) {
                // Your Home screen composable here
                Text("Home Screen")
            }
            composable(NavDestinations.Feed.route) {
                // Your Feed screen composable here
                Text("Feed Screen")
            }
            composable(NavDestinations.Profile.route) {
                // Your Profile screen composable here
                Text("Profile Screen")
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val navItems = listOf(
        NavDestinations.Home,
        NavDestinations.Feed,
        NavDestinations.Profile
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid a large back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}