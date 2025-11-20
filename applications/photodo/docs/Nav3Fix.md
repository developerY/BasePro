# PhotoDo Adaptive Navigation Architecture

This document provides a detailed explanation of the navigation system in the PhotoDo application, which is built on the adaptive principles of **Navigation 3 (Nav3)** and **Material 3 Expressive**.

The core goal of this architecture is to use a **single navigation state** (the `NavBackStack`) to seamlessly render the UI in two distinct ways: a **single-pane view** on compact screens (phones) and a **multi-pane view** on expanded screens (tablets/unfolded devices).

-----

## I. The Core Components

The adaptive behavior is achieved through the interaction of three key components, primarily defined in `MainScreen.kt` and `PhotoDoNavGraph.kt`.

| Component | File / Location | Role in Navigation |
| :--- | :--- | :--- |
| **`NavBackStack<NavKey>`** | `MainScreen.kt` | **The Single Source of Truth (State).** A simple `MutableList` of `NavKey` objects (e.g., `HomeFeedKey`, `TaskListKey(1)`). All navigation is simply adding or removing keys from this list. |
| **`ListDetailSceneStrategy`** | `MainScreen.kt` | **The Decision-Maker (Logic).** Reads the screen size (`isExpandedScreen`) and the contents of the `backStack` to decide which panes (`List`, `Detail`, etc.) should be visible. |
| **`NavDisplay`** | `PhotoDoNavGraph.kt` | **The Renderer (View).** Reads the decision from the `sceneStrategy` and draws the corresponding `Entry` composables (e.g., `HomeEntry`, `ListEntry`) in the available columns. |

-----

## II. The Two Types of Navigation

Every user action that changes the screen falls into one of two categories, each requiring a specific action on the `NavBackStack`.

### 1\. Top-Level Navigation (Switching Tabs)

This occurs when the user clicks a primary icon on the `HomeBottomBar` or `HomeNavigationRail`. The intent is to discard the current context and move to a new root screen.

| Action | Stack State Before | Stack Action (in `MainScreen.kt`) | Stack State After |
| :--- | :--- | :--- | :--- |
| Click **Settings** | `[HomeFeed, TaskList, TaskDetail]` | `backStack.replaceAll(newKey)` | `[SettingsKey]` |
| Click **Tasks** | `[HomeFeed, TaskList, TaskDetail]` | `backStack.replaceAll(newKey)` | `[TaskList(1)]` |

**Key Logic (`MainScreen.kt`):**
The `onNavigate` lambda determines the `keyToNavigate` (using `uiState.lastSelectedCategoryId` if necessary) and then ensures the stack is cleared before navigating to the new destination. This is crucial for avoiding a complex back-press history.

### 2\. Drill-Down Navigation (Adding Panes)

This occurs when the user clicks a list item *within* the main flow (e.g., clicking a Category in Column 1). The intent is to advance to a detail view for the current context.

| Action | Stack State Before | Stack Action (in `*Entry.kt`) | Stack State After |
| :--- | :--- | :--- | :--- |
| Click **Category** (Col 1) | `[HomeFeed]` | `backStack.add(TaskListKey(id))` | `[HomeFeed, TaskList(id)]` |
| Click **Task List** (Col 2) | `[HomeFeed, TaskList(id)]` | `backStack.add(TaskDetailKey(id))` | `[HomeFeed, TaskList(id), TaskDetail(id)]` |

**Key Logic (`HomeEntry.kt` / `ListEntry.kt`):**
The `*Entry.kt` files are responsible for calling the simple `backStack.add(newKey)`. The complexity of rendering is delegated to the `sceneStrategy`.

-----

## III. Adaptive Display Flow

The magic of Nav3 is that the *same* `backStack.add(...)` call produces two different visual outcomes:

### A. Compact Screen (Folded Phone)

When `isExpandedScreen` is `false`, navigation works like a standard single-stack mobile app.

| Stack Contents | `sceneStrategy` Result | Visual Display |
| :--- | :--- | :--- |
| `[HomeFeed]` | Shows the List Pane (P1) | **P1** (Home)
| `[HomeFeed, TaskList(1)]` | Only shows the Detail Pane (P2) | **P2** (Task List) **(P1 is hidden)**
| `[HomeFeed, TaskList(1), Detail(1)]` | Only shows the Detail Pane (P3) | **P3** (Task Detail) **(P1, P2 are hidden)**

### B. Expanded Screen (Unfolded Tablet)

When `isExpandedScreen` is `true`, navigation uses available space to show content side-by-side.

| Stack Contents | `sceneStrategy` Result | Visual Display |
| :--- | :--- | :--- |
| `[HomeFeed]` | Shows P1 and a placeholder for P2 | **P1** (Home) **P\_holder**
| `[HomeFeed, TaskList(1)]` | Shows P1 (List), and P2 (Detail) | **P1** **P2** (Task List)
| `[HomeFeed, TaskList(1), Detail(1)]` | Shows P1, P2, and P3 | **P1** **P2** **P3** (Task Detail)

-----

## IV. Preventing Back Stack Bloat (Crucial)

To prevent the stack from growing infinitely (e.g., `[...Detail1, Detail2, Detail3]`) when clicking different items in Column 2, the `*Entry.kt` files must implement replacement logic.

**Logic for Detail Clicks (in `ListEntry.kt` / `onTaskClick`):**

```kotlin
val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId = listId.toString())

// Check if the key we want to add is ALREADY the last item.
if (backStack.lastOrNull() == detailKey) {
    // 1. Same item clicked again, do nothing.
    return
}

// Check if the last item is *any* other Detail screen.
// This is used for REPLACE logic on unfolded screens.
if (backStack.lastOrNull() is PhotoDoNavKeys.TaskListDetailKey) {
    // 2. We're on Detail screen X, clicking on Detail screen Y.
    // Remove Detail screen X first.
    backStack.removeLastOrNull() 
}

// 3. Add the new Detail Key. This correctly replaces the old Detail Key,
// preventing back stack bloat.
backStack.add(detailKey)
```