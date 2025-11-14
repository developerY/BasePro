

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabAction
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import com.ylabz.basepro.applications.photodo.ui.theme.PhotoDoTheme

private const val TAG = "FabMain"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMain(
    modifier: Modifier = Modifier,
    fabState: FabState?
) {
    // This is the internal state for the menu animation
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    when (fabState) {
        is FabState.Menu -> {
            val fabAction = fabState.mainButtonAction
            val menuItems = fabState.items

            FloatingActionButtonMenu(
                modifier = modifier,
                expanded = isExpanded,
                // --- THIS IS THE FIX ---
                // We use the 'button' slot, as you correctly pointed out.
                button = {
                    ToggleFloatingActionButton(
                        checked = isExpanded,
                        onCheckedChange = {
                            // This click *only* toggles the expanded state.
                            // This fixes the "open/close" race condition bug.
                            isExpanded = it
                            Log.d(TAG, "Main FAB clicked, isExpanded = $isExpanded")
                        }
                    ) {
                        Icon(
                            imageVector = fabAction.icon, // Use the icon from our state
                            contentDescription = fabAction.text
                        )
                    }
                },
                // --- END OF FIX ---
                content = { // <-- This is the correct parameter name, not 'menuItems'
                    menuItems.forEach { item ->
                        FloatingActionButtonMenuItem(
                            onClick = {
                                isExpanded = false // Close menu on item click
                                item.onClick()     // Perform the item's action
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.text
                                )
                            },
                            text = { Text(item.text) }
                        )
                    }
                }
            )
        }
        is FabState.Hidden -> {
            // FAB is explicitly hidden, do nothing.
            if (isExpanded) {
                isExpanded = false // Ensure menu collapses if hidden
            }
        }
        null -> {
            // No FAB state defined for this screen, do nothing.
            if (isExpanded) {
                isExpanded = false // Ensure menu collapses
            }
        }
        is FabState.Single -> {
            // Handle the single FAB state
            FloatingActionButton(
                onClick = fabState.action.onClick,
                modifier = modifier
            ) {
                Icon(
                    imageVector = fabState.action.icon,
                    contentDescription = fabState.action.text
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FabMainPreview() {
    val sampleMenu = FabState.Menu(
        mainButtonAction = FabAction(
            text = "Add",
            icon = Icons.Default.Add,
            onClick = {}
        ),
        items = listOf(
            FabAction(
                text = "Add Item",
                icon = Icons.Default.Add,
                onClick = {}
            ),
            FabAction(
                text = "Add List",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = {}
            ),
            FabAction(
                text = "Add Category",
                icon = Icons.Default.Category,
                onClick = {}
            )
        )
    )
    PhotoDoTheme {
        FabMain(
            fabState = sampleMenu
        )
    }
}