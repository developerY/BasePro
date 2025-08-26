package com.ylabz.basepro.feature.nav3.ui.content.vm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import kotlinx.serialization.Serializable

/**
 * Main activity to host the Nav3 demo.
 */
class Nav3ViewModelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewModelDemoScreen()
        }
    }
}

/**
 * A simple ViewModel to demonstrate state persistence.
 * The 'count' property will survive navigation events.
 */
class MyScreenViewModel : ViewModel() {
    // We use mutableIntStateOf for a more performant, unboxed integer state.
    var count by mutableIntStateOf(0)
}

/**
 * Serializable classes to represent our navigation destinations.
 */
@Serializable
private data object ScreenA : NavKey

@Serializable
private data object ScreenB : NavKey

/**
 * This composable demonstrates how to scope a ViewModel to a NavEntry's lifecycle.
 * The ViewModel will persist as long as the entry remains on the back stack.
 */
@Composable
fun ViewModelDemoScreen(modifier: Modifier = Modifier.Companion) {
    // 1. Initialize the back stack with a start destination.
    val backStack = rememberNavBackStack(ScreenA)

    // Handle system back press if there's something to pop.
    if (backStack.size > 1) {
        BackHandler(enabled = true) {
            backStack.removeLastOrNull()
        }
    }

    // 2. The core of the demo: NavDisplay with the decorators.
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // IMPORTANT: These three decorators are what make ViewModel scoping work.
        entryDecorators = listOf(
            // This sets up the basic composable scene. It's a foundational decorator.
            rememberSceneSetupNavEntryDecorator(),
            // This enables saving and restoring state for the composable,
            // which is essential for surviving process death.
            rememberSavedStateNavEntryDecorator(),
            // This is the key decorator that creates a ViewModelStore,
            // allowing ViewModels to be scoped to each NavEntry.
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // 3. Define the first entry with a ViewModel.
            entry<ScreenA> {
                // The viewModel() function automatically uses the decorator to
                // scope this ViewModel to the current NavEntry.
                val viewModel: MyScreenViewModel = viewModel()

                Column(
                    modifier = Modifier.Companion.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Text("Screen A (with ViewModel)")
                    Spacer(modifier = Modifier.Companion.height(16.dp))
                    Text("Count: ${viewModel.count}")
                    Spacer(modifier = Modifier.Companion.height(16.dp))
                    Button(onClick = { viewModel.count++ }) {
                        Text("Increment Count")
                    }
                    Button(
                        onClick = { backStack.add(ScreenB) },
                        modifier = Modifier.Companion.padding(top = 16.dp)
                    ) {
                        Text("Go to Screen B")
                    }
                }
            }
            // 4. Define a second, simple screen to navigate to.
            entry<ScreenB> {
                Column(
                    modifier = Modifier.Companion.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Text("Screen B (stateless)")
                    Button(
                        onClick = { backStack.removeLastOrNull() },
                        modifier = Modifier.Companion.padding(top = 16.dp)
                    ) {
                        Text("Go Back to A")
                    }
                }
            }
        }
    )
}