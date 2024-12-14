package com.ylabz.basepro.core.data.repository.drive

interface DrivingPtsRepository {
    suspend fun getDrivingPts(org: String, des: String): String
}