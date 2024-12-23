package com.ylabz.basepro.ui.bar

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.core.ui.Screen.BLEPermissionsScreen.route
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppScaffold(
    route: String,
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = route,
                scope = scope,
                drawerState = drawerState,
                actions = {
                    when (route) {
                        Screen.HomeScreen.route -> IconButton(onClick = { /* Handle settings action */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                        Screen.MapScreen.route -> IconButton(onClick = { /* Handle home action */ }) {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        }
                        else -> {
                            IconButton(onClick = { /* Handle home action */ }) {
                                Icon(Icons.Default.Home, contentDescription = "Home")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Log.d("route lower bar ", route)
            when (route) {
                "main" -> HomeBottomBar(route = route, navController = navController)
                "maps" ->  MapBottomBar(route = route, navController = navController)
                "settings" -> HomeBottomBar(route = route,navController = navController)
                else ->  {}
            }
        }
    ) { padding ->
        content(padding)
    }
}
