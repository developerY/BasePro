package com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text

@Composable
fun DataWidget(
    label: String,
    value: String,
    isHero: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label.uppercase(),
            style = GlimmerTheme.typography.bodySmall,
            color = GlimmerTheme.colors.outline
        )
        Text(
            text = value,
            // Use TitleLarge for Hero (Speed), TitleMedium for others if needed
            style = if (isHero) GlimmerTheme.typography.titleLarge else GlimmerTheme.typography.titleMedium,
            color = GlimmerTheme.colors.primary
        )
    }
}