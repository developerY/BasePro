# PhotoDo Architecture: FABs, Events, and State Management

This document explains the application's architecture for handling the global Floating Action Button (FAB) and triggering UI state changes, such as displaying a bottom sheet.

The core of this architecture is a **hybrid model** that combines two powerful Jetpack Compose patterns: **State Hoisting** for the UI and an **Event Bus** for actions. This provides a clean separation of concerns and allows any screen to trigger global actions.

## Architecture Overview

The system is designed to solve a specific problem: How can a FAB, which lives in `MainScreen`, trigger an action (like adding a category) whose logic and state are managed deep inside a feature-specific ViewModel (like `HomeViewModel`)?

Our solution uses two patterns working in harmony:

1.  **State Hoisting (for the UI):** The currently visible screen (e.g., `HomeEntry`, `ListEntry`) is responsible for deciding what the FAB should look like and what its buttons do. It sends this UI configuration up to `MainScreen` using the `setFabState` function. This keeps the UI logic context-aware.

2.  **Event Bus (for Actions):** When a FAB button is clicked, it needs to trigger a global action. It does this by posting an event to the `MainScreenViewModel` (our message bus). This decouples the screen that *triggers* the action from the screen that *handles* the action.

The crucial missing piece was the **Listener**. We have a designated listener in `HomeEntry` that subscribes to the event bus and tells the appropriate ViewModel to change its state.

### Visual Flow

```
+----------------+      +-----------------------+      +----------------+
|      FAB       |----->| MainScreenViewModel   |----->|   HomeEntry    |
| (in any screen)|      |    (The Event Bus)    |      | (The Listener) |
+----------------+      +-----------------------+      +----------------+
      | Clicked!              | Emits Event                  | Hears Event
      |                       |                              |
      v                       v                              v
   Posts Event         (SharedFlow)                    Calls onEvent() on
                                                       HomeViewModel

                                                     +----------------+
                                                     | HomeViewModel  |
                                                     | (State Manager)|
                                                     +----------------+
                                                           | Updates State
                                                           | (isAddingCategory=true)
                                                           |
                                                           v
                                                     +-----------------+
                                                     | PhotoDoHomeUi...|
                                                     | (The UI)        |
                                                     +-----------------+
                                                           | Recomposes &
                                                           | Shows Bottom Sheet
```

-----

## Key Components and Their Roles

### 1\. `MainScreen.kt`

* **Role:** The root UI controller.
* **Responsibilities:**
    * Owns the `Scaffold` and the `floatingActionButton` slot.
    * Holds the `fabState` variable, which determines what `FabMenu` to display.
    * It is "dumb"—it only displays the FAB configuration it's given; it has no knowledge of *why* the FAB looks the way it does.

### 2\. `HomeEntry.kt` (The Screen-Level Controller)

This is the most important component in this architecture. It has two distinct jobs.

* **Job 1: The Listener**

    * It hosts a `LaunchedEffect` that permanently listens to events from the `MainScreenViewModel`.
    * It acts as the **designated listener** for all category-related events. When it hears an event like `RequestAddCategory`, it translates that into a specific command for the `HomeViewModel`.

  <!-- end list -->

  ```kotlin
  // In HomeEntry.kt
  LaunchedEffect(Unit) {
      mainScreenViewModel.events.collectLatest { event: MainScreenEvent ->
          when (event) {
              is MainScreenEvent.RequestAddCategory -> {
                  homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)
              }
              //...
          }
      }
  }
  ```

* **Job 2: The FAB Definer**

    * It hosts another `LaunchedEffect` that determines what the FAB should look like and what its buttons do *when the home screen is active*.
    * It calls `setFabState(...)` to hoist this UI definition up to `MainScreen`.
    * Crucially, the `onClick` for its "Add Category" button **posts an event to the message bus**. This allows any other screen (`ListEntry`, `DetailEntry`) to use the exact same pattern.

  <!-- end list -->

  ```kotlin
  // In HomeEntry.kt
  LaunchedEffect(...) {
      setFabState(
          FabStateMenu.Menu(
              //...
              items = listOfNotNull(
                  FabAction(
                      text = "Category",
                      icon = Icons.Default.Create,
                      // This onClick posts a global event.
                      onClick = {
                          mainScreenViewModel.postEvent(MainScreenEvent.RequestAddCategory)
                      }
                  ),
                  //...
              )
          )
      )
  }
  ```

### 3\. `MainScreenViewModel.kt` (The Event Bus)

* **Role:** A stateless messenger.
* **Responsibilities:**
    * Its only job is to receive an event via `postEvent` and immediately emit it to its `SharedFlow`.
    * It holds no state and has no logic. It is a simple, reliable post office.

### 4\. `HomeViewModel.kt` (The State Manager)

