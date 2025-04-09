package com.ylabz.basepro.applications.bike.database


import com.ylabz.basepro.applications.bike.database.mapper.BikePro
import kotlinx.coroutines.flow.Flow

interface BikeProRepo {
    // Not sure where to put these ...
    fun allGetBikePros(): Flow<List<BikePro>> // NOTE: wrap in Flow<Resource<<>>>

    suspend fun insert(bikepro: BikePro)

    //suspend fun addTodoPhoto(BasePro: BasePro)
    suspend fun delete(bikepro: BikePro)
    suspend fun deleteById(bikeproId: Int)
    suspend fun getBikeProById(bikeproId: Int): BikePro? // NOTE: wrap in Flow<Resource<<>>>
    suspend fun deleteAll()
    //abstract fun insert(BasePro: BasePro)
}

