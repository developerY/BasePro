package com.ylabz.basepro.feature.wearos.home.navigation

sealed class WearScreen(val route: String) {
    object Home : WearScreen("wear_home")
    object Health : WearScreen("wear_health")
    // etc.
}

sealed class WearRoute(val route: String) {
    object Home : WearRoute("wear_home")
    object Health : WearRoute("wear_health")
    object Settings : WearRoute("wear_settings")
    // ... etc.
}
