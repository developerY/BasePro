package com.ylabz.basepro.applications.bike.ui.components.demo.settings.unused

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.ui.components.demo.settings.BikeConfigurationExpandable

// Example data class for each settings item
data class SettingItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val description: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancySettingsScreen(
    onAppPreferencesClick: () -> Unit,
    onBikeConfigurationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val settingsItems = listOf(
        SettingItem(
            title = "App Preferences",
            icon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
            onClick = onAppPreferencesClick,
            description = "Language, notifications, theme..."
        ),
        SettingItem(
            title = "Bike Configuration",
            icon = { Icon(Icons.Default.BikeScooter, contentDescription = null) },
            onClick = onBikeConfigurationClick,
            description = "Motor, gears, brake tuning..."
        ),
        SettingItem(
            title = "About",
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            onClick = onAboutClick,
            description = "Version, licenses, support..."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1) Add a "profile/bike info" card at the top
            item {
                ProfileBikeInfoCard(
                    userName = "John Doe",
                    bikeBattery = "80%",
                    lastRide = "12.5 km",
                    onProfileClick = onProfileClick
                )
            }

            // 2) Add a spacer or a small header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                BikeConfigurationExpandable(
                    expanded = false,
                    onExpandToggle = {},
                    onAdvancedClick = {}
                )
            }

            // 3) List out each setting item as a card or row
            items(settingsItems) { item ->
                SettingCard(item)
            }

        }
    }
}

// A card at the top that shows user/bike info
@Composable
fun ProfileBikeInfoCard(
    userName: String,
    bikeBattery: String,
    lastRide: String,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onProfileClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // You could also use an image or gradient background here
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Battery: $bikeBattery | Last Ride: $lastRide",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// A composable for each settings row/card
@Composable
fun SettingCard(item: SettingItem) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { item.onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon
            item.icon()

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                if (!item.description.isNullOrEmpty()) {
                    Text(text = item.description!!, style = MaterialTheme.typography.bodySmall)
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

//Preview FancySettingsScreen
@Preview
@Composable
fun PreviewFancySettingsScreen() {
    FancySettingsScreen(
        onAppPreferencesClick = {},
        onBikeConfigurationClick = {},
        onProfileClick = {},
        onAboutClick = {}
    )
}

