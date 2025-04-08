package com.ylabz.basepro.applications.bike.database.repository

import androidx.annotation.WorkerThread
import com.ylabz.basepro.applications.bike.database.BikeProDao
import com.ylabz.basepro.core.database.BikeProDao
import com.ylabz.basepro.core.database.BikeProRepo
import com.ylabz.basepro.core.database.mapper.BikePro
import com.ylabz.basepro.core.database.mapper.toBikePro
import com.ylabz.basepro.core.database.mapper.toBikeProEntity
import com.ylabz.bikepro.applications.bike.database.BikeProRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Custom Provision: Since you provided the BikeProDao and BikeProRepoImpl via the @Provides
 * methods in the DatabaseModule, Hilt doesn't need to rely on @Inject constructors for those classes.
 * It uses the methods in the module to resolve and inject dependencies.
 */
class BikeProRepoImpl @Inject constructor (  // NOTE: constructor injection is not needed
    private val BikeProDao: BikeProDao,
) : BikeProRepo {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    /**
     * this is where the conversion happens
     * Type mismatch.
     * Required: Flow<List<BikePro>>
     *     Found:
     *     Flow<List<BikeProEntity>>
     */
    @WorkerThread
    override fun allGetBikePros(): Flow<List<BikePro>> {
        return BikeProDao.getAllBikePros()
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
    override suspend fun insert(basepro: BikePro) {
        val baseproEnt = basepro.toBikeProEntity()
        // Log.d(, "This is in todoDao --  ${photoEnt}")
        BikeProDao.insert(baseproEnt)
    }

    @WorkerThread
    override suspend fun delete(basepro: BikePro) {
        BikeProDao.delete(basepro.toBikeProEntity())
    }

    @WorkerThread
    override suspend fun deleteById(baseproId: Int) {
        BikeProDao.deleteById(baseproId)
    }

    @WorkerThread
    override suspend fun getBikeProById(baseproId: Int): BikePro? {
        return BikeProDao.findByPhotoTodoId(baseproId)?.toBikePro()
    }

    @WorkerThread
    override suspend fun deleteAll() {
        BikeProDao.deleteAll()
    }

}