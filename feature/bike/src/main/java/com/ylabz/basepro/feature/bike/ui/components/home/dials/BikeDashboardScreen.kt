package com.ylabz.basepro.feature.bike.ui.components.home.dials

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier

@Composable
fun BikeDashboardScreen(
    // e.g., from your ViewModel
    distance: Double,
    duration: String,
    avgSpeed: Double,
    elevation: Double,
    battery: Int,
    motorPower: Double,
    heartRate: Int,
    calories: Double
) {
    Column(modifier = Modifier) {
        /* 1) Main stats row
        val mainStats = listOf(
            StatItem(
                icon = Icons.Filled.Speed,
                label = "Avg Speed",
                value = "${avgSpeed} km/h"
            ),
            StatItem(
                icon = Icons.Filled.Terrain,
                label = "Elevation",
                value = "${elevation} m"
            ),
            // Add distance/duration if you want them here or in SpeedAndProgressCard
        )
        StatsSection(stats = mainStats)*/

        // 2) E-bike stats row
        val eBikeStats = listOf(
            StatItem(
                icon = Icons.Filled.BatteryChargingFull,
                label = "Battery",
                value = "${battery}%"
            ),
            StatItem(
                icon = Icons.AutoMirrored.Filled.DirectionsBike,
                label = "Motor",
                value = "${motorPower} W"
            )
        )
        StatsSection(stats = eBikeStats)

        // 3) Health stats row
        val healthStats = listOf(
            StatItem(
                icon = Icons.Filled.Favorite,
                label = "Heart Rate",
                value = "${heartRate} bpm"
            ),
            StatItem(
                icon = Icons.Filled.Fireplace,
                label = "Calories",
                value = "${calories}"
            )
        )
        StatsSection(stats = healthStats)
    }
}

@Preview
@Composable
fun BikeDashboardScreenPreview() {
    BikeDashboardScreen(
        distance = 10.5,
        duration = "30:00",
        avgSpeed = 21.0,
        elevation = 150.0,
        battery = 80,
        motorPower = 250.0,
        heartRate = 120,
        calories = 300.0
    )
}
