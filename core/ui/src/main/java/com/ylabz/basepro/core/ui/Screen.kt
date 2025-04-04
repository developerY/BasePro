package com.ylabz.basepro.core.ui

import kotlinx.serialization.Serializable

/**
 * This code defines a sealed class called `Screen` to represent the different screens in the app.
 * Each screen has a `route` property, which is a string that uniquely identifies the screen.
 *
 * The `Screen` class has two subclasses:
 *
 * * `MAIN`: This subclass represents the main screens of the app.
 * * `PHOTO`: This subclass represents the screens related to photos, such as adding a new photo or viewing a list of photos.
 *
 * The following table lists all of the screens in the app and their routes:
 *
 * | Screen | Route |
 * |---|---|---|
 * | Main Photo List | photo_list_screen |
 * | List | list_screen |
 * | Task | task_screen |
 * | Photo | photo_screen |
 * | Add Photo | add_photo_screen |
 * | Select Date | add_date_screen |
 * | Record Audio | add_audio_screen |
 * | Take Photo | camera_photo_screen |
 * | ML / ml_photo_screen
 *
 * The `Screen` class is used in the Compose Navigation API to define the navigation graph for the app. The navigation graph is a data structure that represents the different screens in the app and how they are connected to each other.
 */

const val PHOTO = "photo"
const val MAIN = "main"
const val ROOT = "root"
const val MAP = "maps"
const val PLACES = "places"
const val HEALTH = "health"
const val BLE = "ble"
const val SETTINGS = "settings"
const val SHOTIME = "shotime"
const val ALARM = "alarm"
const val WEATHER = "weather"
const val NFC = "nfc"
const val ML = "ml"


sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_screen") // These names are not used anywhere.
    data object ListScreen : Screen("list_screen")
    data object SettingsScreen : Screen("settings_screen")
    data object MapScreen : Screen("map_screen")
    data object CameraScreen : Screen("camera_screen")
    data object PlacesScreen : Screen("places_screen")
    data object HealthScreen : Screen("health_screen")
    data object BLEScreen : Screen("ble_screen")
    data object BLEPermissionsScreen : Screen("ble_permissions_screen")
    data object ShotimeScreen : Screen("shotime_screen")
    data object AlarmScreen : Screen("alarm_screen")
    data object WeatherScreen : Screen("weather_screen")
    data object NfcScreen : Screen("nfc_screen")
    data object MLScreen : Screen("ml_screen")
}

@Serializable
object CameraScreen

/*@Serializable
object MapScreen*/

@Serializable
data class PicScreen(
    val name: String?,
    val age: Int
)