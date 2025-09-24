package com.ylabz.basepro.applications.photodo.features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Added for Preview
import androidx.compose.runtime.mutableStateOf // Added for Preview
import androidx.compose.runtime.remember // Added for Preview
import androidx.compose.runtime.setValue // Added for Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview // For Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.settings.R
// import android.util.Log // For logging errors if URL opening fails

@Composable
fun AboutExpandable( // Renamed back, and parameters re-added
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val websiteUrl = stringResource(R.string.settings_about_website_url)

    Card(
        modifier = modifier // Use the modifier passed from the caller
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Or MaterialTheme.colorScheme.surface
    ) {
        Column {
            // Header Row - clickable to toggle expansion
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.settings_about_cd),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_about_title),
                    style = MaterialTheme.typography.titleMedium // Changed from bodyLarge for consistency
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            // Expandable content
            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(
                            R.string.settings_app_version_label,
                            stringResource(R.string.settings_app_version_value_text)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.settings_build_number_label,
                            stringResource(R.string.settings_build_number_value_text)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.settings_about_info_visit_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = websiteUrl,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            val fullUrl = if (!websiteUrl.startsWith("http://") && !websiteUrl.startsWith("https://")) {
                                "https://$websiteUrl"
                            } else {
                                websiteUrl
                            }
                            try {
                                uriHandler.openUri(fullUrl)
                            } catch (e: Exception) {
                                // Log.e("AboutExpandable", "Could not open URL: $fullUrl", e)
                            }
                        }
                    )
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun AboutExpandablePreview() {
    var expanded by remember { mutableStateOf(true) }
    MaterialTheme { // Replace with your app's theme
        AboutExpandable(
            modifier = Modifier.padding(16.dp),
            expanded = expanded,
            onExpandToggle = { expanded = !expanded }
        )
    }
}
*/
