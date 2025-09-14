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

    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    // This 'backStack' is the one observed by NavDisplay
    val backStack = mutableStateListOf<T>(startKey)

    private fun updateBackStack() {
        // The NavDisplay should show the history of the currently selected topLevelKey
        val currentActiveStack = topLevelBackStacks[topLevelKey] ?: mutableStateListOf(topLevelKey) // Fallback to key itself
        backStack.clear()
        backStack.addAll(currentActiveStack)
    }

    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key) // Each tab starts with its own key as its initial stack
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        // Add to the currently active top-level stack
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack() // Refresh the main backStack for NavDisplay
    }

    fun removeLast() {
        val currentActiveStack = topLevelBackStacks[topLevelKey]
        // Only remove if there's more than one item (the initial key) in the current tab's stack
        if (currentActiveStack != null && currentActiveStack.size > 1) {
            currentActiveStack.removeLastOrNull()
            updateBackStack() // Refresh the main backStack for NavDisplay
        }
        // If the stack for a tab becomes empty (or just its root), it stays on that tab's root.
        // No automatic switching to startKey unless explicitly designed.
    }

    fun replaceLast(key: T) {
        val currentActiveStack = topLevelBackStacks[topLevelKey]
        if (currentActiveStack != null && currentActiveStack.isNotEmpty()) {
            currentActiveStack[currentActiveStack.lastIndex] = key
            updateBackStack() // Refresh the main backStack for NavDisplay
        }
    }
}