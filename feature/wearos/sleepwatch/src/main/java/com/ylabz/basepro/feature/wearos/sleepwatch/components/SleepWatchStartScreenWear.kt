package com.ylabz.basepro.feature.wearos.sleepwatch.components


import android.R.attr.fontWeight
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Text
import com.ylabz.basepro.core.data.fake.sleep.FakeHealthRepository
import com.ylabz.basepro.core.model.health.SleepSessionData
import com.ylabz.basepro.feature.wearos.sleepwatch.SleepWatchEvent
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.key.Key.Companion.Sleep
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.Navigation
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.ylabz.basepro.core.model.Purple200
import com.ylabz.basepro.core.model.Purple500
import com.ylabz.basepro.core.model.Purple80
import com.ylabz.basepro.core.model.health.SleepSegment
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun SleepWatchStartScreenWear(
    navController: NavController,
    onEvent: (SleepWatchEvent) -> Unit,
    onRequestPermissions: (Array<String>) -> Unit,
    data: List<SleepSessionData>
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(VignettePosition.TopAndBottom) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Horizontal Pager for different views
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.align(Alignment.Center)
            ) { page ->
                when (page) {
                    0 -> SleepClockFaceOrig(
                        segments = sampleSleepSegments,
                        clockSize = 200.dp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    1 -> SleePieChart(input = samplePieChartInput)
                }
            }

            // Top Section: Time and Total Sleep
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "7:07", // Replace with dynamic time if needed
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total Sleep: 7h 45m",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            // Center Section: Selected Segment
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "",// ""N1 Sleep: 30.0%",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "", //""Sleep Score: 85",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Bottom Section: Icons and Arrow
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Alarm action */ },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alarm",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage((pagerState.currentPage + 1) % 2)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (pagerState.currentPage == 0) Icons.Filled.ArrowForward else Icons.Filled.ArrowBack,
                        contentDescription = "Next View",
                        tint = Color.White
                    )
                }
            }

            /* Navigation button
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage((pagerState.currentPage + 1) % 2)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Text(text = "Next View")
            }*/

        }

    }
}


@Composable
fun TestScreen(
    modifier: Modifier = Modifier,
    data: List<SleepSessionData>
) {
    if (data.isEmpty()) {
        // Debug text for empty data
        Text(
            text = "No sleep data available",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(data) { info ->
                Text(
                    text = info.title ?: "No Title",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
    
}

val sampleSleepSegments = listOf(
    SleepSegment(22.5f, 23.5f, 8f, Color(0xFF6A5ACD), "N2 Sleep"),
    SleepSegment(23.5f, 1.0f, 16f, Color(0xFF7B68EE), "REM"),
    SleepSegment(1.0f, 3.0f, 33f, Color(0xFF483D8B), "Deep Sleep"),
    SleepSegment(3.0f, 6.0f, 30f, Color(0xFF708090), "N1 Sleep"),
    SleepSegment(6.0f, 7.0f, 8f, Color(0xFF9370DB), "Light Sleep")
)

val samplePieChartInput = listOf(
    SleePieChartInput(color = Color(0xFF6A5ACD), value = 2, description = "N2 Sleep"),
    SleePieChartInput(color = Color(0xFF7B68EE), value = 4, description = "REM"),
    SleePieChartInput(color = Color(0xFF483D8B), value = 3, description = "Deep Sleep"),
    SleePieChartInput(color = Color(0xFF708090), value = 3, description = "N1 Sleep"),
    SleePieChartInput(color = LightGray, value = 1, description = "Wake")
)
/*
@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "id:wearos_large_round")
@Composable
fun PreviewSleepWatchStartScreenWear() {
    val navController = rememberNavController()
    SleepWatchStartScreenWear(
        navController = navController,
        onEvent = {},
        onRequestPermissions = {},
        data = listOf()
    )
}


@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "id:wearos_large_round")
@Composable
fun SleepWatchStartScreenWearPreviewOld() {
    val testNavController = NavController(LocalContext.current)

    // Use the existing FakeHealthRepository
    val fakeHealthRepository = FakeHealthRepository()
    val fakeSleepData = fakeHealthRepository.getData()

    SleepWatchStartScreenWear(
        navController = testNavController,
        onEvent = {},
        onRequestPermissions = {},
        data = fakeSleepData
    )
}

@Preview(showBackground = true, device = "id:wearos_large_round")
@Composable
fun SleepWatchStartScreenWearPreview() {
    //BaseProTheme {
        SleepWatchStartScreenWear(
            navController = rememberNavController(),
            onEvent = {},
            onRequestPermissions = {},
            data = listOf(
                SleepSessionData(
                    uid = "1",
                    title = "Night Sleep",
                    notes = "Slept well after workout",
                    startTime = Instant.parse("2023-01-01T22:00:00Z"),
                    startZoneOffset = ZoneOffset.UTC,
                    endTime = Instant.parse("2023-01-02T06:00:00Z"),
                    endZoneOffset = ZoneOffset.UTC,
                    duration = Duration.ofHours(8),
                    stages = listOf(
                        SleepSessionRecord.Stage(
                            startTime = Instant.parse("2023-01-01T23:00:00Z"),
                            endTime = Instant.parse("2023-01-02T01:00:00Z"),
                            stage = SleepSessionRecord.STAGE_TYPE_DEEP
                        )
                    )
                )
            )
        )
    //}
}


 */