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
    @Transient
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
    data class TaskListKey(val categoryId: Long) : PhotoDoNavKeys(), BottomBarItem {
        @Transient override val icon = Icons.Default.Dehaze
        override val title = "List"
    }

    @Serializable
    data class TaskListDetailKey(val listId: String) : PhotoDoNavKeys()

    @Serializable
    data object SettingsKey : PhotoDoNavKeys(), BottomBarItem {
        @Transient override val icon = Icons.Default.Settings
        override val title = "Settings"
    }
}
