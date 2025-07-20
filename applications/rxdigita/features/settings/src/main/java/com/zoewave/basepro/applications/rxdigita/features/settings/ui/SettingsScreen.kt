package com.zoewave.basepro.applications.rxdigita.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A placeholder screen for app settings.
 *
 * This composable provides a basic structure for a settings page,
 * which can be expanded with actual setting controls like switches,
 * sliders, and navigation links to other detail screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Profile Settings",
                    style = MaterialTheme.typography.titleLarge
                )
                // Placeholder for profile settings
                Text(
                    text = "Manage your name, email, and other personal information.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Text(
                    text = "Notification Preferences",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                // Placeholder for notification settings
                Text(
                    text = "Control when and how you receive reminders and alerts.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Text(
                    text = "Data & Privacy",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                // Placeholder for data settings
                Text(
                    text = "Manage your data, export history, or delete your account.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                // Placeholder for about section
                Text(
                    text = "View app version, terms of service, and privacy policy.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Preview function for the SettingsScreen.
 */
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        Surface {
            SettingsScreen(onNavigateBack = {})
        }
    }
}
