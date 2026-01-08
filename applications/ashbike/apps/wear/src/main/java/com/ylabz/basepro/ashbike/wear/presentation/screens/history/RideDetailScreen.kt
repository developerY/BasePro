package com.ylabz.basepro.ashbike.wear.presentation.screens.history

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextConfigurationDefaults.TextAlign

@Composable
fun RideDetailScreen(
    rideId: String,
    viewModel: RideHistoryViewModel = hiltViewModel(),
    onDeleteSuccess: () -> Unit // Tells the app to go back after deleting
) {
    // Collect the single ride
    val ride by viewModel.getRide(rideId).collectAsState(initial = null)
    val listState = rememberScalingLazyListState()

    ScreenScaffold(scrollState = listState) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            val currentRide= ride
            if (currentRide!= null) {
                // 1. HEADER
                item { ListHeader { Text("Ride Details") } }

                // 2. STATS (Add as many rows as you want)
                item { DetailRow("Distance", "${currentRide.totalDistance} km") }
                item { DetailRow("Calories", "${currentRide.caloriesBurned}") }
                item { DetailRow("Duration", "${(currentRide.endTime - currentRide.startTime)/1000/60} min") }
                item { DetailRow("Avg Speed", "${currentRide.averageSpeed} km/h") }

                // Duration Calculation
                val durationMin = (currentRide.endTime - currentRide.startTime) / 1000 / 60
                item { Text("Duration: $durationMin min") }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                // THE DELETE BUTTON
                item {
                    Button(
                        onClick = {
                            viewModel.deleteRide(rideId)
                            onDeleteSuccess()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text("Delete Ride")
                    }
                }
            } else {
                item { Text("Loading...") }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}