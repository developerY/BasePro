package com.ylabz.basepro.applications.bike.ui.navigation.main

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
import com.ylabz.basepro.applications.bike.ui.navigation.graphs.bikeNavGraph
import com.ylabz.basepro.core.ui.BikeScreen

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
            hasLocationPermissions = permissionsMap.values.reduce { acc, isGranted -> acc && isGranted }
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
            bottomBar = { HomeBottomBar(navController = navController, unsyncedRidesCount = unsyncedRidesCount) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BikeScreen.HomeBikeScreen.route,
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
    HomeBottomBar(navController = rememberNavController(), unsyncedRidesCount = 2)
}
