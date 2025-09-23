package com.ylabz.basepro.applications.photodo.features.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepo: AppSettingsRepository,
) : ViewModel() {

    // These are all the possible options for each setting category.
    // Photodo might not need "Units" or "GPS Accuracy",
    // if so, remove them here and from settingsSelections and onEvent.
    private val staticOptions: Map<String, List<String>> = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"), // Example languages
        "Notifications" to listOf("Enabled", "Disabled")
        // "Units" to listOf("Imperial (English)", "Metric (SI)"), // Uncomment if needed
        // "GPS Accuracy" to listOf("Low Power", "Balanced", "High Accuracy") // Uncomment if needed
    )

    // This flow combines the latest selected values for each setting.
    private val settingsSelections: StateFlow<Map<String, String>> = combine(
        appRepo.themeFlow,
        appRepo.languageFlow,
        appRepo.notificationsFlow,
        // appRepo.unitsFlow, // Uncomment if "Units" setting is needed
        // appRepo.gpsAccuracyFlow // Uncomment if "GPS Accuracy" setting is needed
    ) { theme, language, notifications /*, units, gpsAccuracy */ ->
        // Ensure this map only contains keys that are also in staticOptions
        mapOf(
            "Theme" to theme,
            "Language" to language,
            "Notifications" to notifications,
            // "Units" to units, // Uncomment if needed
            // "GPS Accuracy" to gpsAccuracy // Uncomment if needed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = mapOf( // Initial default selections
            "Theme" to "System Default",
            "Language" to "English",
            "Notifications" to "Enabled"
            // "Units" to "Metric (SI)", // Uncomment if needed
            // "GPS Accuracy" to "Balanced" // Uncomment if needed
        )
    )

    // The main UI state flow, combining static options and current selections.
    val uiState: StateFlow<SettingsUiState> =
        settingsSelections.combine(kotlinx.coroutines.flow.flowOf(staticOptions)) { selections, options ->
            Log.d("SettingsViewModel", "Combining UI State: Selections = $selections")
            SettingsUiState.Success(
                options = options,
                selections = selections
            ) as SettingsUiState // Cast to base type
        }
        .catch { e ->
            Log.e("SettingsViewModel", "Error in uiState combine: ${e.message}", e)
            emit(SettingsUiState.Error("Failed to load settings: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // Start eagerly to have state immediately
            initialValue = SettingsUiState.Loading
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                // This is generally a no-op if flows are hot and stateIn is Eagerly or WhileSubscribed.
                // UI should automatically reflect the latest from uiState.
            }
            is SettingsEvent.UpdateSetting -> {
                viewModelScope.launch {
                    Log.d("SettingsViewModel", "Updating setting: Key=${event.key}, Value=${event.value}")
                    when (event.key) {
                        "Theme" -> appRepo.setTheme(event.value)
                        "Language" -> appRepo.setLanguage(event.value)
                        "Notifications" -> appRepo.setNotifications(event.value)
                        // "Units" -> appRepo.setUnits(event.value) // Uncomment if needed
                        // "GPS Accuracy" -> appRepo.setGpsAccuracy(event.value) // Uncomment if needed
                        else -> Log.w("SettingsViewModel", "Unhandled setting key: ${event.key}")
                    }
                }
            }
        }
    }
}
