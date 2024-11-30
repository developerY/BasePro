package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.BluetoothLeRepImpl
import com.ylabz.basepro.core.data.repository.BluetoothLeRepository
import com.ylabz.basepro.core.data.repository.DrivingPtsRepImp
import com.ylabz.basepro.core.data.repository.DrivingPtsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {


    @Singleton
    @Binds
    fun bindsMapsRepo(
        mapsRepository: DrivingPtsRepImp
    ): DrivingPtsRepository


}