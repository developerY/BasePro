package com.ylabz.basepro.applications.photodo.ui.navigation.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass

/**
 * A helper class to manage the back stack for each top-level destination (tab).
 * This version uses the KClass of the NavKey to identify a tab's stack, making it
 * robust against keys with different parameters (e.g., categoryId).
 */
class TopLevelBackStack<T : NavKey>(private val startKey: T) {

    // CHANGE: The map key is now KClass, which represents the "tab".
    private var topLevelBackStacks: HashMap<KClass<out T>, SnapshotStateList<T>> = hashMapOf(
        startKey::class to mutableStateListOf(startKey)
    )

    // This is the representative key for the currently selected TAB
    var topLevelKey by mutableStateOf(startKey)
        private set

    // This is the actual back stack for the NavDisplay
    val backStack = mutableStateListOf<T>(startKey)

    private fun updateBackStack() {
        // CHANGE: Look up the stack using the CLASS of the current top-level key.
        val currentActiveStack = topLevelBackStacks[topLevelKey::class]
            ?: mutableStateListOf(topLevelKey) // Fallback, should not happen
        backStack.clear()
        backStack.addAll(currentActiveStack)
    }

    /**
     * Switches the current top-level destination (tab), clearing the history of that tab
     * and starting fresh with the provided key.
     */
    fun switchTopLevel(key: T) {
        topLevelKey = key // Set the new active key immediately.

        if (topLevelBackStacks[key::class] == null) {
            // First time visiting this tab, create a new stack.
            topLevelBackStacks[key::class] = mutableStateListOf(key)
        } else {
            // This tab has been visited before. Clear its history and start with the new key.
            // This is crucial for navigating from a category to a specific task list.
            topLevelBackStacks[key::class]!!.apply {
                clear()
                add(key)
            }
        }
        updateBackStack()
    }

    /**
     * Adds a new key to the back stack of the currently active tab.
     */
    fun add(key: T) {
        // CHANGE: Look up the stack using the CLASS.
        topLevelBackStacks[topLevelKey::class]?.add(key)
        updateBackStack()
    }

    /**
     * Pops the current tab's navigation stack back to its root screen.
     */
    fun popToRoot() {
        // CHANGE: Look up the stack using the CLASS.
        val currentStack = topLevelBackStacks[topLevelKey::class]
        if (currentStack != null && currentStack.size > 1) {
            val root = currentStack.first()
            currentStack.clear()
            currentStack.add(root)
            updateBackStack()
        }
    }

    /**
     * Replaces the entire back stack for the current tab.
     */
    fun replaceStack(vararg keys: T) {
        if (keys.isEmpty()) return
        val firstKey = keys.first()
        topLevelKey = firstKey // Also update the topLevelKey to stay in sync.
        // CHANGE: Look up the stack using the CLASS.
        topLevelBackStacks[firstKey::class] = mutableStateListOf(*keys)
        updateBackStack()
    }

    /**
     * Removes and returns the last element from the current tab's back stack.
     */
    fun removeLastOrNull(): T? {
        // CHANGE: Look up the stack using the CLASS.
        val currentActiveStack = topLevelBackStacks[topLevelKey::class]
        var removedItem: T? = null
        if (currentActiveStack != null && currentActiveStack.size > 1) {
            removedItem = currentActiveStack.removeLastOrNull()
            updateBackStack()
        }
        return removedItem
    }
}
