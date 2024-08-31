package com.ylabz.twincam.cam.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.twincam.cam.ui.CamEvent
import com.ylabz.twincam.data.TwinCamEntity

@Composable
fun CamCompose(
    modifier: Modifier = Modifier,
    data: List<TwinCamEntity>,
    onEvent: (CamEvent) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row {
            Text("List of Items")
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { onEvent( CamEvent.DeleteAll) }) {
                Text("Delete All!")
            }
        }
        data.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {  onEvent( CamEvent.OnItemClicked(item.todoId)) }
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {  onEvent( CamEvent.DeleteItem(item.todoId)) }) {
                    Text("Delete")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            TextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("New Item") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        onEvent( CamEvent.AddItem(newItemName))
                        newItemName = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add")
            }
        }
    }
}

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
