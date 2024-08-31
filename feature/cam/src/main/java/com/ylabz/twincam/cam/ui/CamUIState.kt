package com.ylabz.twincam.cam.ui

import com.ylabz.twincam.data.mapper.TwinCam

sealed interface CamUIState {
    object Loading : CamUIState
    data class Error(val message: String) : CamUIState
    data class Success(
        val data: List<TwinCam> = emptyList(),
    ) : CamUIState
}

