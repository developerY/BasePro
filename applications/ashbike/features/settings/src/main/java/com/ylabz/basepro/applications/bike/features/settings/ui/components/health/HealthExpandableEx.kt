package com.ylabz.basepro.applications.bike.features.settings.ui.components.health

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.heatlh.ui.HealthRoute

// ---------------------------------------------
// Health Expandable
// ---------------------------------------------
@Composable
fun HealthExpandableEx(
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    navTo: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Health",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Health Connect",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            // Expanded content
            if (expanded) {
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                HealthRoute(
                    navTo = navTo
                )
            }
        }
    }
}

@Preview
@Composable
fun HealthExpandableExCollapsedPreview() {
    HealthExpandableEx(expanded = false, onExpandToggle = {}, navTo = {})
}

@Preview
@Composable
fun HealthExpandableExExpandedPreview() {
    HealthExpandableEx(expanded = true, onExpandToggle = {}, navTo = {})
}


