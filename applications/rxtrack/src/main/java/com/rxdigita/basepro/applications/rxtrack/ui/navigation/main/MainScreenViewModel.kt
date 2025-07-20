package com.rxdigita.basepro.applications.rxtrack.ui.navigation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Import the use case from its new package
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    // Inject the use case
    // getUnsyncedRidesCountUseCase: GetUnsyncedRidesCountUseCase
) : ViewModel() {

    // Use the injected use case

}
