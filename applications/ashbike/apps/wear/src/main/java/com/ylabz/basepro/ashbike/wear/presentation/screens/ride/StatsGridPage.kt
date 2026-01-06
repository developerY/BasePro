package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// Add these imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.ylabz.basepro.core.model.bike.BikeRideInfo

@Composable
fun StatsGridPage(info: BikeRideInfo) {
    // The state required for scrolling behavior
    val listState = rememberScalingLazyListState()

    // ScreenScaffold adds the "Scrollbar" on the right automatically
    ScreenScaffold(scrollState = listState) {

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            // anchored = true (default) ensures items snap to center
        ) {
            // Header (Optional)
            item {
                Text(
                    text = "RIDE STATS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // ROW 1: Speed & Elevation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactStat(label = "AVG SPD", value = String.format("%.1f", info.averageSpeed ?: 0.0), unit = "km/h")
                    CompactStat(label = "ELEV", value = "${info.elevation ?: 0}", unit = "m")
                }
            }

            // ROW 2: Calories & Time
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactStat(label = "CAL", value = "${info.caloriesBurned ?: 0}", unit = "kcal")
                    CompactStat(label = "TIME", value = info.rideDuration ?: "0:0", unit = "min")
                }
            }

            // ROW 3: (New Features from Phone!)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactStat(label = "BATTERY", value = "82", unit = "%")
                    CompactStat(label = "GRADE", value = "4.2", unit = "%")
                }
            }

            // Spacer at bottom so the last item isn't cut off by the curve
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}