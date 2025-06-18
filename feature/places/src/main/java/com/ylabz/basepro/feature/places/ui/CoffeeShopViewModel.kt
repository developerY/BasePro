package com.ylabz.basepro.feature.places.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.api.interfaces.YelpAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeShopViewModel @Inject constructor(
    private val yelpClient: YelpAPI,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoffeeShopUIState>(CoffeeShopUIState.Loading)
    val uiState: StateFlow<CoffeeShopUIState> = _uiState.asStateFlow()

    init {
        loadCoffeeShops(37.7749, -122.4194) // SF latitude and longitude
    }

    fun onEvent(event: CoffeeShopEvent) {
        when (event) {
            // Handle the new event here
            is CoffeeShopEvent.FindCafesNear -> {
                loadCoffeeShops(event.latitude, event.longitude)
            }
            is CoffeeShopEvent.OnCoffeeShopClick -> {
                // Handle click if needed
            }
            is CoffeeShopEvent.LoadCoffeeShops -> loadCoffeeShops(37.7749, -122.4194)
            is CoffeeShopEvent.Retry -> loadCoffeeShops(event.latitude, event.longitude)
        }
    }

    private fun loadCoffeeShops(latitude: Double, longitude: Double) {
        _uiState.value = CoffeeShopUIState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = CoffeeShopUIState.Loading
                val coffeeShops = yelpClient.getBusinesses(
                    latitude = latitude,
                    longitude = longitude,
                    radius = 1000.0,
                    sort_by = "rating",
                    categories = "coffee"
                )?.filterNotNull() ?: emptyList()  // Filter out nulls and provide an empty list if null
                _uiState.value = CoffeeShopUIState.Success(coffeeShops)
            } catch (e: Exception) {
                _uiState.value = CoffeeShopUIState.Error("Failed to load coffee shops")
            }
        }
    }
}
