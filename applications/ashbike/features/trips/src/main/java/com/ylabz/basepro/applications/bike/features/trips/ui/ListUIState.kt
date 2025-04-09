package com.ylabz.basepro.listings.ui

import com.ylabz.basepro.applications.bike.database.mapper.BikePro

sealed interface ListUIState {
    object Loading : ListUIState
    data class Error(val message: String) : ListUIState
    data class Success(
        val data: List<BikePro> = emptyList(),
    ) : ListUIState
}

