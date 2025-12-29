package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme

@Composable
fun ListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 48.dp, end = 8.dp),
        thickness = 1.dp,
        // Use Glimmer's Outline Variant for subtle dividers
        color = GlimmerTheme.colors.outlineVariant.copy(alpha = 0.5f)
    )
}