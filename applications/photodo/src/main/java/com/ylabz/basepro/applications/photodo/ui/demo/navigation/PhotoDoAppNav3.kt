package com.ylabz.basepro.applications.photodo.ui.demo.navigation // Change this to your package name

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

// --- STEP 1: DEFINE DESTINATIONS (UPDATED) ---
@Serializable
private sealed class AppScreen(val title: String) : NavKey {
    abstract val icon: ImageVector

    @Serializable
    data object Home : AppScreen("Home") {
        @Transient
        override val icon = Icons.Default.Home
    }

    // NEW: Add a key for the detail screen. It takes an ID as an argument.
    @Serializable
    data class Detail(val id: Int) : AppScreen("Detail") {
        // This icon is not used for navigation but is required to satisfy the abstract property.
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

// Updated to include all top-level screens for the bottom bar.
private val topLevelScreens = listOf(AppScreen.Home, AppScreen.Search, AppScreen.Profile)

// Updated Saver to handle all AppScreen types.
private val AppScreenSaver = Saver<AppScreen, String>(
    save = { it.title },
    restore = { title -> topLevelScreens.find { it.title == title } ?: AppScreen.Home }
)


// --- STEP 2: CREATE THE UI (UPDATED FOR LIST-DETAIL) ---
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SimpleAdaptiveBottomBar() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    // The back stack now holds the generic NavKey type to accommodate different screen types.
    val backStack = rememberNavBackStack(AppScreen.Home)
    var currentTab: AppScreen by rememberSaveable(stateSaver = AppScreenSaver) {
        mutableStateOf(AppScreen.Home)
    }

    // --- NEW: Remember the ListDetailSceneStrategy ---
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    val onNavigate = { screen: AppScreen ->
        // When switching tabs, we should clear the back stack to avoid weird states.
        if (currentTab.title != screen.title) {
            currentTab = screen
            //backStack.replaceAll(screen)
        }
    }

    // This key is crucial for forcing recomposition when the back stack changes.
    val backStackKey = backStack.joinToString { (it as? AppScreen)?.title ?: "Detail" }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(currentTab = currentTab, onNavigate = onNavigate)
            key(backStackKey) {
                AppContent(
                    backStack = backStack,
                    sceneStrategy = listDetailStrategy // Pass the strategy
                )
            }
        }
    } else {
        Scaffold(
            bottomBar = { AppBottomBar(currentTab = currentTab, onNavigate = onNavigate) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                key(backStackKey) {
                    AppContent(
                        backStack = backStack,
                        sceneStrategy = listDetailStrategy // Pass the strategy
                    )
                }
            }
        }
    }
}

// --- STEP 3: UPDATE CONTENT AND NAVIGATION COMPONENTS ---

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppContent(
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = sceneStrategy, // Use the provided strategy
        entryProvider = entryProvider {
            entry<AppScreen.Home>(
                // Mark this as the "list" part of the list-detail view
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                // Show a list that can navigate to details
                ListScreen(onItemClick = { id -> backStack.add(AppScreen.Detail(id)) })
            }

            entry<AppScreen.Detail>(
                // Mark this as the "detail" part of the list-detail view
                metadata = ListDetailSceneStrategy.detailPane()
            ) { detailKey ->
                // The detail key contains the arguments (e.g., the ID)
                DetailScreen(id = detailKey.id)
            }

            // Other top-level screens remain the same
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

// --- STEP 4: ADD NEW SCREEN COMPOSABLES ---

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