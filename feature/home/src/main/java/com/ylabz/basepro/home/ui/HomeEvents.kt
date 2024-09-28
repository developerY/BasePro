package com.ylabz.basepro.home.ui

import com.ylabz.basepro.home.data.AndFrameworks

sealed interface HomeEvent {
    object LoadFrameworks : HomeEvent
    object Retry : HomeEvent
    data class FrameworkClicked(val framework: AndFrameworks) : HomeEvent
}
