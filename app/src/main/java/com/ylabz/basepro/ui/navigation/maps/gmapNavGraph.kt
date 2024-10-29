package com.ylabz.basepro.ui.navigation.maps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ylabz.basepro.camera.ui.CameraUIRoute
import com.ylabz.basepro.core.ui.CameraScreen
import com.ylabz.basepro.core.ui.MAP
import com.ylabz.basepro.core.ui.Screen

@RequiresApi(Build.VERSION_CODES.S)
fun NavGraphBuilder.gmapNavGraph(navController: NavHostController, paddingVals: PaddingValues) {
    navigation(
        startDestination = Screen.MapScreen.route,
        route = MAP
    ) {
        composable(
            Screen.MapScreen.route
        ) {
            Column(modifier = Modifier.padding(paddingVals)) {
                Text("here")
            }
        }
    }
}