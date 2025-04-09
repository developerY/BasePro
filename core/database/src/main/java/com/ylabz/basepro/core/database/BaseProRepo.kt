package com.ylabz.basepro.core.database

import com.ylabz.basepro.core.database.mapper.BasePro
import kotlinx.coroutines.flow.Flow

interface BaseProRepo {
    // Not sure where to put these ...
    fun allGetBasePros(): Flow<List<BasePro>> // NOTE: wrap in Flow<Resource<<>>>

    suspend fun insert(basepro: BasePro)

    //suspend fun addTodoPhoto(BasePro: BasePro)
    suspend fun delete(basepro: BasePro)
    suspend fun deleteById(baseproId: Int)
    suspend fun getBaseProById(baseproId: Int): BasePro? // NOTE: wrap in Flow<Resource<<>>>
    suspend fun deleteAll()
    //abstract fun insert(BasePro: BasePro)
}