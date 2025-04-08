package com.ylabz.bikepro.applications.bike.database


import kotlinx.coroutines.flow.Flow

interface BikeProRepo {
    // Not sure where to put these ...
    fun allGetBasePros(): Flow<List<BikeProRepo>> // NOTE: wrap in Flow<Resource<<>>>

    suspend fun insert(bikepro: BikeProRepo)

    //suspend fun addTodoPhoto(BasePro: BasePro)
    suspend fun delete(bikepro: BikeProRepo)
    suspend fun deleteById(bikeproId: Int)
    suspend fun getBaseProById(bikeproId: Int): BikeProRepo? // NOTE: wrap in Flow<Resource<<>>>
    suspend fun deleteAll()
    //abstract fun insert(BasePro: BasePro)


}

class InvalidTodoPhotoException(message: String) : Exception(message)