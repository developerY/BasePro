package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail.components.DetailCard

/**
 * The route for displaying the details of a specific task.
 * This composable is stateless and driven by the provided ViewModel.
 */
@Composable
fun PhotoDoDetailUiRoute(
    modifier: Modifier = Modifier,
    // The ViewModel is now a required parameter, removing the default hiltViewModel() call.
    // This ensures that the caller is responsible for providing a correctly initialized ViewModel.
    viewModel: PhotoDoDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoDetailUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoDetailUiState.Success -> {
            DetailCard(
                modifier = modifier,
                state = state
            )
        }
        is PhotoDoDetailUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}
