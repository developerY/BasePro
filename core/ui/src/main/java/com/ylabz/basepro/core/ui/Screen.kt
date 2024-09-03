package com.ylabz.basepro.core.ui

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

sealed class Screen(val route: String) {

    // MAIN
    data object HomeScreen: Screen(route = "home_screen")
    data object HoldScreen: Screen(route = "hold_screen") // is a key that is not used anywhere
    data object SettingsScreen : Screen(route = "settings_screen")

    // data object CameraPhoto: Screen(route = "camera_photo_screen")

}