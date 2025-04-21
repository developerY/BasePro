package com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.BrakesScreen
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.GearingScreen
import com.ylabz.basepro.applications.bike.features.settings.ui.components.unused.settings.SuspensionScreen

@Composable
fun CombinedExpandableScreen() {
    // Track expansion for each section
    var showSuspension by remember { mutableStateOf(false) }
    var showGearing by remember { mutableStateOf(false) }
    var showBrakes by remember { mutableStateOf(false) }

    // Use LazyColumn or a Column with verticalScroll
    LazyColumn {
        item {
            ExpandableCard(
                title = "Suspension",
                expanded = showSuspension,
                onExpandChange = { showSuspension = !showSuspension }
            ) {
                // Show the entire Suspension UI
                SuspensionScreen()
            }
        }
        item {
            ExpandableCard(
                title = "Gearing",
                expanded = showGearing,
                onExpandChange = { showGearing = !showGearing }
            ) {
                GearingScreen()
            }
        }
        item {
            ExpandableCard(
                title = "Brakes",
                expanded = showBrakes,
                onExpandChange = { showBrakes = !showBrakes }
            ) {
                BrakesScreen()
            }
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    expanded: Boolean,
    onExpandChange: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .clickable { onExpandChange() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            // Show content if expanded
            if (expanded) {
                Divider()
                Box(Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

// Preivew
@Preview
@Composable
fun PreviewCombinedExpandableScreen() {
    CombinedExpandableScreen()
}