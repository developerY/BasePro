package com.ylabz.basepro.core.model.health

data class HealthScreenState(
    val isHealthConnectAvailable: Boolean,
    val permissionsGranted: Boolean,
    val permissions: Set<String>,
    val backgroundReadPermissions: Set<String>,
    val backgroundReadAvailable: Boolean,
    val backgroundReadGranted: Boolean,
)
