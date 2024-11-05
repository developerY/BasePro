package com.ylabz.basepro.core.data.api.apollo

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import com.ylabz.basepro.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

private var instance: ApolloClient? = null

fun apolloClient(context: Context): ApolloClient {
    if (instance != null) {
        return instance!!
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(context))
        .build()

    instance = ApolloClient.Builder()
        .serverUrl("https://api.yelp.com/v3/graphql")
        .webSocketServerUrl("wss://api.yelp.com/v3/graphql")
        .okHttpClient(okHttpClient)
        .normalizedCache(MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024))
        .build()

    return instance!!
}

private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("cache-control", "no-cache")
            .addHeader("Authorization", "Bearer ${BuildConfig.YELP_API_KEY}")
            .build()

        return chain.proceed(request)
    }
}