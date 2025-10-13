`PhotoDoHomeUiRoute` is called by **`HomeEntry.kt`**.

### The Call Chain Explained

Here is the simple, top-down flow of how your app's UI is put together:

1.  **`PhotoDoNavGraph.kt`**: This is your main navigation router. When the app starts or the user navigates to the home screen, the graph looks for the entry matching the `HomeFeedKey`. That entry's only job is to call `HomeEntry`.

    ```kotlin
    // Inside PhotoDoNavGraph.kt
    entry<PhotoDoNavKeys.HomeFeedKey>(...) {
        // Calls the entry point for the home screen feature
        HomeEntry(...)
    }
    ```

2.  **`HomeEntry.kt`**: This composable acts as the "bridge" for the home feature. It's responsible for setting up the necessary ViewModels (`HomeViewModel`, `MainScreenViewModel`) and defining how this screen interacts with global UI elements like the FAB. After its setup is done, it calls `PhotoDoHomeUiRoute`.

    ```kotlin
    // Inside HomeEntry.kt
    @Composable
    fun HomeEntry(...) {
        // ... ViewModel setup and LaunchedEffects ...

        // This is the call you asked about
        PhotoDoHomeUiRoute(
            viewModel = homeViewModel,
            onCategorySelected = onCategorySelected,
            onNavigateToDetail = onNavigateToDetail
        )
    }
    ```

3.  **`PhotoDoHomeUiRoute.kt`**: This is the final stateful component. It connects the `HomeViewModel` to the actual UI (`HomeScreen.kt`), collecting the state and passing down the events.

This layered approach is a key part of your MAD architecture. It cleanly separates navigation logic (`PhotoDoNavGraph`), feature setup (`HomeEntry`), and state management (`PhotoDoHomeUiRoute`) from the pure UI (`HomeScreen`).