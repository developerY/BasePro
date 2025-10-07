package com.ylabz.basepro.applications.photodo.ui.demo.navigation


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A simple data class to hold the information for each menu item.
 */
data class FabAction(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandingFabMenuTest(
    menuItems: List<FabAction>
) {
    // Menu starts closed
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // Animate the rotation for '+' to 'x'
    val rotation by animateFloatAsState(
        targetValue = if (isFabMenuExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Show menu items when expanded
        if (isFabMenuExpanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 72.dp) // space above main FAB
            ) {
                menuItems.forEach { item ->
                    ExtendedFloatingActionButton(
                        onClick = {
                            item.onClick()
                            isFabMenuExpanded = false // collapse after click
                        },
                        text = { Text(item.text) },
                        icon = { Icon(item.icon, contentDescription = item.text) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { isFabMenuExpanded = !isFabMenuExpanded }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isFabMenuExpanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}


@Composable
fun TestFabInApp() {
    val menuItems = listOf(
        FabAction(Icons.Default.Create, "Category") {},
        FabAction(Icons.Default.NoteAdd, "List") {}
    )

    Scaffold(
        floatingActionButton = {
            com.ylabz.basepro.applications.photodo.ui.demo.navigation.ExpandingFabMenu(menuItems)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello, FAB test!")
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandingFabMenu(
    menuItems: List<FabAction>
) {
    var isExpanded by remember { mutableStateOf(false) }

    FloatingActionButtonMenu(
        expanded = isExpanded,
        // onExpandedChange = { expanded -> isExpanded = expanded }, // ✅ Required
        button = {
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded } // toggles menu
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 45f else 0f,
                    animationSpec = tween(200)
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) "Close menu" else "Open menu",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) {
        // Menu items shown when expanded
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            menuItems.forEach { item ->
                ExtendedFloatingActionButton(
                    onClick = {
                        item.onClick()
                        isExpanded = false // ✅ closes after selection
                    },
                    text = { Text(item.text) },
                    icon = { Icon(item.icon, contentDescription = item.text) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandingFabMenuWorking(
    menuItems: List<FabAction>
) {
    // Menu starts closed
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // Animate the rotation for '+' to 'x'
    val rotation by animateFloatAsState(
        targetValue = if (isFabMenuExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Show menu items when expanded
        if (isFabMenuExpanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 72.dp) // space above main FAB
            ) {
                menuItems.forEach { item ->
                    ExtendedFloatingActionButton(
                        onClick = {
                            item.onClick()
                            isFabMenuExpanded = false // collapse after click
                        },
                        text = { Text(item.text) },
                        icon = { Icon(item.icon, contentDescription = item.text) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { isFabMenuExpanded = !isFabMenuExpanded }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (isFabMenuExpanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}



/**
 * This is the self-contained expanding FAB menu composable.
 *
 * @param menuItems A list of [FabAction]s to display when the menu is open.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpandingFabMenuOld(
    menuItems: List<FabAction>
) {
    // This is the single source of truth for the menu's state.
    // It's initialized to `false` so the menu always starts closed.
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    FloatingActionButtonMenu(
        expanded = isFabMenuExpanded,
        // --- Main Button ('+' icon) ---
        button = {
            FloatingActionButton(
                // The button's only job is to toggle the menu's state.
                onClick = { isFabMenuExpanded = !isFabMenuExpanded }
            ) {
                // Animate the icon from a '+' to an 'x' when expanded.
                val rotation by animateFloatAsState(
                    targetValue = if (isFabMenuExpanded) 45f else 0f,
                    animationSpec = tween(durationMillis = 200)
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Open menu",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) { // --- Menu Items Content ---
        // This block defines the content that appears when the menu is expanded.
        // We arrange the items in a Column.
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            menuItems.forEach { item ->
                ExtendedFloatingActionButton(
                    onClick = {
                        item.onClick()
                        isFabMenuExpanded = false // Close the menu after clicking.
                    },
                    text = { Text(item.text) },
                    icon = { Icon(item.icon, contentDescription = item.text) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

// --- Preview Section ---

@Preview(showBackground = true)
@Composable
fun ExpandingFabPreview() {
    // 1. Define the list of actions for the preview.
    val sampleMenuItems = listOf(
        FabAction(
            icon = Icons.Default.Create,
            text = "Category",
            onClick = { /* TODO: Handle Category click */ }
        ),
        FabAction(
            icon = Icons.Default.NoteAdd,
            text = "List",
            onClick = { /* TODO: Handle List click */ }
        )
    )

    // 2. Use a Scaffold to place the FAB correctly.
    MaterialTheme {
        Scaffold(
            floatingActionButton = {
                ExpandingFabMenu(menuItems = sampleMenuItems)
            }
        ) { paddingValues ->
            // A simple background content for the preview.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Click the FAB to see the menu")
            }
        }
    }
}