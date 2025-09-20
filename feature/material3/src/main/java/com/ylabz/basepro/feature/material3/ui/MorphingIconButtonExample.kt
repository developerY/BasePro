package com.ylabz.basepro.feature.material3.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MorphingIconButtonExample() {
    var checked by remember { mutableStateOf(false) }

    FilledTonalIconToggleButton(
        checked = checked,
        onCheckedChange = { checked = it },
        shapes = IconToggleButtonShapes( // Directly construct IconToggleButtonShapes
            shape = CircleShape,
            checkedShape = RoundedCornerShape(12.dp),
            pressedShape = CircleShape
        )
    ) {
        val icon = if (checked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        Icon(icon, contentDescription = "Favorite")
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun MorphingIconButtonExamplePreview() {
    MorphingIconButtonExample()
}
