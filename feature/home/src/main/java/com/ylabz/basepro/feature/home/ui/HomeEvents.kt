package com.ylabz.basepro.feature.home.ui

import com.ylabz.basepro.feature.home.data.AndFrameworks

sealed interface HomeEvent {
    object LoadFrameworks : HomeEvent
    object Retry : HomeEvent
    data class FrameworkClicked(val framework: AndFrameworks) : HomeEvent
}
