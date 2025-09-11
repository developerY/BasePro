package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.bike.features.core.ui.components.MapPathScreen
import com.ylabz.basepro.applications.bike.features.trips.R
import com.ylabz.basepro.core.model.location.GpsFix
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import com.ylabz.basepro.core.model.yelp.Coordinates

// Definition for GpsFix, as it was commented in the original file and used in the preview
// data class GpsFix(val lat: Double, val lng: Double, val elevation: Float, val timeMs: Long, val speedMps: Float)

@Preview(showBackground = true)
@Composable
private fun MapPathScreenPreview() {
    val fixes = listOf(
        GpsFix(34.0522, -118.2437, 0, 0.0, 0f),
        GpsFix(34.0532, -118.2447, 0, 15000.0, 8f),
        GpsFix(34.0542, -118.2467, 0, 30000.0, 12f),
        GpsFix(34.0552, -118.2487, 0, 45000.0, 15f),
        GpsFix(34.0562, -118.2507, 0, 60000.0, 5f)
    )
    val coffeeShops = listOf(
        BusinessInfo("1", "Cool Beans", "", 4.5, emptyList(), "$$", Coordinates(34.0545, -118.2495), emptyList()),
        BusinessInfo("2", "Grind House", "", 4.8, emptyList(), "$$$", Coordinates(34.0525, -118.2465), emptyList())
    )
    MapPathScreen(
        fixes = fixes,
        coffeeShops = coffeeShops,
        onFindCafes = { },
        placeName = stringResource(R.string.feature_trips_preview_ride_around_plaza)
    )
}
