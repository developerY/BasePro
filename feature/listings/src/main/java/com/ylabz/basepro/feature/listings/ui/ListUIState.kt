package com.ylabz.basepro.listings.ui

import com.ylabz.basepro.core.database.mapper.BasePro

sealed interface ListUIState {
    object Loading : ListUIState
    data class Error(val message: String) : ListUIState
    data class Success(
        val data: List<BasePro> = emptyList(),
    ) : ListUIState
}

