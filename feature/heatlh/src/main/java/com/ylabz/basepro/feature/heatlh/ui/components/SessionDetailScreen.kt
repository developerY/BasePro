package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ylabz.basepro.core.data.service.health.ExerciseSessionData
import com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    uid: String,
    navController: NavHostController,
    healthVM: HealthViewModel = hiltViewModel(), // pulled in via Hilt
    modifier: Modifier = Modifier
) {
    rememberCoroutineScope()
    var detail by remember { mutableStateOf<ExerciseSessionData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        isLoading = true
        try {
            detail = healthVM.healthSessionManager.readAssociatedSessionData(uid)
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                detail != null -> {
                    SessionDetailContent(
                        detail!!,
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionDetailContent(
    data: ExerciseSessionData,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("ID ${data.uid}", style = MaterialTheme.typography.titleLarge)

        Text(
            "Duration: ${formatDuration(data.totalActiveTime)}",
            style = MaterialTheme.typography.bodyLarge
        )
        data.totalDistance?.let {
            Text(
                "Distance: ${"%.2f".format(it.inFeet)} km",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        data.totalSteps?.let {
            Text("Steps: $it", style = MaterialTheme.typography.bodyLarge)
        }
        data.totalEnergyBurned?.let {
            Text(
                "Calories: ${"%.0f".format(it.inCalories)} kcal",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            data.minHeartRate?.let { Text("Min HR: $it bpm") }
            data.avgHeartRate?.let { Text("Avg HR: $it bpm") }
            data.maxHeartRate?.let { Text("Max HR: $it bpm") }
        }
    }
}

private fun formatDuration(duration: Duration?): String {
    if (duration == null) return "--"
    val h = duration.toHours()
    val m = duration.toMinutes() % 60
    val s = duration.seconds % 60
    return buildString {
        if (h > 0) append("${h}h ")
        if (m > 0 || h > 0) append("${m}m ")
        append("${s}s")
    }
}
/*
/*@Preview(showBackground = true)
@Composable
fun SessionDetailScreenPreview() {
    // Fake data for preview:
    val fake = ExerciseSessionData(
        uid = "abc",
        totalActiveTime = Duration.ofMinutes(45).plusSeconds(30),
        totalSteps = 1234,
        totalDistance = Length.meters(15000.0),
        totalEnergyBurned = Energy.calories(350.0),
        minHeartRate = 60,
        avgHeartRate = 80,
        maxHeartRate = 120
    )
    SessionDetailContent(data = fake)
}
*/