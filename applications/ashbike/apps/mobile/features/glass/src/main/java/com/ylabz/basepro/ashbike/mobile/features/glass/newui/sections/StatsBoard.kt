package com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AvTimer
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.list.VerticalList
import com.ylabz.basepro.ashbike.mobile.features.glass.R


/**
 * The documentation explicitly states: "Warning: Don't use LazyColumn in your AI glasses activities."
 *
 */

@Composable
fun StatsBoard(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    modifier: Modifier = Modifier,
    listFocusRequester: FocusRequester? = null
) {
    // Card(modifier = modifier) {
        // CORRECT: Use VerticalList instead of LazyColumn
        VerticalList(
            modifier = Modifier
                .fillMaxSize()
                .then(if (listFocusRequester != null) Modifier.focusRequester(listFocusRequester) else Modifier),
            // REDUCE PADDING: Was 8.dp, change to 4.dp or 2.dp
            contentPadding = PaddingValues(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Item 1: Distance
            item {
                GlimmerStatItem(
                    icon = Icons.Rounded.Straighten,
                    label = stringResource(R.string.distance),
                    value = "$distance km",
                    accent = GlimmerTheme.colors.secondary
                )
            }
            // Item 2: Duration
            item {
                GlimmerStatItem(
                    icon = Icons.Rounded.AvTimer,
                    label = stringResource(R.string.duration),
                    value = duration,
                    accent = GlimmerTheme.colors.secondary
                )
            }
            // Item 3: Avg Speed
            item {
                GlimmerStatItem(
                    icon = Icons.Rounded.Speed,
                    label = stringResource(R.string.avg_speed),
                    value = "$avgSpeed mph",
                    accent = GlimmerTheme.colors.secondary
                )
            }
            // Item 4: Calories
            item {
                GlimmerStatItem(
                    icon = Icons.Rounded.LocalFireDepartment,
                    label = stringResource(R.string.calories),
                    value = calories,
                    accent = GlimmerTheme.colors.negative
                )
            }
        }
    // }
}

// Helper wrapper to map your data to the official ListItem
@Composable
private fun GlimmerStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color
) {
    // Using the 'Focusable' overload (no onClick needed for read-only)
    ListItem(
        // LEADING ICON
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                //modifier = Modifier.size(24.dp)
            )
        },
        // SUPPORTING LABEL (The descriptor, e.g. "DISTANCE")
        supportingLabel = {
            Text(
                text = label,
                style = GlimmerTheme.typography.bodySmall,
                color = GlimmerTheme.colors.outline
            )
        },
        // CONTENT (The main value, e.g. "12.5 km")
        content = {
            Text(
                text = value,
                style = GlimmerTheme.typography.bodyLarge
            )
        }
    )
}