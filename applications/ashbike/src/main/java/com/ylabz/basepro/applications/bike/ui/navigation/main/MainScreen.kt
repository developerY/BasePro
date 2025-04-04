package com.ylabz.basepro.applications.bike.ui.navigation.main


import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.DrawerState
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.Screen
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val bottomNavigationItems = listOf(
        BikeScreen.HomeBikeScreen,
        BikeScreen.TripBikeScreen,
        BikeScreen.SettingsBikeScreen
    )
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text("AshBike") }) },
        bottomBar = {HomeBottomBar(navController = navController, items = bottomNavigationItems)},
    ) { paddingVals ->
        NavHost(navController, startDestination = BikeScreen.HomeBikeScreen.route) {


            composable(
                BikeScreen.HomeBikeScreen.route,
                /*arguments = listOf(navArgument("stationID") {
                type = NavType.IntType
                defaultValue = 0
            })*/
            ) {
                Text(modifier = Modifier.padding(paddingVals), text = "Home")
            }//{ backStackEntry -> }

            composable(
                BikeScreen.TripBikeScreen.route, //.plus("?imgPath={imgPath}"),
                // arguments = listOf(navArgument("statoinID") { defaultValue = "no Station ID" })
                // NavScreen.Meds.route.plus("/{imgPath}") ,
            ) {
                Text(modifier = Modifier.padding(paddingVals), text = "Home")

            }

            composable(BikeScreen.SettingsBikeScreen.route) {
                Text(modifier = Modifier.padding(paddingVals), text = "Home")
            }
        }
    }
}

@Composable
fun HomeBottomBar(navController: NavHostController, items: List<BikeScreen>) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navController.currentBackStackEntry?.destination
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(id = screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any {
                    it.route == screen.route
                } == true,
                // alwaysShowLabels = false, // This hides the title for the unselected items
                onClick = {
                    // Log.d(TAG, "This is the route ${currentRoute} -> ${screen.route}")
                    // This if check gives us a "singleTop" behavior where we do not create a
                    // second instance of the composable if we are already on that destination
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
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
    when (tabTitle) {
        "Home"-> navController.navigate(Screen.HomeScreen.route)
        "List" -> navController.navigate(Screen.ListScreen.route)
        "Settings" -> navController.navigate(Screen.SettingsScreen.route)
    }
}

@Composable
fun HomeBottomBar(
    route: String,
    navController: NavHostController,
    //state: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {

    val items = listOf(
        BottomNavigationItem(
            title = route + "hi",
            selectedIcon = Icons.TwoTone.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "List",
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


