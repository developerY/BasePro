package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon // <--- CORRECT GLIMMER IMPORT
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.GlassColors

@Composable
fun RideStatsPanel(
    distance: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // 1. DISTANCE ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // GLIMMER ICON
                Icon(
                    imageVector = Icons.Default.Straighten, // Ruler
                    contentDescription = null,
                    tint = GlassColors.NeonCyan,
                    modifier = Modifier.width(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DISTANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlassColors.TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$distance km",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Optional Divider/Spacer
            Spacer(modifier = Modifier.height(4.dp))

            // 2. CALORIES ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // GLIMMER ICON
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment, // Fire
                    contentDescription = null,
                    tint = Color(0xFFFF9800), // Orange
                    modifier = Modifier.width(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "CALORIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlassColors.TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$calories kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}