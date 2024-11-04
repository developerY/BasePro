package com.ylabz.basepro.core.network.mappers

import com.ylabz.basepro.core.network.SearchYelpQuery
import com.ylabz.basepro.core.network.api.dto.BusinessInfo
import com.ylabz.basepro.core.network.api.dto.Category
import com.ylabz.basepro.core.network.api.dto.Coordinates


fun SearchYelpQuery.Business.toBusinessInfo(): BusinessInfo {
    return BusinessInfo(
        id = id ?: "No ID",
        name = name ?: "No Name",
        url = url ?: "No web address",
        rating = rating,
        photos = photos?.mapNotNull { it },
        price = price ?: "No Price",
        coordinates = Coordinates(
            coordinates?.latitude,
            coordinates?.longitude
        ),
        categories = categories?.mapNotNull { Category(it?.title) }
    )
}