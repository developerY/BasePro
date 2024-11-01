package com.ylabz.basepro.maps.data

import com.ylabz.basepro.maps.BuildConfig.MAPS_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


interface MapsAPI {
    suspend fun getMapDirections(org: String, des: String): String
}

class MapsClient : MapsAPI {

    override suspend fun getMapDirections(org: String, des: String): String = withContext(
        Dispatchers.IO) {
        val base = "https://maps.googleapis.com/maps/api/directions/json"
        val orgParam = "origin=$org"
        val desParam = "destination=$des"
        val call = "$base?$orgParam&$desParam&key=$MAPS_API_KEY"
        return@withContext URL(call).readText()
    }
}


