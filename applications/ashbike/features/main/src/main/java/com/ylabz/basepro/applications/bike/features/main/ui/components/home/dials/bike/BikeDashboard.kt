package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.LaunchGlassButton


@Composable
fun BikeDashboard(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success, // Assumes this now has .glassGear
    onBikeEvent: (BikeEvent) -> Unit,
    isBikeConnected: Boolean,
    batteryLevel: Int?,
) {

            // --- NEW: 3-Column Row for Battery, Motor, Gear ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Gap between boxes
            ) {
                // 1. Battery Stat
                BikeStatCard(
                    icon = Icons.Default.ElectricBolt, // Or your Battery icon
                    label = "Battery",
                    value = "-- B",//"${uiState.bikeData.batteryLevel}%",
                    modifier = Modifier.weight(1f) // Equal width
                )

                // 2. Motor Stat
                BikeStatCard(
                    icon = Icons.Default.PedalBike, // Or your Motor icon
                    label = "Motor",
                    value = "-- W", // Replace with real motor power if available
                    modifier = Modifier.weight(1f)
                )

                // 3. Gear Stat (From Glass)
                BikeStatCard(
                    icon = Icons.Default.Settings, // Gear Icon
                    label = "Gear",
                    value = "${uiState.glassGear}", // Connected to Glass Data
                    modifier = Modifier.weight(1f)
                )
            }
            // --------------------------------------------------

            // "Tap to Connect" Button (Existing)
            BikeCard(
                isConnected = isBikeConnected,
                batteryLevel = batteryLevel,
                onConnectClick = { onBikeEvent(BikeEvent.OnBikeClick) }
            )


            // 2. INSERT THE GLASS BUTTON HERE
            LaunchGlassButton(
                buttonState = uiState.glassButtonState, // Read from UI State
                onButtonClick = { onBikeEvent(BikeEvent.ToggleGlassProjection) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

        }
