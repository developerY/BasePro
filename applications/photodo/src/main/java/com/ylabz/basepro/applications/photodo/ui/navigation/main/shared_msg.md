# PhotoDo App Architecture

This document provides a high-level overview of the `PhotoDo` application's architecture. The app is built following Modern Android Development (MAD) principles to be scalable, testable, and maintainable.

## Core Principles

* **Multi-Module Architecture:** The app is divided into distinct modules (`core`, `db`, `features`) to enforce separation of concerns.
* **Feature-Driven UI:** The UI is organized by feature (`home`, `photodolist`, `settings`), with each feature being a self-contained unit.
* **Unidirectional Data Flow (UDF):** Each feature follows a strict UDF pattern, where state flows down from a `ViewModel` to the UI, and events flow up from the UI to the `ViewModel`.
* **Dependency Injection with Hilt:** Hilt is used to manage dependencies and lifecycles, particularly for ViewModels and Repositories.

## Module Breakdown

* `applications/photodo`: The main application module that integrates all other modules and contains the top-level navigation and UI scaffolding (`MainScreen.kt`).
* `applications/photodo/core`: Contains shared UI components (`FabMenu`), data models, and utilities used across multiple features.
* `applications/photodo/db`: The data layer. It contains the Room database (`PhotoDoDatabase`), DAOs, entities, and the repository implementation (`PhotoDoRepoImpl`).
* `applications/photodo/features`: A collection of self-contained feature modules.
    * `features/home`: Manages the main screen with the list of categories.
    * `features/photodolist`: Manages the list and detail screens for a specific to-do list.
    * `features/settings`: Manages the application settings screen.

## UI & State Management

Each feature follows a consistent UDF pattern.

```
+----------------+       sends events       +-----------+
|      UI        | -----------------------> | ViewModel |
| (Composable)   |                          +-----------+
+----------------+                                |
        ^                                         | updates state
        |                                         |
        | observes state                          |
        |                                         v
+----------------+                          +-----------+
|   UiState      | <----------------------- | LiveData  |
| (Data Class)   |                          |  or Flow  |
+----------------+                          +-----------+
```

* **ViewModel:** Holds the business logic and exposes the UI state.
* **UiState:** An immutable data class or sealed interface representing everything the UI needs to display.
* **Events:** User actions are sent from the UI to the ViewModel as events to ensure a predictable data flow.

## Communication in Multi-Pane Layouts

A key architectural challenge in this app is handling user actions in a multi-pane layout (e.g., on a tablet), where the global FAB controlled by one screen (`HomeEntry`) needs to trigger an action in another (`DetailEntry`).

We solve this using a **Shared ViewModel** pattern, which acts as a "message bus" or a central "announcement board."

### The Flow

1.  The **FAB `onClick`** in `HomeEntry` does not contain any business logic. Instead, it calls `postEvent` on a shared `MainScreenViewModel`.
2.  The `MainScreenViewModel` emits this event on a public `SharedFlow`.
3.  The `DetailEntry`, which is also on screen, collects events from the same `MainScreenViewModel` instance.
4.  Upon receiving the event, `DetailEntry` tells its own **local, specialist** `PhotoDoDetailViewModel` to handle the logic (e.g., show the bottom sheet).

This keeps the features decoupled. `HomeEntry` doesn't need to know how to add an item, and `DetailEntry` doesn't need to know where the event came from.

### Key Code Snippets

#### 1\. The Shared ViewModel

This ViewModel's only job is to relay messages.

**File:** `applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/MainScreenViewModel.kt`

```kotlin
@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {
    private val _events = MutableSharedFlow<MainScreenEvent>()
    val events = _events.asSharedFlow()

    suspend fun postEvent(event: MainScreenEvent) {
        _events.emit(event)
    }
}

sealed interface MainScreenEvent {
    data object AddItem : MainScreenEvent
}
```

#### 2\. The Broadcaster (`HomeEntry`)

Injects the shared ViewModel and posts an event on click.

**File:** `applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/HomeEntry.kt`

```kotlin
@Composable
fun HomeEntry(...) {
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    
    // ... Inside the FAB's onClick for the "Item" action
    onClick = {
        mainScreenViewModel.viewModelScope.launch {
            mainScreenViewModel.postEvent(MainScreenEvent.AddItem)
        }
    }
}
```

#### 3\. The Listener (`DetailEntry`)

Injects the shared ViewModel and listens for events, delegating to its local ViewModel.

**File:** `applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/DetailEntry.kt`

```kotlin
@Composable
fun DetailEntry(...) {
    val detailViewModel: PhotoDoDetailViewModel = hiltViewModel()
    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        mainScreenViewModel.events.collectLatest { event ->
            when (event) {
                is MainScreenEvent.AddItem -> {
                    detailViewModel.onEvent(PhotoDoDetailEvent.OnAddItemClicked)
                }
            }
        }
    }
}
```
# Alternatives
That's a fantastic question. You're right to ask if there are other ways. The **Shared ViewModel is not the only way**, but it is the most common and recommended solution for this specific problem in Modern Android Development (MAD).

Let's quickly cover the three main patterns for communication between different screens or features in Compose.

### 1. State Hoisting & Callbacks (The "Pure Compose" Way)

This is the pattern you suggested earlier. You "hoist" the state and the event logic to the nearest common parent, which in this case is `MainScreen`.

* **How it works:** `MainScreen` would own the `isBottomSheetVisible` state and the `onAddItem` lambda. It would then have to pass this state and lambda down as parameters through `AppContent` -> `HomeEntry`.
* **Pros:** It's very explicit and uses only the fundamental principles of Compose.
* **Cons:** For deeply nested screens, it leads to "prop drilling," where you pass parameters through many layers that don't need them. The parent (`MainScreen`) also becomes bloated with the state and logic of all its children.

### 2. CompositionLocal (The "Implicit" Way)

This is a mechanism in Compose for passing data down the composable tree without having to pass it as a parameter at every level.

* **How it works:** You could create a `CompositionLocal` for an "event bus" at the `MainScreen` level. Both `HomeEntry` and `DetailEntry` could then access this event bus implicitly to send and receive messages.
* **Pros:** Avoids "prop drilling."
* **Cons:** It creates an "invisible" or implicit dependency. It's not immediately obvious where the data is coming from, which can make the code harder to debug and reason about. It's generally recommended for things that don't change often, like theme information.

### 3. Shared ViewModel (The "Recommended" Way)

This is the pattern we've been discussing.

* **How it works:** A ViewModel is scoped to a larger lifecycle (like the navigation graph). Any screen within that scope can get the *same instance* of it via Hilt.
* **Pros:** It's lifecycle-aware (it won't leak memory), testable, and keeps your features decoupled. It avoids "prop drilling" without creating the implicit dependencies of `CompositionLocal`.
* **Cons:** It introduces one extra class (`MainScreenViewModel`) for communication.

---

### Conclusion

For your specific use case—communicating between two separate, decoupled feature screens (`Home` and `Detail`) in a complex, multi-pane layout—the **Shared ViewModel** is considered the best practice. It hits the sweet spot by providing a clean, explicit, and lifecycle-safe way for your screens to communicate without becoming tightly coupled or making your parent composables overly complex.