package com.ylabz.basepro.core.network.repository

interface DrivingPtsRepository {
    suspend fun getDrivingPts(org: String, des: String): String
}