* **Role:** The single source of truth for the home screen's UI state.
* **Responsibilities:**
    * Holds the `HomeUiState`, which contains the `showAddCategorySheet` boolean flag.
    * When its `onEvent(HomeEvent.OnAddCategoryClicked)` function is called (by the listener in `HomeEntry`), it updates its state to set `showAddCategorySheet = true`.

### 5\. `PhotoDoHomeUiRoute.kt` (The UI)

* **Role:** The top-level composable for the "home" feature.
* **Responsibilities:**
    * Collects and observes the state from `HomeViewModel`.
    * Contains the UI logic to display the bottom sheet when the state flag is true.
  <!-- end list -->
  ```kotlin
  // In PhotoDoHomeUiRoute.kt
  val uiState by homeViewModel.uiState.collectAsState()

  when (val state = uiState) {
      is HomeUiState.Success -> {
          // ...
          if (state.showAddCategorySheet) {
              AddCategorySheet(...)
          }
      }
  }
  ```

-----

## The Step-by-Step Flow of a Click

This is how adding a category works from **any screen** (e.g., the detail view):

1.  **FAB Definition:** The `DetailEntry` (or whichever screen is active) runs its `LaunchedEffect` and calls `setFabState`, defining a FAB with an "Add Category" button. The `onClick` for this button is set to `mainScreenViewModel.postEvent(MainScreenEvent.RequestAddCategory)`.

2.  **User Click:** The user taps the "Add Category" button in the FAB menu.

3.  **Event Posted:** The `onClick` lambda executes, posting the `RequestAddCategory` event to the `MainScreenViewModel` message bus.

4.  **Event Heard:** The listener `LaunchedEffect` in `HomeEntry.kt`, which is always active as part of the home navigation entry, hears the `RequestAddCategory` event.

5.  **State Update Requested:** The listener calls `homeViewModel.onEvent(HomeEvent.OnAddCategoryClicked)`.

6.  **State Changed:** The `HomeViewModel` receives the event and updates its internal `HomeUiState`, setting `showAddCategorySheet = true`.

7.  **UI Recomposes:** The `PhotoDoHomeUiRoute`, which is observing the `HomeViewModel`'s state, sees that `showAddCategorySheet` is now `true`.

8.  **Bottom Sheet Appears:** The `if (state.showAddCategorySheet)` condition becomes true, and the `AddCategorySheet` is composed and displayed to the user.


# PhotoDo Architecture: FABs, Events, and State Management

This document explains the application's architecture for handling the global Floating Action Button (FAB) and triggering UI state changes, such as displaying a bottom sheet.

The architecture is a **hybrid model** that combines two powerful Jetpack Compose patterns: **State Hoisting** for the UI and an **Event Bus** for actions. This provides a clean separation of concerns and allows any screen to trigger global actions.

## Architectural Flow

1.  **FAB Definition (State Hoisting)**: The currently visible screen entry (e.g., `HomeEntry.kt`, `ListEntry.kt`) is responsible for deciding what the FAB should look like and what its buttons do. It calls `setFabState(...)` to "hoist" this UI configuration up to `MainScreen.kt`.

2.  **FAB Action (Event Bus)**: When a FAB button is clicked, it posts a global event (e.g., `RequestAddCategory`) to the `MainScreenViewModel`, which acts as a message bus.

3.  **Event Listening**: A designated "listener" within a specific entry file (`HomeEntry.kt` for category actions) has a `LaunchedEffect` that is always listening to the `MainScreenViewModel`.

4.  **State Management**: When the listener hears a relevant event, it calls the appropriate function on its feature-specific ViewModel (e.g., `homeViewModel.onEvent(...)`). This ViewModel then updates its own UI state (e.g., setting `showAddCategorySheet = true`).

5.  **UI Update**: The UI layer (`PhotoDoHomeUiRoute.kt`) is observing the state of the feature ViewModel. When the state changes, the UI recomposes and displays the bottom sheet.

### Key Components and Their Roles

* **`MainScreen.kt`**: Owns the `Scaffold` and the `floatingActionButton` slot. It only displays the FAB configuration it's given via `setFabState`.

* **`HomeEntry.kt` / `ListEntry.kt` / `DetailEntry.kt`**: These screen-level "entry" composables are the controllers.
  * They define the FAB's appearance and actions for their context.
  * They post events to the `MainScreenViewModel` when a FAB button is clicked.
  * `HomeEntry.kt` contains the permanent listener for all category-related events.

* **`MainScreenViewModel.kt`**: A stateless event bus. Its only job is to receive and re-emit events.

* **`HomeViewModel.kt`**: The state manager for the home feature. It owns the `HomeUiState` and is the single source of truth for whether the "Add Category" bottom sheet should be visible.

* **`PhotoDoHomeUiRoute.kt`**: The UI layer that observes state from `HomeViewModel` and renders the appropriate UI, including the bottom sheet.