package com.ylabz.basepro.applications.photodo.features.photodolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun PhotoDoListUiRoute(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    viewModel: PhotoDoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PhotoDoListUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is PhotoDoListUiState.Success -> {
            LazyColumn(modifier = modifier) {
                items(state.photoItems) { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) }
                            .padding(16.dp)
                    )
                }
            }
        }
        is PhotoDoListUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message)
            }
        }
    }
}