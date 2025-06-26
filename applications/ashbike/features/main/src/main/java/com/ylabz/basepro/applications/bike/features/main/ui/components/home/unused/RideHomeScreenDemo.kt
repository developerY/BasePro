package com.ylabz.basepro.applications.bike.features.main.ui.components.home.unused

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHomeScreen(
    // 1) Wind
    windSpeedMps: Float,
    windDeg: Float,

    // 2) Weather
    weatherIcon: ImageVector,
    onWeatherClick: () -> Unit,

    // 3 & 4) Speed & Heading
    speedKmh: Float,
    headingDeg: Float,

    // 5) Start/Stop
    isRecording: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,

    // 5a) Route progress
    routeProgress: Float, // 0f..1f

    // 7–11) Stats
    distanceKm: Float,
    durationSeconds: Long,
    avgSpeedKmh: Float,
    heartRate: Int?,
    userStats: UserStats,      // contains .weightKg and .heightCm

    // e-bike
    eBikeAvailable: Boolean,
    onToggleEBike: () -> Unit,

    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ───  Big blue card with gauges ─────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth(),
            colors    = CardDefaults.cardColors(containerColor = Color(0xFF467AD0)),
            shape     = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(Modifier.padding(16.dp)) {
                // 1) Wind gauge (top-start)
                WindGauge(
                    speed = windSpeedMps,
                    direction = windDeg,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopStart)
                )

                // 2) Weather icon (top-end)
                IconButton(
                    onClick = onWeatherClick,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(weatherIcon, contentDescription = "Weather")
                }

                // 3) Speed gauge (center)
                SpeedGauge(
                    speed = speedKmh,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )

                // 4) Heading display (center-bottom)
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 88.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "${headingDeg.toInt()}° ${directionAbbreviation(headingDeg)}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 5) Start / Stop buttons (bottom corners)
                if (!isRecording) {
                    IconButton(
                        onClick = onStart,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.BottomStart)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start Ride")
                    }
                } else {
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.BottomEnd)
                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.Default.Stop, contentDescription = "Stop Ride",
                            tint = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }

                // 5a) Route progress bar (bottom-center)
                RouteProgressBar(
                    progress = routeProgress,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }

        // ───  3×2 Grid of Stats ────────────────────────────────────────────────
        val durationMin = durationSeconds / 60
        val calories   = calculateCalories(
            distanceKm, avgSpeedKmh, userStats.weightKg, durationMin
        )

        // first row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(label = "Distance", value = "%.1f km".format(distanceKm), Modifier.weight(1f))
            MetricCard(label = "Duration", value = "%02d:%02d".format(durationMin / 60, durationMin % 60), Modifier.weight(1f))
            MetricCard(label = "Avg Speed", value = "%.1f km/h".format(avgSpeedKmh), Modifier.weight(1f))
        }
        // second row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                label = "Heart Rate",
                value = heartRate?.let { "$it bpm" } ?: "-- bpm",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Calories",
                value = "%d kcal".format(calories),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(1f)) // empty cell
        }

        // ───  E-bike Stats Accordion ─────────────────────────────────────────────
        ExpandableCard(
            title = "E-bike Stats",
            expanded = false,
            enabled = eBikeAvailable,
            onExpandToggle = { onToggleEBike() }
        ) {
            // put your e-bike UI here when available
            Text("Not available", Modifier.padding(16.dp))
        }
    }
}

// —— Placeholder helpers & stubs ———————————————————————————————————

@Composable
fun WindGauge(speed: Float, direction: Float, modifier: Modifier = Modifier) {
    // your existing gauge code…
    Box(modifier.background(Color(0xFFCEE7FF), shape = CircleShape)) {
        Text("%.1f m/s".format(speed), Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SpeedGauge(speed: Float, modifier: Modifier = Modifier) {
    // your existing large speed gauge code…
    Box(modifier.background(Color(0xFFB3D4FC), shape = RoundedCornerShape(100.dp))) {
        Text("%.0f km/h".format(speed), Modifier.align(Alignment.Center), style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun RouteProgressBar(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(progress = progress, modifier = modifier, trackColor = Color.LightGray)
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.height(80.dp),
        shape     = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFDCEEFB))
    ) {
        Column(
            Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

fun calculateCalories(
    distanceKm: Float,
    speedKmh: Float,
    weightKg: Float,
    durationMin: Long
): Int {
    // simple MET-based estimate:
    val met = 0.1f * speedKmh + 1f
    val hours = durationMin / 60f
    return (met * weightKg * hours).roundToInt()
}

@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean,
    enabled: Boolean,
    onExpandToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    if (enabled) onExpandToggle else null?.let {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = it,
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = enabled) { onExpandToggle() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.3f)
                    )
                }
                if (expanded && enabled) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), content = content)
                }
            }
        }
    }
}

// utility to convert degrees to N/NE/E…
fun directionAbbreviation(deg: Float): String {
    return when ((deg / 45).roundToInt() % 8) {
        0 -> "N"; 1 -> "NE"; 2 -> "E"; 3 -> "SE"
        4 -> "S"; 5 -> "SW"; 6 -> "W"; 7 -> "NW"
        else -> ""
    }
}



@Preview
@Composable
fun RideHomeScreenPreview() {
    val userStats = UserStats(heightCm = 170f, weightKg = 70f)
    RideHomeScreen(
        windSpeedMps = 5.5f,
        windDeg = 90f,
        weatherIcon = Icons.AutoMirrored.Filled.DirectionsBike,
        onWeatherClick = {},
        speedKmh = 25.3f,
        headingDeg = 180f,
        isRecording = false,
        onStart = {},
        onStop = {},
        routeProgress = 0.7f,
        distanceKm = 15.2f,
        durationSeconds = 3600L,
        avgSpeedKmh = 20.5f,
        heartRate = 150,
        userStats = userStats,
        eBikeAvailable = true,
        onToggleEBike = {}
    )
}

@Preview
@Composable
fun WindGaugePreview() {
    WindGauge(speed = 5.5f, direction = 90f)
}

@Preview
@Composable
fun SpeedGaugePreview() {
    SpeedGauge(speed = 25.3f)
}

@Preview
@Composable
fun RouteProgressBarPreview() {
    RouteProgressBar(progress = 0.7f)
}

@Preview
@Composable
fun MetricCardPreview() {
    MetricCard(label = "Distance", value = "15.2 km")
}

@Preview
@Composable
fun ExpandableCardPreview() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExpandableCard(
        title = "E-bike Stats",
        expanded = expanded,
        enabled = true,
        onExpandToggle = { expanded = !expanded }
    ) {
        Text("Battery: 80%")
    }
}

@Preview
@Composable
fun ExpandableCardDisabledPreview() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExpandableCard(
        title = "E-bike Stats",
        expanded = expanded,
        enabled = false,
        onExpandToggle = { expanded = !expanded }
    ) {
        Text("Not available")
    }
}

// UserStats data class
data class UserStats(val heightCm: Float, val weightKg: Float)
