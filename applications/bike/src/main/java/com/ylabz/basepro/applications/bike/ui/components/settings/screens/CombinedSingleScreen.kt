package com.ylabz.basepro.applications.bike.ui.components.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.bike.ui.components.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.GearingScreen
import com.ylabz.basepro.applications.bike.ui.components.settings.SuspensionScreen

@Composable
fun CombinedSingleScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Suspension
        SuspensionScreen()
        Divider()
        // Gearing
        GearingScreen()
        Divider()
        // Brakes
        BrakesScreen()
    }
}
