package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.feature.home.data.AndFrameworks
import com.ylabz.basepro.feature.home.ui.HomeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainScreen(
    modifier: Modifier = Modifier,
    //data: List<AndFrameworks>,
    onEvent: (HomeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val frameworkList = listOf(
        AndFrameworks("BLE", "Bluetooth Low Energy"),
        AndFrameworks("Camera", "Camera functionality"),
        AndFrameworks("Location", "Location services"),
        AndFrameworks("Sensors", "Device Sensors"),
        AndFrameworks("Contacts", "Contacts access")
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

            Button(onClick = {
                navTo("photo")
            }) {
                Text(text = "Navigate to Camera Screen")
            }

            Button(onClick = {
                navTo("maps")
            }) {
                Text(text = "Navigate to Maps Screen")
            }

            Button(onClick = {
                navTo("places")
            }) {
                Text(text = "Navigate to Places Screen")
            }

            Button(onClick = {
                navTo("health")
            }) {
                Text(text = "Navigate to Health Screen")
            }

            Button(onClick = {
                navTo("ble")
            }) {
                Text(text = "Navigate to BLE Screen")
            }



        }
    }
}
