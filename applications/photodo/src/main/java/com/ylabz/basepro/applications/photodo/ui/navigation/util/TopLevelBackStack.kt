package com.ylabz.basepro.applications.photodo.ui.navigation.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

/**
 * A helper class to manage the back stack for each top-level destination in the bottom navigation bar.
 * This ensures that each tab maintains its own navigation history.
 */
class TopLevelBackStack<T : NavKey>(private val startKey: T) {

    // The key of the map is the representative NavKey instance that identifies the tab.
    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    // This is the representative key for the currently selected TAB (e.g., Home, List, Settings)
    var topLevelKey by mutableStateOf(startKey)
        private set

    // This is the actual back stack for the NavDisplay, which shows the history of the current tab
    val backStack = mutableStateListOf<T>(startKey)

    private fun updateBackStack() {
        val currentActiveStack = topLevelBackStacks[topLevelKey] ?: mutableStateListOf(topLevelKey)
        backStack.clear()
        backStack.addAll(currentActiveStack)
    }

    /**
     * Switches the current top-level destination (tab).
     */
    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    /**
     * Adds a new key to the back stack of the currently active tab.
     */
    fun add(key: T) {
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    /**
     * Pops the current tab's navigation stack back to its root screen.
     * This is typically called when a user re-selects an already active tab.
     */
    fun popToRoot() {
        val currentStack = topLevelBackStacks[topLevelKey]
        if (currentStack != null && currentStack.size > 1) {
            val root = currentStack.first()
            currentStack.clear()
            currentStack.add(root)
            updateBackStack()
        }
    }
    
    /**
     * Replaces the entire back stack for the current tab.
     * Used for navigating from one tab's content to another's root.
     */
    fun replaceStack(vararg keys: T) {
        topLevelBackStacks[topLevelKey] = mutableStateListOf(*keys)
        updateBackStack()
    }

    /**
     * Removes and returns the last element from the current tab's back stack.
     * This is the standard "back" navigation action.
     */
    fun removeLastOrNull(): T? {
        val currentActiveStack = topLevelBackStacks[topLevelKey]
        var removedItem: T? = null
        if (currentActiveStack != null && currentActiveStack.size > 1) {
            removedItem = currentActiveStack.removeLastOrNull()
            updateBackStack()
        }
        return removedItem
    }
}
