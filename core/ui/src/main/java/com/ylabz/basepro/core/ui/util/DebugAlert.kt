package com.ylabz.basepro.core.ui.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun DebugAlert(
    trigger: MutableState<String?>, // null = hidden, string = show
) {
    val message = trigger.value
    if (message != null) {
        AlertDialog(
            onDismissRequest = { trigger.value = null },
            confirmButton = {
                TextButton(onClick = { trigger.value = null }) {
                    Text("OK")
                }
            },
            title = { Text("Debug Click") },
            text = { Text(message) }
        )
    }
}

fun MutableState<String?>.debug(message: String) {
    this.value = message
}

@Composable
fun ExampleDialog(modifier: Modifier = Modifier) {
    val debugAlert = remember { mutableStateOf<String?>(null) }
    DebugAlert(trigger = debugAlert)
    // onClick = { debugAlert.value = "Button clicked at ${System.currentTimeMillis()}"
}
