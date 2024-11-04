package com.ylabz.basepro.core.network.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.ylabz.basepro.core.network.api.interfaces.MapsAPI
import com.ylabz.basepro.core.network.api.interfaces.YelpAPI
import com.ylabz.basepro.core.network.client.MapsClient

import com.ylabz.basepro.core.network.apollo.apolloClient
import com.ylabz.basepro.core.network.client.YelpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideApolloClient(
        @ApplicationContext context: Context
    ): ApolloClient {
        return apolloClient(context)
    }

    @Provides
    @Singleton
    fun bindsYelpAPI(
        apolloClient: ApolloClient
    ): YelpAPI {
        return YelpClient(apolloClient)
    }

    @Provides
    @Singleton
    fun bindsMapsAPI(): MapsAPI {
        return MapsClient()
    }
}