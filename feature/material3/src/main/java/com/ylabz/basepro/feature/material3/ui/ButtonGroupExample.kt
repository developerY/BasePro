package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonListGroupExample(modifier: Modifier = Modifier) {
    val numButtons = 7 // Example with 7 buttons
    ButtonGroup(
        modifier = modifier,
        overflowIndicator = { menuState ->
            FilledIconButton(
                onClick = {
                    if (menuState.isExpanded) {
                        menuState.dismiss()
                    } else {
                        menuState.show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Show more options",
                )
            }
        }
    ) { // ButtonGroupScope
        for (i in 0 until numButtons) {
            clickableItem(onClick = {}, label = "$i")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonListGroupExamplePreview() {
    MaterialTheme {
        // Constrain width to demonstrate overflow behavior
        ButtonListGroupExample(modifier = Modifier.width(200.dp))
    }
}
