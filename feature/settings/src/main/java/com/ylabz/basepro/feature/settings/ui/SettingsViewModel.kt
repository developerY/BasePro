package com.ylabz.basepro.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: BaseProRepo  // Inject the repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        onEvent(SettingsEvent.LoadSettings)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                loadSettings()
            }
            is SettingsEvent.UpdateSetting -> {
                updateSetting(event.settingKey, event.settingValue)
            }
            is SettingsEvent.DeleteAllEntries -> {
                deleteAllEntries()
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simulate loading settings data
            _uiState.value = SettingsUiState.Success( settings = mapOf(
                "Theme" to listOf("Light", "Dark", "System Default"),
                "Language" to listOf("English", "Spanish", "French"),
                "Notifications" to listOf("Enabled", "Disabled")
                )
            )
        }
    }

    private fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            // Handle setting updates
            val currentSettings = (_uiState.value as? SettingsUiState.Success)?.settings ?: emptyMap()
            val updatedSettings = currentSettings.toMutableMap().apply {
                // this[key] = value
            }
            _uiState.value = SettingsUiState.Success(settings = updatedSettings)
        }
    }

    private fun deleteAllEntries() {
        viewModelScope.launch {
            repository.deleteAll()  // Assuming this method exists in your repository
            // Optionally, update UI state or reload settings
            // loadSettings()  // Reload settings or update UI after deletion
        }
    }
}
