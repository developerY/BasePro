package com.ylabz.basepro.ui.navigation.main


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.ylabz.basepro.camera.ui.CameraUIRoute
import com.ylabz.basepro.camera.ui.components.SimpleCameraCaptureWithImagePreview
import com.ylabz.basepro.core.ui.CameraScreen
import com.ylabz.basepro.listings.ui.ListUIRoute
import com.ylabz.basepro.listings.ui.components.DetailsRoute
import com.ylabz.basepro.core.ui.MAIN
import com.ylabz.basepro.core.ui.PicScreen
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.home.ui.HomeMainRoute
import com.ylabz.basepro.settings.ui.SettingsUiRoute
import com.ylabz.basepro.ui.navigation.maps.gmapNavGraph
import com.ylabz.basepro.ui.navigation.photo.photodoNavGraph


/**
 * This code defines the main navigation graph for an Android application using Jetpack Compose
 * and the Navigation component. The navigation graph specifies the destinations and routes within
 * the app, allowing users to navigate between different screens.
 *
 * Composable Function:
 * - `MainNavGraph(navController: NavHostController, padding: PaddingValues)`: The primary composable
 *   function that sets up the main navigation graph for the application. It configures the NavHost
 *   with various composable destinations corresponding to different screens.
 *
 * Destinations and Composables:
 * - The code defines several composable destinations for the NavHost.
 *   1. `Screen.Task.route`: Represents the "Task" screen and displays the text "Next Task" along
 *      with a windwatersnowScreen composable.
 *   2. `Screen.PhotoT.route`: Represents the "Photo" screen and displays a PhotoRoute composable.
 *   3. `Screen.List.route`: Represents the "List" screen and displays a ListRoute composable.
 *   4. "details/{id}": A dynamic route that takes an "id" parameter and displays a DetailsRoute
 *      composable for showing details related to a specific item.
 *   5. `Screen.Cat.route`: Represents the "Cat" screen and displays the text "Next Cat" along with
 *      a ListwindwatersnowScreen composable.
 *   6. `photoNavGraph(navController, padding)`: Invokes a separate navigation graph for the "Photo" tab.
 *
 * Overall, this code defines the structure of the main navigation graph for the app, setting up
 * different destinations and their associated composable functions. Users can navigate between
 * these screens using the provided NavHostController, creating a smooth navigation experience.
 */


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainNavGraph(
    navController: NavHostController,
    padding: PaddingValues,
) {
    NavHost(
        navController = navController,
        route = MAIN,
        startDestination = Screen.HomeScreen.route
    ) {
        photodoNavGraph(navController, padding)
        gmapNavGraph(navController, padding)
        composable(
            Screen.HomeScreen.route,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Button(onClick = {
                        navController.navigate(
                            "photo"
                        )
                    }) {
                        Text(text = "Go to screen B")
                    }
                    Button(onClick = {
                        navController.navigate(
                            "maps"
                        )
                    }) {
                        Text(text = "Go to Map")
                    }

                }
                HomeMainRoute(
                    modifier = Modifier.padding(padding),
                    navTo = {path -> navController.navigate(path)},
                   // navPlay = {path -> navController.navigate(path)}
                )
            }
        }

        composable(
            Screen.ListScreen.route
        ) {
            ListUIRoute(
                modifier = Modifier.padding(padding),
                navTo = {path -> navController.navigate(path)}
            )
        }

        composable(
            Screen.SettingsScreen.route
        ) {
            SettingsUiRoute(
                modifier = Modifier.padding(padding),
                navTo = {path -> navController.navigate(path)}
            )
        }

        // master / detail views for tasks
        composable(
            route = "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("id")
            itemId?.let {
                DetailsRoute(
                    modifier = Modifier.padding(padding),
                    navController = navController,
                    itemId = it)
            }
        }

    }
}
