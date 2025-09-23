package com.ylabz.basepro.applications.photodo.db.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ylabz.basepro.applications.photodo.db.repo.AppSettingsRepository
import com.ylabz.basepro.applications.photodo.db.repo.DataStoreAppSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TOP-LEVEL: the actual file-backed DataStore<Preferences>
private const val APP_PREFS_NAME = "app_photodo_settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFS_NAME)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // 2) Provide the DataStore<Preferences> as an injectable dependency
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.dataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(
        impl: DataStoreAppSettingsRepository
    ): AppSettingsRepository

}