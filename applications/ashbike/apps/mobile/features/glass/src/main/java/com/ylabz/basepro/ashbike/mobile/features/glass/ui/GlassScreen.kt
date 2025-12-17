package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.runtime.Composable

@Composable
fun GlassScreenNOTUSED(
    uiState: GlassUiState,
    onEvent: (GlassUiEvent) -> Unit,
    onClose: () -> Unit
) {
    when (uiState.currentScreen) {
        ScreenState.HOME -> {
            HomeScreenTest(
                currentGear = uiState.currentGear,
                onGearChange = { newGear ->
                    //onEvent(GlassUiEvent.OnGearChange(newGear))
                },
                onOpenGearList = {
                    //onEvent(GlassUiEvent.OnOpenGearList)
                },
                onClose = onClose,
                // Note: Try to avoid passing repository here if possible;
                // pass specific data from state instead.
                // But if HomeScreen needs raw repo access, you can keep it.
                // repository = null
            )
        }
        ScreenState.GEAR_LIST -> {
            GearSelectionScreen(
                currentGear = uiState.currentGear,
                onGearSelected = { selectedGear ->
                    //onEvent(GlassUiEvent.OnGearSelect(selectedGear))
                }
            )
        }
    }
}