package com.ylabz.basepro.feature.places.ui

import com.ylabz.basepro.core.model.yelp.BusinessInfo

sealed class CoffeeShopEvent {
    object LoadCoffeeShops : CoffeeShopEvent()
    data class FindCafesNear(val latitude: Double, val longitude: Double) : CoffeeShopEvent()
    data class Retry(val latitude: Double, val longitude: Double) : CoffeeShopEvent()
    data class OnCoffeeShopClick(val coffeeShop: BusinessInfo) : CoffeeShopEvent()
}
