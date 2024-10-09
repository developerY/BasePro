package com.ylabz.basepro.listings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.listings.ui.components.ListCompose
import com.ylabz.basepro.listings.ui.components.ErrorScreen
import com.ylabz.basepro.listings.ui.components.LoadingScreen

@Composable
fun ListUIRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
   val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is ListUIState.Loading -> {
            LoadingScreen()
        }
        is ListUIState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(ListEvent.OnRetry)
            }
        }
        is ListUIState.Success -> {
            Column(modifier = modifier) {
                ListCompose(
                    modifier = modifier,
                    data = uiState.data,
                    onEvent = { event -> viewModel.onEvent(event) },
                    navTo = navTo
                )
            }
        }
    }
}
