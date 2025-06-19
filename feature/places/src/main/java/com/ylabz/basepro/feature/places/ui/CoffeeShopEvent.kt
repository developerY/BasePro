package com.ylabz.basepro.feature.places.ui

import com.ylabz.basepro.core.model.yelp.BusinessInfo

sealed class CoffeeShopEvent {
    object LoadCoffeeShops : CoffeeShopEvent()
    data class FindCafesNear(val latitude: Double, val longitude: Double) : CoffeeShopEvent()
    data class Retry(val latitude: Double, val longitude: Double) : CoffeeShopEvent()
    data class OnCoffeeShopClick(val coffeeShop: BusinessInfo) : CoffeeShopEvent()

    /**
     * A generic event to find cafes in a circular area.
     * The caller is responsible for calculating the center and radius.
     */
    data class FindCafesInArea(
        val latitude: Double,
        val longitude: Double,
        val radius: Double // Radius in meters
    ) : CoffeeShopEvent()

}
