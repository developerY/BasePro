package com.ylabz.basepro.feature.weather.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.location.LocationRepository
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import com.ylabz.basepro.core.model.weather.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: BaseProRepo,           // Your existing repo
    private val weatherRepo: WeatherRepo,          // Your new repo
    private val locationRepository: LocationRepository // The location repo
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState


    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    private var getWindWeatherJob: Job? = null


    init {

        // Simulate a network call to fetch weather.
        viewModelScope.launch {

            val currentState = _uiState.value
            if (currentState is WeatherUiState.Success) {
                // Copy the existing settings, update the location
                _uiState.value = currentState.copy(weather = Weather(
                    temperature = 22.5,
                    description = "Sunny",
                    iconUrl = "", // Replace with a URL or local resource reference.
                    location = "San Francisco, CA"
                ))
            }
        }

        // Trigger initial load of settings
        onEvent(WeatherEvent.LoadBike)

        // Start collecting location changes
        viewModelScope.launch {
            val weatherOpen = weatherRepo.openCurrentWeather((_uiState.value as WeatherUiState.Success).locationString)
            Log.d("Weather", "WindViewModel    ---- Weather: ${weatherOpen.toString()} ")
            locationRepository.currentLocation.collect { latLng ->
                // Only update if we're in the Success state
                val currentState = _uiState.value
                if (currentState is WeatherUiState.Success) {
                    // Copy the existing settings, update the location
                    _uiState.value = currentState.copy(
                        weatherOpen = weatherOpen,
                        location = latLng
                    )
                }
            }
        }

        // Optionally, trigger an initial location fetch
        viewModelScope.launch {
            locationRepository.updateLocation()
        }
    }

    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.LoadBike -> {
                loadSettings()
            }
            is WeatherEvent.UpdateSetting -> {
                updateSetting(event.settingKey, event.settingValue)
            }
            is WeatherEvent.DeleteAllEntries -> {
                deleteAllEntries()
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading settings data
            // In a real app, you'd retrieve these from your repository
            _uiState.value = WeatherUiState.Success(
                weather = Weather(
                    temperature = 22.5,
                    description = "Sunny",
                    iconUrl = "", // Replace with a URL or local resource reference.
                    location = "San Francisco, CA"
                ),
                settings = mapOf(
                    "Theme" to listOf("Light", "Dark", "System Default"),
                    "Language" to listOf("English", "Spanish", "French"),
                    "Notifications" to listOf("Enabled", "Disabled")
                ),
                location = null,
                weatherOpen = null,
                locationString = "Santa Barbara, US" // We'll update this once location flow emits
            )
        }
    }

    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            val currentSettings = (_uiState.value as? WeatherUiState.Success)?.settings ?: emptyMap()
            val updatedSettings = currentSettings.toMutableMap().apply {
                // e.g., handle your setting changes here
            }
            _uiState.value = WeatherUiState.Success(
                weather = Weather(
                    temperature = 22.5,
                    description = "Sunny",
                    iconUrl = "", // Replace with a URL or local resource reference.
                    location = "San Francisco, CA"
                ),
                settings = updatedSettings,
                location = (_uiState.value as? WeatherUiState.Success)?.location,
                weatherOpen = null,
                locationString = "Santa Barbara, US" // We'll update this once location flow emits
            )
        }
    }

    private fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()
            // Optionally, update UI state or reload settings
        }
    }
}
