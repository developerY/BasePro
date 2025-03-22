package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListDetailPaneScaffold(
    listContent: @Composable (onItemClick: (item: String) -> Unit) -> Unit,
    detailContent: @Composable (item: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf<String?>(null) }

    Row(modifier = modifier.fillMaxSize()) {
        // List Pane
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            listContent { item ->
                selectedItem = item
            }
        }

        // Divider between List and Detail
        HorizontalDivider(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
        )

        // Detail Pane
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
        ) {
            detailContent(selectedItem)
        }
    }
}
