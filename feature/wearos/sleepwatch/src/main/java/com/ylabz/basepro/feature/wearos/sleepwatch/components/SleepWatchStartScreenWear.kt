package com.ylabz.basepro.feature.wearos.sleepwatch.components


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.ylabz.basepro.core.data.fake.sleep.FakeHealthRepository
import com.ylabz.basepro.core.model.health.SleepSessionData
import com.ylabz.basepro.feature.wearos.sleepwatch.SleepWatchEvent
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text

@Composable
fun SleepWatchStartScreenWear(
    navController: NavController,
    onEvent: (SleepWatchEvent) -> Unit,
    onRequestPermissions: (Array<String>) -> Unit,
    data: List<SleepSessionData>
) {
    // Log whenever the Composable is recomposed
    LaunchedEffect(data) {
        Log.d("SleepWatchStartScreenWear", "Sleep data list size: ${data.size}")
        data.forEachIndexed { index, item ->
            Log.d("SleepWatchStartScreenWear", "Item $index: $item")
        }
    }

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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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
