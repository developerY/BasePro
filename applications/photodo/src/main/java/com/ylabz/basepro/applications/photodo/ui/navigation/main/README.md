# PhoToDo Navigation Architecture

This document outlines the navigation architecture for the PhoToDo application, which is built using the experimental **Jetpack Navigation 3** library for Compose. The architecture is designed to be robust, type-safe, and scalable, supporting adaptive layouts and correct back navigation behavior.

## Core Principles

The navigation system is built around a few core principles:

1.  **Single Source of Truth**: The entire navigation state is driven by a single `MutableList<NavKey>`, which we call the `NavBackStack`. The UI is a direct reflection of the contents of this list.
2.  **State Hoisting**: Navigation logic, especially for back-press handling, is hoisted to the highest common parent (`MainScreen`) where all relevant state (the backstack and the current tab selection) can be managed atomically.
3.  **Type-Safe Navigation**: Destinations are represented by serializable `NavKey` objects, not string routes. This eliminates runtime errors from typos and allows for passing complex data between screens in a type-safe manner.
4.  **ViewModel Scoping**: Each screen's `ViewModel` is automatically scoped to its lifecycle on the backstack. When a screen is popped, its `ViewModel` is automatically cleared, preventing memory leaks.

---

## Key Components

### `NavKey`

A `NavKey` is a `data object` or `data class` that represents a unique destination in the app.

```kotlin
// Example from ui/navigation/PhotoDoNavKeys.kt
@Serializable
sealed interface PhotoDoNavKeys : NavKey {
    @Serializable
    data object HomeFeedKey : PhotoDoNavKeys

    @Serializable
    data class TaskListKey(val categoryId: Long) : PhotoDoNavKeys

    @Serializable
    data class TaskListDetailKey(val listId: String) : PhotoDoNavKeys

    @Serializable
    data object SettingsKey : PhotoDoNavKeys
}
```

### `NavBackStack`

This is the heart of the navigation system. It's a simple mutable list that holds the history of `NavKey` destinations.

```kotlin
// In MainScreen.kt
val backStack = rememberNavBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey)
```

-   The UI is always a function of what's in this `backStack`.
-   Forward navigation is achieved by adding a key: `backStack.add(key)`.
-   Back navigation is achieved by removing a key: `backStack.removeLast()`.

### `currentTopLevelKey`

This state variable tracks which of the main tabs (Home, Tasks, Settings) is currently active. It is crucial for highlighting the correct item in the `BottomBar` or `NavigationRail` and is kept in sync with the `NavBackStack`.

### `NavDisplay`

This is the Composable from the Navigation 3 library that does the rendering. It takes the `backStack` and an `onBack` lambda as parameters. It observes the `backStack` and displays the composable content defined in its `entryProvider` that corresponds to the last key in the list.

---

## Navigation Logic

### 1. Tab Navigation (Switching Top-Level Screens)

When the user taps a destination in the `BottomBar` or `NavigationRail`, we treat it as a top-level action that should clear the existing history for that tab.

-   The `onNavigate` lambda is triggered.
-   It updates the `currentTopLevelKey` to reflect the new tab.
-   It calls `backStack.replaceAll(newKey)`, which clears the list and adds the new top-level key as the sole entry.

```kotlin
// In MainScreen.kt -> onNavigate
if (currentTopLevelKey::class != keyToNavigate::class) {
    currentTopLevelKey = keyToNavigate
    backStack.replaceAll(keyToNavigate) // Clear history when switching tabs
}
```

### 2. Forward Navigation (Drilling Down)

When navigating deeper into a feature (e.g., from the Home screen to a specific Category's list), we simply **add** the new destination's key to the stack. This preserves the history.

```kotlin
// In the Home screen's entry, when a category is clicked
val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
backStack.add(listKey)
```

### 3. Back Navigation

Back navigation is the most critical piece of custom logic. To ensure the app behaves predictably and the UI state remains synchronized, the **`onBack` handler is hoisted to `MainScreen`**. This allows it to modify both `backStack` and `currentTopLevelKey` in one place.

The logic is as follows:

```kotlin
// In MainScreen.kt
val onBack: () -> Unit = {
    val currentKey = backStack.lastOrNull()

    // 1. If we are deep in a stack, just pop the last screen.
    if (backStack.size > 1) {
        backStack.removeLastOrNull()
    }
    // 2. If we are at the root of the "Tasks" or "Settings" tab, navigate back to Home.
    else if (currentKey is PhotoDoNavKeys.TaskListKey || currentKey is PhotoDoNavKeys.SettingsKey) {
        currentTopLevelKey = PhotoDoNavKeys.HomeFeedKey
        backStack.replaceAll(PhotoDoNavKeys.HomeFeedKey)
    }
    // 3. If we are at the root of the Home tab, finish the activity (exit the app).
    else {
        activity.finish()
    }
}
```
This handler is then passed down to `AppContent` and used by the `NavDisplay`.

---

## ViewModel State Scoping

We leverage Navigation 3's built-in support for `ViewModel` lifecycles. By calling `hiltViewModel()` within the `entry` block for a destination, the `ViewModel` instance is automatically created and its lifecycle is tied to that entry on the backstack.

```kotlin
// In AppContent.kt -> entryProvider
entry<PhotoDoNavKeys.TaskListKey> { listKey ->
    // This ViewModel will be created when TaskListKey is shown
    // and destroyed when TaskListKey is popped from the backstack.
    val viewModel: PhotoDoListViewModel = hiltViewModel()

    LaunchedEffect(listKey.categoryId) {
        viewModel.loadCategory(listKey.categoryId)
    }

    PhotoDoListUiRoute(viewModel = viewModel, ...)
}
```

This gives us automatic memory management for our screen-level state holders without any extra effort.