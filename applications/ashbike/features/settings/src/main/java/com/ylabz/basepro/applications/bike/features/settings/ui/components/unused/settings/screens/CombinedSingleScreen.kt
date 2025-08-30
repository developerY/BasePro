package com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.GearingScreen
//import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.SuspensionScreen

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

/*
@Preview(showBackground = true)
@Composable
fun CombinedSingleScreenPreview() {
    CombinedSingleScreen()
}
*/