package com.ylabz.basepro.feature.maps.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.network.repository.DrivingPtsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val drivingPtsRepository : DrivingPtsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MapUIState>(MapUIState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.LoadData -> {
                val start = "origin=${(37.7749 + Math.random() / 100)},${-122.4194 + Math.random() / 100}"
                val end = "destination=${(37.7749 + Math.random() / 100)},${-122.4194 + Math.random() / 100}"
                fetchDirections(start, end)
            }
            is MapEvent.OnRetry -> {
                _uiState.value = MapUIState.Loading
                val start = "origin=${(37.7749 + Math.random() / 100)},${-122.4194 + Math.random() / 100}"
                val end = "destination=${(37.7749 + Math.random() / 100)},${-122.4194 + Math.random() / 100}"
                fetchDirections(start, end)
            }
            is MapEvent.UpdateDirections -> {
                fetchDirections(event.org, event.des)
            }
        }
    }

    private fun fetchDirections(org: String, des: String) {
        _uiState.value = MapUIState.Loading
        viewModelScope.launch {
            try {
                val directions = drivingPtsRepository.getDrivingPts(org, des)
                _uiState.value = MapUIState.Success(directions)
            } catch (e: Exception) {
                _uiState.value = MapUIState.PartialSuccess("Failed to load directions")
            }
        }
    }

}
