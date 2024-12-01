package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.ble.BluetoothLeRepImpl
import com.ylabz.basepro.core.data.repository.ble.BluetoothLeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothLeRepository(
        impl: BluetoothLeRepImpl
    ): BluetoothLeRepository
}
