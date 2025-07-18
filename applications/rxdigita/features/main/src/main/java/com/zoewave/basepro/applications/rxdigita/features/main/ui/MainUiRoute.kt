package com.zoewave.basepro.applications.rxdigita.features.main.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Replace this with your actual UI structure based on uiState
    // This is a basic example how you might handle different states.
    when (uiState) {
        is MainUiState.Loading -> {
            // You would typically show a loading indicator here
            // e.g., LoadingScreen()
            Text(modifier = modifier, text = "Loading Main Feature...")
        }
        is MainUiState.Error -> {
            // You would typically show an error message here
            // e.g., ErrorScreen(errorMessage = uiState.message, onRetry = { viewModel.onEvent(MainEvent.LoadData) })
            Text(modifier = modifier, text = "Error: ${uiState.message}")
        }
        is MainUiState.Success -> {
            // This is where you'''d call your actual screen composable for the main feature
            // e.g., MainScreen(data = uiState.data, onEvent = viewModel::onEvent, navTo = navTo)
            Text(modifier = modifier, text = "Main Feature Loaded: ${uiState.data}")
        }
    }
}
