package com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AvTimer
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

@Composable
fun RideStatsPanel(
    distance: String,
    duration: String,
    avgSpeed: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // CHANGED: Use Column instead of LazyColumn to avoid nested scrolling.
        // The parent HomeContent handles the scroll now.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassListItem(Icons.Rounded.Straighten, "DISTANCE", "$distance km", GlimmerTheme.colors.secondary)
            ListDivider()

            GlassListItem(Icons.Rounded.AvTimer, "DURATION", duration, GlimmerTheme.colors.secondary)
            ListDivider()

            GlassListItem(Icons.Rounded.Speed, "AVG SPEED", "$avgSpeed mph", GlimmerTheme.colors.secondary)
            ListDivider()

            GlassListItem(Icons.Rounded.LocalFireDepartment, "CALORIES", calories, GlimmerTheme.colors.negative)
        }
    }
}

@Composable
private fun GlassListItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = value,
                style = GlimmerTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = GlimmerTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun ListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 36.dp, end = 8.dp),
        thickness = 1.dp,
        color = GlimmerTheme.colors.outlineVariant
    )
}