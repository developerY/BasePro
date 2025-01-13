package com.ylabz.basepro.feature.wearos.home.navigation

sealed class WearScreen(val route: String) {
    object Home : WearScreen("wear_home")
    object Health : WearScreen("wear_health")
    object Sleep : WearScreen("wear_sleep")
    object Drunk : WearScreen("wear_drunk")
    // etc.
}

/*sealed class WearRoute(val route: String) {
    object Home : WearRoute("wear_home_screen")
    object Health : WearRoute("wear_health_screen")
    object SleepWatch : WearRoute("wear_sleep_watch_screen")
    object DrunkWatch : WearRoute("wear_drunk")
}*/
