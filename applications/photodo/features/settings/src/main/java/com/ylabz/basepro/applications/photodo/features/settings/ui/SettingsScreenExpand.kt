package com.ylabz.basepro.applications.photodo.features.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // Keep for now if other cards might be expanded via nav args
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.settings.R
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.AboutExpandable
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.NotificationSettingsItem // UPDATED IMPORT
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.QrExpandableEx
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.SectionHeader
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.ThemeSettingsCard

internal object AppPreferenceKeys {
    const val KEY_THEME = "Theme"
    const val KEY_NOTIFICATIONS = "Notifications"

    const val VALUE_THEME_DARK = "Dark"
    const val VALUE_THEME_LIGHT = "Light"
    const val VALUE_THEME_SYSTEM = "System"
    const val VALUE_NOTIFICATIONS_ENABLED = "Enabled"
    const val VALUE_NOTIFICATIONS_DISABLED = "Disabled"
}

private enum class SectionKey { App, Connectivity }
// CardKey.AppPrefs removed as NotificationSettingsItem is not an expandable card
private enum class CardKey { Theme, About, Qr }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenEx(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState.Success,
    onEvent: (SettingsEvent) -> Unit,
    navTo: (String) -> Unit,
    initialCardKeyToExpand: String? = null
) {
    val expandedSections = remember { mutableStateSetOf<SectionKey>() }
    val expandedCards = remember { mutableStateSetOf<CardKey>() }

    // Effect to expand a card specified by the navigation argument (if any)
    // If AppPrefs is no longer a concept to be expanded, this specific check is removed.
    // This LaunchedEffect can be adapted if other cards need deep-linking expansion.
    LaunchedEffect(initialCardKeyToExpand) {
        // Example: if (initialCardKeyToExpand == CardKey.About.name) {
        //     expandedSections.add(SectionKey.App)
        //     expandedCards.add(CardKey.About)
        // }
        // For now, the AppPrefs specific expansion is removed.
    }

    fun <T> toggle(set: MutableSet<T>, key: T) {
        if (set.contains(key)) set.remove(key) else set.add(key)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp),
    ) {
        stickyHeader {
            SectionHeader(
                title = stringResource(R.string.settings_section_title_app),
                expanded = expandedSections.contains(SectionKey.App),
                onToggle = { toggle(expandedSections, SectionKey.App) }
            )
        }
        if (expandedSections.contains(SectionKey.App)) {
            item {
                ThemeSettingsCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = stringResource(R.string.settings_card_title_theme),
                    currentTheme = uiState.selections[AppPreferenceKeys.KEY_THEME]
                        ?: AppPreferenceKeys.VALUE_THEME_SYSTEM,
                    onThemeSelected = { theme ->
                        onEvent(
                            SettingsEvent.UpdateSetting(
                                AppPreferenceKeys.KEY_THEME,
                                theme
                            )
                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            item {
                // Replaced AppPreferencesExpandable with NotificationSettingsItem
                NotificationSettingsItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    uiState = uiState,
                    onEvent = onEvent
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            item {
                AboutExpandable(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    expanded = expandedCards.contains(CardKey.About),
                    onExpandToggle = { toggle(expandedCards, CardKey.About) }
                )
                // No divider after the last item in a section usually
            }
        }

        stickyHeader {
            SectionHeader(
                title = stringResource(R.string.settings_section_title_connectivity),
                expanded = expandedSections.contains(SectionKey.Connectivity),
                onToggle = { toggle(expandedSections, SectionKey.Connectivity) }
            )
        }
        if (expandedSections.contains(SectionKey.Connectivity)) {
            item {
                QrExpandableEx(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    expanded = expandedCards.contains(CardKey.Qr),
                    onExpandToggle = { toggle(expandedCards, CardKey.Qr) }
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun SettingsScreenExPreview() {
    val dummyPhotodoSelections = mapOf(
        AppPreferenceKeys.KEY_THEME to AppPreferenceKeys.VALUE_THEME_SYSTEM,
        AppPreferenceKeys.KEY_NOTIFICATIONS to AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED
    )
    val dummyPhotodoOptions = mapOf(
        AppPreferenceKeys.KEY_THEME to listOf(
            AppPreferenceKeys.VALUE_THEME_LIGHT,
            AppPreferenceKeys.VALUE_THEME_DARK,
            AppPreferenceKeys.VALUE_THEME_SYSTEM
        ),
        AppPreferenceKeys.KEY_NOTIFICATIONS to listOf(
            AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED,
            AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED
        )
    )
    val dummyUiState = SettingsUiState.Success(
        options = dummyPhotodoOptions,
        selections = dummyPhotodoSelections
    )
    // PhotoDoTheme { // Assuming your theme is PhotoDoTheme
    //    SettingsScreenEx(
    //        uiState = dummyUiState, onEvent = { }, navTo = { },
    //        initialCardKeyToExpand = null
    //    )
    // }
}
*/
