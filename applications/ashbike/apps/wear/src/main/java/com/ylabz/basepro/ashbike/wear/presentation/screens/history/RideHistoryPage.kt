package com.ylabz.basepro.ashbike.wear.presentation.screens.history

// Add these imports
// ... imports including hiltViewModel
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text

@Composable
fun RideHistoryPage(
    viewModel: RideHistoryViewModel = hiltViewModel(),
    onRideClick: (String) -> Unit // <--- Add this
) {
    val historyList by viewModel.historyList.collectAsState()
    val listState = rememberScalingLazyListState()

    // We wrap it in ScreenScaffold so we get the Scrollbar (Position Indicator)
    // ONLY on this page.
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

            items(historyList) { ride ->
                HistoryItemCard(
                    ride = ride,
                    onClick = { onRideClick(ride.id) } // Pass ID up
                )
            }

            if (historyList.isEmpty()) {
                item {
                    Text(
                        text = "No rides yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(historyList) { ride ->
                    // Reuse the Card we made earlier
                    HistoryItemCard(
                        ride = ride,
                        onClick = { onRideClick(ride.id) } // Trigger callback
                    )
                }
            }
        }
    }
}