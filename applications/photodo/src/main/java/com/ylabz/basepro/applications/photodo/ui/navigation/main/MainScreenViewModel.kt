package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A shared ViewModel that acts as a stateless "event bus" for communication
 * between different navigation entries, especially in multi-pane layouts.
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

    private val _events = MutableSharedFlow<MainScreenEvent>()
    val events = _events.asSharedFlow()

    fun postEvent(event: MainScreenEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}