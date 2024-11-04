package com.ylabz.basepro.core.network.client

import com.apollographql.apollo3.ApolloClient
import com.ylabz.basepro.core.network.SearchYelpQuery
import com.ylabz.basepro.core.network.api.dto.BusinessInfo
import com.ylabz.basepro.core.network.api.interfaces.YelpAPI
import com.ylabz.basepro.core.network.mappers.toBusinessInfo


import javax.inject.Inject

class YelpClient @Inject constructor(
    private val apolloClient: ApolloClient
) : YelpAPI {

    override suspend fun getBusinesses(
        latitude: Double,
        longitude: Double,
        radius: Double,
        sort_by: String,
        categories: String
    ): List<BusinessInfo?>? {
        return apolloClient.query(
            SearchYelpQuery(
                latitude = latitude,
                longitude = longitude,
                radius = radius,
                sort_by = sort_by,
                categories = categories
            )
        ).execute()
            .data
            ?.search
            ?.business
            ?.map { it?.toBusinessInfo() }
            ?: emptyList<BusinessInfo>()
    }
}