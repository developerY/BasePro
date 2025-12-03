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
 * This version uses KClass for tab identity and ensures recomposition by creating
 * a new list object for the backStack on every update.
 */
class TopLevelBackStacRef<T : NavKey>(private val startKey: T) {

    private var topLevelBackStacks: HashMap<KClass<out T>, SnapshotStateList<T>> = hashMapOf(
        startKey::class to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)
        private set

    // SOLUTION: 'backStack' is now a state-backed variable holding a list.
    // Every time we assign a new list to it, Compose will trigger recomposition
    // for any composable that reads this state.
    var backStack by mutableStateOf<List<T>>(listOf(startKey))
        private set

    private fun updateBackStack() {
        val currentActiveStack = topLevelBackStacks[topLevelKey::class]
            ?: mutableStateListOf(topLevelKey)
        // By calling toList(), we create a NEW list instance.
        backStack = currentActiveStack.toList()
    }

    fun switchTopLevel(key: T) {
        topLevelKey = key

        if (topLevelBackStacks[key::class] == null) {
            topLevelBackStacks[key::class] = mutableStateListOf(key)
        } else {
            topLevelBackStacks[key::class]!!.apply {
                clear()
                add(key)
            }
        }
        updateBackStack()
    }

    fun add(key: T) {
        topLevelBackStacks[topLevelKey::class]?.add(key)
        updateBackStack()
    }

    fun popToRoot() {
        val currentStack = topLevelBackStacks[topLevelKey::class]
        if (currentStack != null && currentStack.size > 1) {
            val root = currentStack.first()
            currentStack.clear()
            currentStack.add(root)
            updateBackStack()
        }
    }

    fun replaceStack(vararg keys: T) {
        if (keys.isEmpty()) return
        val firstKey = keys.first()
        topLevelKey = firstKey
        topLevelBackStacks[firstKey::class] = mutableStateListOf(*keys)
        updateBackStack()
    }

    fun removeLastOrNull(): T? {
        val currentActiveStack = topLevelBackStacks[topLevelKey::class]
        var removedItem: T? = null
        if (currentActiveStack != null && currentActiveStack.size > 1) {
            removedItem = currentActiveStack.removeLastOrNull()
            updateBackStack()
        }
        return removedItem
    }
}