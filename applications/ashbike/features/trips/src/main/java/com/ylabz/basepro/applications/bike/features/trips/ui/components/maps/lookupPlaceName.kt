package com.ylabz.basepro.applications.bike.features.trips.ui.components.maps

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Reverse-geocodes a (lat, lng) into something like
 * “SoHo, New York, NY” (no ZIP, no country).
 * Requires API 33+.
 */
suspend fun lookupPlaceName(
    context: Context,
    lat: Double,
    lng: Double
): String? = withContext(Dispatchers.IO) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@withContext null
    if (!Geocoder.isPresent()) return@withContext null

    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: List<Address> = try {
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(
                lat, lng, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(results: MutableList<Address>) {
                        cont.resume(results)
                    }
                    override fun onError(errorMessage: String?) {
                        cont.resume(emptyList())
                    }
                }
            )
        }
    } catch (e: IOException) {
        return@withContext null
    }

    val addr = addresses.firstOrNull() ?: return@withContext null

    val raw = addr.getAddressLine(0)
    raw
        ?.split(",")         // → ["Chelsea", " New York", " NY 10011", " USA"]
        ?.take(3)            // → ["Chelsea", " New York"]
        ?.map { it.trim() }  // → ["Chelsea", "New York"]
        ?.joinToString(", ") // → "Chelsea, New York"
        ?.takeIf { it.isNotBlank() }

    /* Only take subLocality, locality and adminArea
    listOfNotNull(
        //addr.featureName?.takeIf     { it.isNotBlank() },
        addr.getAddressLine(0)?.split(",")[0]?.takeIf { it.isNotBlank() },
        //addr.locality?.takeIf     { it.isNotBlank() },
        //addr.adminArea?.takeIf    { it.isNotBlank() }
    )
        .takeIf { it.isNotEmpty() }
        ?.joinToString(", ")*/
}
