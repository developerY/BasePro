package com.ylabz.basepro.applications.bike.features.main.ui.components.home.demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.core.model.bike.BikeRideInfo

@Composable
fun BikeDashboardLook(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val isBikeConnected = bikeRideInfo.isBikeConnected
    val heartRate       = null // bikeRideInfo.heartRate    // may be null
    val calories        = bikeRideInfo.caloriesBurned

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement   = Arrangement.spacedBy(16.dp),
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        // ─── 1) Top gauge Card ──────────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth(),
            colors    = CardDefaults.cardColors(containerColor = Color(0xFF467AD0)),
            shape     = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            SpeedAndProgressCard(
                bikeRideInfo = bikeRideInfo,
                onBikeEvent  = onBikeEvent,
                navTo        = navTo
            )
        }

        // ─── 2) Stats Row: Distance | Duration | Avg Speed ────────────────
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCardLook(
                label = "Distance",
                value = "%.1f km".format(bikeRideInfo.currentTripDistance),
                modifier = Modifier.weight(1f)
            )
            MetricCardLook(
                label = "Duration",
                value = bikeRideInfo.rideDuration, // expected "HH:mm" format
                modifier = Modifier.weight(1f)
            )
            MetricCardLook(
                label = "Avg Speed",
                value = "%.1f km/h".format(bikeRideInfo.averageSpeed),
                modifier = Modifier.weight(1f)
            )
        }

        // ─── 3) Health Row: Heart Rate | Calories ─────────────────────────
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCardLook(
                label = "Heart Rate",
                value = heartRate?.let { "$it bpm" } ?: "-- bpm",
                modifier = Modifier.weight(1f)
            )
            MetricCardLook(
                label = "Calories",
                value = if (calories != null) "$calories kcal" else "-- kcal",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // ─── 4) E-bike Stats Accordion ──────────────────────────────────────
        var expanded by rememberSaveable { mutableStateOf(false) }
        Card(
            modifier  = Modifier.fillMaxWidth(),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape     = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("E-bike Stats", style = MaterialTheme.typography.titleMedium)
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = if (isBikeConnected) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.3f)
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit  = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment= Alignment.CenterHorizontally
                    ) {
                        // Battery & Motor stats
                        val eBikeStats = listOf(
                            StatItem(
                                icon = Icons.Filled.BatteryChargingFull,
                                label = "Battery",
                                value = if (isBikeConnected && bikeRideInfo.batteryLevel != null)
                                    "${bikeRideInfo.batteryLevel}%"
                                else "--%"
                            ),
                            StatItem(
                                icon = Icons.AutoMirrored.Filled.DirectionsBike,
                                label = "Motor",
                                value = if (isBikeConnected && bikeRideInfo.motorPower != null)
                                    "${bikeRideInfo.motorPower} W"
                                else "-- W"
                            )
                        )
                        StatsSection(stats = eBikeStats)

                        // Connect Bike Button / Status
                        BikeBatteryLevels(
                            isConnected     = isBikeConnected,
                            batteryLevel    = bikeRideInfo.batteryLevel,
                            onConnectClick  = { onBikeEvent(BikeEvent.Connect) }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BikeDashboardLookPreview() {
    val sampleBikeRideInfo = BikeRideInfo(
        location = null,
        currentSpeed = 15.5,
        averageSpeed = 18.2,
        maxSpeed = 30.1,
        currentTripDistance = 10.5f,
        totalTripDistance = 150.2f,
        remainingDistance = 5.0f,
        elevationGain = 50.0,
        elevationLoss = 20.0,
        caloriesBurned = 450,
        rideDuration = "01:35",
        settings = mapOf("Assistance" to listOf("Level 3"), "Lights" to listOf("Auto")),
        heading = 45.0f,
        elevation = 150.0,
        isBikeConnected = true,
        batteryLevel = 80,
        motorPower = 300.0f,
        rideState = com.ylabz.basepro.core.model.bike.RideState.Riding,
        bikeWeatherInfo = null
    )

    BikeDashboardLook(
        bikeRideInfo = sampleBikeRideInfo,
        onBikeEvent = {}, // Dummy lambda for preview
        navTo = {}       // Dummy lambda for preview
    )
}

@Preview(showBackground = true)
@Composable
fun MetricCardLookPreview() {
    MetricCardLook(label = "Test Label", value = "123 Units")
}


// ──────────────────────────────────────────────────────────────────────────────
// A reusable equal-width card for any “label / value” metric
// ──────────────────────────────────────────────────────────────────────────────
@Composable
fun MetricCardLook(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.height(80.dp),
        shape     = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFDCEEFB))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
