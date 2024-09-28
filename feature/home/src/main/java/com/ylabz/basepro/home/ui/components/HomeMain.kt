package com.ylabz.basepro.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ylabz.basepro.home.data.AndFrameworks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainScreen() {
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
        Text(
            modifier = Modifier.padding(paddingValues),text = "Android AndFrameworks")

    }
}
