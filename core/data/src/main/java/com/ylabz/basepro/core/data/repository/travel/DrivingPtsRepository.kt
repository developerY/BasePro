package com.ylabz.basepro.core.data.repository.travel

interface DrivingPtsRepository {
    suspend fun getDrivingPts(org: String, des: String): String
}