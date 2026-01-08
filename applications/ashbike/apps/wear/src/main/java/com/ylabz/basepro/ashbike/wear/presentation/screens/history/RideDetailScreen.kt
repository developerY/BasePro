package com.ylabz.basepro.ashbike.wear.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.*

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
            val current = ride
            if (current != null) {
                item { ListHeader { Text("Ride Details") } }

                item { Text("Distance: ${current.totalDistance} km") }
                item { Text("Calories: ${current.caloriesBurned}") }

                // Duration Calculation
                val durationMin = (current.endTime - current.startTime) / 1000 / 60
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
                        modifier = Modifier.fillMaxWidth()
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