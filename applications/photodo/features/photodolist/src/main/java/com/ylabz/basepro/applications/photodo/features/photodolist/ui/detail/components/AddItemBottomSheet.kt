package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.PhotoDoDetailEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    onDismiss: () -> Unit,
    onSaveItem: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var itemName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(), // Handles insets for the system navigation bar
            horizontalAlignment = Alignment.End
        ) {
            Text("Add New Item", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (itemName.isNotBlank()) {
                        onSaveItem(itemName)
                    }
                },
                enabled = itemName.isNotBlank()
            ) {
                Text("Save Item")
            }
        }
    }
}

@Preview
@Composable
fun AddItemBottomSheetPreview() {
    AddItemBottomSheet(
        onDismiss = {},
        onSaveItem = {}
    )
}