package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.ylabz.basepro.feature.nav3.ui.content.ContentBlue
import com.ylabz.basepro.feature.nav3.ui.content.ContentGreen
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
            // Nav only on Detail Screen
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
                is RouteB -> NavEntry(
                    key,
                    metadata = NavDisplay.transitionSpec {
                        // New screen slides up from the bottom
                        slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight },
                            animationSpec = tween(400)
                        ) togetherWith
                                // Keep the old screen in place, visible until the new screen's transition finishes
                                ExitTransition.KeepUntilTransitionsFinished
                    } + NavDisplay.popTransitionSpec {
                        // The screen being revealed (underneath) has no special enter animation
                        EnterTransition.None togetherWith
                                // The screen being popped (RouteB) slides out downwards
                                slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(400)
                                )
                    } + NavDisplay.predictivePopTransitionSpec {
                        // Define predictive back pop animation if needed, similar to popTransitionSpec
                        EnterTransition.None togetherWith
                                slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(400)
                                )
                    }
                    ) {
                    ContentBlue("Route id: ${key.id} ")
                }

                else -> {
                    error("Unknown route: $key")
                }
            }
        },

        // Global animation

        // Animation for forward navigation (e.g., backStack.add)
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },

        // Animation for pop navigation (e.g., backStack.removeLastOrNull)
        popTransitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        // back button using the nav bar
        predictivePopTransitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        }
    )
}
