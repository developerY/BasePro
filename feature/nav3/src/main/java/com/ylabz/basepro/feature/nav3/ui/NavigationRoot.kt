package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.content.ContentBlue
import com.example.nav3recipes.content.ContentGreen
import kotlinx.serialization.Serializable

@Serializable
data object NoteListScreen : NavKey

@Serializable
data class NoteDetailDetailScreen(val id: Int) : NavKey

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(NoteListScreen)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<NoteListScreen> {

                ContentGreen("Welcome to Nav3") {
                    Button(onClick = {
                        backStack.add(NoteDetailDetailScreen(123))
                    }) {
                        Text("Click to navigate")
                    }
                }
            }

            entry<NoteDetailDetailScreen> { key ->
                ContentBlue("Route id: ${key.id} ")
            }
        }
    )
}