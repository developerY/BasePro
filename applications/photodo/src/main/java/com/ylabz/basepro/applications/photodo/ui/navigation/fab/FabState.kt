package com.ylabz.basepro.applications.photodo.ui.navigation.fab

import androidx.compose.ui.graphics.vector.ImageVector

// By making this a public data class in its own file, it becomes accessible
// to any module that depends on the main ':applications:photodo' module.
//
// NOTE: For a stricter architecture, this would live in a `:core:ui` module that
// both `:applications:photodo` and `:features:home` would implement.
// For this project, placing it here is the simplest fix.

// --- OLD DEFINITION (REMOVED) ---
// sealed interface FabState {
//    data object Hidden : FabState
//    data class Single(...) : FabState
//    data class Split(...) : FabState
// }
// --- END OF REMOVAL ---


/**
 * Represents a single action item within a FAB menu.
 * @param text The text label for the action.
 * @param icon The icon for the action.
 * @param onClick The lambda to execute when the action is triggered.
 */
data class FabAction(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * A sealed interface to represent all possible states of the Floating Action Button.
 *
 * --- THIS IS THE FIX ---
 * Renamed from `FabStateMenu` to `FabState` to be the single source of truth.
 * --- END OF FIX ---
 */
sealed interface FabState {
    /**
     * The FAB is not visible.
     */
    data object Hidden : FabState

    /**
     * A standard, single-action Extended FAB.
     */
    data class Single(val action: FabAction) : FabState

    /**
     * A FAB menu with a primary button and a list of secondary menu items.
     * The FAB is a menu with a main button and multiple secondary items.
     * @param mainButtonAction The action for the always-visible button.
     * @param items The list of actions that appear in the expanded menu.
     */
    data class Menu(
        val mainButtonAction: FabAction,
        val items: List<FabAction>
    ) : FabState
}