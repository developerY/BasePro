package com.ylabz.basepro.feature.nav3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.content.ContentGreen
import com.example.nav3recipes.content.ContentMauve
import com.example.nav3recipes.content.ContentOrange
import kotlinx.serialization.Serializable

/**
 * This recipe shows how to override the default animations at the `NavDisplay` level, and at the
 * individual destination level, shown for `ScreenC`.
 *
 */
@Serializable
private data object ScreenA : NavKey

@Serializable
private data object ScreenB : NavKey

@Serializable
private data object ScreenC : NavKey


@Composable
fun LeanNav(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(ScreenA)

    // Handle system back press if there's something to pop
    // Assuming backStack.entries.size > 1 indicates that we are not on the initial entry
    if (backStack.size > 1) {
        BackHandler(enabled = true) {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() }, 
        entryProvider = entryProvider {
            entry<ScreenA> {
                ContentOrange("This is Screen A") {
                    Button(onClick = { backStack.add(ScreenB) }) {
                        Text("Go to Screen B")
                    }
                }
            }
            entry<ScreenB> {
                ContentMauve("This is Screen B") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { backStack.add(ScreenC) }) {
                            Text("Go to Screen C")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { backStack.removeLastOrNull() }) {
                            Text("Go Back to A (Button)")
                        }
                    }
                }
            }
            entry<ScreenC>(
               /* metadata = NavDisplay.transitionSpec {
                    // Slide new content up, keeping the old content in place underneath
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(1000)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } + NavDisplay.popTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                } + NavDisplay.predictivePopTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(1000)
                            )
                }*/
            ) {
                ContentGreen("This is Screen C") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { backStack.removeLastOrNull() }) {
                            Text("Go Back to B (Button)")
                        }
                    }
                }
            }
        },
        /*transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        }*/
    )
}
