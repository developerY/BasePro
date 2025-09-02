package com.ylabz.basepro.feature.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.home.ui.components.HomeMainScreen

@Composable
fun HomeMainRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is HomeUiState.Loading -> {
            LoadingScreen()
        }

        is HomeUiState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(HomeEvent.Retry)
            }
        }

        is HomeUiState.Success -> {
            HomeMainScreen(
                modifier = modifier,
                //data = uiState.data,
                onEvent = { event -> viewModel.onEvent(event) },
                navTo = navTo
            )
        }
    }
}

// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}