package com.zoewave.basepro.applications.rxdigita.features.medlist.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun MedListUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: MedListViewModel = hiltViewModel()
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is MedListUiState.Loading -> {
            Text(modifier = modifier, text = "Loading MedList Feature...")
        }

        is MedListUiState.Error -> {
            Text(modifier = modifier, text = "Error: ${uiState.message}")
        }

        is MedListUiState.Success -> {
            MedListContent(
                modifier = modifier,
                onMedListEvent = {},//viewModel::onMedListEvent,
                navTo = navTo
            )
        }
    }
}
