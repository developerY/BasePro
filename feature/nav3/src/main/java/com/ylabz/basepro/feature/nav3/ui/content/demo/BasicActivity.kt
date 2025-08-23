package com.ylabz.basepro.feature.nav3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation3.runtime.NavKey

import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
// import androidx.navigation3.ui.NavDisplayScope // No longer needed for the global transitions
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

    var globalEnterExitEnabled by remember { mutableStateOf(false) }
    var globalPopEnabled by remember { mutableStateOf(false) }
    var globalPredictivePopEnabled by remember { mutableStateOf(false) }

    // TODO: Add state and toggles for ScreenC's specific transitions if needed

    val slideRightSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    }

    val slideLeftSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    }

    val noAnimationSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        EnterTransition.None togetherWith ExitTransition.None
    }


    Column(modifier = modifier) {
        Text("Global NavDisplay Transitions:", modifier = Modifier.padding(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enter/Exit Slides:")
            Button(onClick = { globalEnterExitEnabled = !globalEnterExitEnabled }) {
                Text(if (globalEnterExitEnabled) "ON" else "OFF")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pop Slides:")
            Button(onClick = { globalPopEnabled = !globalPopEnabled }) {
                Text(if (globalPopEnabled) "ON" else "OFF")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Predictive Pop Slides:")
            Button(onClick = { globalPredictivePopEnabled = !globalPredictivePopEnabled }) {
                Text(if (globalPredictivePopEnabled) "ON" else "OFF")
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Handle system back press if there's something to pop
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
                                Text("Go Back to A")
                            }
                        }
                    }
                }
                entry<ScreenC>(
                    // TODO: Make ScreenC metadata transitions also toggleable
                    /* metadata = NavDisplay.transitionSpec {
                        // ...
                    } + NavDisplay.popTransitionSpec {
                        // ...
                    } + NavDisplay.predictivePopTransitionSpec {
                        // ...
                    }*/
                ) {
                    ContentGreen("This is Screen C") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { backStack.removeLastOrNull() }) {
                                Text("Go Back to B")
                            }
                        }
                    }
                }
            },
            transitionSpec = if (globalEnterExitEnabled) slideRightSpec else noAnimationSpec,
            popTransitionSpec = if (globalPopEnabled) slideLeftSpec else noAnimationSpec,
            predictivePopTransitionSpec = if (globalPredictivePopEnabled) slideLeftSpec else noAnimationSpec
        )
    }
}
