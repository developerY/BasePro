package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text


@Composable
fun GlassListItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    // STATE: Track focus to highlight the row when selected (enables scrolling via D-pad)
    var isFocused by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            // FIX: Use defaultMinSize + Padding instead of fixed height
            // This prevents clipping if the Glimmer font is tall
            //.height(48.dp) // Taller touch/focus target for easier scrolling
            // .defaultMinSize(minHeight = 48.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable() // <--- CRITICAL: Makes it selectable so the list scrolls
            // Glimmer typically handles focus via outline, but simple background highlight is okay
            .background(
                color = if (isFocused) GlimmerTheme.colors.surface.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp)
    ) {
        // LEADING ICON
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // TEXT CONTENT
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = value,
                color = GlimmerTheme.colors.surface,
                style = GlimmerTheme.typography.titleMedium
            )
            Text(
                text = label,
                color = GlimmerTheme.colors.secondary,
                style = GlimmerTheme.typography.titleSmall
            )
        }
    }
}

