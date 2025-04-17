package com.ylabz.basepro.feature.weather.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigator
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.travel.LocationRepository
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.database.BaseProRepo
import com.ylabz.basepro.core.model.weather.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @Named("real") private val weatherRepo: WeatherRepo,
    private val repository: BaseProRepo,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        // Initialize the UI state by loading settings
        loadSettings()
    }

    /**
     * Called when the user presses a button to fetch the weather from OpenWeather API.
     */
    fun onFetchWeatherClicked() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                val city = currentState.locationString  // e.g., "Santa Barbara, US"
                try {
                    val response = weatherRepo.openCurrentWeatherByCity(city)
                    Log.d("Weather", "API call success: $response")
                    _uiState.value = currentState.copy(weatherOpen = response)
                } catch (e: Exception) {
                    Log.e("Weather", "API call failed", e)
                    // Optionally update UI state with an error.
                }
            } else {
                Log.w("Weather", "UI state not ready for API call.")
            }
        }
    }


    /**
     * Handle various events (Load, Update, Delete).
     */
    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.LoadBike -> loadSettings()
            is WeatherEvent.UpdateSetting -> updateSetting(event.settingKey, event.settingValue)
            is WeatherEvent.DeleteAllEntries -> deleteAllEntries()
            is WeatherEvent.FetchWeather -> onFetchWeatherClicked()  // Handle the fetch weather event
        }
    }


    /**
     * Loads or simulates loading initial settings/data.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading or retrieve actual settings from your repository
            _uiState.value = WeatherUiState.Success(
                weather = Weather(
                    temperature = 22.5,
                    description = "Sunny",
                    iconUrl = "",
                    location = "San Francisco, CA"
                ),
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null,
                weatherOpen = null,
                locationString = "Santa Barbara, US" // Default location for API calls
            )
        }
    }

    /**
     * Updates a specific setting in the UI state.
     */
    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                val updatedSettings = currentState.settings.toMutableMap().apply {
                    // Modify your settings as needed
                    this[key] = listOf(value)
                }
                _uiState.value = currentState.copy(settings = updatedSettings)
            }
        }
    }

    /**
     * Deletes all entries in your repository (example usage).
     */
    private fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()
            Log.d("Weather", "All entries deleted from repository.")
        }
    }
}
