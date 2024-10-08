package com.ylabz.basepro.listings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.listings.ui.components.CamCompose
import com.ylabz.basepro.listings.ui.components.ErrorScreen
import com.ylabz.basepro.listings.ui.components.LoadingScreen

@Composable
fun ListUIRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: CamViewModel = hiltViewModel()
) {
   val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is CamUIState.Loading -> {
            LoadingScreen()
        }
        is CamUIState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(CamEvent.OnRetry)
            }
        }
        is CamUIState.Success -> {
            Column(modifier = modifier) {
                CamCompose(
                    modifier = modifier,
                    data = uiState.data,
                    onEvent = { event -> viewModel.onEvent(event) },
                    navTo = navTo
                )
            }
        }
    }
}
