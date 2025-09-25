package com.ylabz.basepro.applications.photodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * An interface to mark top-level destinations that should appear in the bottom navigation bar.
 */
interface BottomBarItem {
    @Transient // Add Transient here as well if the interface itself is ever directly serialized (though less common for interfaces)
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
        @Transient override val icon = Icons.Default.Home
        override val title = "Home"
    }

    @Serializable
    data class PhotoDoDetailKey(val photoId: String) : PhotoDoNavKeys()

    @Serializable
    data class PhotoDolListKey(val projectId: Long) : PhotoDoNavKeys(), BottomBarItem {
        @Transient override val icon = Icons.Default.Dehaze // Or Icons.Default.List as it was before
        override val title = "List" // You might want to make the title dynamic based on the project
    }

    @Serializable
    data object SettingsKey : PhotoDoNavKeys(), BottomBarItem {
        @Transient override val icon = Icons.Default.Settings
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