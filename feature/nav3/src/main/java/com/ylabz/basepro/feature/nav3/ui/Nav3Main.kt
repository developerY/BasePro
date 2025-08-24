package com.ylabz.basepro.feature.nav3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
// TODO: Ensure these Content composables are correctly referenced or moved/redefined in LeanNav.kt
// For now, these imports will cause errors until LeanNav is set up.
// If your Content composables (ContentOrange etc.) are in the feature.nav3.ui.content package,
// the import should be: import com.ylabz.basepro.feature.nav3.ui.content.*
import com.example.nav3recipes.content.ContentGreen
import com.example.nav3recipes.content.ContentMauve
import com.example.nav3recipes.content.ContentOrange
import kotlinx.serialization.Serializable

@Serializable
sealed class NavMainScreens(val title: String) : NavKey {
    @Serializable
    data object MainScreensA : NavMainScreens("Screen A (Orange)")

    @Serializable
    data object MainScreensB : NavMainScreens("Screen B (Mauve)")

    @Serializable
    data object MainScreensC : NavMainScreens("Screen C (Green)")

    @Serializable
    data object MainScreensD : NavMainScreens("Screen D (Predictive Pop - Orange)")
    // Add other screen-specific properties here if needed in the future
}

@Composable
fun Nav3Main(modifier: Modifier = Modifier) {

    val backStack = rememberNavBackStack(NavMainScreens.MainScreensA)

    var globalEnterExitEnabled by remember { mutableStateOf(false) }
    var globalPopEnabled by remember { mutableStateOf(false) }
    var globalPredictivePopEnabled by remember { mutableStateOf(false) }
    var transitionsExpanded by remember { mutableStateOf(false) } // Or true to be open by default


    // New state to control Screen D's animation
    var screenDVerticalSlideEnabled by remember { mutableStateOf(false) } // Renamed for clarity

    val slideRightSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    }

    val slideLeftSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    }

    // New animation spec for sliding vertically
    val slideUpSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(initialOffsetY = { it }) togetherWith
                slideOutVertically(targetOffsetY = { it })
    }

    /*val slideUpSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        // Screen D slides in from the bottom
        slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) togetherWith
        // Exiting screen stays in place
        ExitTransition.KeepUntilTransitionsFinished
    }*/

    val slideDownSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        // Screen D slides out to the bottom
        EnterTransition.None togetherWith // Entering screen (revealed) has no animation
        slideOutVertically(targetOffsetY = { fullHeight -> fullHeight })
    }
    
    val noAnimationSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        EnterTransition.None togetherWith ExitTransition.None
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { transitionsExpanded = !transitionsExpanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Global NavDisplay Transitions:")
            Icon(
                imageVector = if (transitionsExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = if (transitionsExpanded) "Collapse" else "Expand"
            )
        }
        AnimatedVisibility(visible = transitionsExpanded) {
            Column {
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
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (backStack.size > 1) { // Corrected from backStack.size
            BackHandler(enabled = true) {
                backStack.removeLastOrNull()
            }
        }

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<NavMainScreens.MainScreensA> {
                    ContentOrange("This is Screen A") {
                        Button(onClick = { backStack.add(NavMainScreens.MainScreensB) }) {
                            Text("Go to Screen B")
                        }
                    }
                }
                entry<NavMainScreens.MainScreensB> {
                    ContentMauve("This is Screen B") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { backStack.add(NavMainScreens.MainScreensC) }) {
                                Text("Go to Screen C")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { backStack.removeLastOrNull() }) {
                                Text("Go Back to A (Button)")
                            }
                        }
                    }
                }
                entry<NavMainScreens.MainScreensC>(
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
                            Button(onClick = { backStack.add(NavMainScreens.MainScreensD) }) {
                                Text("Go to Screen D")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { backStack.removeLastOrNull() }) {
                                Text("Go Back to B (Button)")
                            }
                        }
                    }
                }
                entry<NavMainScreens.MainScreensD>(
                    // Conditionally apply the vertical slide animation
                    metadata = if (screenDVerticalSlideEnabled) {
                        NavDisplay.transitionSpec(slideUpSpec) +
                                NavDisplay.popTransitionSpec(slideUpSpec)
                    } else {
                        // Use default animations if the toggle is off
                        NavDisplay.transitionSpec(noAnimationSpec) +
                                NavDisplay.popTransitionSpec(noAnimationSpec)
                    }
                    // You could also add custom pop and predictive pop for ScreenD here:
                    // + NavDisplay.popTransitionSpec { ... }
                    // + NavDisplay.predictivePopTransitionSpec { ... }
                ) {
                    // Using ContentOrange as placeholder for ScreenD's content.
                    // Replace with ContentYellow or another if available/preferred.
                    ContentOrange("This is Screen D (Predictive Pop Demo)") {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // New UI for Screen D's specific transition
                            Text("Vertical Slide (Bottom):")
                            Button(onClick = { screenDVerticalSlideEnabled = !screenDVerticalSlideEnabled }) {
                                Text(if (screenDVerticalSlideEnabled) "ON" else "OFF")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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

// TODO: Define LeanNav composable in LeanNav.kt.
// Example signature:
// @Composable
// internal fun LeanNav(screenType: ScreenType, title: String, actions: @Composable () -> Unit) { ... }

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