package com.ylabz.basepro.applications.photodo.features.photodolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PhotoDoListUiRoute(
    // The onItemClick lambda will notify the parent when an item is selected
    onItemClick: (String) -> Unit
) {
    // A dummy list of items for demonstration purposes
    val photoItems = List(20) { "Photo Item ${it + 1}" }

    LazyColumn {
        items(photoItems) { item ->
            Text(
                text = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) } // Pass the ID of the clicked item
                    .padding(16.dp)
            )
        }
    }
}