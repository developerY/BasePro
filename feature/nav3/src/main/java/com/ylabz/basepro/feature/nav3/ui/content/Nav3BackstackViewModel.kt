package com.ylabz.basepro.feature.nav3.ui.content

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/// Your navigation keys must be @Serializable
@Serializable
data object Home : NavKey

@Serializable
data class Profile(val userId: String) : NavKey

// The ViewModel correctly uses Hilt and SavedStateHandle.
@HiltViewModel
class Nav3BackstackViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val BACK_STACK_KEY = "navigation_back_stack"

    // The private back stack is initialized from SavedStateHandle.
    private val _backStack: NavBackStack = mutableStateListOf<NavKey>().apply {
        val serializedKeys = savedStateHandle.get<List<String>>(BACK_STACK_KEY)
        val restoredKeys = serializedKeys?.map { Json.decodeFromString<NavKey>(it) }
        addAll(restoredKeys ?: listOf(Home))
    }

    // A public, read-only view of the back stack.
    val backStack: NavBackStack = _backStack

    // A helper function to save the back stack.
    private fun save() {
        val serializedKeys = _backStack.map { Json.encodeToString(it) }
        savedStateHandle[BACK_STACK_KEY] = serializedKeys
    }

    // Public navigation actions.
    fun navigateToProfile(userId: String) {
        _backStack.add(Profile(userId))
        save()
    }

    fun goBack() {
        _backStack.removeLastOrNull()
        save()
    }
}
