package com.ylabz.basepro.ui.navigation.main

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.core.ui.CameraScreen
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.core.ui.Screen.BLEPermissionsScreen.route
import com.ylabz.basepro.ui.DrawerContent
import com.ylabz.basepro.ui.bar.AppScaffold
import com.ylabz.basepro.ui.bar.AppTopBar
import com.ylabz.basepro.ui.bar.HomeBottomBar
import com.ylabz.basepro.ui.bar.MapBottomBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * This code represents an Android application with a bottom navigation bar for navigating
 * between different tabs. It utilizes Jetpack Compose for creating the user interface.
 * The code includes functions and data classes for managing navigation and defining the
 * appearance and behavior of the bottom navigation bar.
 *
 * Functions:
 * - `navigateTo(tabTitle: String, navController: NavHostController)`: Navigates to the
 *   specified tab based on the provided tab title using the provided NavHostController.
 *
 * Data Classes:
 * - `BottomNavigationItem`: Represents a bottom navigation item with properties such as title,
 *   selected and unselected icons, news availability, and an optional badge count.
 *
 * Composable Functions:
 * - `MainScreen(navController: NavHostController = rememberNavController())`: The main screen
 *   of the application, which displays the content of the selected tab and the bottom navigation bar.
 * - `AppBottomBar(navController: NavHostController, modifier: Modifier = Modifier)`: The composable
 *   function for rendering the app's bottom navigation bar. It allows users to switch between tabs,
 *   and it supports dynamic updates based on the selected tab.
 *
 * The `MainScreen` composable function sets up the main UI layout, including the scaffold with a
 * nested bottom navigation bar. The `AppBottomBar` composable function displays the navigation items
 * with icons, labels, and optional badges, allowing users to switch between different tabs.
 */

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

/**
 * Main Screen
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onNavigate = { route ->
                        scope.launch {
                            drawerState.close() // Close the drawer
                            navController.navigate(route) // Navigate to the selected route
                        }
                    }
                )
            }
        },
        drawerState = drawerState
    ) {

        MainNavGraph(
            navController = navController,
            drawerState = drawerState,
            scope = scope,
            padding = PaddingValues(0.dp) // Provide initial padding
        )
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    TopAppBar(
        title = { Text("BasePro") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }

        },
        /*navigationIcon = {
            IconButton(onClick = { /* Handle navigation icon press */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },*/
        actions = {
            IconButton(onClick = {  }) {
                Icon(Icons.Default.Face, contentDescription = "Face")
            }
            /*IconButton(onClick = { /* Handle more icon press */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More")
            }*/
        },
        //backgroundColor = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
