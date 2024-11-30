package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.BluetoothLeRepImpl
import com.ylabz.basepro.core.data.repository.BluetoothLeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BluetoothLeModule {


    @Provides
    @Singleton
    fun bindsBluetoothLeRepository() : BluetoothLeRepository {
        return BluetoothLeRepImpl()
    }

}