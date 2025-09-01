package com.ylabz.basepro.feature.nav3.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.NavDisplay.transitionSpec
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
// TODO: Ensure these Content composables are correctly referenced or moved/redefined in LeanNav.kt
// For now, these imports will cause errors until LeanNav is set up.
// If your Content composables (ContentOrange etc.) are in the feature.nav3.ui.content package,
// the import should be: import com.ylabz.basepro.feature.nav3.ui.content.*
import com.ylabz.basepro.feature.nav3.ui.content.ContentGreen
import com.ylabz.basepro.feature.nav3.ui.content.ContentMauve
import com.ylabz.basepro.feature.nav3.ui.content.ContentOrange
import com.ylabz.basepro.feature.nav3.ui.content.ContentYellow
import com.ylabz.basepro.feature.nav3.ui.content.strategy.AdaptiveLayoutDemo
import kotlinx.serialization.Serializable


/**
 * This code demonstrates an adaptive list-detail layout using Nav3.
 *
 * In portrait mode, navigating from Screen A to Screen B will show Screen B
 * as a full-screen view.
 *
 * In landscape mode, Nav3 will automatically display both Screen A (the list pane)
 * and Screen B (the detail pane) side-by-side.
 */

@Serializable
sealed class NavMainScreens(val title: String) : NavKey {
    @Serializable
    data object MainScreensA : NavMainScreens("Screen A (List Pane)")

    @Serializable
    data object MainScreensB : NavMainScreens("Screen B (Detail Pane)")

    @Serializable
    data object MainScreensC : NavMainScreens("Screen C (Green)")

    @Serializable
    data object MainScreensD : NavMainScreens("Screen D (Predictive Pop - Orange)")
    // Add other screen-specific properties here if needed in the future
}

@Composable
fun Nav3Main(modifier: Modifier = Modifier) {
    //Nav3MainExample(modifier = modifier)
    AdaptiveLayoutDemo(modifier = modifier)
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Nav3MainExample(modifier: Modifier = Modifier) {

    // Initialize the back stack with ScreenA as the starting destination.
    val backStack = rememberNavBackStack(NavMainScreens.MainScreensA)

    var globalEnterExitEnabled by remember { mutableStateOf(false) }
    var globalPopEnabled by remember { mutableStateOf(false) }
    var globalPredictivePopEnabled by remember { mutableStateOf(false) }
    var transitionsExpanded by remember { mutableStateOf(false) } // Or true to be open by default

    // New state to control Screen D's animation
    var screenDVerticalSlideEnabled by remember { mutableStateOf(false) } // Renamed for clarity

    // Example
    /**
     * slideInHorizontally starts the new screen off-screen to the right (initialOffsetX = { it }
     * where it is the full width of the screen) and moves it to the center. At the same time,
     * slideOutHorizontally moves the old screen from the center off-screen to the left (targetOffsetX = { -it }).
     * This creates the standard "push" effect that you see when navigating forward in many apps.
     *
    val slideInHorizontallytransitionSpec = {
        // forward and backward
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    }*/

    // Standard horizontal slide animation for global navigation.
    val slideRightSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    }

    // Standard horizontal slide animation for global pop navigation.
    val slideLeftSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    }

    // Animation for a vertical pop-up.
    val slideUpSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        slideInVertically(initialOffsetY = { it }) togetherWith
                slideOutVertically(targetOffsetY = { it })
    }

    // An animation spec that does nothing. Used to disable transitions.
    val noAnimationSpec: AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        EnterTransition.None togetherWith ExitTransition.None
    }

    // This is the core of the adaptive layout. It manages the list-detail pattern.
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    Column(modifier = modifier) {
        // Collapsible section for global navigation toggles.
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

        // The global transition toggles are only visible when the user expands the section.
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

        // Handle the system back button.
        if (backStack.size > 1) {
            BackHandler(enabled = true) {
                backStack.removeLastOrNull()
            }
        }

        // This button controls Screen D's specific transition, and is placed at the top level
        /* to always be visible.
        Text("Screen D Vertical Slide Transition:", modifier = Modifier.padding(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vertical Slide (Bottom):")
            Button(onClick = { screenDVerticalSlideEnabled = !screenDVerticalSlideEnabled }) {
                Text(if (screenDVerticalSlideEnabled) "ON" else "OFF")
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) */

        // This is the core navigation composable.
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            // Add the adaptive scene strategy to enable list-detail layouts.
            sceneStrategy = listDetailStrategy,
            entryDecorators = listOf(
                // This decorator sets up the scene for a composable.
                rememberSceneSetupNavEntryDecorator(),
                // This decorator saves the state of the ViewModel.
                rememberSavedStateNavEntryDecorator(),
                // This decorator scopes the ViewModel to the NavEntry's lifecycle.
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                // MainScreensA is now the list pane.
                entry<NavMainScreens.MainScreensA>(
                    metadata = ListDetailSceneStrategy.listPane()
                    // Or make it super complex by ...
                    /* metadata = ListDetailSceneStrategy.listPane() + NavDisplay.transitionSpec {
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
                    }*/

                ) {
                    // The viewModel() function automatically uses the decorator to
                    // scope this ViewModel to the current NavEntry.
                    val viewModel: ScreenAViewModel = viewModel()

                    ContentOrange("This is Screen A (List Pane)") {
                        Button(onClick = { backStack.add(NavMainScreens.MainScreensB) }) {
                            Text("Go to Screen B")
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("Count: ${viewModel.count}")
                        Button(onClick = { viewModel.count++ }) {
                            Text("Increment")
                        }
                    }
                }
                // MainScreensB is now the detail pane.
                entry<NavMainScreens.MainScreensB>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    ContentMauve("This is Screen B (Detail Pane)") {
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
                // Screen C and D demonstrate a standard push-pull navigation,
                // and D still has its custom slide-up animation.
                entry<NavMainScreens.MainScreensC> {
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
                        // ListDetailSceneStrategy.extraPane() + // Ensure extraPane is added if intended for full screen override
                        transitionSpec(slideUpSpec) +
                                NavDisplay.popTransitionSpec(slideUpSpec)
                    } else {
                        // Use default animations if the toggle is off
                        // ListDetailSceneStrategy.extraPane() + // Ensure extraPane is added if intended for full screen override
                        transitionSpec(noAnimationSpec) +
                                NavDisplay.popTransitionSpec(noAnimationSpec)
                    }
                    // You could also add custom pop and predictive pop for ScreenD here:
                    // + NavDisplay.popTransitionSpec { ... }
                    // + NavDisplay.predictivePopTransitionSpec { ... }
                ) {
                    // Using ContentOrange as placeholder for ScreenD's content.
                    // Replace with ContentYellow or another if available/preferred.
                    ContentYellow("This is Screen D (Predictive Pop Demo)") {
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
            // Global animations that will be overridden by screen-specific metadata.
            transitionSpec = if (globalEnterExitEnabled) slideRightSpec else noAnimationSpec,
            popTransitionSpec = if (globalPopEnabled) slideLeftSpec else noAnimationSpec,
            predictivePopTransitionSpec = if (globalPredictivePopEnabled) slideLeftSpec else noAnimationSpec

            // FYI ... normal looks like this without the if statements
            // Animation for forward / back navigation (e.g., backStack.add)
            /*
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },

            // Animation for remove with pop navigation (e.g., backStack.removeLastOrNull)
            popTransitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },

            // Animation for back button transition
            predictivePopTransitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
            */
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
