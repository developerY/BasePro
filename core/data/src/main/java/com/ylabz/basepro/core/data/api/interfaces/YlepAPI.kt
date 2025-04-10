package com.ylabz.basepro.core.data.api.interfaces

import com.ylabz.basepro.core.model.yelp.BusinessInfo


interface YelpAPI {
    suspend fun getBusinesses(
        latitude: Double,
        longitude: Double,
        radius: Double,
        sort_by: String,
        categories: String
    ): List<BusinessInfo?>?
}