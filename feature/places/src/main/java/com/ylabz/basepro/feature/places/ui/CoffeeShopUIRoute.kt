package com.ylabz.basepro.feature.places.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.places.ui.components.CoffeeShopList

@Composable
fun CoffeeShopUIRoute(
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: CoffeeShopViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is CoffeeShopUIState.Loading -> LoadingScreen()
        is CoffeeShopUIState.Success -> CoffeeShopList((uiState as CoffeeShopUIState.Success).coffeeShops)
        is CoffeeShopUIState.Error -> ErrorScreen(
            (uiState as CoffeeShopUIState.Error).message,
            onRetry = { viewModel.onEvent(CoffeeShopEvent.LoadCoffeeShops) }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
