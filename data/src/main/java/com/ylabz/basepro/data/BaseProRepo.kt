package com.ylabz.basepro.data

import com.ylabz.basepro.data.mapper.BasePro
import kotlinx.coroutines.flow.Flow

interface BaseProRepo {
    // Not sure where to put these ...
    fun allGetBasePros(): Flow<List<BasePro>> // NOTE: wrap in Flow<Resource<<>>>

    suspend fun insert(probase: BasePro)

    //suspend fun addTodoPhoto(BasePro: BasePro)
    suspend fun delete(probase: BasePro)
    suspend fun getBaseProById(probaseId: Int): BasePro? // NOTE: wrap in Flow<Resource<<>>>
    suspend fun deleteAll()
    //abstract fun insert(BasePro: BasePro)


}

class InvalidTodoPhotoException(message: String) : Exception(message)