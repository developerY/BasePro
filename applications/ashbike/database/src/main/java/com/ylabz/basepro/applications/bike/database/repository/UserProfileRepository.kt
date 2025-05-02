package com.ylabz.basepro.applications.bike.database.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 1) Repository interface (contract)
interface UserProfileRepository {
    val nameFlow: Flow<String>
    val heightFlow: Flow<String>
    val weightFlow: Flow<String>

    suspend fun setName(newName: String)
    suspend fun setHeight(cm: String)
    suspend fun setWeight(kg: String)
}