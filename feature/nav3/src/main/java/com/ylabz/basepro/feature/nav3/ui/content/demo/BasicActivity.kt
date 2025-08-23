package com.ylabz.basepro.feature.nav3.ui.content.demo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
// It seems the Content composables are in com.example.nav3recipes.content
// Adjust this if your project structure for these is different.
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

@Serializable
private data object ScreenD : NavKey


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
            Text("Forward Slide (Right):")
            Button(onClick = { globalEnterExitEnabled = !globalEnterExitEnabled }) {
                Text(if (globalEnterExitEnabled) "ON" else "OFF")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Backward Slide (Left):")
            Button(onClick = { globalPopEnabled = !globalPopEnabled }) {
                Text(if (globalPopEnabled) "ON" else "OFF")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Predictive Back Slide (Left):")
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
                                Text("Go Back to A (Button)")
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
                            Button(onClick = { backStack.add(ScreenD) }) {
                                Text("Go to Screen D")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { backStack.removeLastOrNull() }) {
                                Text("Go Back to B (Button)")
                            }
                        }
                    }
                }
                entry<ScreenD> {
                    // Using ContentOrange as placeholder for ScreenD's content.
                    // Replace with ContentYellow or another if available/preferred.
                    ContentOrange("This is Screen D (Predictive Pop Demo)") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { backStack.removeLastOrNull() }) {
                                Text("Go Back to C (Button)")
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

// TODO: Define ContentYellow or ensure ContentOrange, ContentMauve, ContentGreen
// are correctly imported and available in the scope.
// Example for ContentOrange (if it's not in com.example.nav3recipes.content):
/*
@Composable
fun ContentOrange(text: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Orange)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}
*/
