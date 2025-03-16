package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.nfc.NfcRepository
import com.ylabz.basepro.core.data.repository.nfc.NfcRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.SpeedRepository
import com.ylabz.basepro.core.data.repository.travel.SpeedRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NfcModule {

    @Binds
    @Singleton
    abstract fun bindNFCRepository(
        impl: NfcRepositoryImpl
    ): NfcRepository
}