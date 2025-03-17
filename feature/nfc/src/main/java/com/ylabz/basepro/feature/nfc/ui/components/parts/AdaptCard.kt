package com.ylabz.basepro.feature.nfc.ui.components.parts

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AdaptivePane(
    showOnePane: Boolean,
    /* ... */
) {
    if (showOnePane) {
        Text("one ")
    } else {
        Text("Two")
    }
}
