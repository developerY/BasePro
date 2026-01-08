package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.HorizontalPageIndicator
import androidx.wear.compose.material3.MaterialTheme
import com.ylabz.basepro.ashbike.wear.presentation.screens.history.RideHistoryPage
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// 3. Stateless UI Wrapper (The Pager)
@Composable
fun BikeControlContent(
    rideInfo: BikeRideInfo,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onHistoryClick: (String) -> Unit // <--- Add this
) {
    // 1. Create the PagerState (from Foundation)
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MainGaugePage(rideInfo, onStart, onStop)
                1 -> StatsGridPage(rideInfo)
                2 -> RideHistoryPage(onRideClick = onHistoryClick)
            }
        }

        // 2. The Indicator (Matches your signature)
        HorizontalPageIndicator(
            pagerState = pagerState, // <--- Matching the signature param
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            // OPTIONAL: Match AshBike branding
            selectedColor = MaterialTheme.colorScheme.primary,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
    }
}