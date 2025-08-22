package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.content.ContentBlue
import com.example.nav3recipes.content.ContentGreen
import kotlinx.serialization.Serializable

@Serializable
data object NoteListScreen : NavKey

@Serializable
data class NoteDetailDetailScreen(val id: Int) : NavKey

// Or
sealed interface AppScreen : NavKey {
    @Serializable
    data object Home : AppScreen

    @Serializable
    data class Profile(val userId: String) : AppScreen
}

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NoteListScreen)
    rememberNavBackStack(AppScreen.Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<NoteListScreen> {

                ContentGreen("Welcome to Nav3") {
                    Button(onClick = {
                        backStack.add(NoteDetailDetailScreen(123))
                    }) {
                        Text("Click to navigate")
                    }
                }
            }

            entry<NoteDetailDetailScreen> { key ->
                ContentBlue("Route id: ${key.id} ")
            }
        }
    )
}


@Serializable
private data object RouteA : NavKey

@Serializable
private data class RouteB(val id: String) : NavKey

@Composable
fun TestNavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(RouteA)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is RouteA -> NavEntry(key) {
                    ContentGreen("Welcome to Nav3") {
                        Button(onClick = {
                            backStack.add(RouteB("123"))
                        }) {
                            Text("Click to navigate")
                        }
                    }
                }

                is RouteB -> NavEntry(key) {
                    ContentBlue("Route id: ${key.id} ")
                }

                else -> {
                    error("Unknown route: $key")
                }
            }
        },

        // Animation for forward navigation (e.g., backStack.add)
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },

        // Animation for back navigation (e.g., backStack.removeLastOrNull)
        popTransitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        }
    )
}
