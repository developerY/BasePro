package com.ylabz.basepro.applications.photodo.core.ui

import androidx.compose.ui.graphics.vector.ImageVector

// By making this a public data class in its own file, it becomes accessible
// to any module that depends on the main ':applications:photodo' module.
//
// NOTE: For a stricter architecture, this would live in a `:core:ui` module that
// both `:applications:photodo` and `:features:home` would implement.
// For this project, placing it here is the simplest fix.

// private data class FabState(val text: String, val onClick: () -> Unit)


/**
 * A sealed interface to represent all possible states of the Floating Action Button.
 * This allows for different types of FABs (single, split) or for it to be hidden.
 */
sealed interface FabState {
    /**
     * The FAB is not visible.
     */
    data object Hidden : FabState

    /**
     * A standard, single-action Extended FAB.
     * @param text The text to display on the button.
     * @param icon The icon to display.
     * @param onClick The action to perform when clicked.
     */
    data class Single(
        val text: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    ) : FabState

    /**
     * A split FAB with a primary and secondary action.
     * @param primaryText The text for the main action.
     * @param primaryIcon The icon for the main action.
     * @param primaryOnClick The action for the main button.
     * @param secondaryText The text for the secondary action.
     * @param secondaryIcon The icon for the secondary action.
     * @param secondaryOnClick The action for the secondary button.
     */
    data class Split(
        val primaryText: String,
        val primaryIcon: ImageVector,
        val primaryOnClick: () -> Unit,
        val secondaryText: String,
        val secondaryIcon: ImageVector,
        val secondaryOnClick: () -> Unit
    ) : FabState
}

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
 */
sealed interface FabStateMenu {
    /**
     * The FAB is not visible.
     */
    data object Hidden : FabStateMenu

    /**
     * A standard, single-action Extended FAB.
     */
    // data class Single_NOTUSE(val action: FabAction) : FabStateMenu

    /**
     *  A FAB menu with a primary button and a list of secondary menu items.
     * The FAB is a menu with a main button and multiple secondary items.
     * @param mainButtonAction The action for the always-visible button.
     * @param items The list of actions that appear in the expanded menu.
     */
    data class Menu(
        val mainButtonAction: FabAction,
        val items: List<FabAction>
    ) : FabStateMenu
}