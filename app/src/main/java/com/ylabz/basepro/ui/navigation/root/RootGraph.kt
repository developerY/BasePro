package com.ylabz.basepro.ui.navigation.root

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import com.ylabz.basepro.ui.navigation.main.MainScreen


@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun RootNavGraph(
    navHostController: NavHostController,
) {
    //mainNavGraph(navHostController)
    //photoNavGraph(navHostController)
    MainScreen(navController = navHostController)
}

/*
composable(route = MAIN) {
            MainScreen(navHostController)
        }
 */