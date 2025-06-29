package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
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
        "Notifications" to listOf("Enabled", "Disabled")
    )

    // 2) Build a flow of Map<settingKey,selectedValue>
    private val settingsSelections = combine(
        appRepo.themeFlow,
        appRepo.languageFlow,
        appRepo.notificationsFlow
    ) { theme, lang, notif ->
        mapOf(
            "Theme" to theme,
            "Language" to lang,
            "Notifications" to notif
        )
    }

    // 3) Build a flow of ProfileData
    private val profileData = combine(
        profileRepo.nameFlow,
        profileRepo.heightFlow,
        profileRepo.weightFlow
    ) { name, h, w ->
        ProfileData(name = name, heightCm = h, weightKg = w)
    }

    // 4) Combine everything into one UiState
    val uiState: StateFlow<SettingsUiState> =
        combine(settingsSelections, profileData) { selections, profile ->
            SettingsUiState.Success(
                options    = staticOptions,
                selections = selections,
                profile    = profile
            ) as SettingsUiState
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.Loading)

    // 5) Handle both settings and profile updates
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                // no-op: Flows are already hot
            }
            is SettingsEvent.UpdateSetting -> {
                viewModelScope.launch {
                    when (event.key) {
                        "Theme"         -> appRepo.setTheme(event.value)
                        "Language"      -> appRepo.setLanguage(event.value)
                        "Notifications" -> appRepo.setNotifications(event.value)
                    }
                }
            }
            is SettingsEvent.SaveProfile -> {
                viewModelScope.launch {
                    profileRepo.saveProfile(event.profile)
                }
            }
        }
    }
}