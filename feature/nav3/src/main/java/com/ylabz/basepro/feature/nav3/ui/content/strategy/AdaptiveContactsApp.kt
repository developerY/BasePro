package com.ylabz.basepro.feature.nav3.ui.content.strategy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
// TODO: Ensure these Content composables are correctly referenced or moved/redefined in LeanNav.kt
// For now, these imports will cause errors until LeanNav is set up.
// If your Content composables (ContentOrange etc.) are in the feature.nav3.ui.content package,
// the import should be: import com.ylabz.basepro.feature.nav3.ui.content.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContactScreen : NavKey {
    @Serializable
    data object ContactList : ContactScreen

    @Serializable
    data class ContactDetail(val contactId: Int) : ContactScreen
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveContactsApp(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(ContactScreen.ContactList)

    NavDisplay(
        backStack = backStack,
        // The onBack lambda now respects the keysToRemove count from the strategy.
        onBack = { keysToRemove ->
            repeat(keysToRemove) { backStack.removeLastOrNull() }
        },
        sceneStrategy = rememberListDetailSceneStrategy(),
        // entryDecorators = listOf(/*... */), // Assuming you have other decorators here
        entryProvider = entryProvider {
            // When a key is passed, the entry provider will select the correct composable.
            entry<ContactScreen.ContactList> {
                NavEntry(
                    key = it, // 'it' is the key passed to this lambda.
                    // This entry is marked as the list pane.
                    metadata = ListDetailSceneStrategy.listPane {
                        DetailPlaceholder()
                    }
                ) {
                    ContactListScreen(
                        modifier = modifier,
                        onContactClick = { contactId ->
                            // CORRECTED: Use backStack.add() to navigate.
                            // The strategy will handle showing the correct pane.
                            backStack.add(ContactScreen.ContactDetail(contactId))
                        }
                    )
                }
            }

            entry<ContactScreen.ContactDetail> {
                NavEntry(
                    key = it,
                    // This entry is marked as the detail pane.
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    ContactDetailScreen(contactId = it.contactId)
                }
            }
        }
    )
}

/**
 * A composable screen that displays the details of a single contact.
 *
 * @param contactId The ID of the contact to display. This is passed via navigation.
 */
@Composable
fun ContactDetailScreen(contactId: Int) {
    // Mock data for demonstration. In a real app, you would
    // fetch this data from a ViewModel or a repository.
    val contacts = listOf(
        Contact(1, "Alice Johnson", "alice@example.com"),
        Contact(2, "Bob Smith", "bob@example.com"),
        Contact(3, "Charlie Brown", "charlie@example.com"),
        Contact(4, "Diana Prince", "diana@example.com"),
        Contact(5, "Clark Kent", "clark@example.com")
    )

    // Find the contact with the matching ID.
    val contact = contacts.find { it.id == contactId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (contact != null) {
            Text(
                text = "Contact Details",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Name: ${contact.name}", fontSize = 20.sp)
            Text(text = "Email: ${contact.email}", fontSize = 16.sp)
        } else {
            Text(text = "Contact not found.", fontSize = 18.sp)
        }
    }
}

