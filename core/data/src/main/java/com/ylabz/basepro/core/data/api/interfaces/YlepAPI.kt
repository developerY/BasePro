package com.ylabz.basepro.core.data.api.interfaces

import com.ylabz.basepro.core.model.ylep.BusinessInfo


interface YelpAPI {
    suspend fun getBusinesses(
        latitude: Double,
        longitude: Double,
        radius: Double,
        sort_by: String,
        categories: String
    ): List<com.ylabz.basepro.core.model.ylep.BusinessInfo?>?
}