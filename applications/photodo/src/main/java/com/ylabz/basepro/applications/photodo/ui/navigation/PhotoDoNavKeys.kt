package com.ylabz.basepro.applications.photodo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface BottomBarItem {
    val title: String
    val icon: ImageVector
}

@Serializable
sealed class PhotoDoNavKeys : NavKey {

    @Serializable
    data object HomeFeedKey : PhotoDoNavKeys(), BottomBarItem {
        override val title = "Home"
        @Transient
        override val icon = Icons.Default.Home
    }

    @Serializable
    data class TaskListKey(val categoryId: Long) : PhotoDoNavKeys(), BottomBarItem {
        override val title = "Tasks"
        @Transient
        override val icon = Icons.Default.List
    }

    @Serializable
    data class TaskListDetailKey(val listId: String) : PhotoDoNavKeys()

    @Serializable
    data object SettingsKey : PhotoDoNavKeys(), BottomBarItem {
        override val title = "Settings"
        @Transient
        override val icon = Icons.Default.Settings
    }
}
