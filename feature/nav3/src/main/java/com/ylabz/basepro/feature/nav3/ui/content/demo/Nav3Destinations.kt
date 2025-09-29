package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// --- Navigation Keys ---

@Serializable
data object HomeKey : NavKey

@Serializable
data object HomeDetailKey : NavKey // Example detail screen

@Serializable
data object FeedKey : NavKey

@Serializable
data object FeedDetailKey : NavKey // Example detail screen

@Serializable
data object ProfileKey : NavKey

@Serializable
data object ProfileDetailKey : NavKey // Example detail screen


// --- Screen Composables ---
// These now accept onNavigate and onBack lambdas

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (NavKey) -> Unit,
    onBack: () -> Unit // Though likely not used on a root screen
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateToDetail(HomeDetailKey) }) {
            Text("Go to Home Detail")
        }
    }
}

@Composable
fun HomeDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Detail Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back to Home")
        }
    }
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (NavKey) -> Unit,
    onBack: () -> Unit // Though likely not used on a root screen
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Feed Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateToDetail(FeedDetailKey) }) {
            Text("Go to Feed Detail")
        }
    }
}

@Composable
fun FeedDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Feed Detail Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back to Feed")
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (NavKey) -> Unit,
    onBack: () -> Unit // Though likely not used on a root screen
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigateToDetail(ProfileDetailKey) }) {
            Text("Go to Profile Detail")
        }
    }
}

@Composable
fun ProfileDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Detail Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back to Profile")
        }
    }
}


// --- Entry Provider Function ---

fun createNav3EntryProvider(
    onNavigate: (NavKey) -> Unit,
    onBack: () -> Unit
): (NavKey) -> NavEntry<NavKey> { // <<< CORRECTED RETURN TYPE
    return androidx.navigation3.runtime.entryProvider {
        entry<HomeKey> {
            HomeScreen(
                onNavigateToDetail = onNavigate,
                onBack = onBack
            )
        }
        entry<HomeDetailKey> {
            HomeDetailScreen(onBack = onBack)
        }
        entry<FeedKey> {
            FeedScreen(
                onNavigateToDetail = onNavigate,
                onBack = onBack
            )
        }
        entry<FeedDetailKey> {
            FeedDetailScreen(onBack = onBack)
        }
        entry<ProfileKey> {
            ProfileScreen(
                onNavigateToDetail = onNavigate,
                onBack = onBack
            )
        }
        entry<ProfileDetailKey> {
            ProfileDetailScreen(onBack = onBack)
        }
    }
}
