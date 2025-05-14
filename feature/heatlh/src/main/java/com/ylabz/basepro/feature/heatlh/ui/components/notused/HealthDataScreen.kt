package com.ylabz.basepro.feature.heatlh.ui.components.notused

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.core.model.health.SleepSessionData

@Composable
fun HealthDataScreen(healthData: List<SleepSessionData>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(healthData) { session ->
            Text(text = "Session: ${session.title}")
        }
    }
}
