package com.ylabz.basepro.applications.photodo.features.settings.ui.components

//import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.settings.R

@Composable
fun AboutExpandable(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
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
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.settings_action_collapse else R.string.settings_action_expand)
                )
            }

            if (expanded) {
                HorizontalDivider()
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(
                            R.string.settings_app_version_label,
                            stringResource(R.string.settings_app_version_value_text)
                        )
                    )
                    Text(
                        stringResource(
                            R.string.settings_build_number_label,
                            stringResource(R.string.settings_build_number_value_text)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.settings_about_info_visit_text))
                    Text(
                        text = stringResource(R.string.settings_about_website_url),
                        color = MaterialTheme.colorScheme.primary
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
    AshBikeTheme {
        AboutExpandable(expanded = true, onExpandToggle = { })
    }
}
*/