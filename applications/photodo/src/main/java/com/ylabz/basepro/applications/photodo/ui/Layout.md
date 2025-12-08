Based on the code provided in `MainScreen.kt`, here is a conceptual preview of how your UI is laid out for both **Compact (Phone)** and **Expanded (Tablet/Desktop)** modes.

### 1\. Compact Layout (Phone / Portrait)

In this mode, the app uses a standard bottom navigation flow. Notably, your **Debug Stack UI** is attached to the bottom bar area.

```text
+-------------------------------------------------+
|  [TopAppBar]                                    |
|  < Back   Title (e.g., "PhotoDo")       [Edit]  |
+-------------------------------------------------+
|                                                 |
|                                                 |
|                                                 |
|               [Main Content Area]               |
|        (NavGraph: Home / List / Detail)         |
|                                                 |
|                                                 |
|                                         [FAB]   |
|                                       ( + )     |
+-------------------------------------------------+
|  [HomeBottomBar] (Home | Tasks | Settings)      |
+-------------------------------------------------+
|  [DebugStackUi] (Black Bar: Stack/Category info)| <--- Placed below/inside BottomBar
+-------------------------------------------------+
```

**Key Layout Details:**

* **Navigation:** Controlled by `HomeBottomBar` at the bottom.
* **Debug UI:** The `DebugStackUi` is nested inside the `bottomBar` slot's `Column`. It will appear attached to the bottom of the navigation icons.
* **Content:** The `appContent` fills the space between the top bar and the bottom navigation assembly.

-----

### 2\. Expanded Layout (Tablet / Landscape)

In this mode, the app shifts to a side navigation rail. The **Debug Stack UI** moves to the top of the content area.

```text
+---+---------------------------------------------+
| N |  [TopAppBar]                                |
| A |  Title (e.g., "Task Lists")         [Trash] |
| V |---------------------------------------------|
|   |  [DebugStackUi] (Collapsible Debug Info)    | <--- Placed at the top of content
| R |---------------------------------------------|
| A |                                             |
| I |                                             |
| L |                                             |
|   |            [Main Content Area]              |
|   |      (Adaptive List/Detail Strategy)        |
| H |      Pane 1: Categories | Pane 2: Tasks     |
| O |                                             |
| M |                                             |
| E |                                     [FAB]   |
|   |                                     ( + )   |
+---+---------------------------------------------+
```

**Key Layout Details:**

* **Navigation:** `HomeNavigationRail` is fixed on the **Left**.
* **Debug UI:** The `DebugStackUi` is the first element in the content `Column`, placing it directly **under the Top App Bar**.
* **Content:** The `appContent` uses `Modifier.fillMaxSize()` to take up all remaining space below the debug bar.
* **Double Padding Fix:** Your recent fix ensures `appContent` doesn't have double padding, so it should sit flush against the Debug UI.

### 3\. Logic Highlights

* **Smart Tab Selection:** The tab selection (`currentTopLevelKey`) automatically updates based on the backstack state (`backStack.lastOrNull()`). If you are viewing a "Task List", the "Tasks" tab will highlight even if you navigated there from a different flow.
* **Fold Awareness:** The `LaunchedEffect(isExpandedScreen)` block detects if the device is folded (switching from Expanded to Compact). If this happens, it resets the navigation stack to the Root (Home) to prevent UI glitches where a Detail pane might get stuck without context.
* **Dynamic FAB:** The FAB (`FabMain`) changes its icon and action based on the current destination in the `NavGraph` (e.g., "Add Category" on Home vs "Add Item" on Detail), as passed up via `setFabState`.