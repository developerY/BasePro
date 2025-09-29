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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// --- DATA CLASSES AND SAVERS (No changes) ---
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

val AppScreenSaver = Saver<AppScreen, String>(
    save = { it.title },
    restore = { title -> topLevelScreens.find { it.title == title } ?: AppScreen.Home }
)

// --- NEW: State holder for the FAB ---
private data class FabStateRef(val text: String, val onClick: () -> Unit)


// --- FINAL, WORKING VERSION (UPDATED FOR HOISTED STATE) ---
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SimpleAdaptiveBottomBar() {
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpandedScreen = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val backStack = rememberNavBackStack<NavKey>(AppScreen.Home)
    var currentTab: AppScreen by rememberSaveable(stateSaver = AppScreenSaver) {
        mutableStateOf(AppScreen.Home)
    }

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    // --- NEW: Hoisted state for TopAppBar and FAB ---
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }
    var FabStateRef: FabStateRef? by remember { mutableStateOf(null) }

    val onNavigate = { screen: AppScreen ->
        if (currentTab.title != screen.title) {
            currentTab = screen
            backStack.replaceAll(screen)
        }
    }

    val backStackKey = backStack.joinToString { (it as? AppScreen)?.title ?: "Detail" }

    if (isExpandedScreen) {
        Row(modifier = Modifier.fillMaxSize()) {
            AppNavigationRail(currentTab = currentTab, onNavigate = onNavigate)
            // --- UPDATED: Pass hoisted state into the Scaffold ---
            Scaffold(
                modifier = Modifier.weight(1f).nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = topBar,
                floatingActionButton = {
                    FabStateRef?.let {
                        ExtendedFloatingActionButton(
                            onClick = it.onClick,
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            text = { Text(it.text) }
                        )
                    }
                }
            ) { padding ->
                key(backStackKey) {
                    AppContent(
                        modifier = Modifier.padding(padding),
                        backStack = backStack,
                        sceneStrategy = listDetailStrategy,
                        // Pass lambdas to update the hoisted state from the content
                        setTopBar = { topBar = it },
                        setFabStateRef = { FabStateRef = it }
                    )
                }
            }
        }
    } else {
        // --- UPDATED: Pass hoisted state into the Scaffold ---
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = topBar,
            bottomBar = { AppBottomBar(currentTab = currentTab, onNavigate = onNavigate) },
            floatingActionButton = {
                FabStateRef?.let {
                    ExtendedFloatingActionButton(
                        onClick = it.onClick,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text(it.text) }
                    )
                }
            }
        ) { padding ->
            key(backStackKey) {
                AppContent(
                    modifier = Modifier.padding(padding),
                    backStack = backStack,
                    sceneStrategy = listDetailStrategy,
                    // Pass lambdas to update the hoisted state from the content
                    setTopBar = { topBar = it },
                    setFabStateRef = { FabStateRef = it }
                )
            }
        }
    }
}

// --- UPDATED: AppContent now takes lambdas to set the hoisted state ---
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    sceneStrategy: ListDetailSceneStrategy<NavKey>,
    setTopBar: (@Composable () -> Unit) -> Unit,
    setFabStateRef: (FabStateRef?) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = sceneStrategy,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<AppScreen.Home>(metadata = ListDetailSceneStrategy.listPane()) {
                // This screen sets the TopAppBar and FAB
                setTopBar { LargeTopAppBar(title = { Text("Home") }, scrollBehavior = scrollBehavior) }
                setFabStateRef(FabStateRef("New Item") { /* TODO */ })

                ListScreen(onItemClick = { id -> backStack.add(AppScreen.Detail(id)) })
            }
            entry<AppScreen.Detail>(metadata = ListDetailSceneStrategy.detailPane()) { detailKey ->
                // The detail screen has a simple TopAppBar and no FAB
                setTopBar { LargeTopAppBar(title = { Text("Detail ${detailKey.id}") }, scrollBehavior = scrollBehavior) }
                setFabStateRef(null)

                DetailScreen(id = detailKey.id)
            }
            entry<AppScreen.Search> {
                // This screen has its own title and no FAB
                setTopBar { LargeTopAppBar(title = { Text("Search") }, scrollBehavior = scrollBehavior) }
                setFabStateRef(null)

                ScreenContent(name = "Search Screen")
            }
            entry<AppScreen.Profile> {
                // This screen has its own title and no FAB
                setTopBar { LargeTopAppBar(title = { Text("Profile") }, scrollBehavior = scrollBehavior) }
                setFabStateRef(null)

                ScreenContent(name = "Profile Screen")
            }
        }
    )
}

// --- UI COMPONENTS AND HELPERS (No changes below this line) ---

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

private fun <T : Any> MutableList<T>.replaceNav3(item: T) {
    if (isNotEmpty()) this[lastIndex] = item else add(item)
}

private fun <T : Any> MutableList<T>.replaceAllNav3(item: T) {
    clear()
    add(item)
}