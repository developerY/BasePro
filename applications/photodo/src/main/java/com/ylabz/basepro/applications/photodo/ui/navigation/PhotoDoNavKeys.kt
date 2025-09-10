package com.ylabz.basepro.applications.photodo.ui.navigation


import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeFeedKey : NavKey

@Serializable
data class PhotoDoDetailKey(val photoDoId: String) : NavKey

@Serializable
data object SettingsKey : NavKey

// You can add more NavKeys here as your application evolves.
// For example:
// @Serializable
// data object AddPhotoDoKey : NavKey
//
// @Serializable
// data class AlbumViewKey(val albumId: String) : NavKey