package com.ylabz.basepro.feature.material3.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonGroupTest(modifier: Modifier = Modifier) {
    val numButtons = 5
    ButtonGroup(
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
                contentDescription = "Localized description",
            )
        }
    }
    ) {
        for (i in 0 until numButtons) {
            clickableItem(onClick = {}, label = "$i")
        }
    }
}

@Preview
@Composable
fun ButtonGroupTestPreview() {
    ButtonGroupTest()
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonGroupExample(modifier: Modifier = Modifier) {
    val numButtons = 7
    ButtonGroup(
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
                    contentDescription = "Localized description",
                )
            }
        }
    ) {
        for (i in 0 until numButtons) {
            clickableItem(onClick = {}, label = "$i")
        }
    }
}

@Preview
@Composable
fun ButtonGroupExamplePreview() {
    ButtonGroupExample()
}