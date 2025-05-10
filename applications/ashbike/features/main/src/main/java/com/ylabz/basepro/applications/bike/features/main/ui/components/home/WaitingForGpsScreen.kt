package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryUnknown
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatsRow
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState

import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@RequiresPermission(anyOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
fun WaitingForGpsScreen(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit,
    onEnableGpsSettings: () -> Unit
) {
    val context = LocalContext.current

    // Accompanist’s PermissionState
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // helper boolean
    val hasPermission = permissionState.status == PermissionStatus.Granted

    // get the LocationManager once
    val lm = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // track GPS toggle state
    //  Track GPS on/off
    var gpsEnabled by remember {
        mutableStateOf(
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        )
    }

    // only register for updates *if* we really have the permission
    //  Only request updates if we have the permission
    DisposableEffect(hasPermission) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(loc: Location) { /*…*/ }
            override fun onProviderEnabled(name: String)  { gpsEnabled = true }
            override fun onProviderDisabled(name: String) { gpsEnabled = false }
            override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {}
        }

        if (hasPermission &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L, 0f, listener
            )
        }

        onDispose {
            lm.removeUpdates(listener)
        }
    }
    // 3) animate a rotating / pulsing icon
    val infinite = rememberInfiniteTransition()
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val scale by infinite.animateFloat(
        initialValue = 0.8f,
        targetValue  = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Waiting for GPS…")

            Spacer(Modifier.height(16.dp))

            // 1) Ask for the permission
            if (!hasPermission) {
                Text("Location permission is required")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRequestPermission) {
                    Text("Grant permission")
                }
                return@Column
            }

            // 2) If GPS is off, prompt user to enable it
            if (!gpsEnabled) {
                Text("GPS is turned off")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onEnableGpsSettings) {
                    Text("Open location settings")
                }
                return@Column
            }

            // finally: permission granted & GPS on
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("Acquiring your location…")
        }
    }
}
