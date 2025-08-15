package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry // For the NavEntry type if needed by screens
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry // Function to create NavEntry
import androidx.navigation3.runtime.entryProvider // Function to create EntryProvider
import kotlinx.serialization.Serializable

// Define your NavKeys for the bottom bar destinations
@Serializable
data object HomeKey : NavKey

@Serializable
data object FeedKey : NavKey

@Serializable
data object ProfileKey : NavKey

// Simple screen composables
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home Screen (Nav3)")
    }
}

@Composable
fun FeedScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Feed Screen (Nav3)")
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen (Nav3)")
    }
}

// Create the EntryProvider
// This maps each NavKey to its Composable content using the 'entry' function.
val appEntryProvider = entryProvider {
    // NavKey.serializer() can be used if your keys have arguments,
    // but for simple data objects, direct mapping is fine.
    entry(HomeKey) { HomeScreen() }
    entry(FeedKey) { FeedScreen() }
    entry(ProfileKey) { ProfileScreen() }
    // Add other entries as needed
}
