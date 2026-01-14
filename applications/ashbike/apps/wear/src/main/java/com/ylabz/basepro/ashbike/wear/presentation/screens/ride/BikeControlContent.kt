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
    uiState: WearBikeUiState,
    onEvent: (WearBikeEvent) -> Unit
) {
    // UI-only state (like pager position) is fine to stay in Compose
    // because it doesn't affect business logic.
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> MainGaugePage(
                    uiState = uiState,
                    onEvent = onEvent
                )
                1 -> StatsGridPage(
                    uiState = uiState // Assuming this needs state too
                )
                2 -> RideHistoryPage(
                    onRideClick = { rideId -> onEvent(WearBikeEvent.OnHistoryClick(rideId)) }
                )
            }
        }

        HorizontalPageIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            selectedColor = MaterialTheme.colorScheme.primary,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
    }
}