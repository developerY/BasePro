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

/**
 * A sealed class representing all possible navigation destinations in the PhotoDo app.
 * This provides type-safety and allows for exhaustive checks in `when` statements.
 */
@Serializable
sealed class PhotoDoNavKeys : NavKey {

    @Serializable
    data object HomeFeedKey : PhotoDoNavKeys(), BottomBarItem {
        override val icon = Icons.Default.Home
        override val title = "Home"
    }

    @Serializable
    data class PhotoDoDetailKey(val photoId: String) : PhotoDoNavKeys()

    @Serializable
    data object PhotoDolListKey : PhotoDoNavKeys(), BottomBarItem {
        override val icon = Icons.Default.List
        override val title = "List"
    }

    @Serializable
    data object SettingsKey : PhotoDoNavKeys(), BottomBarItem {
        override val icon = Icons.Default.Settings
        override val title = "Settings"
    }

}


// You can add more NavKeys here as your application evolves.
// For example:
// @Serializable
// data object AddPhotoDoKey : NavKey
//
// @Serializable
// data class AlbumViewKey(val albumId: String) : NavKey