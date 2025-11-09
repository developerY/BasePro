# PhotoDo Architecture: FAB & Bottom Sheet State Management

This document explains the application's architecture for handling the global Floating Action Button (FAB) and Modal Bottom Sheets.

The core of this architecture is a **Stateful, Shared ViewModel** (`MainScreenViewModel`). This ViewModel acts as the **single source of truth** for the global UI state of `MainScreen.kt` (i.e., "which FAB is showing?" and "which bottom sheet is open?").

This model avoids the race conditions found in a pure "event bus" system and follows modern, unidirectional data flow (UDF) principles.

## Architectural Flow

The flow is managed in two distinct loops: one for **State Hoisting** (what the UI should look like) and one for **Event Handling** (what the UI should do).



**1. State Hoisting (UI Definition):**
The currently visible screen (e.g., `HomeEntry`) is responsible for *defining* the UI.

1.  **`HomeEntry.kt`** composes (because it's the current navigation destination).
2.  A `LaunchedEffect` inside `HomeEntry.kt` calls `mainScreenViewModel.setFabState(FabState.Menu(...))`.
3.  The **`MainScreenViewModel.kt`** receives this configuration and updates its internal `MainScreenUiState` `StateFlow`.
4.  **`MainScreen.kt`** (which owns the `Scaffold`) collects this `uiState` and passes the `uiState.fabState` to the `FabMain` component.
5.  **`FabMain.kt`** (a "dumb" component) simply renders the `FabState.Menu` it was given.

**2. Event Handling (User Clicks):**
This is the flow that fixes the "open/close" bug.

1.  **User Clicks `+`:** The user taps the main `+` button in `FabMain.kt`.
2.  **Internal State Change:** The `onFABClick` lambda in `FabMain.kt` is called. This function **only** toggles its *internal* `isExpanded` state. It does **not** call any event.
3.  **Result:** The FAB menu animates open. There is no race condition.
4.  **User Clicks "Add Category":** The user taps one of the menu items (e.g., "Add Category").
5.  **Event Fired:** The `FloatingActionButtonMenuItem`'s `onClick` is triggered. This calls the `onClick` lambda from the `FabAction` that was defined back in `HomeEntry.kt`.
6.  `HomeEntry.kt`'s `onClick` lambda finally calls `onAddCategoryClicked()`.
7.  **State Updated:** `MainScreen.kt` (which defines `onAddCategoryClicked`) calls `mainScreenViewModel.onEvent(MainScreenEvent.OnAddCategoryClicked)`.
8.  The **`MainScreenViewModel.kt`** receives this event and *atomically* updates its `uiState` to:
    * `currentSheet = BottomSheetType.ADD_CATEGORY`
    * `fabState = FabState.Hidden` (to get out of the way)
9.  **UI Recomposes:** `MainScreen.kt` collects this single new state and:
    * Hides the `FabMain` component.
    * Shows the `ModalBottomSheet` with the `AddCategoryBottomSheet` content.

---

## Key Components and Their Roles

### 1. `MainScreen.kt` (The UI Host)

* **Role:** The top-level screen that owns the `Scaffold`, `TopAppBar`, `FabMain`, and `ModalBottomSheet`.
* **Responsibilities:**
    * Collects `uiState` from `MainScreenViewModel`.
    * Renders the `FabMain` composable based on `uiState.fabState`.
    * Renders the `ModalBottomSheet` based on `uiState.currentSheet`.
    * Passes the `mainScreenViewModel::setFabState` and `mainScreenViewModel::onEvent` lambdas down to the `PhotoDoNavGraph`.

### 2. `MainScreenViewModel.kt` (The State Holder)

* **Role:** The **Single Source of Truth** for all "global" UI state related to the `MainScreen` `Scaffold`.
* **Responsibilities:**
    * Holds the `MainScreenUiState` in a `StateFlow`.
    * `MainScreenUiState` contains `val fabState: FabState?` and `val currentSheet: BottomSheetType`.
    * Exposes `setFabState(FabState?)`: This allows the current navigation entry (like `HomeEntry`) to tell the ViewModel what the FAB should look like.
    * Exposes `onEvent(MainScreenEvent)`: This allows any component to send an action (like "Add Category" or "Dismiss Sheet").
    * **The Bug Fix:** This ViewModel contains the crucial logic: when it receives an event like `OnAddCategoryClicked`, it *atomically* updates the state to show the sheet **and** hide the FAB. When it receives `OnBottomSheetDismissed`, it hides the sheet **and** restores the FAB.

### 3. `HomeEntry.kt` / `ListEntry.kt` / `DetailEntry.kt` (The UI Controllers)

* **Role:** Composable "entry" files that control the content of a navigation destination.
* **Responsibilities:**
    * They use a `LaunchedEffect` to call `mainScreenViewModel.setFabState(...)` when they become the active screen. This is how the FAB changes from the Home screen (a full menu) to the Detail screen (hidden).
    * They provide the `onClick` lambdas for the FAB menu items, which are wired to the `onEvent` lambdas from `MainScreen.kt` (e.g., `onAddCategoryClicked`).

### 4. `FabMain.kt` (The "Dumb" View)

* **Role:** A reusable, expressive FAB menu component.
* **Responsibilities:**
    * Receives `fabState: FabState?` and renders it.
    * Manages its **own internal `isExpanded` state**.
    * The main `+` button's `onFABClick` **only** toggles this internal `isExpanded` state. It **does not** call any external `onClick` lambda. This is the change that fixed the race condition.
    * The *menu items* (e.g., "Add Category") *do* call the `onClick` lambdas provided to them in the `FabAction` data class.

---

## Summary: State Holder vs. Event Bus

The old `FAB.md` described an "Event Bus" (`SharedFlow`) where a click immediately fired an event, causing a race condition.

This **new, working architecture** is a "State Holder" (`StateFlow`). A click on the `+` button only changes local state. A click on a menu item sends an event to the *single source of truth* (`MainScreenViewModel`), which then publishes a single, coherent `UiState` update for `MainScreen` to consume. This is a more robust, predictable, and bug-free pattern.