package com.ylabz.twincam.cam.ui

import com.ylabz.twincam.data.mapper.TwinCam

data class CamUIState(
    val isLoading: Boolean = false,
    val data: List<TwinCam> = emptyList(),  // Updated to use ExampleEntity
    val error: String? = null
)
