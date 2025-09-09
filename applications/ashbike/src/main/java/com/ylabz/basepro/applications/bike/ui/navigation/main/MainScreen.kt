package com.ylabz.basepro.applications.bike.ui.navigation.main

//import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log // Added import
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
// import androidx.compose.ui.graphics.vector.ImageVector // No longer directly used here
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.AshBikeTabRoutes
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph

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
    val unsyncedRidesCount by viewModel.unsyncedRidesCount.collectAsState()
    val context = LocalContext.current

    // --- Settings ViewModel for Profile Alert ---
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val showProfileAlert = when (val state = settingsUiState) {
        is SettingsUiState.Success -> state.isProfileIncomplete
        else -> true // Default to true (show alert) if state is not Success (e.g., Loading)
    }

    // --- Service Binding Logic ---
    val bikeViewModel: BikeViewModel = hiltViewModel() // Instance for MainScreen and its children
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, bikeViewModel) { // Added bikeViewModel as a key
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    bikeViewModel.bindToService(context)
                }

                Lifecycle.Event.ON_STOP -> {
                    bikeViewModel.unbindFromService(context)
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var hasLocationPermissions by remember {
        mutableStateOf(
            locationPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsMap ->
            hasLocationPermissions =
                permissionsMap.values.reduce { acc, isGranted -> acc && isGranted }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermissions) {
            permissionsLauncher.launch(locationPermissions)
        }
    }

    if (hasLocationPermissions) {
        Scaffold(
            topBar = { TopBarForCurrentRoute(navController) },
            bottomBar = {
                HomeBottomBar(
                    currentNavController = navController,
                    onTabSelected = { route -> // route is now directly from AshBikeTabRoutes
                        Log.d("MainScreen", "onTabSelected called with route: $route") // LOGGING
                        navController.navigate(route) { // Use the route directly
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    unsyncedRidesCount = unsyncedRidesCount,
                    showSettingsProfileAlert = showProfileAlert
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AshBikeTabRoutes.HOME_ROOT,
                modifier = Modifier.padding(innerPadding)
            ) {
                bikeNavGraph(
                    modifier = Modifier,
                    navHostController = navController,
                    bikeViewModel = bikeViewModel
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Location permissions are required. Please grant them in app settings or restart the app to try again.")
            // Consider adding buttons to request again or go to settings.
        }
    }
}

// The BottomNavigationItem data class previously here can be removed if it was only for the old HomeBottomBar logic
// and is not used elsewhere in this file or package.

/*
@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = rememberNavController())
}

// Preview for HomeBottomBar would now need a NavHostController that is actually navigating
// to correctly test the selected item logic, or be simplified to just show the bar.
// @Preview
// @Composable
// fun HomeBottomBarPreview() {
//     val navController = rememberNavController()
//     HomeBottomBar(
//         currentNavController = navController,
//         onTabSelected = { route -> println("Tab selected with route: $route") },
//         unsyncedRidesCount = 2,
//         showSettingsProfileAlert = true
//     )
// }
*/