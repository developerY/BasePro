package com.ylabz.basepro.feature.places.ui

import com.ylabz.basepro.core.model.ylep.BusinessInfo


sealed class CoffeeShopUIState {
    object Loading : CoffeeShopUIState()
    data class Error(val message: String) : CoffeeShopUIState()

    data class Success(
        val coffeeShops: List<BusinessInfo> = emptyList(),
    ) : CoffeeShopUIState()

}
