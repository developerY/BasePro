package com.ylabz.basepro.core.ui

/**
 * A sealed interface to represent explicit navigation actions that can be
 * requested from a composable, while keeping the NavController implementation
 * details at a higher level.
 */
sealed interface NavigationCommand {
    /**
     * A standard navigation to a destination. Pushes the destination
     * onto the back stack. Use for navigating to detail screens.
     *
     * @param route The destination route string.
     */
    data class To(val route: String) : NavigationCommand

    /**
     * A navigation action intended for switching between top-level tabs
     * in a bottom navigation bar. This uses specific NavOptions to correctly
     * manage the back stack and save state.
     *
     * @param route The destination route string.
     */
    data class ToTab(val route: String) : NavigationCommand
}
