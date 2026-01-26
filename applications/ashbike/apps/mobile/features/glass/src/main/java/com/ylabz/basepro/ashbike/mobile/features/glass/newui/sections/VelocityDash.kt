package com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.R
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements.DataWidget

@Composable
fun VelocityDash(
    speed: String,
    heading: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // ADD THIS: Explicitly disable focus for this component
                .focusProperties { canFocus = false }
                // REDUCE PADDING: Was 12.dp, change to 4.dp or 8.dp
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. SPEED (Primary Data)
            DataWidget(
                label = stringResource(R.string.speed),
                value = speed,
                isHero = true // Flag for "Extra Big"
            )

            // 2. HEADING (Secondary Data)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = GlimmerTheme.colors.secondary
                )
                Text(
                    text = heading,
                    style = GlimmerTheme.typography.titleMedium
                )
            }
        }
    }
}