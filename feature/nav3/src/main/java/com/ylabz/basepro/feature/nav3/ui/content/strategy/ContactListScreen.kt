package com.ylabz.basepro.feature.nav3.ui.content.strategy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A data class to represent a contact.
 */
data class Contact(val id: Int, val name: String, val email: String)

/**
 * A composable screen that displays a list of contacts.
 *
 * @param onContactClick A lambda that is invoked when a contact is clicked,
 * passing the ID of the clicked contact.
 */
@Composable
fun ContactListScreen(
    modifier: Modifier = Modifier,
    onContactClick: (Int) -> Unit
) {
    // A simple mock list of contacts for demonstration purposes.
    val contacts = listOf(
        Contact(1, "Alice Johnson", "alice@example.com"),
        Contact(2, "Bob Smith", "bob@example.com"),
        Contact(3, "Charlie Brown", "charlie@example.com"),
        Contact(4, "Diana Prince", "diana@example.com"),
        Contact(5, "Clark Kent", "clark@example.com")
    )

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Contacts",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts) { contact ->
                ContactListItem(contact = contact, onClick = onContactClick)
            }
        }
    }
}

/**
 * A composable item for a single contact in the list.
 */
@Composable
fun ContactListItem(contact: Contact, onClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(contact.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = contact.name, fontSize = 18.sp)
        Text(text = contact.email, fontSize = 14.sp)
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

/**
 * A simple placeholder for the detail pane when no contact is selected.
 */
@Composable
fun DetailPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Select a contact from the list", fontSize = 18.sp)
    }
}
