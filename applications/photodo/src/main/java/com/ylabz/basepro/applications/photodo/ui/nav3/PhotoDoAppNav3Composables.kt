package com.ylabz.basepro.applications.photodo.ui.nav3

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// import androidx.hilt.navigation.compose.hiltViewModel // Keep for other routes if needed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeUiRoute
// import com.ylabz.basepro.applications.photodo.features.home.ui.PhotoDoHomeViewModel // Assuming ViewModel name
// No longer directly importing PhotoDoListUiRoute here, it's used inside PhotoDoListFeatureWithListDetailStrategy
import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsUiRoute
// import com.ylabz.basepro.applications.photodo.features.settings.ui.PhotoDoSettingsViewModel // Assuming ViewModel name

@Composable
fun PhotoDoAppNav3(modifier: Modifier = Modifier) {
    val sectionBackStack = rememberNavBackStack<NavKey>(PhotoDoHomeSectionKey)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppBottomNavigationBarNav3(
                currentSectionKey = sectionBackStack.lastOrNull(),
                onSectionSelected = { selectedKey ->
                    sectionBackStack.clear() // Simple tab switching
                    sectionBackStack.add(selectedKey)
                }
            )
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = sectionBackStack,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            onBack = { keysToRemove ->
                repeat(keysToRemove) {
                    if (sectionBackStack.size > 1) {
                        sectionBackStack.removeLastOrNull()
                    }
                }
            },
            entryProvider = entryProvider {
                entry<PhotoDoHomeSectionKey> {
                    // val homeViewModel = hiltViewModel<PhotoDoHomeViewModel>() // Assuming PhotoDoHomeViewModel
                    PhotoDoHomeUiRoute(
                        // viewModel = homeViewModel, // Pass ViewModel if needed
                        onNavigateToSettings = {
                            sectionBackStack.clear()
                            sectionBackStack.add(PhotoDoSettingsSectionKey)
                        }
                    )
                }
                entry<PhotoDoListSectionKey> {
                    PhotoDoListFeatureWithListDetailStrategy() // Use the new list-detail strategy composable
                }
                entry<PhotoDoSettingsSectionKey> {
                    // val settingsViewModel = hiltViewModel<PhotoDoSettingsViewModel>() // Assuming PhotoDoSettingsViewModel
                    PhotoDoSettingsUiRoute(
                        // viewModel = settingsViewModel // Pass ViewModel if needed
                    )
                }
            }
        )
    }
}

private data class TabItem(val title: String, val icon: ImageVector, val key: NavKey)

@Composable
private fun AppBottomNavigationBarNav3(
    currentSectionKey: NavKey?,
    onSectionSelected: (NavKey) -> Unit
) {
    val navItems = listOf(
        TabItem("Home", Icons.Filled.Home, PhotoDoHomeSectionKey),
        TabItem("List", Icons.Filled.List, PhotoDoListSectionKey),
        TabItem("Settings", Icons.Filled.Settings, PhotoDoSettingsSectionKey)
    )

    NavigationBar {
        navItems.forEach { tab ->
            NavigationBarItem(
                selected = currentSectionKey == tab.key,
                onClick = { onSectionSelected(tab.key) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) }
            )
        }
    }
}
