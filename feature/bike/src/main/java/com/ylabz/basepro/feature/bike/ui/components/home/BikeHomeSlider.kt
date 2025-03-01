package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.bike.ui.components.BikeCompose
import com.ylabz.basepro.feature.bike.ui.components.route.RoutePlanningScreen

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BikeHomeSlider() {
    // Create a pager state with three pages
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val sampleSettings = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bike Home") }
            )
        },
        // Instead of a bottom nav, we add optional navigation buttons or indicators below the pager.
        bottomBar = {
            // Simple dot indicator row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    val color = if (pagerState.currentPage == index)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                //pageCount = 3,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> BikeCompose(
                        settings = sampleSettings,
                        onEvent = {},
                        location = LatLng(0.0,0.0),
                        navTo = {} // No-op for preview
                    )
                    1 -> RoutePlanningScreen() // RoutesScreen()
                    2 -> SettingsScreen()
                }
            }

            // Optional: Add buttons to programmatically change pages
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                }) {
                    Text("Home")
                }
                Button(onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(1) }
                }) {
                    Text("Routes")
                }
                Button(onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(2) }
                }) {
                    Text("Settings")
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Screen", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun RoutesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Routes Screen", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun SettingsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings Screen", style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBikeHomeSlider() {
    MaterialTheme {
        BikeHomeSlider()
    }
}
