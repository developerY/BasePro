package com.rxdigita.basepro.applications.rxtrack.ui.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.rxdigita.basepro.applications.rxtrack.ui.navigation.main.MainScreen


@Composable
fun RootNavGraph(navController: NavHostController) {
    // In this example, RootNavGraph simply delegates to MainScreen.
    // If you have multiple flows (e.g., auth, bike, settings) you can switch here.
    MainScreen(navController = navController)
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */