package com.ylabz.basepro.ui.bar

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "BasePro", // Dynamic title
    scope: CoroutineScope,
    drawerState: DrawerState,
    actions: @Composable RowScope.() -> Unit = {} // Slot for optional actions
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            // Default action
            IconButton(onClick = { /* Handle face action */ }) {
                Icon(Icons.Default.Face, contentDescription = "Face")
            }
            // Additional actions passed via the slot
            actions()
        }
    )
}

/*
@Composable
fun AppTopBarPreview() {
    // Mock DrawerState and CoroutineScope for the preview
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppTopBar(
        title = "Preview Title",
        scope = scope,
        drawerState = drawerState,
        actions = {
            // Example additional action for the preview
            IconButton(onClick = { /* Handle settings action */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}


 */