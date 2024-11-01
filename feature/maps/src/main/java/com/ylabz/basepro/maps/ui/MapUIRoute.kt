package com.ylabz.basepro.maps.ui

import MapContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.GoogleMap
import com.ylabz.basepro.maps.ui.components.ErrorScreen
import com.ylabz.basepro.maps.ui.components.LoadingScreen


@Composable
fun MapUIRoute(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    navTo: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.onEvent(MapEvent.LoadData)
    }

    when (uiState) {
        is MapUIState.Loading -> {
            LoadingScreen()
        }
        is MapUIState.Error -> {
            ErrorScreen(
                errorMessage = uiState.message,
                onRetry = { viewModel.onEvent(MapEvent.OnRetry) }
            )
        }
        is MapUIState.Success -> {
            MapContent(
                directions = uiState.directions,
                paddingValues = paddingValues,
                modifier = modifier
            )
        }
        is MapUIState.PartialSuccess -> {
            MapContent(
                directions = "Directions failed to load",
                paddingValues = paddingValues,
                isError = true,
                errorMessage = uiState.message,
                onRetry = { viewModel.onEvent(MapEvent.OnRetry) },
                modifier = modifier
            )
        }
    }
}

