package com.ylabz.basepro.core.database.repository

import androidx.annotation.WorkerThread
import com.ylabz.basepro.core.database.BaseProDao
import com.ylabz.basepro.core.database.BaseProRepo
import com.ylabz.basepro.core.database.mapper.BasePro
import com.ylabz.basepro.core.database.mapper.toBasePro
import com.ylabz.basepro.core.database.mapper.toBaseProEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Custom Provision: Since you provided the BaseProDao and BaseProRepoImpl via the @Provides
 * methods in the DatabaseModule, Hilt doesn't need to rely on @Inject constructors for those classes.
 * It uses the methods in the module to resolve and inject dependencies.
 */
class BaseProRepoImpl @Inject constructor(
    // NOTE: constructor injection is not needed
    private val BaseProDao: BaseProDao,
) : BaseProRepo {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    /**
     * this is where the conversion happens
     * Type mismatch.
     * Required: Flow<List<BasePro>>
     *     Found:
     *     Flow<List<BaseProEntity>>
     */
    @WorkerThread
    override fun allGetBasePros(): Flow<List<BasePro>> {
        return BaseProDao.getAllBasePros()
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
    override suspend fun insert(basepro: BasePro) {
        val baseproEnt = basepro.toBaseProEntity()
        // Log.d(, "This is in todoDao --  ${photoEnt}")
        BaseProDao.insert(baseproEnt)
    }

    @WorkerThread
    override suspend fun delete(basepro: BasePro) {
        BaseProDao.delete(basepro.toBaseProEntity())
    }

    @WorkerThread
    override suspend fun deleteById(baseproId: Int) {
        BaseProDao.deleteById(baseproId)
    }

    @WorkerThread
    override suspend fun getBaseProById(baseproId: Int): BasePro? {
        return BaseProDao.findByPhotoTodoId(baseproId)?.toBasePro()
    }

    @WorkerThread
    override suspend fun deleteAll() {
        BaseProDao.deleteAll()
    }

}