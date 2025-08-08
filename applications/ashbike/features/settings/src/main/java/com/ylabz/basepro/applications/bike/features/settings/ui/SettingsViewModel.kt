package com.ylabz.basepro.applications.bike.features.settings.ui

import android.util.Log
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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepo: AppSettingsRepository,
    private val profileRepo: UserProfileRepository
) : ViewModel() {

    // MINIMAL CHANGE 1 of 2: Add a temporary holder for the UI's selection.
    private val _locallySelectedEnergyLevel = MutableStateFlow<LocationEnergyLevel?>(null)

    val theme: StateFlow<String> = appRepo.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "System"
        )

    private val staticOptions = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled"),
        "Units" to listOf("Imperial (English)", "Metric (SI)")
    )

    private val settingsSelections = combine(
        appRepo.themeFlow,
        appRepo.languageFlow,
        appRepo.notificationsFlow,
        appRepo.unitsFlow
    ) { theme, lang, notif, units ->
        mapOf(
            "Theme" to theme,
            "Language" to lang,
            "Notifications" to notif,
            "Units" to units
        )
    }

    private val profileData = combine(
        profileRepo.nameFlow,
        profileRepo.heightFlow,
        profileRepo.weightFlow
    ) { name, heightStr, weightStr ->
        ProfileData(name = name, heightCm = heightStr, weightKg = weightStr)
    }

    // Expose the showGpsCountdownFlow
    val showGpsCountdown: StateFlow<Boolean> = profileRepo.showGpsCountdownFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val uiState: StateFlow<SettingsUiState> =
        combine(
            _locallySelectedEnergyLevel, // Add the temporary holder to the combine function
            settingsSelections,
            profileData,
            profileRepo.profileReviewedOrSavedFlow,
            profileRepo.locationEnergyLevelFlow
        ) { localOverride, selections, profile, profileHasBeenReviewedOrSaved, savedEnergyLevel ->

            // This is the key change: Use the local selection if it exists, otherwise use the saved one.
            // This prevents the UI from "snapping back" while the save is in progress.
            val energyLevel = localOverride ?: savedEnergyLevel

            Log.d("ViewModelCombine", "Profile in combine: Name='${profile.name}', H='${profile.heightCm}', W='${profile.weightKg}'")
            Log.d("ViewModelCombine", "Profile reviewed or saved by user: $profileHasBeenReviewedOrSaved")
            Log.d("ViewModelCombine", "Energy Level in combine: $energyLevel")

            val actuallyIncomplete = !profileHasBeenReviewedOrSaved
            Log.d("ViewModelCombine", "Calculated actuallyIncomplete (isProfileIncomplete): $actuallyIncomplete")

            SettingsUiState.Success(
                options = staticOptions,
                selections = selections,
                profile = profile,
                isProfileIncomplete = actuallyIncomplete,
                currentEnergyLevel = energyLevel
            )
        }
            .map { successState -> successState as SettingsUiState }
            .catch { e ->
                Log.e("ViewModelCombine", "Error in uiState combine: ${e.message}", e)
                emit(SettingsUiState.Error("Failed to combine UI state: ${e.message}"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SettingsUiState.Loading
            )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                // This is a no-op as the flows are hot and actively combined.
            }
            is SettingsEvent.UpdateSetting -> {
                viewModelScope.launch {
                    when (event.key) {
                        "Theme"         -> appRepo.setTheme(event.value)
                        "Language"      -> appRepo.setLanguage(event.value)
                        "Notifications" -> appRepo.setNotifications(event.value)
                        "Units"         -> appRepo.setUnits(event.value)
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
                    } catch (e: Exception) {
                        Log.e("SettingsViewModel", "Failed to save profile for ${event.profile.name}", e)
                    }
                }
            }
            is SettingsEvent.UpdateEnergyLevel -> {
                // MINIMAL CHANGE 2 of 2: Update the temporary holder instantly for the UI.
                _locallySelectedEnergyLevel.value = event.level
                viewModelScope.launch {
                    Log.d("SettingsViewModel", "Updating Energy Level to: ${event.level}")
                    try {
                        profileRepo.setLocationEnergyLevel(event.level)
                        Log.d("SettingsViewModel", "Successfully called repo to set energy level.")
                    } catch (e: Exception) {
                        Log.e("SettingsViewModel", "Failed to set energy level.", e)
                    }
                }
            }
            // Handle the new event
            is SettingsEvent.OnShowGpsCountdownChanged -> {
                viewModelScope.launch {
                    profileRepo.setShowGpsCountdown(event.show)
                }
            }
        }
    }
}
