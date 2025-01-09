package com.ylabz.basepro.ui.bar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.twotone.List
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.core.ui.Screen
import com.ylabz.basepro.ui.navigation.main.BottomNavigationItem
import kotlin.math.roundToInt



@Composable
fun MapBottomBar(
    route: String,
    navController: NavHostController,
    //state: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val items = listOf(
        BottomNavigationItem(
            title = route,
            selectedIcon = Icons.TwoTone.Face,
            unselectedIcon = Icons.Outlined.Face,
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "List",
            selectedIcon = Icons.AutoMirrored.TwoTone.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
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

// only used by the bottom bar
private fun navigateTo(tabTitle: String, navController: NavHostController) {
    when (tabTitle) {
        "Home"-> navController.navigate(Screen.HomeScreen.route)
        "List" -> navController.navigate(Screen.ListScreen.route)
        "Settings" -> navController.navigate(Screen.SettingsScreen.route)
    }
}

@Preview(showBackground = true)
@Composable
fun MapBottomBarPreview() {
    // Mock NavController for the preview
    val mockNavController = rememberNavController()

    MapBottomBar(
        route = "Home",
        navController = mockNavController,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    )
}

    /*
    AppBottomBar(
                navController = navController,
                //state = bottomBarVisibility(navController),
                modifier = Modifier
                    .height(bottomBarHeight)
                    .offset {
                        IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt())
                    }
            )
     */