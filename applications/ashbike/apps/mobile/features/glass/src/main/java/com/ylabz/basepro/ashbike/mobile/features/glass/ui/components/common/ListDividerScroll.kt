package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme

@Composable
fun ListDividerScroll() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 32.dp, end = 8.dp),
        thickness = 0.5.dp,
        // Use Glimmer's Outline Variant for subtle dividers
        // color = GlimmerTheme.colors.outlineVariant.copy(alpha = 0.3f)
        // Allowed: Use 'outlineVariant' for dividers
        color = GlimmerTheme.colors.outlineVariant
    )
}