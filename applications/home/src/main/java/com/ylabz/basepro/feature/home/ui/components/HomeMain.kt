package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.home.data.AndFrameworks
import com.ylabz.basepro.feature.home.ui.HomeEvent

data class AppModel(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val path: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainScreen(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvent) -> Unit,
    navTo: (String) -> Unit,
) {

    val appList = listOf(
        AppModel("Bike", "Electric Bike Application", Icons.Filled.ElectricBike, "bike"),
        AppModel("Shot", "Electric Bike Application", Icons.Filled.Medication, "shotime"),
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Android AndFrameworks") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(
                modifier = Modifier.padding(paddingValues), text = "Android AndFrameworks"
            )
            ApplicationsScreen(
                appList,
                navTo,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeMainScreenPreview() {
    HomeMainScreen(
        modifier = Modifier.fillMaxSize(),
        onEvent = { /* Mock event handling */ },
        navTo = { destination -> println("Navigating to $destination") } // Mock navigation function
    )
}

