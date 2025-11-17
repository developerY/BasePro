# PhotoDo Adaptive Navigation (Nav3) Explained

This document details the navigation architecture for the PhotoDo application, built on `androidx.navigation3.adaptive`. It explains how a single `NavBackStack` can produce different UI layouts (e.Sg., single-pane vs. multi-pane) for compact (phones/folded) and expanded (tablets/unfolded) screens.

## The Core Concept: The "Magic" of Adaptive Navigation

The "magic" of our adaptive system comes from three key components working together:

1.  **`NavBackStack` (The State):** This is the "single source of truth." It's just a simple `MutableList<NavKey>` held in `MainScreen.kt`. Every time we "navigate," we are just adding or removing keys from this list.

2.  **`ListDetailSceneStrategy` (The Brain):** This is the policy we create in `MainScreen.kt` using `rememberListDetailSceneStrategy()`. Its job is to look at the `NavBackStack` and the screen size (compact vs. expanded) and *decide* which panes should be visible.

3.  **`NavDisplay` (The Renderer):** This is the composable in `MainScreen.kt` that physically renders the panes. It takes the `NavBackStack` and the `sceneStrategy` and asks: "Given this list of keys and this strategy, what composables should I draw on the screen?"

The key takeaway is that **clicking a list item *always* does the same thing: it calls `backStack.add(...)`**. The `ListDetailSceneStrategy` is what intercepts this change and produces a different *visual result* based on the available space.

---

## The Key Files

* **`MainScreen.kt` (The Host):** This is the main, stateful composable. It owns the `NavBackStack`, the `ListDetailSceneStrategy`, and the `NavDisplay`. It's the "root" of our navigation.
* **`PhotoDoNavGraph.kt` (The Map):** This file defines *all* possible destinations in the app. Crucially, it provides **metadata** for each screen, telling the `sceneStrategy` what *kind* of pane it is (a "list" pane or a "detail" pane).
* **`HomeEntry.kt` / `ListEntry.kt` / `DetailEntry.kt` (The Destinations):** These are the bridge files that connect the "Map" (`PhotoDoNavGraph`) to the actual feature UI (e.g., `PhotoDoHomeUiRoute`). They are responsible for passing the `backStack` down to the UI so that child composables can perform navigation.

---

## How it Works: Step-by-Step Scenarios

Let's trace the user flow in both screen modes.

### Scenario 1: Compact Screen (Phone / "Folded")

On a compact screen, the `ListDetailSceneStrategy` decides to **only show one pane at a time**, creating a traditional "forward" navigation flow.

1.  **App Start:**
    * `MainScreen.kt` initializes the stack: `backStack = [HomeFeedKey]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy says: "Show the last item."
    * **Result:** The screen displays `HomeEntry` (Column 1).

2.  **User Clicks a Category (e.g., "Shopping"):**
    * The click handler in `HomeEntry.kt` is called.
    * It performs: `backStack.add(PhotoDoNavKeys.TaskListKey(1L))`.
    * The `backStack` is now: `[HomeFeedKey, TaskListKey(1)]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy sees the screen is compact and says: "Show only the last item."
    * **Result:** The `HomeEntry` (Column 1) animates out, and the `ListEntry` (Column 2) animates in.

3.  **User Clicks a List (e.g., "Shopping List"):**
    * The click handler in `ListEntry.kt` is called.
    * It performs: `backStack.add(PhotoDoNavKeys.TaskListDetailKey("1"))`.
    * The `backStack` is now: `[HomeFeedKey, TaskListKey(1), TaskListDetailKey("1")]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy sees the screen is compact and says: "Show only the last item."
    * **Result:** The `ListEntry` (Column 2) animates out, and the `DetailEntry` (Column 3) animates in.

**Back Navigation (Compact):**
* User presses "Back."
* `NavDisplay`'s `onBack` handler calls `backStack.removeLastOrNull()`.
* The stack becomes `[HomeFeedKey, TaskListKey(1)]`.
* The `sceneStrategy` sees this and shows the last item, `ListEntry` (Column 2).

---

### Scenario 2: Expanded Screen (Tablet / "Unfolded")

On an expanded screen, the `ListDetailSceneStrategy` has room and decides to show *all* panes in the stack side-by-side.

1.  **App Start:**
    * `backStack = [HomeFeedKey]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy sees the screen is *expanded* and that `HomeFeedKey` is a `listPane` (from the metadata in `PhotoDoNavGraph.kt`).
    * **Result:** The screen displays `HomeEntry` (Column 1) in the list slot and a **placeholder** in the detail slot.

