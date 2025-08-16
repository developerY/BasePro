package com.ylabz.basepro.feature.nav3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class TopLevelBackStack<T : NavKey>(private val startKey: T) {

    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey: T by mutableStateOf(startKey)
        private set

    // This is the combined backStack that NavDisplay will observe
    val backStack: SnapshotStateList<T> = mutableStateListOf(startKey)

    init {
        updateBackStack() // Initialize backStack correctly
    }

    private fun updateBackStack() {
        backStack.clear()
        val currentActiveStack = topLevelBackStacks[topLevelKey] ?: mutableStateListOf(topLevelKey).also { topLevelBackStacks[topLevelKey] = it }

        if (topLevelKey == startKey) {
            backStack.addAll(currentActiveStack)
        } else {
            // If not on the startKey tab, NavDisplay's stack includes the startKey's root
            // followed by the current active tab's full stack.
            // Ensure startKey's stack always has at least startKey itself.
            val startKeyStack = topLevelBackStacks[startKey]?.takeIf { it.isNotEmpty() } ?: mutableStateListOf(startKey)
            if (startKeyStack.firstOrNull() != startKey && startKey == HomeKey) { // A bit of a safeguard for HomeKey as root
                 backStack.add(startKey) // Add startKey if not already the first in its own stack representation for combining
            } else if (!startKeyStack.contains(startKey) && startKey == HomeKey) {
                 backStack.add(startKey)
            } else {
                 backStack.addAll(startKeyStack.distinct().take(1)) // Typically just [startKey]
            }
            backStack.addAll(currentActiveStack.distinct()) // Add current tab's stack, ensuring no double-adds if current is startKey
        }
        // Ensure the combined backStack is not empty, defaulting to startKey if something went wrong.
        if (backStack.isEmpty()) {
            backStack.add(startKey)
        }
    }


    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val currentActiveStack = topLevelBackStacks[topLevelKey] ?: return

        if (currentActiveStack.size > 1) {
            currentActiveStack.removeLastOrNull()
        } else if (topLevelKey != startKey) {
            // If current tab has only one item AND it's not the startKey tab,
            // switch back to the startKey tab.
            topLevelKey = startKey
        }
        // If current tab is startKey and has only one item, effectively block popping the root of startKey.
        updateBackStack()
    }

    // Call 'removeLast()' n times.
    fun removeLast(count: Int) {
        repeat(count) {
            removeLast()
        }
    }

    fun replaceStack(vararg keys: T) {
        val currentActiveStack = topLevelBackStacks[topLevelKey] ?: return
        currentActiveStack.clear()
        currentActiveStack.addAll(keys.toList())
        updateBackStack()
    }

    fun clearAndSetStack(newTopLevelKey: T, vararg keys: T) {
        topLevelKey = newTopLevelKey
        topLevelBackStacks[newTopLevelKey] = mutableStateListOf(*keys)
        updateBackStack()
    }
}
