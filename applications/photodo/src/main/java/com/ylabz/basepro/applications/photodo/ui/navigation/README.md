# PhotoDo Adaptive Navigation Architecture

This document provides a detailed explanation of the navigation architecture implemented in the PhotoDo application, focusing on its use of the experimental **Jetpack Compose Navigation 3** library to create a seamless and adaptive user experience for all screen sizes, including foldable devices.

## 🏛️ Core Architectural Principles

The navigation system is designed around two fundamental principles:

1.  **Separation of State:** The navigation state is split into two distinct, independent parts:

    * **Top-Level Navigation State (`currentTopLevelKey`):** This tracks which main section of the app the user is in (e.g., "Home," "Tasks," or "Settings"). This state is preserved across configuration changes and process death using `rememberSaveable`.
    * **In-App Navigation History (`backStack`):** This is the actual history of screens the user has visited within a top-level section. It's managed by the `rememberNavBackStack` composable from the Navigation 3 library.

2.  **Adaptive Layouts via Scene Strategy:** The UI automatically adapts to different screen sizes (compact, medium, expanded) without requiring conditional navigation logic in the UI code. This is achieved by delegating the layout decisions to a `ListDetailSceneStrategy`.

-----

## 🧩 Key Components and How They Work

### 1\. State Management in `MainScreen.kt`

The entire navigation logic is orchestrated within the `MainScreen` composable.

```kotlin
// The actual navigation history for the NavDisplay.
val backStack = rememberNavBackStack<NavKey>(PhotoDoNavKeys.HomeFeedKey)

// The currently selected top-level tab.
var currentTopLevelKey: NavKey by rememberSaveable(stateSaver = NavKeySaver) {
    mutableStateOf(PhotoDoNavKeys.HomeFeedKey)
}
```

* `backStack`: This is the source of truth for the screens that are currently displayed. It's initialized with the `HomeFeedKey` as the starting destination. All navigation actions (pushing a new screen, going back) are performed on this object.
* `currentTopLevelKey`: This state variable is used to highlight the correct item in the `BottomBar` or `NavigationRail`. It changes *only* when the user explicitly taps on a different top-level destination.

### 2\. The `NavDisplay` Composable

The `NavDisplay` is the heart of the Navigation 3 library. It's responsible for observing the `backStack` and displaying the content for the current screen(s).

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    sceneStrategy = listDetailStrategy,
    modifier = modifier,
    entryProvider = entryProvider {
        // ... screen entries defined here ...
    }
)
```

* `backStack`: The `NavDisplay` automatically recomposes and shows the correct screen whenever this list changes.
* `sceneStrategy`: This is where the magic happens. We provide a `rememberListDetailSceneStrategy`, which understands how to display list-detail layouts.
* `entryProvider`: This is where we define all the possible screens in our app, mapping each `NavKey` to its corresponding composable content.

### 3\. The `ListDetailSceneStrategy`

This is the adaptive layout engine. When the `backStack` contains both a "list" screen and a "detail" screen, the strategy checks the device's screen size:

* **On a compact screen (e.g., a folded phone):** It will only show the last item in the back stack (the detail screen), covering the list screen.
* **On an expanded screen (e.g., an unfolded phone or a tablet):** It will display the list and detail screens side-by-side in two panes.

This is made possible by providing metadata for each screen:

```kotlin
entry<PhotoDoNavKeys.TaskListKey>(
    metadata = ListDetailSceneStrategy.listPane(
        detailPlaceholder = { /* ... */ }
    )
) { /* ... */ }

entry<PhotoDoNavKeys.TaskListDetailKey>(
    metadata = ListDetailSceneStrategy.detailPane()
) { /* ... */ }
```

The `.listPane()` and `.detailPane()` functions tag each screen, telling the `ListDetailSceneStrategy` how to treat it.

-----

## 🌊 Navigation Flows

### Top-Level Navigation (Switching Tabs)

When the user taps an item in the `BottomBar` or `NavigationRail`, the `onNavigate` lambda is called:

```kotlin
val onNavigate: (NavKey) -> Unit = { newKey ->
    if (currentTopLevelKey::class != newKey::class) {
        currentTopLevelKey = newKey
        backStack.replaceAll(newKey) // Clear history when switching tabs
    }
}
```

This logic is robust:

1.  It first checks if the user is tapping a *different* tab.
2.  If so, it updates `currentTopLevelKey` to highlight the new tab.
3.  Crucially, it calls `backStack.replaceAll(newKey)`. This **clears the entire back stack** for the old tab and replaces it with the root screen of the new tab, ensuring a clean navigation history for each section of the app.

### Drill-Down Navigation (Moving Forward)

This is the most important concept for the adaptive layout. There is **no conditional logic** based on screen size in the navigation calls. The same code works for both folded and unfolded states.

#### From Category List to Task List

When the user selects a category on the home screen:

```kotlin
PhotoDoHomeUiRoute(
    navTo = { categoryId ->
        val listKey = PhotoDoNavKeys.TaskListKey(categoryId)
        backStack.add(listKey)
    },
    // ...
)
```

We use `backStack.add(listKey)`. This pushes the `TaskListKey` onto the stack. The back stack now looks like: `[HomeFeedKey, TaskListKey]`. The `ListDetailSceneStrategy` sees two "list" panes and will show the last one, which is the desired behavior.

#### From Task List to Task Detail

When the user selects a task from the list:

```kotlin
PhotoDoListUiRoute(
    onTaskClick = { listId ->
        val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
        backStack.add(detailKey)
    },
    // ...
)
```

Again, we use `backStack.add(detailKey)`. The back stack now becomes: `[HomeFeedKey, TaskListKey, TaskListDetailKey]`.

Now, the `ListDetailSceneStrategy` sees a `listPane` (`TaskListKey`) followed by a `detailPane` (`TaskListDetailKey`) and adapts the UI accordingly:

* **Folded:** It shows only the `TaskListDetailKey` screen, full-screen.
* **Unfolded:** It shows the `TaskListKey` screen in the left pane and the `TaskListDetailKey` screen in the right pane.

This architecture creates a powerful, flexible, and maintainable navigation system that is perfectly suited for the diverse range of devices on the market today.