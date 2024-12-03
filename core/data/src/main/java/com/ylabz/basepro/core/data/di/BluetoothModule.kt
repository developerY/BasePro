package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothJuul
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothJuulImpl
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothLeRepImpl
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothLeRepository
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothRepImpl
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothRepository
import com.ylabz.basepro.core.data.repository.Bluetooth.CompanionDeviceRepository
import com.ylabz.basepro.core.data.repository.Bluetooth.CompanionDeviceRepositoryImpl
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

    @Binds
    abstract fun bindBluetoothRepository(
        impl: BluetoothRepImpl
    ): BluetoothRepository


    @Binds
    abstract fun bindBluetoothJuulRepository(
        impl: BluetoothJuulImpl
    ): BluetoothJuul

    @Binds
    abstract fun bindCompanionDeviceRepository(
        impl: CompanionDeviceRepositoryImpl
    ): CompanionDeviceRepository


}
