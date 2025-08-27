package com.ylabz.basepro.feature.nav3.ui.content.strategy


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
// Define the navigation keys.
@Serializable
data object ProductList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey

@Serializable
data object Profile : NavKey

/**
 * The main composable for the adaptive layout demo.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveLayoutDemo(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ProductList)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    NavDisplay(
        backStack = backStack,
        modifier = modifier.fillMaxSize(),
        // The onBack lambda now respects the keysToRemove count from the strategy.
        onBack = { keysToRemove ->
            repeat(keysToRemove) {
                if (backStack.isNotEmpty()) {
                    backStack.removeLastOrNull()
                }
            }
        },
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<ProductList>(
                // Mark this entry as the list pane.
                metadata = ListDetailSceneStrategy.listPane(
                    // This is the placeholder content for the detail pane
                    // when no item is selected.
                    detailPlaceholder = {
                        ContentYellow("Choose a product from the list")
                    }
                )
            ) {
                // 'it' is the NavKey (ProductList)
                ContentRed("Welcome to Nav3") {
                    Button(onClick = {
                        backStack.add(ProductDetail("ABC"))
                    }) {
                        Text("View product")
                    }
                }
            }
            entry<ProductDetail>(
                // Mark this entry as the detail pane.
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                // 'key' is the NavKey (ProductDetail), so we can access its id.
                ContentBlue("Product Detail: ${key.id}") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = {
                            backStack.add(Profile)
                        }) {
                            Text("View profile")
                        }
                    }
                }
            }
            entry<Profile>(
                // Mark this entry as an extra pane to take over the whole screen.
                metadata = ListDetailSceneStrategy.extraPane()
            ) {
                // 'it' is the NavKey (Profile)
                ContentGreen("Profile")
            }
        }
    )
}

/**
 * A simple composable for the Product List screen.
 */
@Composable
fun ContentRed(text: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * A simple composable for the Product Detail screen.
 */
@Composable
fun ContentBlue(text: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * A simple composable for the Profile screen.
 */
@Composable
fun ContentGreen(text: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

/**
 * A simple composable for a placeholder.
 */
@Composable
fun ContentYellow(text: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow.copy(alpha = 0.5f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}
