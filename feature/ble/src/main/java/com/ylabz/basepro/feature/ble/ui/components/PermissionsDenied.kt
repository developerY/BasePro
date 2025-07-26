package com.ylabz.basepro.feature.ble.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.ble.R // Added import
import com.ylabz.basepro.core.ui.R as CoreUiR // Added import

@Composable
fun PermissionsDenied(modifier: Modifier = Modifier, onOpenSettingsClick: () -> Unit) { // Renamed onPermissionsDenied to onOpenSettingsClick for clarity
    Column(
        modifier = modifier // Used modifier parameter
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.ble_message_permissions_denied), // Replaced hardcoded string
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp)) // Increased spacer for better separation
        Button(onClick = onOpenSettingsClick) { // Used onOpenSettingsClick
            Text(stringResource(id = CoreUiR.string.action_open_settings)) // Replaced hardcoded string
        }
    }
}