package com.ylabz.basepro.applications.photodo.ui.navigation.main // Change this to your package name

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// STEP 1: DEFINE THE DESTINATIONS (No changes here)
@Serializable
sealed class SimpleScreen(val title: String) : NavKey {
    @Transient
    abstract val icon: ImageVector

    @Serializable
    data object Home : SimpleScreen("Home") {
        @Transient
        override val icon = Icons.Default.Home
    }

    @Serializable
    data object Search : SimpleScreen("Search") {
        @Transient
        override val icon = Icons.Default.Search
    }

    @Serializable
    data object Profile : SimpleScreen("Profile") {
        @Transient
        override val icon = Icons.Default.Person
    }
}

private val bottomBarItems = listOf(
    SimpleScreen.Home,
    SimpleScreen.Search,
    SimpleScreen.Profile
)

val SimpleScreenSaver = Saver<SimpleScreen, String>(
    save = { it.title },
    restore = { title -> bottomBarItems.find { it.title == title } ?: SimpleScreen.Home }
)

// STEP 2: CREATE THE UI (With Adaptive Layout)
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SimpleAdaptiveBottomBar() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val backStack = rememberNavBackStack<SimpleScreen>(SimpleScreen.Home)
    var currentTab: SimpleScreen by rememberSaveable(stateSaver = SimpleScreenSaver) {
        mutableStateOf(SimpleScreen.Home)
    }

    val onNavigate = { screen: SimpleScreen ->
        if (currentTab.title != screen.title) {
            currentTab = screen
            backStack.replace(screen)
        }
    }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(
                currentTab = currentTab,
                onNavigate = onNavigate
            )
            // This call is now correct
            AppContent(backStack = backStack)
        }
    } else {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    currentTab = currentTab,
                    onNavigate = onNavigate
                )
            }
        ) {
            // This call is now correct
            AppContent(backStack = backStack)
        }
    }
}

// --- CORRECTED: The parameter type is now NavBackStack ---
@Composable
fun AppContent(backStack: NavBackStack<NavKey>) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SimpleScreen.Home> {
                ScreenContent(name = "Home Screen")
            }
            entry<SimpleScreen.Search> {
                ScreenContent(name = "Search Screen")
            }
            entry<SimpleScreen.Profile> {
                ScreenContent(name = "Profile Screen")
            }
        }
    )
}

// --- UI COMPONENTS (No changes below this line) ---
@Composable
fun AppBottomBar(currentTab: SimpleScreen, onNavigate: (SimpleScreen) -> Unit) {
    NavigationBar {
        bottomBarItems.forEach { screen ->
            val isSelected = currentTab.title == screen.title
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(text = screen.title) }
            )
        }
    }
}

@Composable
fun AppNavigationRail(currentTab: SimpleScreen, onNavigate: (SimpleScreen) -> Unit) {
    NavigationRail {
        bottomBarItems.forEach { screen ->
            val isSelected = currentTab.title == screen.title
            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(text = screen.title) }
            )
        }
    }
}

@Composable
fun ScreenContent(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

fun <T : Any> MutableList<T>.replace(item: T) {
    if (this.isNotEmpty()) {
        this[this.lastIndex] = item
    } else {
        this.add(item)
    }
}