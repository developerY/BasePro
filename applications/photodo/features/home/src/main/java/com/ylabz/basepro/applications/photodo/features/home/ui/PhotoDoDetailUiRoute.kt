package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PhotoDoDetailUiRoute(photoId: String, modifier: Modifier = Modifier) { // Added modifier parameter
    // This is where you would fetch and display the full details for the given photoId
    Box(
        modifier = modifier.fillMaxSize(), // Applied the modifier
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Details for Photo ID: $photoId")
    }
}
