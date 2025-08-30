package com.zoewave.basepro.applications.rxdigita.ui.navigation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.core.ui.RxDigitaScreen
import com.zoewave.basepro.applications.rxdigita.ui.navigation.graphs.rxDigitaNavGraph


// The @RequiresPermission annotation can be helpful for static analysis
// but the runtime check is the most crucial part.
// It should be on the composable that directly uses the permission-gated features
// or the screen that orchestrates it.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopBarForCurrentRoute(navController) },
        bottomBar = {
            HomeBottomBar(
                navController = navController,
                //unsyncedRidesCount = unsyncedRidesCount,
                //showSettingsProfileAlert = showProfileAlert // Pass the new state here
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RxDigitaScreen.HomeRxDigitaScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            rxDigitaNavGraph(
                modifier = Modifier,
                navHostController = navController,
                //bikeViewModel = rxDigitaViewModel // <<< MODIFIED LINE: Pass the bikeViewModel instance
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
/*
@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}

@Preview
@Composable
fun HomeBottomBarPreview() {
    HomeBottomBar(
        navController = rememberNavController(),
    )
}
*/