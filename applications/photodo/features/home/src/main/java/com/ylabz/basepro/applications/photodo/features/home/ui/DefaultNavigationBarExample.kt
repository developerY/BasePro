package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DefaultNavigationBarExample() {
    // State to keep track of the selected item index
    var selectedItem by remember { mutableStateOf(0) }

    // List of navigation items: title, icon
    val navItems = listOf(
        "Home" to Icons.Default.Home,
        "Favorites" to Icons.Default.Favorite,
        "Settings" to Icons.Default.Settings
    )

    // The Scaffold provides a standard layout structure
    Scaffold(
        bottomBar = {
            // The NavigationBar component is placed here
            NavigationBar {
                // Loop through the navigation items
                navItems.forEachIndexed { index, (title, icon) ->
                    NavigationBarItem(
                        // Check if this item is currently selected
                        selected = selectedItem == index,
                        // Update the selected item index on click
                        onClick = { selectedItem = index },
                        // The icon for the item
                        icon = { Icon(imageVector = icon, contentDescription = title) },
                        // The text label for the item
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // This is the main content area of your screen.
        // The innerPadding provided by the Scaffold ensures your content
        // isn't hidden behind the top or bottom bars.
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Content for ${navItems[selectedItem].first}"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultNavigationBarPreview() {
    // You would wrap this in your app's theme, but for a default
    // preview, this is sufficient.
    DefaultNavigationBarExample()
}