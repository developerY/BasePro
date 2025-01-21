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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ylabz.basepro.listings.ui.ListUIRoute
import com.ylabz.basepro.listings.ui.components.DetailsRoute
import com.ylabz.basepro.core.ui.MAIN
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.feature.home.ui.HomeMainRoute
import com.ylabz.basepro.settings.ui.SettingsUiRoute
import com.ylabz.basepro.ui.bar.AppScaffold
import com.ylabz.basepro.ui.navigation.graphs.bluetoothLeNavGraph
import com.ylabz.basepro.ui.navigation.graphs.gmapNavGraph
import com.ylabz.basepro.ui.navigation.graphs.healthNavGraph
import com.ylabz.basepro.ui.navigation.graphs.photodoNavGraph
import com.ylabz.basepro.ui.navigation.graphs.placesNavGraph
import com.ylabz.basepro.ui.navigation.graphs.settingsNavGraph
import com.ylabz.basepro.ui.navigation.graphs.shotimeNavGraph
import kotlinx.coroutines.CoroutineScope


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
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    NavHost(
        navController = navController,
        route = MAIN,
        startDestination = Screen.HomeScreen.route
    ) {
        photodoNavGraph(
            drawerState,
            scope,
            navController
        )
        gmapNavGraph(
            drawerState,
            navController,
            scope
        )
        placesNavGraph(
            drawerState,
            scope,
            navController
        )
        healthNavGraph(
            drawerState,
            scope,
            navController
        )

        shotimeNavGraph(
            drawerState,
            scope,
            navController
        )

        bluetoothLeNavGraph(
            drawerState,
            scope,
            navController
        )

        settingsNavGraph(
            drawerState,
            scope,
            navController
        )

        composable(
            Screen.HomeScreen.route,
        ) {

            AppScaffold(
                route.toString(),
                drawerState = drawerState,
                scope = scope,
                navController = navController
            ) { innerPadding ->
                HomeMainRoute(
                    modifier = Modifier.padding(innerPadding),
                    navTo = { path -> navController.navigate(path) },
                )
            }

        }

        composable(
            Screen.ListScreen.route
        ) {
            AppScaffold(
                route.toString(),
                drawerState = drawerState,
                scope = scope,
                navController = navController
            ) { padding ->
                ListUIRoute(
                    modifier = Modifier.padding(padding),
                    navTo = { path -> navController.navigate(path) }
                )
            }
        }


            // master / detail views for tasks
            composable(
                route = "details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("id")
                itemId?.let {
                    DetailsRoute(
                        navController = navController,
                        itemId = it
                    )
                }
            }
        }

}
