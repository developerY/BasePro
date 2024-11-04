package com.ylabz.basepro.feature.places.ui

sealed class CoffeeShopEvent {
    object LoadCoffeeShops : CoffeeShopEvent()
    data class Retry(val latitude: Double, val longitude: Double) : CoffeeShopEvent()
}
