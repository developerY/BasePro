package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent

// —————————————————————————————————————————————————————————
//  SECTION HEADER (Rounded + Elevation)
// —————————————————————————————————————————————————————————
@Composable
fun TripSectionHeader(
    onEvent: (TripsEvent) -> Unit,
    title: String,
    bgColor: Color,
    count: Int? = null,
    healthConnected: Boolean,            // ← new
    onHealthToggle: () -> Unit           // ← new
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show “(N) Bike Rides” if count != null
            val label = if (count != null) "($count) $title" else title
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(8.dp)
            )

            // —————————————————————
            //  Health indicator in header
            // —————————————————————
            IconButton(onClick = onHealthToggle) {
                if (healthConnected) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Health Connected",
                        tint = Color(0xFF009688)
                    )
                } else {
                    Box (
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Health Error",
                            tint = Color(0xFFA22116)
                        )
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Connect Health"
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = { onEvent(TripsEvent.DeleteAll) }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Delete all"
                )
            }
        }
    }
}
/*
@Preview
@Composable
fun TripSectionHeaderPreview() {
    TripSectionHeader(
        onEvent = {},
        title = "Bike Rides",
        bgColor = Color.LightGray,
        count = 5,
        healthConnected = true,
        onHealthToggle = {}
    )
}
*/

