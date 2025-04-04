package com.ylabz.basepro.applications.bike.ui.components.settings.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.ui.components.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.GearingScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.SuspensionScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceTuningScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Suspension", "Gearing", "Brakes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Tuning") },
                navigationIcon = {
                    IconButton(onClick = { } ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

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

