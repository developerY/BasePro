package com.ylabz.basepro.cam.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.cam.ui.components.CamCompose
import com.ylabz.basepro.cam.ui.components.ErrorScreen
import com.ylabz.basepro.cam.ui.components.LoadingScreen

@Composable
fun CamUIRoute(
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
            CamCompose(
                modifier = modifier,
                data = uiState.data,
                onEvent = { event -> viewModel.onEvent(event) },
                navTo = navTo
            )
        }
    }
}
