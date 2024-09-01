package com.ylabz.twincam.ui.navigation.main

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.List
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ylabz.twincam.core.ui.Screen
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

private fun navigateTo(tabTitle: String, navController: NavHostController) {
    when (tabTitle) {
        "Home"-> navController.navigate(Screen.HomeScreen.route)
        "Hold" -> navController.navigate(Screen.HoldScreen.route)
        "Settings" -> navController.navigate(Screen.SettingsScreen.route)
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

val items = listOf(
    BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.TwoTone.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false,
    ),
    BottomNavigationItem(
        title = "Hold",
        selectedIcon = Icons.TwoTone.List,
        unselectedIcon = Icons.Outlined.List,
        hasNews = false,
        badgeCount = 0
    ),
    BottomNavigationItem(
        title = "Settings", // Category -> Cat
        selectedIcon = Icons.TwoTone.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        hasNews = true,
    ),
)

/**
 * Main Screen
 */
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) { // Change to Animated

    /**
     * bottom bar variables for nested scroll
     */
    val bottomBarHeight = 56.dp
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    Scaffold(
        /*topBar = {
            AppTopBar()
        },*/
        bottomBar = {
            AppBottomBar(
                navController = navController,
                //state = bottomBarVisibility(navController),
                modifier = Modifier
                    .height(bottomBarHeight)
                    .offset {
                        IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt())
                    }
            )
        }
    ) { padding ->
        MainNavGraph(navController,padding = padding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text("TwinCam") },
        /*navigationIcon = {
            IconButton(onClick = { /* Handle navigation icon press */ }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },*/
        actions = {
            IconButton(onClick = {  }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            /*IconButton(onClick = { /* Handle more icon press */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More")
            }*/
        },
        //backgroundColor = MaterialTheme.colorScheme.primary
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomBar(
    navController: NavHostController,
    //state: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        contentColor = Color.Blue
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                //colors = NavigationBarItemColors(),
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navigateTo(item.title, navController = navController)
                },
                label = {
                    Text(text = item.title)
                },
                alwaysShowLabel = false,
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
