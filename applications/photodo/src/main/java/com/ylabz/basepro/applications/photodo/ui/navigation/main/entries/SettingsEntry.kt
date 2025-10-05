package com.ylabz.basepro.applications.photodo.ui.navigation.main.entries

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.applications.photodo.core.ui.FabStateMenu
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsUiRoute
import com.ylabz.basepro.applications.photodo.features.settings.ui.SettingsViewModel


private const val TAG = "DetailEntry"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsEntry(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabState: (FabStateMenu?) -> Unit
) { // NAV_LOG: Log rendering of SettingsKey entry
    Log.d(TAG, "Displaying content for SettingsKey")
    val viewModel: SettingsViewModel = hiltViewModel()
    setTopBar {
        LargeTopAppBar(
            title = { Text("Settings") },
            scrollBehavior = scrollBehavior
        )
    }
    setFabState(FabStateMenu.Hidden)

    SettingsUiRoute(
        modifier = Modifier,
        navTo = {},
        viewModel = viewModel,
        initialCardKeyToExpand = null
    )
}