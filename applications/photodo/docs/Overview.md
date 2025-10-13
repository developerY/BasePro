# PhotoDo - Android Application

PhotoDo is a modern Android application built to demonstrate a robust, scalable, and adaptive UI architecture using the latest Jetpack libraries. The app is a simple to-do list manager where users can create categories, add task lists to those categories, and then add individual photo-based tasks to each list.

This document provides a comprehensive overview of the project's architecture, navigation system, and key components.

## Key Technologies & Libraries

This project leverages a modern tech stack to build a flexible and maintainable application:

* **UI:** 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the user interface declaratively.
* **Navigation:** [Navigation 3 (Experimental)](https://developer.android.com/jetpack/compose/navigation) for managing navigation state and creating adaptive layouts for different screen sizes.
* **Architecture:** Follows the official Android [Guide to app architecture](https://developer.android.com/topic/architecture), employing a reactive MVI (Model-View-Intent) pattern with ViewModels.
* **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for managing dependencies and decoupling components.
* **Database:** [Room](https://developer.android.com/training/data-storage/room) for local data persistence.
* **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/flow.html) for managing background tasks and handling streams of data.

-----

## Application Architecture

The application follows a standard layered architecture, separating concerns to ensure the codebase is testable, scalable, and easy to maintain.

### 1\. UI Layer (Features)

The UI layer is responsible for displaying application data and handling user interactions. It is organized into independent feature modules:

* `features/home`: The main entry screen of the app.
* `features/photodolist`: Manages the display of task lists and their detailed views.
* `features/settings`: Contains the application settings screen.

Each feature module is self-contained and follows the MVI pattern:

* **View (`Composable` functions):** Renders the UI based on the current state and sends user events to the ViewModel.
* **ViewModel (`HomeViewModel`, `PhotoDoListViewModel`, etc.):** Listens to user events, processes them (often by interacting with the data layer), and exposes a stream of `UiState` for the View to observe.
* **State (`HomeUiState`, `PhotoDoListUiState`):** A Kotlin `sealed interface` or `data class` that represents the entire state of a screen at any given moment (e.g., `Loading`, `Success`, `Error`).

### 2\. Data Layer (DB Module)

The data layer is responsible for all data handling logic.

* **Repository (`PhotoDoRepo`):** This is the single source of truth for the application's data. It abstracts the data sources (in this case, the Room database) from the rest of the app. ViewModels interact with the repository to fetch and save data.
* **Database (`PhotoDoDatabase`):** The Room database that defines the schema and provides the Data Access Objects (DAOs).
* **DAOs (`PhotoDoDao`):** Contains the methods for querying and manipulating the database tables.
* **Entities (`CategoryEntity`, `TaskListEntity`, `PhotoEntity`):** Data classes that define the structure of the database tables.

### 3\. Dependency Injection (Hilt)

Hilt is used to provide dependencies where they are needed.

* **`@HiltViewModel`:** This annotation on a `ViewModel` allows Hilt to create and manage its lifecycle.
* **`@AndroidEntryPoint`:** Used on Activities and Fragments to enable dependency injection.
* **Modules (`DatabaseModule`, `DataStoreModule`):** Hilt modules are used to tell Hilt how to provide instances of types that cannot be constructor-injected, such as interfaces or classes from external libraries (like the Room database).

-----

## The Adaptive Navigation System

The core of this application's user experience is its adaptive navigation system, built using the experimental **Navigation 3** library. This system allows the app to seamlessly transition between a single-pane layout on compact screens (like phones) and a multi-pane (list-detail) layout on larger screens (like tablets and foldables).

### Key Components

* **`MainScreen.kt`:** This is the central hub for navigation. It owns the primary navigation state and is responsible for setting up the main `Scaffold` and `NavDisplay`.

* **`NavKey` (`PhotoDoNavKeys.kt`):** Instead of using string-based routes, Navigation 3 uses type-safe `NavKey` objects. These are Kotlin objects or data classes that represent a specific destination. This provides compile-time safety and allows for passing complex data between screens.

* **`NavBackStack`:** This is the navigation history, managed within `MainScreen`. It is a mutable list of `NavKey` objects representing the screens the user has visited. Actions like `backStack.add()` and `backStack.removeLastOrNull()` are used to navigate forward and backward.

* **`NavDisplay`:** This is the main composable from the Navigation 3 library. It takes the `backStack` and an `entryProvider` and is responsible for rendering the content of the current destination.

### The `ListDetailSceneStrategy`

The magic behind the adaptive layout is the **`ListDetailSceneStrategy`**.

```kotlin
// In MainScreen.kt
val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
```

When defining navigation destinations in the `entryProvider`, you provide metadata to this strategy:

* **`ListDetailSceneStrategy.listPane()`:** This metadata marks a screen as a "list" pane.
* **`ListDetailSceneStrategy.detailPane()`:** This marks a screen as a "detail" pane.

**How it Works:**

The `ListDetailSceneStrategy` automatically analyzes the `backStack` and the screen size to decide how to display the content.

* **On a Compact Screen (Phone):**

    * If the back stack is `[HomeFeedKey]`, it shows the home screen (list).
    * If the user navigates and the back stack becomes `[HomeFeedKey, TaskListKey]`, it replaces the home screen with the task list screen (a new list).
    * If the back stack becomes `[HomeFeedKey, TaskListKey, TaskListDetailKey]`, it replaces the task list screen with the detail screen.

* **On an Expanded Screen (Tablet):**

    * If the back stack is `[HomeFeedKey]`, it shows the home screen in the first pane and a placeholder in the second.
    * If the back stack becomes `[HomeFeedKey, TaskListKey]`, it shows the home screen in the first pane, the task list in the second, and a placeholder in the third.
    * If the back stack is `[HomeFeedKey, TaskListKey, TaskListDetailKey]`, it shows all three panes: **Home (Categories) | Task List | Task Detail**.

### State Management in Navigation

A crucial aspect of this architecture is how top-level navigation state is managed in `MainScreen.kt`.

* **`currentTopLevelKey`:** This state variable tracks which main tab the user is on (Home, Tasks, or Settings). It is used to highlight the correct icon in the bottom navigation bar or navigation rail. It is only changed when the user explicitly clicks a tab icon.

* **`lastSelectedCategoryId`:** This state variable "remembers" the last category the user interacted with. This is a great example of **state hoisting**. The state is owned by `MainScreen`, and children composables are given a function (`onCategorySelected`) to call when they need to update it. This ensures that if the user navigates away and then returns to the "Tasks" tab, the app can intelligently show them the list for the category they were last viewing.