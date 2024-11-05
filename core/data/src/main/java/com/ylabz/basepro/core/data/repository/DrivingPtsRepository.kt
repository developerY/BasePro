package com.ylabz.basepro.core.data.repository

interface DrivingPtsRepository {
    suspend fun getDrivingPts(org: String, des: String): String
}