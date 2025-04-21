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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph
import com.ylabz.basepro.core.ui.BikeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopBarForCurrentRoute(navController)}, //TopAppBar(title = { Text("AshBike") }) }, //  MinTopAppBar()
        bottomBar = { HomeBottomBar(navController = navController) },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BikeScreen.HomeBikeScreen.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            bikeNavGraph(modifier = Modifier, navHostController = navController)
        }
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
