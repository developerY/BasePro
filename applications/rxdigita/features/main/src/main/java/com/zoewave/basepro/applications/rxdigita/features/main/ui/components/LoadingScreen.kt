package com.zoewave.basepro.applications.rxdigita.features.main.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen() {
    Text(
        text = "Loading... Not sure what ... how did you get here?",
        modifier = Modifier.fillMaxSize()
    )
}