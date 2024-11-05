package com.ylabz.basepro.core.data.mappers

import com.ylabz.basepro.core.data.dto.ylep.BusinessInfo
import com.ylabz.basepro.core.data.dto.ylep.Category
import com.ylabz.basepro.core.data.dto.ylep.Coordinates
import com.ylabz.basepro.core.network.SearchYelpQuery


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