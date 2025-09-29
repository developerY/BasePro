package com.ylabz.basepro.applications.photodo.ui.navigation.main // Change this to your package name

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


// STEP 1: DEFINE DESTINATIONS (No changes)
@Serializable
sealed class AppScreen(val title: String) : NavKey {
    @Transient
    abstract val icon: ImageVector

    @Serializable
    data object Home : AppScreen("Home") {
        @Transient
        override val icon = Icons.Default.Home
    }

    @Serializable
    data class Detail(val id: Int) : AppScreen("Detail") {
        @Transient
        override val icon = Icons.Default.Home
    }

    @Serializable
    data object Search : AppScreen("Search") {
        @Transient
        override val icon = Icons.Default.Search
    }

    @Serializable
    data object Profile : AppScreen("Profile") {
        @Transient
        override val icon = Icons.Default.Person
    }
}

private val topLevelScreens = listOf(AppScreen.Home, AppScreen.Search, AppScreen.Profile)

val AppScreenSaverNav3 = Saver<AppScreen, String>(
    save = { it.title },
    restore = { title -> topLevelScreens.find { it.title == title } ?: AppScreen.Home }
)

// --- FINAL, WORKING VERSION ---
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable fun SimpleAdaptiveBottomBar() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val backStack = rememberNavBackStack<NavKey>(AppScreen.Home)
    var currentTab: AppScreen by rememberSaveable(stateSaver = AppScreenSaverNav3) {
        mutableStateOf(AppScreen.Home)
    }

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    val onNavigate = { screen: AppScreen ->
        if (currentTab.title != screen.title) {
            currentTab = screen
            backStack.replaceAll(screen)
        }
    }

    // --- THE CRUCIAL FIX ---
    // We create a variable that represents the current state of the back stack.
    // By using this as a `key`, we tell Compose that if this value changes,
    // the content inside the key MUST be recomposed from scratch. This fixes
    // the issue where adding a detail pane wasn't updating the UI.
    val backStackKey = backStack.joinToString { (it as? AppScreen)?.title ?: "Detail" }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(currentTab = currentTab, onNavigate = onNavigate)
            key(backStackKey) { // Apply the key here
                AppContent(
                    backStack = backStack,
                    sceneStrategy = listDetailStrategy
                )
            }
        }
    } else {
        Scaffold(
            bottomBar = { AppBottomBar(currentTab = currentTab, onNavigate = onNavigate) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                key(backStackKey) { // Apply the key here as well
                    AppContent(
                        backStack = backStack,
                        sceneStrategy = listDetailStrategy
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppContent(
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = sceneStrategy,
        entryProvider = entryProvider {
            entry<AppScreen.Home>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                ListScreen(onItemClick = { id -> backStack.add(AppScreen.Detail(id)) })
            }
            entry<AppScreen.Detail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { detailKey ->
                DetailScreen(id = detailKey.id)
            }
            entry<AppScreen.Search> { ScreenContent(name = "Search Screen") }
            entry<AppScreen.Profile> { ScreenContent(name = "Profile Screen") }
        }
    )
}

@Composable
private fun AppBottomBar(currentTab: AppScreen, onNavigate: (AppScreen) -> Unit) {
    NavigationBar {
        topLevelScreens.forEach { screen ->
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
private fun AppNavigationRail(currentTab: AppScreen, onNavigate: (AppScreen) -> Unit) {
    NavigationRail {
        topLevelScreens.forEach { screen ->
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
private fun ListScreen(onItemClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Home List", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onItemClick(1) }) { Text("View Detail 1") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onItemClick(2) }) { Text("View Detail 2") }
    }
}

@Composable
private fun DetailScreen(id: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Detail for item #$id", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
private fun ScreenContent(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name, style = MaterialTheme.typography.headlineLarge)
    }
}

private fun <T : Any> MutableList<T>.replace(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}

private fun <T : Any> MutableList<T>.replaceAll(item: T) {
    clear()
    add(item)
}