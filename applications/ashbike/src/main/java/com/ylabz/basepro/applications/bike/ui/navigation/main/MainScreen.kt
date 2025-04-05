package com.ylabz.basepro.applications.bike.ui.navigation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
) {
    MainNavGraph(
        navController = navController,
    )
}