package com.ylabz.basepro.ui.navigation.root

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ylabz.basepro.core.ui.MAIN
import com.ylabz.basepro.core.ui.ROOT
import com.ylabz.basepro.ui.navigation.main.MainScreen


@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun RootNavGraph(
    navHostController: NavHostController,
    startDestination: String = MAIN
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = ROOT,
    ) {
        //mainNavGraph(navHostController)
        //photoNavGraph(navHostController)
        composable(route = MAIN) {
            MainScreen(
                navController = navHostController
            )
        }
    }
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */