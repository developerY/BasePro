package com.ylabz.basepro.applications.bike.features.trips.ui.components.maps

import android.location.Geocoder
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

/**
 * Returns something like “Central Park, New York, NY” (or null on failure)
 */
suspend fun lookupPlaceName(
    context: Context,
    lat: Double,
    lng: Double
): String? = withContext(Dispatchers.IO) {
    if (!Geocoder.isPresent()) return@withContext null
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = geocoder.getFromLocation(lat, lng, 1)
        if (results.isNullOrEmpty()) return@withContext null
        // you can pick any of getAddressLine(0), locality, subLocality, etc.
        results[0].getAddressLine(0)
    } catch (e: IOException) {
        null
    }
}
