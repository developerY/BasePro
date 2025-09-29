package com.ylabz.basepro.applications.photodo.ui.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


// This Saver is crucial for `rememberSaveable` to work with your custom NavKey class.
val NavKeySaverWorking = Saver<NavKey, String>(
    save = {
        when (it) {
            is PhotoDoNavKeys.HomeFeedKey -> "Home"
            is PhotoDoNavKeys.TaskListKey -> "Tasks"
            is PhotoDoNavKeys.SettingsKey -> "Settings"
            else -> "Unknown" // Should not happen for top-level items
        }
    },
    restore = {
        when (it) {
            "Home" -> PhotoDoNavKeys.HomeFeedKey
            "Tasks" -> PhotoDoNavKeys.TaskListKey(0L) // Restore with a default
            "Settings" -> PhotoDoNavKeys.SettingsKey
            else -> PhotoDoNavKeys.HomeFeedKey // Default fallback
        }
    }
)

@OptIn(InternalSerializationApi::class)
val NavKeySaver = Saver<NavKey, String>(
    save = { navKey ->
        val serializer = navKey::class.serializer() as KSerializer<NavKey>
        Json.encodeToString(serializer, navKey)
    },
    restore = { savedValue ->
        // This is a simplified approach. For a real app, you'd need a more robust
        // way to determine the correct serializer based on the saved data.
        // This might involve storing the class name along with the JSON.
        // For this specific app, we can make some assumptions.
        when {
            savedValue.contains("HomeFeedKey") -> Json.decodeFromString<PhotoDoNavKeys.HomeFeedKey>(savedValue)
            savedValue.contains("TaskListKey") -> Json.decodeFromString<PhotoDoNavKeys.TaskListKey>(savedValue)
            savedValue.contains("TaskListDetailKey") -> Json.decodeFromString<PhotoDoNavKeys.TaskListDetailKey>(savedValue)
            savedValue.contains("SettingsKey") -> Json.decodeFromString<PhotoDoNavKeys.SettingsKey>(savedValue)
            else -> throw IllegalArgumentException("Unknown NavKey type")
        }
    }
)
