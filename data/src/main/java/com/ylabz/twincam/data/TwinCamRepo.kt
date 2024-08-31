package com.ylabz.twincam.data

import com.ylabz.twincam.data.mapper.TwinCam
import kotlinx.coroutines.flow.Flow

interface TwinCamRepo {
    // Not sure where to put these ...
    fun allGetTwinCams(): Flow<List<TwinCam>> // NOTE: wrap in Flow<Resource<<>>>

    suspend fun insert(twincam: TwinCam)

    //suspend fun addTodoPhoto(TwinCam: TwinCam)
    suspend fun delete(twincam: TwinCam)
    suspend fun getTwinCamById(twincamId: Int): TwinCam? // NOTE: wrap in Flow<Resource<<>>>
    suspend fun deleteAll()
    //abstract fun insert(TwinCam: TwinCam)


}

class InvalidTodoPhotoException(message: String) : Exception(message)