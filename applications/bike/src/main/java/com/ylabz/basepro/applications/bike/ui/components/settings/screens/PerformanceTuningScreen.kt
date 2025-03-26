package com.ylabz.basepro.applications.bike.ui.components.settings.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.core.model.health.HealthScreenState
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.GearingScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.SuspensionScreen
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.TagScanned
import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceTuningScreen(
    modifier: Modifier = Modifier
) {
    // Track which tab is selected
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Suspension", "Gearing", "Brakes")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Performance Tuning") })
        }
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            // TabRow
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Show the composable for the selected tab
            when (selectedTab) {
                0 -> SuspensionScreen()
                1 -> GearingScreen()
                2 -> BrakesScreen()
            }
        }
    }
}

// Preview
@Preview
@Composable
fun PerformanceTuningScreenPreview() {
    PerformanceTuningScreen()
}

