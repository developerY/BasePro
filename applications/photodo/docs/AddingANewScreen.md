# How to Add a New UI Screen with Navigation

This guide walks through the complete, end-to-end process for adding a new composable screen and navigating to it from an existing screen. The process follows a Unidirectional Data Flow (UDF) pattern, where UI events are sent to a ViewModel, which updates its State, and the UI reacts to that state change.

### The Data Flow at a Glance

**UI Interaction -> Event -> ViewModel -> State -> UI Reaction (Navigation)**

---

### Step 1: Create the New Screen Composable

First, create the composable for your new screen. It can be as simple as a `Box` with some `Text`.

`applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/NewScreen.kt`
```kotlin
package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NewScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "This is the new screen!")
    }
}
```

### Step 2: Define a Unique Navigation Key

Every screen in the `NavGraph` needs a unique key. Add a new `data object` to the sealed class.

`applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/PhotoDoNavKeys.kt`
```kotlin
@Serializable
sealed class PhotoDoNavKeys : NavKey {
    // ... other keys

    @Serializable
    data object NewScreenKey : PhotoDoNavKeys()
}
```

### Step 3: Trigger the Navigation from the UI

In the composable where the navigation will be initiated (e.g., from a button click), add a lambda parameter to signal the navigation intent.

`applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/components/CategoryList.kt`
```kotlin
@Composable
fun CategoryList(
    // ... other parameters
    onNavigateToNewUi: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ...
    Button(onClick = {
        Log.d("CategoryList", "Button clicked")
        onNavigateToNewUi() // Call the lambda
    }) {
        Text("Add New Category")
    }
    // ...
}
```

### Step 4: Propagate the Intent as a UI Event

The calling composable should handle this intent by sending an `Event` to the `ViewModel`.

`applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/HomeScreen.kt`
```kotlin
@Composable
fun HomeScreen(
    // ... other parameters
    onEvent: (HomeEvent) -> Unit,
) {
    // ...
    CategoryList(
        // ... other parameters
        onNavigateToNewUi = { onEvent(HomeEvent.OnNavigateToNewUi) }
    )
    // ...
}
```

### Step 5: Update the ViewModel, State, and Events

1.  **Define the Events:** Create events to represent the user's intent to navigate and a corresponding event to reset the state after navigation is complete.

    `applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/HomeEvent.kt`
    ```kotlin
    sealed interface HomeEvent {
        // ... other events
        data object OnNavigateToNewUi : HomeEvent
        data object OnNewUiNavigated : HomeEvent
    }
    ```

2.  **Update the UI State:** Add a boolean flag to your `UiState` to represent the navigation trigger.

    `applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/HomeUiState.kt`
    ```kotlin
    data class Success(
        // ... other state properties
        val navigateToNewUi: Boolean = false
    ) : HomeUiState
    ```

3.  **Handle the Events in the ViewModel:** In the `ViewModel`, update the state flag based on the events.

    `applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/HomeViewModel.kt`
    ```kotlin
    fun onEvent(event: HomeEvent) {
        when (event) {
            // ...
            HomeEvent.OnNavigateToNewUi -> {
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(navigateToNewUi = true)
                        ?: currentState
                }
            }

            HomeEvent.OnNewUiNavigated -> {
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(navigateToNewUi = false)
                        ?: currentState
                }
            }
        }
    }
    ```

### Step 6: Observe State and Execute Navigation

The UI "Route" composable observes the state. When the `navigateToNewUi` flag is `true`, it triggers the navigation and immediately sends the `OnNewUiNavigated` event to reset the flag.

`applications/photodo/features/home/src/main/java/com/ylabz/basepro/applications/photodo/features/home/ui/PhotoDoHomeUiRoute.kt`
```kotlin
@Composable
fun PhotoDoHomeUiRoute(
    // ...
    navToNewUi: () -> Unit,
    homeViewModel: HomeViewModel,
) {
    val uiState by homeViewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Success -> {
            if (state.navigateToNewUi) {
                // Use LaunchedEffect to ensure this is called only once per state change
                LaunchedEffect(Unit) {
                    navToNewUi()
                    homeViewModel.onEvent(HomeEvent.OnNewUiNavigated)
                }
            } else {
                HomeScreen(
                    // ...
                )
            }
            // ...
        }
        // ...
    }
}
```

### Step 7: Connect the Navigation Graph

The final step is to plumb the navigation call through the graph entries and define the new screen in the graph itself.

1.  **Pass the Navigation Lambda Down:** The `HomeEntry` is the bridge between the `NavGraph` and the `PhotoDoHomeUiRoute`. Pass the navigation lambda through it.

    `applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/entries/HomeEntry.kt`
    ```kotlin
    @Composable
    fun HomeEntry(
        // ...
        navToNewUi: () -> Unit
    ) {
        //...
        PhotoDoHomeUiRoute(
            // ...
            navToNewUi = navToNewUi,
            // ...
        )
    }
    ```

2.  **Define the Screen and the Action in the NavGraph:** In the main navigation graph, provide the implementation for the navigation lambda and register the new screen.

    `applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/navigation/main/PhotoDoNavGraph.kt`
    ```kotlin
    @Composable
    fun PhotoDoNavGraph(
        // ...
        backStack: NavBackStack<NavKey>,
        // ...
    ) {
        NavDisplay(
            // ...
            entryProvider = entryProvider {
                // ...

                // 1. Provide the navigation implementation
                entry<PhotoDoNavKeys.HomeFeedKey>(/*...*/) {
                    HomeEntry(
                        // ...
                        navToNewUi = { backStack.add(PhotoDoNavKeys.NewScreenKey) }
                    )
                }

                // ... other entries

                // 2. Register the new screen with its key
                entry<PhotoDoNavKeys.NewScreenKey> {
                    NewScreen(modifier = modifier)
                }
            }
        )
    }
    ```
