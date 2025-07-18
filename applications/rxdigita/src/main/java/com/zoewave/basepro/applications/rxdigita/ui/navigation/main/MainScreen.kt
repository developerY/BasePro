package com.zoewave.basepro.applications.rxdigita.ui.navigation.main

import android.Manifest // Required for permission strings
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat // Required for ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
import com.ylabz.basepro.core.ui.RxDigitaScreen
import com.zoewave.basepro.applications.rxdigita.ui.navigation.graphs.RxDigitaNavGraph


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
            startDestination = {},//RxDigitaScreen.HomeBikeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            RxDigitaNavGraph(
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
