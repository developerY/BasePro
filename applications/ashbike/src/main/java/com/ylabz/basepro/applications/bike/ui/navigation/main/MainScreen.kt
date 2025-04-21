package com.ylabz.basepro.applications.bike.ui.navigation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.core.ui.BikeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("AshBike") }) }, //  MinTopAppBar()
        bottomBar = { HomeBottomBar(navController = navController) },
    ) { innerPadding ->
        MainNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
        /*RootNavGraph(
            modifier = Modifier.padding(innerPadding),
            navHostController = navController
        )*/
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

// only used by the bottom bar
private fun navigateTo(tabTitle: String, navController: NavHostController) {
    val route = when (tabTitle) {
        "Home"-> BikeScreen.HomeBikeScreen.route
        "Ride" -> BikeScreen.TripBikeScreen.route
        "Settings" -> BikeScreen.SettingsBikeScreen.route
        else -> BikeScreen.HomeBikeScreen.route
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

@Composable
fun HomeBottomBar(
    navController: NavHostController
) {

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
            badgeCount = 0
        ),
        BottomNavigationItem(
            title = "Settings", // Category -> Cat
            selectedIcon = Icons.TwoTone.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = true,
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
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
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


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}

@Preview
@Composable
fun HomeBottomBarPreview() {
    HomeBottomBar(navController = rememberNavController())
}
