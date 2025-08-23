package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// The ViewModels for your screens
class ScreenAViewModel : ViewModel() {
    var count by mutableIntStateOf(0)
}