package com.ylabz.basepro.applications.bike.features.core.ui.components

import android.os.Build

object DeviceCapabilities {
    // Centralized logic for whether this device supports Glass/XR features
    val supportsGlassFeatures: Boolean
        get() = Build.VERSION.SDK_INT >= 36
}