package com.ylabz.basepro.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import com.ylabz.basepro.core.ui.PicScreen
import com.ylabz.basepro.core.ui.Screen.Companion.CameraScreenRoute
import com.ylabz.basepro.home.data.AndFrameworks
import com.ylabz.basepro.home.ui.HomeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainScreen(
    modifier: Modifier = Modifier,
    //data: List<AndFrameworks>,
    onEvent: (HomeEvent) -> Unit,
    navTo: (String) -> Unit,
    navToCam: () -> Unit
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
                navToCam()
            }) {
                Text(text = "Navigate to Camera Screen")
            }


        }
    }
}
