package com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.screens


//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.GearingScreen
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.SuspensionScreen


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
                    IconButton(onClick = { }) {
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
/*
@Preview
@Composable
fun PerformanceTuningScreenPreview() {
    PerformanceTuningScreen()
}
*/
