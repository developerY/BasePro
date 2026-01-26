package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.drafts.RideStatsList

@Preview(name = "Stats List (Disconnected)", device = "id:ai_glasses_device")
@Composable
fun StatsListPreview() {
    MaterialTheme {

            RideStatsList(
                distance = "12.5",
                duration = "00:45:10",
                avgSpeed = "18.2",
                calories = "350",
                modifier = Modifier.fillMaxSize()
            )
        }

}