package com.ylabz.basepro.applications.bike.features.main.ui

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ylabz.basepro.applications.bike.features.main.ui.components.ErrorScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.LoadingScreen
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.BikeDashboardContent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.WaitingForGpsScreen
import com.ylabz.basepro.ashbike.mobile.features.glass.GlassesMainActivity
import com.ylabz.basepro.core.ui.BikeScreen
import com.ylabz.basepro.core.ui.NavigationCommand
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.places.ui.CoffeeShopEvent
import com.ylabz.basepro.feature.places.ui.CoffeeShopUIState
import com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalProjectedApi::class)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun BikeUiRoute(
    modifier: Modifier = Modifier,
    navTo: (NavigationCommand) -> Unit,
    viewModel: BikeViewModel
) {
    val healthViewModel = hiltViewModel<HealthViewModel>()
    val nfcViewModel = hiltViewModel<NfcViewModel>()
    val coffeeShopViewModel = hiltViewModel<CoffeeShopViewModel>() // Added CoffeeShopViewModel

    val bikeUiState by viewModel.uiState.collectAsState()
    val cafeUiState by coffeeShopViewModel.uiState.collectAsState() // Added Cafe UI State

    Log.d(
        "BikeUiRoute_InstanceTest",
        "BikeUiRoute using BikeViewModel instance: ${viewModel.hashCode()}"
    )
    Log.d(
        "BikeUiRoute_StateLog",
        "BikeUiRoute recomposing with bikeUiState: ${bikeUiState::class.java.simpleName}, cafeUiState: ${cafeUiState::class.java.simpleName}"
    )

    val context = LocalContext.current

    // 1. Handle Service Binding Lifecycle
    // This ensures we bind when the screen opens and unbind when it closes
    DisposableEffect(Unit) {
        viewModel.bikeServiceManager.bindService(context)
        onDispose {
            viewModel.bikeServiceManager.unbindService(context)
        }
    }

    // 1. Listen for Side Effects
    // This runs in the coroutine scope of the Composable
    // --- BEST PRACTICE: Handle Side Effects Here ---
    LaunchedEffect(key1 = true) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BikeSideEffect.LaunchGlassProjection -> {
                    // This is where Android Framework code lives
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                        try {
                            val options = ProjectedContext.createProjectedActivityOptions(context)
                            val intent = Intent(context, GlassesMainActivity::class.java)
                            context.startActivity(intent, options.toBundle())
                        } catch (e: Exception) {
                            Toast.makeText(context, "Projection Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Requires Android 15+", Toast.LENGTH_SHORT).show()
                    }
                }
                is BikeSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val eventHandler = { event: BikeEvent ->
        when (event) {
            is BikeEvent.NavigateToSettingsRequested -> {
                val route = BikeScreen.SettingsBikeScreen.createRoute(event.cardKey)
                Log.d("BikeUiRoute", "Requesting TAB navigation to: $route")
                navTo(NavigationCommand.ToTab(route))
            }
            else -> {
                viewModel.onEvent(event)
            }
        }
    }

    when (val currentBikeUiState = bikeUiState) {
        is BikeUiState.WaitingForGps -> {
            WaitingForGpsScreen(
                onRequestPermission = { permissionState.launchPermissionRequest() },
                onEnableGpsSettings = {
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            )
        }

        is BikeUiState.Success -> {
            val coffeeShops = when (val currentCafeUiState = cafeUiState) {
                is CoffeeShopUIState.Success -> currentCafeUiState.coffeeShops
                else -> emptyList()
            }

            val onFindCafes = {
                val currentLocation = currentBikeUiState.bikeData.location
                if (currentLocation != null && (currentLocation.latitude != 0.0 || currentLocation.longitude != 0.0)) {
                    coffeeShopViewModel.onEvent(
                        CoffeeShopEvent.FindCafesInArea(
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude,
                            radius = 1000.0 // Radius in meters
                        )
                    )
                } else {
                    Log.d("BikeUiRoute", "Cannot find cafes: Location not available or is (0,0)")
                    // Optionally, inform the user e.g., via a Toast or a Snackbar
                }
            }



            if (currentBikeUiState.bikeData.location != null && (currentBikeUiState.bikeData.location?.latitude != 0.0 || currentBikeUiState.bikeData.location?.longitude != 0.0)) {
                BikeDashboardContent(
                    modifier = modifier.fillMaxSize(),
                    uiState = currentBikeUiState,
                    onBikeEvent = eventHandler,
                    navTo = navTo,
                    coffeeShops = coffeeShops, // Passed coffeeShops
                    placeName = null, // Passed null for placeName, decide source later
                    onFindCafes = onFindCafes as () -> Unit // Passed onFindCafes lambda
                )
            } else {
                WaitingForGpsScreen(
                    onRequestPermission = { permissionState.launchPermissionRequest() },
                    onEnableGpsSettings = {
                        context.startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                )
            }
        }

        is BikeUiState.Error -> {
            ErrorScreen(
                errorMessage = currentBikeUiState.message,
                onRetry = { viewModel.onEvent(BikeEvent.StartRide) }
            )
        }

        BikeUiState.Loading -> {
            LoadingScreen()
        }

        BikeUiState.Idle -> {
            LoadingScreen()
        }
    }
}