2.  **User Clicks a Category (e.g., "Shopping"):**
    * The click handler in `HomeEntry.kt` is called.
    * It performs: `backStack.add(PhotoDoNavKeys.TaskListKey(1L))`.
    * The `backStack` is now: `[HomeFeedKey, TaskListKey(1)]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy sees the screen is *expanded* and looks at the stack. It finds a `listPane` (`HomeFeedKey`) and a second `listPane` (`TaskListKey`).
    * **Result:** The `HomeEntry` (Column 1) **stays on screen**. The placeholder is replaced by `ListEntry` (Column 2). A placeholder for Column 3 appears.

3.  **User Clicks a List (e.g., "Shopping List"):**
    * The click handler in `ListEntry.kt` is called.
    * It performs: `backStack.add(PhotoDoNavKeys.TaskListDetailKey("1"))`.
    * The `backStack` is now: `[HomeFeedKey, TaskListKey(1), TaskListDetailKey("1")]`.
    * `NavDisplay` asks the `sceneStrategy` what to show.
    * The strategy sees the screen is *expanded* and looks at the full stack. It finds the root `listPane`, the second `listPane`, and the final `detailPane`.
    * **Result:** `HomeEntry` (Column 1) **stays**. `ListEntry` (Column 2) **stays**. The placeholder is replaced by `DetailEntry` (Column 3).

This is why your click in Column 2 *doesn't* switch tabs. You are correctly just adding to the `backStack`, and the `sceneStrategy` is smart enough to just show the third column.

**Back Navigation (Expanded):**
* User presses "Back."
* `NavDisplay`'s `onBack` handler calls `backStack.removeLastOrNull()`.
* The stack becomes `[HomeFeedKey, TaskListKey(1)]`.
* The `sceneStrategy` sees this and shows `HomeEntry` (Column 1) and `ListEntry` (Column 2). The `DetailEntry` (Column 3) animates away.

---

## The Two Types of Navigation in Our App

It's critical to understand the two different ways we navigate:

### 1. Drill-Down Navigation (Adding Panes)
* **What it is:** Clicking on an item in a list to see its details (Column 1 -> Column 2, or Column 2 -> Column 3).
* **How it works:** We call `backStack.add(newKey)`.
* **Where:** This logic is inside our `*Entry.kt` files (like `HomeEntry.kt` and `ListEntry.kt`), which pass the `backStack` down to the feature UI.
* **Example (from `ListEntry.kt`):**
    ```kotlin
    PhotoDoListUiRoute(
        onTaskClick = { listId ->
            // This just ADDS to the stack.
            // The sceneStrategy handles the rest.
            val detailKey = PhotoDoNavKeys.TaskListDetailKey(listId.toString())
            backStack.add(detailKey)
        },
        // ...
    )
    ```

### 2. Top-Level Navigation (Switching Tabs)
* **What it is:** Clicking a main destination on the `HomeBottomBar` or `HomeNavigationRail` (e.g., "Home", "Tasks", "Settings").
* **How it works:** We *clear* the stack and replace it. We call `backStack.replaceAll(newKey)`.
* **Where:** This logic is *only* in `MainScreen.kt`, inside the `onNavigate` lambda that is passed to `HomeBottomBar` and `HomeNavigationRail`.
* **Example (from `MainScreen.kt`):**
    ```kotlin
    val onNavigate: (NavKey) -> Unit = { navKey ->
        // ... (logic to get keyToNavigate) ...

        val isAlreadyAtRoot = backStack.firstOrNull() == keyToNavigate && backStack.size == 1

        if (!isAlreadyAtRoot) {
            currentTopLevelKey = keyToNavigate
            // This REPLACES the stack, e.g.,
            // [Home, List, Detail] -> [Settings]
            backStack.replaceAll(keyToNavigate) 
        }
    }
    ```

---

## How to Add a New Screen (The Correct Way)

This process is based on your `AddingANewScreen.md` but updated with the concepts above.

**Example: Adding a new "Archived" screen.**

1.  **Define the Key (`PhotoDoNavKeys.kt`):**
    Create a new key.
    ```kotlin
    @Serializable
    @Parcelize
    data object ArchivedKey : NavKey, BottomBarItem {
        override val title = "Archived"
        override val icon = Icons.Default.Archive
    }
    ```

2.  **Create the Feature UI:**
    Create the feature module (`features/archived`) with its `ViewModel`, `UiState`, `Event`, and `ArchivedUiRoute` composable.

3.  **Create the Bridge Entry (`ui/navigation/main/entries/`):**
    Create a new file `ArchivedEntry.kt`. This file is the bridge that connects the `NavGraph` to your feature.
    ```kotlin
    @Composable
    fun ArchivedEntry(
        modifier: Modifier = Modifier,
        setTopBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
        setFabState: (FabState?) -> Unit
    ) {
        val viewModel: ArchivedViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // Set the TopBar and FAB for this screen
        LaunchedEffect(Unit) {
            setTopBar { /* ... Your TopBar ... */ }
            setFabState(null) // Or a FAB for this screen
        }

        // Call your feature's UI
        ArchivedUiRoute(
            modifier = modifier,
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
    ```

4.  **Add to the "Map" (`PhotoDoNavGraph.kt`):**
    This is the final, most important step. Add your new key and entry to the `entryProvider`.

    ```kotlin
    @OptIn(...)
    @Composable
    fun PhotoDoNavGraph( ... ) {
        NavDisplay(
            // ...
            entryProvider = entryProvider {
                
                // ... (HomeFeedKey, TaskListKey, etc.)
                
                /**
                 * =================================================================
                 * Entry: Archived Screen (`PhotoDoNavKeys.ArchivedKey`)
                 * =================================================================
                 * This is a standard, top-level screen. It is NOT part
                 * of the list-detail flow, so it has no metadata.
                 */
                entry<PhotoDoNavKeys.ArchivedKey> {
                    ArchivedEntry(
                        modifier = Modifier,
                        setTopBar = setTopBar,
                        setFabState = setFabState
                    )
                }

                // ... (SettingsKey)
            }
        )
    }
    ```
    * **Note:** Because `ArchivedKey` has no `ListDetailSceneStrategy` metadata, the `sceneStrategy` will treat it as a "full screen" destination, which is what you want. It will cover all other panes, whether on a phone or a tablet.