package com.ylabz.basepro.feature.maps.ui

import MapContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.feature.maps.ui.components.ErrorScreen
import com.ylabz.basepro.feature.maps.ui.components.LoadingScreen


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

