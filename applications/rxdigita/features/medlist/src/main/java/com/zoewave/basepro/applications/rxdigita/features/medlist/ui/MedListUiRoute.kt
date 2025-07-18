package com.zoewave.basepro.applications.rxdigita.features.medlist.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun MedListUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: MedListViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is MedListUiState.Loading -> {
            Text(modifier = modifier, text = "Loading MedList Feature...")
        }
        is MedListUiState.Error -> {
            Text(modifier = modifier, text = "Error: ${uiState.message}")
        }
        is MedListUiState.Success -> {
            Text(modifier = modifier, text = "MedList Feature Loaded: ${uiState.data}")
        }
    }
}
