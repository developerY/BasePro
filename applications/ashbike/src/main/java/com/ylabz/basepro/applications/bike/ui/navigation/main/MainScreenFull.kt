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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenFull(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    nfcUiState: NfcUiState,
    nfcEvent: (NfcRwEvent) -> Unit,
    bikeRideInfo : BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val bottomNavigationItems = listOf(
        BikeScreen.HomeBikeScreen,
        BikeScreen.TripBikeScreen,
        BikeScreen.SettingsBikeScreen
    )
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(title = { Text("AshBike") }) },
        bottomBar = { HomeBottomBar(navController = navController) },
    ) { paddingVals ->
        NavHost(navController, startDestination = BikeScreen.HomeBikeScreen.route) {

            composable(
                BikeScreen.HomeBikeScreen.route,
                /*arguments = listOf(navArgument("stationID") { type = NavType.IntType defaultValue = 0 })*/
            ) {
                BikeDashboardContent(
                    modifier = Modifier.padding(paddingVals),
                    bikeRideInfo = bikeRideInfo,
                    onBikeEvent = onBikeEvent,
                    navTo = navTo
                )
            }//{ backStackEntry -> }

            composable(
                BikeScreen.TripBikeScreen.route, //.plus("?imgPath={imgPath}"),
                // arguments = listOf(navArgument("statoinID") { defaultValue = "no Station ID" })
                // NavScreen.Meds.route.plus("/{imgPath}") ,
            ) {
                Text(modifier = Modifier.padding(paddingVals), text = "Trip")

            }

            composable(BikeScreen.SettingsBikeScreen.route) {
                Text(modifier = Modifier.padding(paddingVals), text = "Settings")
            }
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
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val items = listOf(
        BottomNavigationItem(
            title = "home",
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


