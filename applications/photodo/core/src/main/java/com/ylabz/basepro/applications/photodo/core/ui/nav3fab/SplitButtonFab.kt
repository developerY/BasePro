package com.ylabz.basepro.applications.photodo.core.ui.nav3fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.core.ui.FabStateOrig

@Composable
fun SplitButtonFab(
    fabStateOrig: FabStateOrig.Split
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row {
        // This is the secondary, expandable action
        AnimatedVisibility(visible = isExpanded) {
            ExtendedFloatingActionButton(
                onClick = {
                    fabStateOrig.secondaryOnClick()
                    isExpanded = false
                },
                text = { Text(fabStateOrig.secondaryText) },
                icon = { Icon(fabStateOrig.secondaryIcon, contentDescription = null) },
            )
        }

        Spacer(Modifier.width(16.dp))

        // This is the primary action button
        FloatingActionButton(
            onClick = {
                // If secondary action is available, this button expands it.
                // Otherwise, it just performs the primary action.
                if (isExpanded) {
                    fabStateOrig.primaryOnClick()
                } else {
                    isExpanded = true
                }
            },
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(fabStateOrig.primaryIcon, contentDescription = null)
        }
    }
}

@Preview
@Composable
fun SplitButtonFabPreview() {
    val fabStateOrig = FabStateOrig.Split(
        primaryText = "Primary Action",
        primaryIcon = Icons.Default.Add,
        primaryOnClick = {},
        secondaryText = "Secondary Action",
        secondaryIcon = Icons.Default.Add,
        secondaryOnClick = {}
    )
    SplitButtonFab(fabStateOrig = fabStateOrig)
}
