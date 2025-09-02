package com.ylabz.basepro.ui.navigation.main

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.ui.DrawerContent
import kotlinx.coroutines.launch

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
        drawerState = drawerState,
        gesturesEnabled = false // Disable swipe gestures
    ) {

        MainNavGraph(
            navController = navController,
            drawerState = drawerState,
            scope = scope,
        )
    }
}

/*
@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
*/