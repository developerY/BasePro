package com.ylabz.basepro.applications.bike.features.settings.ui

import android.util.Log // Added for robust logging
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// 3c) The ViewModel
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepo: AppSettingsRepository,
    private val profileRepo: UserProfileRepository
) : ViewModel() {

    // Expose a dedicated, hot StateFlow for the theme, compliant with MAD principles
    val theme: StateFlow<String> = appRepo.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "System" // Default value
        )

    // 1) Static option lists
    private val staticOptions = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled"),
        "Units" to listOf("Imperial (English)", "Metric (SI)") // Added Units
    )

    // 2) Build a flow of Map<settingKey,selectedValue>
    private val settingsSelections = combine(
        appRepo.themeFlow,
        appRepo.languageFlow,
        appRepo.notificationsFlow,
        appRepo.unitsFlow // Added Units Flow
    ) { theme: String, lang: String, notif: String, units: String ->
        mapOf(
            "Theme" to theme,
            "Language" to lang,
            "Notifications" to notif,
            "Units" to units // Added Units
        )
    }

    // 3) Build a flow of ProfileData
    private val profileData = combine(
        profileRepo.nameFlow, // Flow<String>
        profileRepo.heightFlow, // Flow<String> from UserProfileRepository
        profileRepo.weightFlow  // Flow<String> from UserProfileRepository
    ) { name: String, heightStr: String, weightStr: String ->
        // Pass String values directly as expected by ProfileData constructor
        ProfileData(name = name, heightCm = heightStr, weightKg = weightStr)
    }

    // 4) Combine everything into one UiState
    val uiState: StateFlow<SettingsUiState> =
        combine(
            settingsSelections,
            profileData,
            profileRepo.profileReviewedOrSavedFlow // Collect the new flow
        ) { selections: Map<String, String>, profile: ProfileData, profileHasBeenReviewedOrSaved: Boolean -> // New parameter from the flow

            Log.d("ViewModelCombine", "Profile in combine: Name='${profile.name}', H='${profile.heightCm}', W='${profile.weightKg}'")
            Log.d("ViewModelCombine", "Profile reviewed or saved by user: $profileHasBeenReviewedOrSaved")

            // Badge is ON until the first save, then it's OFF permanently.
            val actuallyIncomplete = if (!profileHasBeenReviewedOrSaved) {
                true // Profile hasn't been explicitly saved/reviewed by user yet
            } else {
                false // User has saved at least once, badge turns off and stays off
            }
            Log.d("ViewModelCombine", "Calculated actuallyIncomplete (isProfileIncomplete): $actuallyIncomplete")

            SettingsUiState.Success(
                options = staticOptions,
                selections = selections,
                profile = profile,
                isProfileIncomplete = actuallyIncomplete
            )
        }
            .map { successState -> successState as SettingsUiState } // Upcast to allow Error emission in catch
            .catch { e ->
                Log.e("ViewModelCombine", "Error in uiState combine: ${e.message}", e)
                emit(SettingsUiState.Error("Failed to combine UI state: ${e.message}"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SettingsUiState.Loading
            )

    // --- New additions for Location Energy Level ---
    val currentLocationEnergyLevel: StateFlow<LocationEnergyLevel> =
        profileRepo.locationEnergyLevelFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LocationEnergyLevel.BALANCED
            )

    fun setLocationEnergyLevel(level: LocationEnergyLevel) {
        viewModelScope.launch {
            profileRepo.setLocationEnergyLevel(level)
        }
    }
    // --- End of new additions ---

    // 5) Handle both settings and profile updates
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                // no-op: Flows are already hot and uiState is actively combining them.
                // If there's a need to force a refresh from repository (e.g., if repo doesn't use hot flows),
                // you would trigger that here. But with DataStore flows, it's usually not needed.
            }
            is SettingsEvent.UpdateSetting -> {
                viewModelScope.launch {
                    when (event.key) {
                        "Theme"         -> appRepo.setTheme(event.value)
                        "Language"      -> appRepo.setLanguage(event.value)
                        "Notifications" -> appRepo.setNotifications(event.value)
                        "Units"         -> appRepo.setUnits(event.value) // Added Units
                        // Add other settings handling here if necessary
                        else -> Log.w("SettingsViewModel", "Unhandled setting key: ${event.key}")
                    }
                }
            }
            is SettingsEvent.SaveProfile -> {
                viewModelScope.launch {
                    Log.d("SettingsViewModel", "Saving profile: Name=${event.profile.name}, H=${event.profile.heightCm}, W=${event.profile.weightKg}")
                    try {
                        profileRepo.saveProfile(event.profile)
                        Log.d("SettingsViewModel", "Profile save initiated for ${event.profile.name}")
                        // The uiState flow will automatically update due to DataStore's reactive nature
                        // if profileRepo.saveProfile successfully updates the DataStore values that
                        // nameFlow, heightFlow, and weightFlow are observing.
                    } catch (e: Exception) {
                        Log.e("SettingsViewModel", "Failed to save profile for ${event.profile.name}", e)
                        // Optionally update UI to show error, though the state itself might not change
                        // if the save fails silently in the repo or if the flows don't reflect the error.
                        // Consider emitting a temporary error event or using a separate error StateFlow.
                    }
                }
            }
        }
    }
}