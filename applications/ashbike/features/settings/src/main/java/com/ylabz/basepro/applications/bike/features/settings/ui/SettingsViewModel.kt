package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 3c) The ViewModel
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: AppSettingsRepository
) : ViewModel() {

    // 1) Static option-lists you already had
    private val staticOptions = mapOf(
        "Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
    )

    // 2) Combine the three “selected value” flows into your UiState
    val uiState: StateFlow<SettingsUiState> = combine(
        repo.themeFlow,
        repo.languageFlow,
        repo.notificationsFlow
    ) { theme, lang, notif ->
        SettingsUiState.Success(
            options    = staticOptions,
            selections = mapOf(
                "Theme" to theme,
                "Language" to lang,
                "Notifications" to notif
            )
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState.Loading)

    // 3) Handle updates
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> {
                // no-op: the flows above are “hot” and already emitting
            }
            is SettingsEvent.UpdateSetting -> {
                viewModelScope.launch {
                    when (event.settingKey) {
                        "Theme"         -> repo.setTheme(event.settingValue)
                        "Language"      -> repo.setLanguage(event.settingValue)
                        "Notifications" -> repo.setNotifications(event.settingValue)
                    }
                }
            }
        }
    }
}