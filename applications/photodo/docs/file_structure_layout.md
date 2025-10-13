# PhotoDo File Structure

This document outlines the file structure of the PhotoDo application, which is built on a modern, modular Android architecture (MAD). 
The structure is designed to be scalable, maintainable, and easy to navigate.

## Root Directory

```
.
├── applications
│   ├── photodo
│   │   ├── build.gradle.kts
│   │   ├── db
│   │   ├── features
│   │   └── src
│   └── ... (other applications)
├── core
│   ├── ui
│   └── util
└── ... (other root directories)
```

* **`applications/photodo`**: This is the main module for the PhotoDo application. It contains the application's entry point, navigation graph, and feature modules.
* **`core`**: This directory contains shared modules that can be used across multiple applications.
    * **`core/ui`**: A shared module for UI components, themes, and other UI-related utilities.
    * **`core/util`**: A shared module for utility functions and helper classes.

## Application Module: `applications/photodo`

This is the heart of the PhotoDo application. It brings together all the feature modules and defines the overall architecture.

### `build.gradle.kts`

This file contains the build configuration for the PhotoDo application. It includes dependencies for feature modules, as well as libraries like 
Jetpack Compose, Compose Navigation 3, Hilt, and Room.

### `db` Module

This module is responsible for all database-related operations. It encapsulates the Room database, DAOs, and entities.

* **`PhotoDoDatabase.kt`**: Defines the Room database class.
* **`PhotoDoDao.kt`**: Contains the data access objects (DAOs) for interacting with the database.
* **`entity`**: This package contains the data entities that represent the tables in the database (e.g., `ProjectEntity`, `TaskEntity`, `PhotoEntity`).

### `features` Directory

This directory contains the feature modules of the application. Each feature is a self-contained module with its own UI, ViewModel, and business logic.

* **`home`**: The home screen of the application, which displays a grid of projects.
* **`photodolist`**: The feature that displays the list of tasks for a given project.
* **`settings`**: The settings screen of the application.

### `src/main/java` Directory

This is where the main source code for the application resides.

* **`MainActivity.kt`**: The entry point of the application.
* **`MyApplication.kt`**: The application class, used for initializing application-level components.
* **`ui/navigation/main/MainScreen.kt`**: This is the core of the navigation system. It defines the navigation graph using **Compose Navigation 3** and the `ListDetailSceneStrategy` for foldable devices.

## Feature Module Example: `features/home`

Each feature module follows a consistent structure, which makes it easy to add new features to the application.

```
features/home
├── build.gradle.kts
└── src
    └── main
        └── java
            └── com/ylabz/basepro/applications/photodo/features/home/ui
                ├── HomeScreen.kt
                ├── HomeViewModel.kt
                ├── HomeUiState.kt
                └── HomeEvent.kt
```

* **`build.gradle.kts`**: The build configuration for the `home` feature module.
* **`ui`**: This package contains all the UI-related components for the feature.
    * **`HomeScreen.kt`**: The main composable for the home screen, which displays the list of projects.
    * **`HomeViewModel.kt`**: The ViewModel for the home screen, which handles business logic and exposes the `UiState`.
    * **`HomeUiState.kt`**: A data class that represents the state of the home screen.
    * **`HomeEvent.kt`**: A sealed class that defines the events that can be sent from the UI to the ViewModel.