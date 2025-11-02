# PhoToDo FAB Architecture: State Hoisting with Navigation 3

This document explains the state management pattern used for the Floating Action Button (FAB) in the `photodo` application. The architecture is designed to allow individual screens (navigation entries) to define and control the app's *single* FAB, which physically lives in the top-level `MainScreen`.

This pattern is a classic example of **state hoisting**.

## 🚀 Core Concept

1.  **State Owner (`MainScreen.kt`):** The `MainScreen` composable owns the `Scaffold` and the `floatingActionButton` slot. It holds the *state* for the FAB (e.g., `var currentFabState by remember { mutableStateOf<FabStateMenu?>(null) }`).
2.  **State Controller (`HomeScreen.kt`, etc.):** Individual screens, like `HomeScreen`, *know* which FAB (if any) they need to display.
3.  **Hoisted Event (Setter):** `MainScreen` passes a "setter" function (e.g., `setFabState: (FabStateMenu?) -> Unit`) down the composable tree.
4.  **Lifecycle-Aware Control:** When a screen like `HomeScreen` appears (is composed), it uses a `DisposableEffect` to call the `setFabState` function, passing up the specific FAB configuration it needs.
5.  **Cleanup:** In the `onDispose` block of its `DisposableEffect`, the screen calls `setFabState(null)` to automatically hide the FAB when the user navigates away from it.
6.  **Action Hoisting:** The *actions* for the FAB (like `onAddCategoryClicked`) are also defined in `MainScreen` (so they can interact with the `NavBackStack`) and are passed *down* to the screens. The screens bundle these actions into their `FabStateMenu` object before hoisting it back up.

## 📁 Key Components & Data Flow

Here is the flow of state and events through the application:

### 1\. `com.ylabz.basepro.applications.photodo.ui.navigation.main.MainScreen.kt`

* **Role:** The **State Owner**.
* **Responsibilities:**
    * Creates and `remembers` the mutable state: `var currentFabState by remember { mutableStateOf<FabStateMenu?>(null) }`.
    * Defines the actual navigation actions (e.g., `onAddCategoryClicked`, `onAddListClicked`).
    * Renders the `Scaffold` and its `floatingActionButton` slot.
    * The FAB UI (e.g., `FabMain`) is rendered *based on* `currentFabState`.
    * Passes the state setter (`setFabState = { newFabState -> currentFabState = newFabState }`) and the actions down to `PhotoDoNavGraph`.

<!-- end list -->

```kotlin
// Simplified MainScreen.kt
@Composable
fun MainScreen() {
    // 1. State is owned here
    var currentFabState by remember { mutableStateOf<FabStateMenu?>(null) }
    
    // 2. Actions are defined here (where backStack is available)
    val onAddCategoryClicked = { /* backStack.add(...) */ }
    val onAddListClicked = { /* backStack.add(...) */ }
    val onAddItemClicked = { /* backStack.add(...) */ }

    Scaffold(
        floatingActionButton = {
            // 3. FAB is rendered based on the hoisted state
            if (currentFabState != null) {
                FabMain(fabState = currentFabState)
            }
        }
    ) {
        PhotoDoNavGraph(
            // ...
            // 4. The setter and actions are passed down
            setFabState = { newFabState ->
                currentFabState = newFabState
            },
            onAddCategoryClicked = onAddCategoryClicked,
            onAddListClicked = onAddListClicked,
            onAddItemClicked = onAddItemClicked
        )
    }
}
```

### 2\. `com.ylabz.basepro.applications.photodo.ui.navigation.main.PhotoDoNavGraph.kt`

* **Role:** The **Router**.
* **Responsibilities:**
    * Receives `setFabState` and the actions from `MainScreen`.
    * Renders the `NavDisplay`.
    * Passes `setFabState` and the actions down to the specific `entry` composable (like `HomeEntry`) that is currently active.

<!-- end list -->

```kotlin
// Simplified PhotoDoNavGraph.kt
@Composable
fun PhotoDoNavGraph(
    // ...
    setFabState: (FabStateMenu?) -> Unit,
    onAddCategoryClicked: () -> Unit,
    onAddListClicked: () -> Unit,
    onAddItemClicked: () -> Unit
) {
    NavDisplay(
        // ...
        entryProvider = entryProvider {
            entry<HomeKey> {
                HomeEntry(
                    // ...
                    setFabState = setFabState, // <-- Pass through
                    onAddCategoryClicked = onAddCategoryClicked,
                    onAddListClicked = onAddListClicked,
                    onAddItemClicked = onAddItemClicked
                )
            }
            entry<OtherKey> {
                // Other screens can also control the FAB
                OtherEntry(setFabState = setFabState) 
            }
        }
    )
}
```

### 3\. `com.ylabz.basepro.applications.photodo.ui.navigation.main.entries.HomeEntry.kt`

* **Role:** The **Entry Bridge**.
* **Responsibilities:**
    * Receives `setFabState` and actions from `PhotoDoNavGraph`.
    * Gets the `ViewModel` and `UiState`.
    * Passes `setFabState` and actions to the actual screen composable, `HomeScreen`.

### 4\. `com.ylabz.basepro.applications.photodo.features.home.ui.HomeScreen.kt`

* **Role:** The **State Controller**.
* **Responsibilities:**
    * Receives `setFabState` and the actions (e.g., `onAddCategoryClicked`) as parameters.
    * Uses `DisposableEffect` to manage the FAB's state for its lifecycle.

<!-- end list -->

```kotlin
// Simplified HomeScreen.kt
@Composable
fun HomeScreen(
    // ...
    setFabState: (FabStateMenu?) -> Unit,
    onAddCategoryClicked: () -> Unit,
    onAddListClicked: () -> Unit,
    onAddItemClicked: () -> Unit
) {
    // 1. Define the FAB this screen needs, bundling the received actions
    val homeScreenFab = FabStateMenu(
        // ... configure icons, etc. ...
        onAddCategory = onAddCategoryClicked,
        onAddList = onAddListClicked,
        onAddItem = onAddItemClicked
    )

    // 2. Control the MainScreen's state using the setter
    DisposableEffect(homeScreenFab) {
        // 3. SET the FAB when this screen appears
        setFabState(homeScreenFab)

        onDispose {
            // 4. CLEAR the FAB when this screen disappears
            setFabState(null)
        }
    }

    // ... Rest of HomeScreen UI ...
}
```

## 📊 Visual Flow

```plaintext
   [ MainScreen ]
 (Owns FabState)
 (Renders FAB UI)
        |
        | 1. Passes (setFabState, onAddAction) down
        v
 [ PhotoDoNavGraph ]
 (Routes to Entry)
        |
        | 2. Passes (setFabState, onAddAction) down
        v
   [ HomeEntry ]
 (Bridge to Screen)
        |
        | 3. Passes (setFabState, onAddAction) down
        v
   [ HomeScreen ]
 (Controls State)
        |
        | 4. (OnCompose):
        |    Calls setFabState(MyFabConfig)
        |
        | 5. (OnDispose):
        |    Calls setFabState(null)
        |
        ^-----------------------------
        (Hoists state up to MainScreen)
```