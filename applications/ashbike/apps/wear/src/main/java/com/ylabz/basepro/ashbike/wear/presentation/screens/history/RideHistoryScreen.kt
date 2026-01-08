package com.ylabz.basepro.ashbike.wear.presentation.screens.history

// ... imports including hiltViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard

@Composable
fun RideHistoryScreen(
    // Inject ViewModel automatically
    viewModel: RideHistoryViewModel = hiltViewModel(),
    onRideClick: (String) -> Unit
) {
    // Collect the Real Data from the Database!
    val historyList by viewModel.historyList.collectAsState()

    val listState = rememberScalingLazyListState()

    ScreenScaffold(scrollState = listState) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "HISTORY",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (historyList.isEmpty()) {
                item {
                    Text(
                        text = "No rides yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(historyList) { ride ->
                    HistoryItemCard(
                        ride = ride, // We updated this card signature below
                        onClick = { onRideClick(ride.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(ride: RideHistoryUiItem, onClick: () -> Unit) {
    TitleCard(
        onClick = onClick,
        title = { Text(ride.distanceStr) },
        subtitle = { Text("${ride.dateStr} • ${ride.durationStr}") },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        // Optional: Calories badge
    }
}