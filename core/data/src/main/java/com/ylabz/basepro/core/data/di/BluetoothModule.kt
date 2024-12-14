package com.ylabz.basepro.core.data.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothJuul
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothJuulImpl
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepImpl
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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


    @Binds
    abstract fun bindBluetoothJuulRepository(
        impl: BluetoothJuulImpl
    ): BluetoothJuul

}
