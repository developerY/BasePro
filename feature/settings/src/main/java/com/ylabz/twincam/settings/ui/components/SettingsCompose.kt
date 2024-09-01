package com.ylabz.twincam.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.twincam.settings.ui.SettingsEvent

@Composable
fun SettingsCompose(
    modifier: Modifier = Modifier,
    settings: Map<String, String>,
    onEvent: (SettingsEvent) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        settings.forEach { (key, value) ->
            val newValue = remember { mutableStateOf(value) }
            Text(text = "$key: ")
            BasicTextField(
                value = newValue.value,
                onValueChange = { newValue.value = it }
            )
            Button(onClick = { onEvent(SettingsEvent.UpdateSetting(key, newValue.value)) }) {
                Text("Save $key")
            }
        }

        // Button to delete all entries
        Button(onClick = { onEvent(SettingsEvent.DeleteAllEntries) }) {
            Text("Delete All Entries")
        }
    }
}

// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}
