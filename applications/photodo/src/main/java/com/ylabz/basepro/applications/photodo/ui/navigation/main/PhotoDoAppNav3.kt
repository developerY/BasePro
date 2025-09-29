package com.ylabz.basepro.applications.photodo.ui.navigation.main // Change this to your package name

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// STEP 1: DEFINE THE DESTINATIONS (CORRECTED)
@Serializable
sealed class SimpleScreen(val title: String) : NavKey {
    // @Transient tells the serialization library to completely ignore this property.
    // This solves the error because the ImageVector is never part of the serialization process.
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

// STEP 2: CREATE THE UI
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotoDoAppNav3() {
    val backStack = rememberNavBackStack<SimpleScreen>(SimpleScreen.Home)

    var currentTab: SimpleScreen by rememberSaveable(stateSaver = SimpleScreenSaver) {
        mutableStateOf(SimpleScreen.Home)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomBarItems.forEach { screen ->
                    val isSelected = currentTab.title == screen.title
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                currentTab = screen
                                backStack.replace(screen)
                            }
                        },
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { Text(text = screen.title) }
                    )
                }
            }
        }
    ) {
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