package com.ylabz.basepro.feature.nav3.ui.content.strategy


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
 * This is the main Activity for the demonstration.
 */
class AdaptiveLayoutActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold { paddingValues ->
                    AdaptiveLayoutDemo(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

// Data class for a product. Must be @Serializable to be used as a NavKey argument.
@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String
)

// Define the navigation keys.
@Serializable
data object ProductList : NavKey

@Serializable
data class ProductDetail(val productJson: String) : NavKey

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
                ProductListScreen { product ->
                    val productJson = Json.encodeToString(product)
                    backStack.add(ProductDetail(productJson))
                }
            }
            entry<ProductDetail>(
                // Mark this entry as the detail pane.
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                val product = remember { Json.decodeFromString<Product>(key.productJson) }
                ProductDetailScreen(product = product) {
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

@Composable
fun ProductListScreen(onProductClick: (Product) -> Unit) {
    val products = remember {
        listOf(
            Product("1", "Smartphone", "A device that combines a mobile phone with a personal computer."),
            Product("2", "Laptop", "A small, portable computer with a screen and keyboard."),
            Product("3", "Headphones", "A pair of small loudspeakers for private listening."),
            Product("4", "Smartwatch", "A wearable device that offers smartphone-like functions.")
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Products",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(products) { product ->
                ProductListItem(product = product, onProductClick = onProductClick)
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onProductClick: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProductDetailScreen(product: Product, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
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