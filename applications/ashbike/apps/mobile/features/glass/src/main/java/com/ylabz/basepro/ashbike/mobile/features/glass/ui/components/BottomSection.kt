package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components


// Glimmer Imports
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiEvent
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassUiState


@Composable
fun BottomSection(
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- ROW 2: BOTTOM SECTION ---
    // (Same as before: Power & Heart Rate)
    Column(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
        Row(
            modifier = Modifier.weight(0.35f).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Power
            Box(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                Card(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "POWER",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlassColors.TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Glimmer Icon
                            // Icon(Icons.Default.Bolt, "Watts", tint = Color(0xFFFFD600), modifier = Modifier.width(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${uiState.motorPower} W",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            // Heart Rate
            Box(modifier = Modifier.weight(1f).padding(start = 6.dp)) {
                HeartRateCard(heartRate = uiState.heartRate, modifier = Modifier.fillMaxSize())
            }
        }
    }
}