package com.ylabz.twincam.data.repository

import androidx.annotation.WorkerThread
import com.ylabz.twincam.data.TwinCamDao
import com.ylabz.twincam.data.TwinCamRepo
import com.ylabz.twincam.data.mapper.TwinCam
import com.ylabz.twincam.data.mapper.toTwinCam
import com.ylabz.twincam.data.mapper.toTwinCamEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Custom Provision: Since you provided the TwinCamDao and TwinCamRepoImpl via the @Provides
 * methods in the DatabaseModule, Hilt doesn't need to rely on @Inject constructors for those classes.
 * It uses the methods in the module to resolve and inject dependencies.
 */
class TwinCamRepoImpl @Inject constructor (  // NOTE: constructor injection is not needed
    private val TwinCamDao: TwinCamDao,
) : TwinCamRepo {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    /**
     * this is where the conversion happens
     * Type mismatch.
     * Required: Flow<List<TwinCam>>
     *     Found:
     *     Flow<List<TwinCamEntity>>
     */
    @WorkerThread
    override fun allGetTwinCams(): Flow<List<TwinCam>> {
        return TwinCamDao.getAllTwinCams()
        /*return flow {
            val td = todoDao.getTask().map { it.toPhoto() }
            if(td.isNotEmpty())
                Log.d(TAG, "this is the last in flow in Repo Imp ${td.last().title}")
            emit(td)
        }*/
    }


    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    override suspend fun insert(twincam: TwinCam) {
        val twincamEnt = twincam.toTwinCamEntity()
        // Log.d(, "This is in todoDao --  ${photoEnt}")
        TwinCamDao.insert(twincamEnt)
    }

    @WorkerThread
    override suspend fun delete(twincam: TwinCam) {
        TwinCamDao.delete(twincam.toTwinCamEntity())
    }

    @WorkerThread
    override suspend fun getTwinCamById(twincamId: Int): TwinCam? {
        return TwinCamDao.findByPhotoTodoId(twincamId)?.toTwinCam()
    }

    @WorkerThread
    override suspend fun deleteAll() {
        TwinCamDao.deleteAll()
    }

}