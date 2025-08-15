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
        Text("Home Screen")
    }
}

@Composable
fun FeedScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Feed Screen")
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}

@Composable
fun ErrorScreen(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: ${message ?: "Unknown error"}")
    }
}
