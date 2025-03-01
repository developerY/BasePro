package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle//DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BikeNavItem(
    val label: String,
    val icon: ImageVector
) {
    object Home : BikeNavItem("Home", Icons.Filled.Home)
    object Routes : BikeNavItem("Routes", Icons.Filled.AccountCircle)
    object Settings : BikeNavItem("Settings", Icons.Filled.Settings)

    companion object {
        val allItems: List<BikeNavItem> = listOf(Home, Routes, Settings)
    }
}
