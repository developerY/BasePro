package com.ylabz.basepro.feature.material3.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun Material3Main(modifier: Modifier = Modifier) {
    Text("Hello")
}

@Composable
fun VerticalSliderExample() {
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    VerticalSlider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        modifier = Modifier.height(200.dp)
    )
}