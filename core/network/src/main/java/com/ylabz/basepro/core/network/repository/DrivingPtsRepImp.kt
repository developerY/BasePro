package com.ylabz.basepro.core.network.repository

import android.util.Log
import com.ylabz.basepro.core.network.api.interfaces.MapsAPI
import javax.inject.Inject

class DrivingPtsRepImp @Inject constructor(
    private val mapCall: MapsAPI
) : DrivingPtsRepository {

    override suspend fun getDrivingPts(org: String, des: String): String {
        val directionsString = mapCall.getMapDirections(org, des)
        // NOTE: This cost money
        Log.d("GraphQL", "getDrivingPts: Call Server --> Cost Money")
        return directionsString
    }
}