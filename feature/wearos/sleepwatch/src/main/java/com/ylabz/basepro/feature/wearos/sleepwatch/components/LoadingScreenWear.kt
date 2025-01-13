package com.ylabz.basepro.feature.wearos.sleepwatch.components

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Text

@Composable
fun LoadingScreenWear() {
    // On Wear you can just show a loading indicator or a “busy” icon
    Text(text = "Loading...")
    // Alternatively, use something like CircularProgressIndicator()
}
