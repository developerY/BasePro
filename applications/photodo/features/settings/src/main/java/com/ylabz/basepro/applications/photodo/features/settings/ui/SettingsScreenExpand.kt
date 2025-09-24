package com.ylabz.basepro.applications.photodo.features.settings.ui

// import androidx.compose.foundation.clickable // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.Box // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.fillMaxWidth // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.size // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.foundation.layout.width // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material.icons.Icons // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material.icons.filled.Person // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.Card // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.CardDefaults // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.material3.Icon // Not directly used after removing ProfileBikeInfoCardEx
// import androidx.compose.ui.text.font.FontWeight // Not directly used after removing ProfileBikeInfoCardEx
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider // Added for visual separation
import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Text // Text is still used in Preview, but not directly in SettingsScreenEx if all components are external
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.photodo.features.settings.R
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.AboutExpandable
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.AppPreferencesExpandable
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.QrExpandableEx
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.SectionHeader
import com.ylabz.basepro.applications.photodo.features.settings.ui.components.ThemeSettingsCard

internal object AppPreferenceKeys {
    const val KEY_THEME = "Theme"
    const val KEY_NOTIFICATIONS = "Notifications"
    // const val KEY_UNITS = "Units" // Removed
    // const val KEY_LONG_RIDE_ENABLED = "LongRideEnabled" // Removed

    const val VALUE_THEME_DARK = "Dark"
    const val VALUE_THEME_LIGHT = "Light"
    const val VALUE_THEME_SYSTEM = "System"
    const val VALUE_NOTIFICATIONS_ENABLED = "Enabled"
    const val VALUE_NOTIFICATIONS_DISABLED = "Disabled"
    // const val VALUE_UNITS_METRIC = "Metric (SI)" // Removed
    // const val VALUE_UNITS_IMPERIAL = "Imperial (English)" // Removed
}

private enum class SectionKey { App, Connectivity } // Removed Bike
private enum class CardKey { Theme, About, Qr, AppPrefs } // Removed Health, Nfc, Ble, BikeConfig (Theme kept for now in case other logic depends on it, but not for ThemeSettingsCard expansion)

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
    // var isEditing by remember { mutableStateOf(false) } // This state is not used, consider removing if not needed for future edits (already commented in previous step)

    // Effect to expand the card specified by the navigation argument
    LaunchedEffect(initialCardKeyToExpand) {
        if (initialCardKeyToExpand == CardKey.AppPrefs.name) {
            expandedSections.add(SectionKey.App) // Expand the parent section
            expandedCards.add(CardKey.AppPrefs)  // Expand the AppPrefs card
        }
    }

    fun <T> toggle(set: MutableSet<T>, key: T) {
        if (set.contains(key)) set.remove(key) else set.add(key)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp), // Horizontal padding removed from LazyColumn, will be on items/sections
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
                    modifier = Modifier.padding(horizontal = 16.dp), // Add padding to individual card
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
                AppPreferencesExpandable(
                    modifier = Modifier.padding(horizontal = 16.dp), // Add padding
                    expanded = expandedCards.contains(CardKey.AppPrefs),
                    onExpandToggle = { toggle(expandedCards, CardKey.AppPrefs) },
                    uiState = uiState,
                    onEvent = onEvent
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) // Divider
            }
            item {
                AboutExpandable(
                    modifier = Modifier.padding(horizontal = 16.dp), // Add padding
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
                    modifier = Modifier.padding(horizontal = 16.dp), // Add padding
                    expanded = expandedCards.contains(CardKey.Qr),
                    onExpandToggle = { toggle(expandedCards, CardKey.Qr) }
                )
                // No divider after the last item in a section usually
            }
        }
    }
}

// ProfileBikeInfoCardEx has been removed.

/*
@Preview(showBackground = true)
@Composable
fun SettingsScreenExPreview() {
    // This preview will need to be updated to reflect the new SettingsUiState
    // and remove Ashbike-specific data like ProfileData, NfcUiState, BleUiState etc.
    // Also, the options and selections in the dummyUiState should match the cleaned up AppPreferenceKeys.
    val dummyPhotodoSelections = mapOf(
        AppPreferenceKeys.KEY_THEME to AppPreferenceKeys.VALUE_THEME_SYSTEM,
        // AppPreferenceKeys.KEY_LANGUAGE to "English", // If you add Language setting
        AppPreferenceKeys.KEY_NOTIFICATIONS to AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED
    )
    val dummyPhotodoOptions = mapOf(
        AppPreferenceKeys.KEY_THEME to listOf(
            AppPreferenceKeys.VALUE_THEME_LIGHT,
            AppPreferenceKeys.VALUE_THEME_DARK,
            AppPreferenceKeys.VALUE_THEME_SYSTEM
        ),
        // AppPreferenceKeys.KEY_LANGUAGE to listOf("English", "Spanish", "French"),
        AppPreferenceKeys.KEY_NOTIFICATIONS to listOf(
            AppPreferenceKeys.VALUE_NOTIFICATIONS_ENABLED,
            AppPreferenceKeys.VALUE_NOTIFICATIONS_DISABLED
        )
    )
    val dummyUiState = SettingsUiState.Success(
        options = dummyPhotodoOptions,
        selections = dummyPhotodoSelections
    )
    // Replace AshBikeTheme with your PhotoDoTheme if it's different
    // PhotoDoTheme { // Assuming your theme is PhotoDoTheme
    //    SettingsScreenEx(
    //        uiState = dummyUiState, onEvent = { }, navTo = { },
    //        initialCardKeyToExpand = null
    //    )
    // }
}
*/
