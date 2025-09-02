package com.ylabz.basepro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(onNavigate: (String) -> Unit) {

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            "Drawer Title",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()

        Text(
            "Section 1",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        NavigationDrawerItem(
            label = { Text("Main") },
            selected = false,
            onClick = { onNavigate("main") }
        )
        NavigationDrawerItem(
            label = { Text("Maps") },
            selected = false,
            onClick = { onNavigate("maps") }
        )
        NavigationDrawerItem(
            label = { Text("BLE") },
            selected = false,
            onClick = { onNavigate("ble") }
        )
        NavigationDrawerItem(
            label = { Text("Camera X") },
            selected = false,
            onClick = { onNavigate("photo") }
        )

        NavigationDrawerItem(
            label = { Text("Places") },
            selected = false,
            onClick = { onNavigate("places") }
        )

        NavigationDrawerItem(
            label = { Text("ML") },
            selected = false,
            onClick = { onNavigate("ml") }
        )

        //health
        NavigationDrawerItem(
            label = { Text("Health") },
            selected = false,
            onClick = { onNavigate("health") }
        )

        //Alarm Time
        NavigationDrawerItem(
            label = { Text("Alarm") },
            selected = false,
            onClick = { onNavigate("alarm") }
        )


        //Weather
        NavigationDrawerItem(
            label = { Text("Weather") },
            selected = false,
            onClick = { onNavigate("weather") }
        )

        //NFC
        NavigationDrawerItem(
            label = { Text("NFC") },
            selected = false,
            onClick = { onNavigate("nfc") }
        )

        //NAV3
        NavigationDrawerItem(
            label = { Text("NAV3") },
            selected = false,
            onClick = { onNavigate("nav3") }
        )


        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            "Section 2",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
            onClick = { onNavigate("settings") }
        )
        NavigationDrawerItem(
            label = { Text("Help and Feedback") },
            selected = false,
            icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
            onClick = { onNavigate("help") },
        )
        Spacer(Modifier.height(12.dp))
    }

}

@Composable
fun DrawerContentOrig(modifier: Modifier = Modifier) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Drawer Title",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider()

            Text(
                "Section 1",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            NavigationDrawerItem(
                label = { Text("Item 1") },
                selected = false,
                onClick = { /* Handle click */ }
            )
            NavigationDrawerItem(
                label = { Text("Item 2") },
                selected = false,
                onClick = { /* Handle click */ }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Section 2",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                badge = { Text("20") }, // Placeholder
                onClick = { /* Handle click */ }
            )
            NavigationDrawerItem(
                label = { Text("Help and feedback") },
                selected = false,
                icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null) },
                onClick = { /* Handle click */ },
            )
            Spacer(Modifier.height(12.dp))
        }
    }

}
