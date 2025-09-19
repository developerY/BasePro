package com.ylabz.basepro.feature.material3.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier // Added missing Modifier import
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Material3Main(modifier: Modifier = Modifier) {
    Text("Hello")
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ExpressiveCardExample() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Save as Draft", "Export as PDF", "Delete")
    var selectedOption by remember { mutableStateOf("Save") }
    var isSelected by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = "Dinner club",
            style = MaterialTheme.typography.bodyLarge,
            color =
                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
        )
    }
}