# BasePro Project

Welcome to the BasePro Project! This project is an Android application built with Jetpack Compose, Hilt, Room, and Navigation. It provides a modular, scalable, and maintainable architecture for building modern Android apps.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Architecture Overview](#architecture-overview)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Article 
[Zero to Compose: Room, Hilt, & Nav](https://medium.com/@zoewave/zero-to-compose-room-hilt-nav-da1bc0b5ab8c)

## Features

- **Jetpack Compose UI / Material 3**: Modern, declarative UI framework for Android.
- **Hilt for Dependency Injection**: Simplifies dependency management across the app.
- **Room Database Integration**: Provides a robust and easy-to-use abstraction over SQLite.
- **Kotlin Coroutines & Kotlin Flow**: For concurrency & reactive approach
- **Navigation Component**: Handles in-app navigation in a modular way.
- **Stateless Composables**: Ensures that UI components are side-effect-free and easy to test.
- **Modular Architecture**: Encourages clean code separation and reusability.
- **TOML Version Catalog**: for dependency management
- API keys stored in app’s BuildConfig


Here's a full file structure for your app based on what we've discussed so far, following **Modern Android Development** practices with a clean, modularized architecture. This structure includes all core layers and feature modules, showing directory organization, files, and naming conventions for clarity.

```plaintext
BasePro/
│
├── app/                                   # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/ylabz/basepro
│   │   │   │   ├── MainActivity.kt       # Entry point of the app, initializes NavController
│   │   │   └── res/                      # Resources for the app module (themes, layouts, etc.)
│   └── build.gradle.kts                  # Build configuration for app module
│
├── core/
│   ├── data/                              # Data layer (e.g., Room database, repositories)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/data
│   │   │   │   │   ├── converter/        # Converters for data transformations
│   │   │   │   │   │   └── Converters.kt
│   │   │   │   │   ├── di/               # Dependency injection for the data layer
│   │   │   │   │   │   └── DatabaseModule.kt
│   │   │   │   │   ├── mapper/           # Mappers for converting between entities and models
│   │   │   │   │   │   └── TwinCamMapper.kt
│   │   │   │   │   ├── model/            # Data models specific to the data layer (Room entities, etc.)
│   │   │   │   │   │   ├── BaseProEntity.kt
│   │   │   │   │   │   ├── BaseProDB.kt
│   │   │   │   │   ├── repository/       # Data repositories
│   │   │   │   │   │   ├── BaseProRepo.kt
│   │   │   │   │   │   └── BaseProRepoImpl.kt
│   │   └── build.gradle.kts              # Build configuration for core-data module
│   │
│   ├── network/                           # Network layer (e.g., Apollo, Retrofit clients, GraphQL)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── graphql/              # GraphQL queries and schema files
│   │   │   │   │   ├── SearchQuery.graphql
│   │   │   │   │   └── schema.json
│   │   │   │   ├── java/com/ylabz/basepro/core/network
│   │   │   │   │   ├── api/              # API interfaces and DTOs
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   ├── BusinessInfo.kt
│   │   │   │   │   │   │   └── Coordinates.kt
│   │   │   │   │   │   ├── interfaces/   # Interfaces for API access
│   │   │   │   │   │   │   └── YelpAPI.kt
│   │   │   │   │   │   ├── mappers/      # Mappers to convert API responses to domain models
│   │   │   │   │   │   │   └── BusinessMappers.kt
│   │   │   │   │   ├── apollo/           # Apollo client setup and GraphQL configuration
│   │   │   │   │   │   └── Apollo.kt
│   │   │   │   │   ├── client/           # Network clients
│   │   │   │   │   │   ├── MapsClient.kt
│   │   │   │   │   │   └── YelpClient.kt
│   │   │   │   │   ├── di/               # Dependency injection for the network layer
│   │   │   │   │   │   └── NetworkModule.kt
│   │   │   │   │   ├── repository/       # Repository for network data handling
│   │   │   │   │   │   ├── DrivingPtsRepository.kt
│   │   │   │   │   │   └── DrivingPtsRepImpl.kt
│   │   └── build.gradle.kts              # Build configuration for core-network module
│   │
│   ├── model/                             # Shared data models across modules
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/model
│   │   │   │   │   ├── BusinessInfo.kt   # Domain model (used across features)
│   │   │   │   │   └── Coordinates.kt
│   │   └── build.gradle.kts              # Build configuration for core-model module
│   │
│   ├── ui/                                # Core UI components and themes
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/ui
│   │   │   │   │   ├── components/       # Shared UI components
│   │   │   │   │   └── theme/            # App themes and styling
│   │   └── build.gradle.kts              # Build configuration for core-ui module
│
├── domain/                                # Domain layer (optional) for business logic
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/ylabz/basepro/core/domain
│   │   │   │   └── ExampleUseCase.kt     # Optional use cases for business logic
│   └── build.gradle.kts                  # Build configuration for domain module
│
├── feature/                               # Feature-specific modules
│   ├── camera/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/camera/ui
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── SimpleCameraCaptureWithImagePreview.kt
│   │   │   │   │   ├── CameraUIRoute.kt
│   │   │   │   │   ├── CamViewModel.kt
│   │   │   │   │   ├── CamUIState.kt
│   │   │   │   │   └── CamEvent.kt
│   │   └── build.gradle.kts              # Build configuration for camera feature
│   │
│   ├── coffeeshop/                        # Feature module for Yelp Coffee Shop search
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/coffeeshop/ui
│   │   │   │   │   ├── CoffeeShopUIRoute.kt
│   │   │   │   │   ├── CoffeeShopViewModel.kt
│   │   │   │   │   ├── CoffeeShopUIState.kt
│   │   │   │   │   └── CoffeeShopEvent.kt
│   │   └── build.gradle.kts              # Build configuration for coffee shop feature
│
│   ├── home/                              # Home feature module
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/home/ui
│   │   │   │   │   └── HomeUIRoute.kt
│   │   └── build.gradle.kts              # Build configuration for home feature
│
│   └── maps/                              # Maps feature module
│       ├── src/
│       │   ├── main/
│       │   │   ├── AndroidManifest.xml
│       │   │   ├── java/com/ylabz/basepro/feature/maps/ui
│       │   │   │   └── MapViewModel.kt
│      

 │   └── build.gradle.kts          # Build configuration for maps feature
│
├── build-logic/                           # Unified Gradle build logic
│   ├── conventions/                       # Custom Gradle convention plugins for shared configurations
│   └── build.gradle.kts
│
├── settings.gradle.kts                    # Settings for module dependency management
└── build.gradle.kts                       # Root build configuration for the project
```

### Highlights

- **Core Modules**:
    - **core-data** for data management (Room, repositories).
    - **core-network** for network operations (Apollo GraphQL, REST clients).
    - **core-model** for shared domain models.
    - **core-ui** for reusable UI components and themes.
    - **domain** as an optional module for business logic use cases.

- **Feature Modules**:
    - Modularized by feature (e.g., `camera`, `coffeeshop`, `home`, `maps`) to isolate functionality.
    - Each feature has its own ViewModel, UI state, and event handling, making it self-contained.

- **build-logic**:
    - Centralized build logic with Gradle convention plugins to standardize configurations across modules.

This structure is a robust, scalable, and modular approach, adhering to Google’s recommended **Modern Android Development** best practices!

## Project Structure

BasePro/
│
├── app/                                   # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/ylabz/basepro
│   │   │   │   ├── MainActivity.kt       # Entry point of the app, initializes NavController
│   │   │   └── res/                      # Resources for the app module (themes, layouts, etc.)
│   └── build.gradle.kts                  # Build configuration for app module
│
├── core/
│   ├── data/                              # Data layer (e.g., Room database, repositories)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/data
│   │   │   │   │   ├── converter/        # Converters for data transformations
│   │   │   │   │   │   └── Converters.kt
│   │   │   │   │   ├── di/               # Dependency injection for the data layer
│   │   │   │   │   │   └── DatabaseModule.kt
│   │   │   │   │   ├── mapper/           # Mappers for converting between entities and models
│   │   │   │   │   │   └── TwinCamMapper.kt
│   │   │   │   │   ├── model/            # Data models specific to the data layer (Room entities, etc.)
│   │   │   │   │   │   ├── BaseProEntity.kt
│   │   │   │   │   │   ├── BaseProDB.kt
│   │   │   │   │   ├── repository/       # Data repositories
│   │   │   │   │   │   ├── BaseProRepo.kt
│   │   │   │   │   │   └── BaseProRepoImpl.kt
│   │   └── build.gradle.kts              # Build configuration for core-data module
│   │
│   ├── network/                           # Network layer (e.g., Apollo, Retrofit clients, GraphQL)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── graphql/              # GraphQL queries and schema files
│   │   │   │   │   ├── SearchQuery.graphql
│   │   │   │   │   └── schema.json
│   │   │   │   ├── java/com/ylabz/basepro/core/network
│   │   │   │   │   ├── api/              # API interfaces and DTOs
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   ├── BusinessInfo.kt
│   │   │   │   │   │   │   └── Coordinates.kt
│   │   │   │   │   │   ├── interfaces/   # Interfaces for API access
│   │   │   │   │   │   │   └── YelpAPI.kt
│   │   │   │   │   │   ├── mappers/      # Mappers to convert API responses to domain models
│   │   │   │   │   │   │   └── BusinessMappers.kt
│   │   │   │   │   ├── apollo/           # Apollo client setup and GraphQL configuration
│   │   │   │   │   │   └── Apollo.kt
│   │   │   │   │   ├── client/           # Network clients
│   │   │   │   │   │   ├── MapsClient.kt
│   │   │   │   │   │   └── YelpClient.kt
│   │   │   │   │   ├── di/               # Dependency injection for the network layer
│   │   │   │   │   │   └── NetworkModule.kt
│   │   │   │   │   ├── repository/       # Repository for network data handling
│   │   │   │   │   │   ├── DrivingPtsRepository.kt
│   │   │   │   │   │   └── DrivingPtsRepImpl.kt
│   │   └── build.gradle.kts              # Build configuration for core-network module
│   │
│   ├── model/                             # (placeholder) Shared data models across modules
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/model
│   │   │   │   │   ├── BusinessInfo.kt   # Domain model (used across features)
│   │   │   │   │   └── Coordinates.kt
│   │   └── build.gradle.kts              # Build configuration for core-model module
│   │
│   ├── ui/                                # Core UI components and themes
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/core/ui
│   │   │   │   │   ├── components/       # Shared UI components
│   │   │   │   │   └── theme/            # App themes and styling
│   │   └── build.gradle.kts              # Build configuration for core-ui module
│
├── domain/                                # Domain layer (optional) for business logic
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/ylabz/basepro/core/domain
│   │   │   │   └── ExampleUseCase.kt     # Optional use cases for business logic
│   └── build.gradle.kts                  # Build configuration for domain module
│
├── feature/                               # Feature-specific modules
│   ├── camera/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/camera/ui
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── SimpleCameraCaptureWithImagePreview.kt
│   │   │   │   │   ├── CameraUIRoute.kt
│   │   │   │   │   ├── CamViewModel.kt
│   │   │   │   │   ├── CamUIState.kt
│   │   │   │   │   └── CamEvent.kt
│   │   └── build.gradle.kts              # Build configuration for camera feature
│   │
│   ├── palces/                        # Feature module for Yelp Coffee Shop search
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/coffeeshop/ui
│   │   │   │   │   ├── CoffeeShopUIRoute.kt
│   │   │   │   │   ├── CoffeeShopViewModel.kt
│   │   │   │   │   ├── CoffeeShopUIState.kt
│   │   │   │   │   └── CoffeeShopEvent.kt
│   │   └── build.gradle.kts              # Build configuration for coffee shop feature
│
│   ├── home/                              # Home feature module
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/ylabz/basepro/feature/home/ui
│   │   │   │   │   └── HomeUIRoute.kt
│   │   └── build.gradle.kts              # Build configuration for home feature
│
│   └── maps/                              # Maps feature module
│       ├── src/
│       │   ├── main/
│       │   │   ├── AndroidManifest.xml
│       │   │   ├── java/com/ylabz/basepro/feature/maps/ui
│       │   │   │   └── MapViewModel.kt
│       │   └── build.gradle.kts          # Build configuration for maps feature
│
├── build-logic/                           # Unified Gradle build logic
│   ├── conventions/                       # Custom Gradle convention plugins for shared configurations
│   └── build.gradle.kts
│
├── settings.gradle.kts                    # Settings for module dependency management
└── build.gradle.kts                       # Root build configuration for the project


**Directory Comments and Explanations:**

- **BasePro/**: The root directory of your Android project, containing all modules and configuration files.

- **app/**: The main application module that ties together all features and shared modules.
   - **src/**: Contains the source code for the app module, including the `MainActivity`, application classes, and resources.
   - **build.gradle.kts**: The Gradle build script for the app module, specifying dependencies and build configurations.

- **shared/**: Modules that provide shared functionality across the app.
   - **shared-data/**: Manages data storage and retrieval.
      - **di/**: Contains Dependency Injection configurations (e.g., Hilt modules) for providing data-related dependencies.
      - **repo/**: Houses repository interfaces and implementations for data access.
      - **src/**: Source code for entities, DAOs, data sources, and data mappers.
      - **build.gradle.kts**: Build script for the shared-data module.
   - **shared-network/**: Handles network communication and API interactions.
      - **di/**: DI setup for network layer dependencies like Retrofit or OkHttp clients.
      - **src/**: Source code for API interfaces, network clients, and networking utilities.
      - **build.gradle.kts**: Build script for the shared-network module.
   - **shared-ui/**: Contains reusable UI components and theming resources.
      - **src/**: Source code for common composables, custom views, themes, and styles.
      - **build.gradle.kts**: Build script for the shared-ui module.

- **domain/**: Encapsulates the core business logic and domain models.
   - **src/**: Source code for use cases (business logic), domain models, and interfaces that define the application's core functionality.
   - **build.gradle.kts**: Build script for the domain module.

- **features/**: Contains feature-specific modules, each representing a distinct part of the app's functionality.
   - **cam/**: Camera feature module.
      - **di/**: DI setup for providing camera feature dependencies like ViewModels and use cases.
      - **repo/**: Repositories specific to the camera feature (if any).
      - **src/**: Source code for UI components, ViewModels, and camera logic.
      - **build.gradle.kts**: Build script for the cam module.
   - **settings/**: Settings feature module.
      - **di/**: DI setup for settings feature dependencies.
      - **src/**: Source code for settings UI, ViewModels, and preferences handling.
      - **build.gradle.kts**: Build script for the settings module.

- **features-maps/**: Modules related to mapping functionalities, separated by platform.
   - **gmap/**: Google Maps feature module.
      - **di/**: DI setup for Google Maps dependencies.
      - **src/**: Source code for Google Maps UI components, ViewModels, and map interactions.
      - **build.gradle.kts**: Build script for the gmap module.
   - **amap/**: Apple Maps feature module.
      - **di/**: DI setup for Apple Maps dependencies.
      - **src/**: Source code for Apple Maps UI components and logic.
      - **build.gradle.kts**: Build script for the amap module.

- **features-health/**: Modules related to health data functionalities.
   - **ghealth/**: Google Health feature module.
      - **di/**: DI setup for Google Health dependencies.
      - **src/**: Source code for health data handling, UI components, and ViewModels.
      - **build.gradle.kts**: Build script for the ghealth module.
   - **ahealth/**: Apple Health feature module.
      - **di/**: DI setup for Apple Health dependencies.
      - **src/**: Source code for health data handling and UI components.
      - **build.gradle.kts**: Build script for the ahealth module.

- **build.gradle.kts**: The root-level Gradle build script that applies global plugins and configurations for all modules.

- **settings.gradle.kts**: The Gradle settings file that includes all the modules in the project, enabling them to be recognized by the build system.

**Additional Notes:**

- **`di/` Directories**: Each `di` directory contains Dependency Injection setups specific to that module. This includes Hilt modules or other DI frameworks' configurations to provide module-specific dependencies.

- **`repo/` Directories**: Repositories are responsible for handling data operations. They abstract data sources (e.g., network, database) and provide a clean API for the rest of the app.

- **Modularity**: This structure promotes high cohesion within modules and low coupling between them, making it easier to maintain, test, and scale your application.

- **Reusability**: Shared modules like `shared-ui`, `shared-data`, and `shared-network` contain code that can be reused across different features, reducing duplication.

- **Feature Modules**: Keeping features in separate modules allows teams to work independently on different parts of the app and can improve build times.



## Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

- Android Studio (version 2024.1 or later recommended)
- Java Development Kit (JDK) 21
- Gradle 8.7

### Setup and Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/basepro.git
   cd basepro
   ```

2. **Open the project in Android Studio**:
    - Open Android Studio and select "Open an existing project."
    - Navigate to the `basepro` directory and click "OK."

3. **Sync the project**:
    - Let Android Studio sync the project and download the necessary dependencies.

4. **Build and Run**:
    - Connect your Android device or start an emulator.
    - Click "Run" to build and deploy the app on your device.

## Architecture Overview

This project follows a modular architecture pattern with a focus on separation of concerns:

- **UI Layer**: Built with Jetpack Compose, containing stateless composables that render the UI based on the `UIState` provided by the ViewModel.
- **ViewModel Layer**: Manages UI state and handles business logic. It processes `UIEvent`s and updates `UIState`.
- **Repository Layer**: Acts as a mediator between the ViewModel and the data source (Room database).
- **Room Database**: Handles local data persistence, providing an abstraction over SQLite.

## Technologies Used

- **Jetpack Compose**: For building modern, declarative UI components.
- **Hilt**: For dependency injection, making the code more modular and testable.
- **Room**: For local database management, providing a clean API over SQLite.
- **Navigation Component**: For managing in-app navigation.
- **Kotlin**: The programming language used for all the code.

## Usage

### Main Features

- **Camera Feature**: A UI for displaying and managing camera feed data.
- **Settings Feature**: Allows users to adjust app settings and preferences.
- **Navigation**: The app supports easy navigation between different sections using the bottom navigation bar.

### Deleting All Entries

A button in the UI allows users to delete all entries in the Room database. This feature is integrated into the `Settings` screen.

### Modular Navigation

The app uses a modular navigation structure, with the `RootNavGraph` and `MainNavGraph` handling the primary navigation logic.

## Contributing

We welcome contributions to the BasePro project! To contribute:

1. **Fork the repository**.
2. **Create a new branch**: `git checkout -b feature/your-feature-name`.
3. **Commit your changes**: `git commit -m 'Add some feature'`.
4. **Push to the branch**: `git push origin feature/your-feature-name`.
5. **Submit a pull request**.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
