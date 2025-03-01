package com.ylabz.basepro.core.model.weather

// Define a simple weather model.
data class Weather(
    val temperature: Double,
    val description: String,
    val iconUrl: String, // You might use this with an image loader (e.g., Coil)
    val location: String
)