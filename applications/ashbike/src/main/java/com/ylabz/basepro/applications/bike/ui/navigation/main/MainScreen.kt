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
import androidx.compose.ui.graphics.vector.ImageVector 
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
                    onTabSelected = { navigationKey ->
                        Log.d("MainScreen", "onTabSelected called with key: $navigationKey") // LOGGING
                        val route = when (navigationKey) {
                            "Home" -> AshBikeTabRoutes.HOME_ROOT
                            "Ride" -> AshBikeTabRoutes.TRIPS_ROOT
                            "Settings" -> AshBikeTabRoutes.SETTINGS_ROOT
                            else -> AshBikeTabRoutes.HOME_ROOT 
                        }
                        Log.d("MainScreen", "Navigating to route: $route") // LOGGING
                        navController.navigate(route) {
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
                    bikeViewModel = bikeViewModel // <<< MODIFIED LINE: Pass the bikeViewModel instance
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


// This data class might be defined in HomeBottomBar.kt or a shared file.
// If it's only used by the old HomeBottomBar, it might be removable if HomeBottomBar doesn't expose it.
// For now, keeping it here if it's referenced elsewhere in this file or package.
// data class BottomNavigationItem(
//     val title: String,
//     val selectedIcon: ImageVector,
//     val unselectedIcon: ImageVector,
//     val hasNews: Boolean,
//     val badgeCount: Int? = null
// )

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
        unsyncedRidesCount = 2,
        showSettingsProfileAlert = true // Add default for preview
    )
}

 */