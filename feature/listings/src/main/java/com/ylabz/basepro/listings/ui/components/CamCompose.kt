package com.ylabz.basepro.listings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.listings.ui.CamEvent
import com.ylabz.basepro.data.BaseProEntity

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun CamCompose(
    modifier: Modifier = Modifier,
    data: List<BaseProEntity>,
    onEvent: (CamEvent) -> Unit,
    navTo: (String) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "List of Items",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onEvent(CamEvent.DeleteAll) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete All!")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable list of items inside LazyColumn
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data) { item ->
                CamItemRow(item = item, onEvent = onEvent, navTo = navTo)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input for new item
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("New Item") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        onEvent(CamEvent.AddItem(newItemName))
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
fun CamItemRow(
    item: BaseProEntity,
    onEvent: (CamEvent) -> Unit,
    navTo: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navTo("details/${item.todoId}")
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = getPastelColor(item.todoId.hashCode())
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onEvent(CamEvent.DeleteItem(item.todoId)) },
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item"
                )
            }
        }
    }
}


// Helper function to get a pastel color based on the index
@Composable
fun getPastelColor(index: Int): Color {
    val pastelColors = listOf(
        Color(0xFFFFF0F5), // LavenderBlush
        Color(0xFFF0FFF0), // Honeydew
        Color(0xFFFFF5E6), // Seashell
        Color(0xFFE0FFFF), // LightCyan
        Color(0xFFFFE4E1)  // MistyRose
    )
    return pastelColors[index % pastelColors.size]
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

@Preview(showBackground = true)
@Composable
fun PreviewCamCompose() {
    val sampleData = listOf(
        BaseProEntity(todoId = 1, title = "Sample Task 1", description = "Description for Task 1"),
        BaseProEntity(todoId = 2, title = "Sample Task 2", description = "Description for Task 2"),
        BaseProEntity(todoId = 3, title = "Sample Task 3", description = "Description for Task 3")
    )

    CamCompose(
        data = sampleData,
        onEvent = {},
        navTo = {}
    )
}
