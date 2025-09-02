package com.rxdigita.basepro.applications.rxtrack.features.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.components.ErrorScreen
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.components.LoadingScreen
import com.rxdigita.basepro.applications.rxtrack.features.main.ui.components.home.MedDashboardContent

@Composable
fun MedUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: MedViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    // Replace this with your actual UI structure based on uiState
    // This is a basic example how you might handle different states.
    when (uiState) {
        is MedUiState.Loading -> {
            LoadingScreen()
        }

        is MedUiState.Error -> {
            ErrorScreen(
                errorMessage = "error", //currentBikeUiState.message,
                onRetry = { } // viewModel.onEvent(MedEvent.) } // <<< MODIFIED LINE: Use the passed-in viewModel
            )
        }

        is MedUiState.Success -> {
            MedDashboardContent(
                modifier = modifier.fillMaxSize(),
                // bikeRideInfo = medData,
                onMedEvent = {}, // viewModel::onEvent, // <<< MODIFIED LINE: Use the passed-in viewModel
                navTo = navTo
            )
        }
    }
}
