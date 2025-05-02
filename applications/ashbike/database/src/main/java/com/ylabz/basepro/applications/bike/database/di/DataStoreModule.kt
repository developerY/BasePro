package com.ylabz.basepro.applications.bike.database.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

// 1) Top‐level extension to create the DataStore<Preferences>
private const val USER_PREFS_NAME = "user_prefs"
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFS_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // 2) Provide the DataStore<Preferences> as an injectable dependency
    @Provides
    @Singleton
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.userDataStore
}
