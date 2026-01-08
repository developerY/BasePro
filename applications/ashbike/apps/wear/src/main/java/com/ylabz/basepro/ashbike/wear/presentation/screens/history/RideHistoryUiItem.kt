package com.ylabz.basepro.ashbike.wear.presentation.screens.history

// Simple UI Model so the View doesn't need to know about Database Entities
data class RideHistoryUiItem(
    val id: String,
    val dateStr: String,
    val distanceStr: String,
    val durationStr: String,
    val caloriesStr: String
)