package com.ylabz.basepro.camera.ui

import com.ylabz.basepro.data.mapper.BasePro


sealed interface CamUIState {
    object Loading : CamUIState
    data class Error(val message: String) : CamUIState
    data class Success(
        val data: List<BasePro> = emptyList(),
    ) : CamUIState
}