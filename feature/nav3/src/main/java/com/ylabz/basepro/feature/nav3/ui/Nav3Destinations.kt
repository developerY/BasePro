package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry // Correct import
import androidx.navigation3.runtime.NavKey    // Correct import
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@Serializable
data object FeedKey : NavKey

@Serializable
data object ProfileKey : NavKey

@Serializable
data class ErrorKey(val message: String) : NavKey

@Composable
fun HomeScreen(navEntry: NavEntry<HomeKey>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Screen. Key: ${navEntry.key}")
    }
}

@Composable
fun FeedScreen(navEntry: NavEntry<FeedKey>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Feed Screen. Key: ${navEntry.key}")
    }
}

@Composable
fun ProfileScreen(navEntry: NavEntry<ProfileKey>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen. Key: ${navEntry.key}")
    }
}

@Composable
fun ErrorScreen(navEntry: NavEntry<ErrorKey>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: ${navEntry.key.message}")
    }
}

val nav3EntryProvider: (NavKey) -> NavEntry<out NavKey> = { key ->
    when (key) {
        is HomeKey -> NavEntry(key) { HomeScreen(it) }
        is FeedKey -> NavEntry(key) { FeedScreen(it) }
        is ProfileKey -> NavEntry(key) { ProfileScreen(it) }
        is ErrorKey -> NavEntry(key) { ErrorScreen(it) }
        else -> NavEntry(ErrorKey("Unknown key type: ${key::class.simpleName}")) {
            ErrorScreen(it)
        }
    }
}
