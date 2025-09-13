package com.ylabz.basepro.applications.photodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * An interface to mark top-level destinations that should appear in the bottom navigation bar.
 */
interface BottomBarItem {
    val icon: ImageVector
    val title: String
}

// Renamed HomeFeedKey to HomeListKey for clarity
@Serializable
data object HomeFeedKey : NavKey, BottomBarItem {
    override val icon = Icons.Default.Home
    override val title = "Home"
}

// This will represent your second tab, which can also have its own master-detail flow
@Serializable
data object PhotoListKey : NavKey, BottomBarItem {
    override val icon = Icons.Default.List
    override val title = "List"
}

@Serializable
data object SettingsKey : NavKey, BottomBarItem {
    override val icon = Icons.Default.Settings
    override val title = "Settings"
}

// The key for the detail screen, which takes an ID
@Serializable
data class PhotoDoDetailKey(val photoDoId: String) : NavKey


// You can add more NavKeys here as your application evolves.
// For example:
// @Serializable
// data object AddPhotoDoKey : NavKey
//
// @Serializable
// data class AlbumViewKey(val albumId: String) : NavKey