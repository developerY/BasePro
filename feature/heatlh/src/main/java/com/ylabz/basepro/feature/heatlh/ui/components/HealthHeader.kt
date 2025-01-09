package com.ylabz.basepro.feature.heatlh.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient

@Composable
fun HealthHeader(
    isHealthConnectAvailable: Boolean,
    backgroundReadGranted: Boolean,
    backgroundReadAvailable: Boolean,
    permissions: Set<String>,
    onPermissionsLaunch: (Set<String>) -> Unit,
    backgroundReadPermissions: Set<String>,
    activity: ComponentActivity?
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize() // Smooth transition for expand/collapse
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHealthConnectAvailable) "Health Connect: Available" else "Health Connect: Not Available",
                    color = if (isHealthConnectAvailable) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            // Expandable Content
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Settings Button
                Button(
                    onClick = {
                        val settingsIntent = Intent().apply {
                            action = HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS
                        }
                        activity?.startActivity(settingsIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Settings")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Permissions Buttons
                if (!backgroundReadGranted) {
                    Button(
                        onClick = { onPermissionsLaunch(backgroundReadPermissions) },
                        enabled = backgroundReadAvailable,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (backgroundReadAvailable) "Request Background Read" else "Background Read Not Available"
                        )
                    }
                } else {
                    Button(
                        onClick = { onPermissionsLaunch(permissions) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Read Steps in Background")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthHeaderPreview() {
    HealthHeader(
        isHealthConnectAvailable = true,
        backgroundReadGranted = false,
        backgroundReadAvailable = true,
        permissions = setOf("android.permission.ACTIVITY_RECOGNITION"),
        onPermissionsLaunch = { /* Mock permission launch */ },
        backgroundReadPermissions = setOf("android.permission.ACTIVITY_RECOGNITION"),
        activity = null // We set this to null for preview purposes since it's not needed in Compose previews
    )
}

