package com.ylabz.basepro.ui.navigation.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ylabz.basepro.feature.camera.ui.CameraUIRoute
import com.ylabz.basepro.core.ui.CameraScreen
import com.ylabz.basepro.core.ui.PHOTO
import com.ylabz.basepro.core.ui.PicScreen
import com.ylabz.basepro.core.ui.Screen
import androidx.navigation.navigation
import com.ylabz.basepro.ui.bar.AppScaffold
import kotlinx.coroutines.CoroutineScope


/**
 * This function defines the navigation graph for the photo screen of the app.
 *
 * The navigation graph consists of four screens:
 *
 * * AddPhoto: A screen to add a new photo.
 * * CameraPhoto: A screen to take a photo with the camera.
 * * DatePicker: A screen to select a date for the photo.
 * * AudioRec: A screen to record audio for the photo.
 *
 * This navigation graph is only called from the AddToDo screen.
 *
 * @param navController The NavController used to navigate between screens.
 * @param paddingVals The padding to apply to the navigation graph.
 */
@RequiresApi(Build.VERSION_CODES.S)
fun NavGraphBuilder.photoNavGraph(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.CameraScreen.route,
        route = PHOTO
    ) {

        // We are using this ...
        composable(route = Screen.CameraScreen.route) {
            AppScaffold(
                route.toString(),
                scope = scope,
                drawerState = drawerState,
                navController = navController
            ) { paddingVals ->
                CameraUIRoute(
                    paddingValues = paddingVals,
                    navTo = { path -> navController.navigate(path) },
                )
            }
        }

        // We are not using type save navigation.
        composable<CameraScreen> {
            CameraUIRoute(
                paddingValues = PaddingValues(0.dp),
                navTo = {path -> navController.navigate(path)},
            )
        }

        composable<PicScreen> {
            val args = it.toRoute<PicScreen>()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${args.name}, ${args.age} years old")
            }
        }
    }
}