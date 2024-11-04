package com.ylabz.basepro.core.network.api.interfaces

import com.ylabz.basepro.core.network.api.dto.BusinessInfo


interface YelpAPI {
    suspend fun getBusinesses(
        latitude: Double,
        longitude: Double,
        radius: Double,
        sort_by: String,
        categories: String
    ): List<BusinessInfo?>?
}