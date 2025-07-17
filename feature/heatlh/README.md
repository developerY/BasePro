# Feature: Health

## Overview

The `feature:health` module serves as the primary interface for interacting with health and fitness data via **Google Health Connect**. It provides a standardized way for applications within the `BasePro` ecosystem to request permissions, read, and write health data, such as exercise sessions.

This module is crucial for fitness-oriented applications like `AshBike`, allowing them to contribute to and read from the user's centralized health data store on their device.

## Key Components

-   **`HealthRoute.kt`**: The main Jetpack Compose UI entry point. It handles the overall screen layout and orchestrates the display of data fetched from Health Connect.
-   **`HealthViewModel.kt`**: Manages the UI state (`HealthUiState`) for the health screen. It is responsible for initiating data reads/writes and handling permissions via the `HealthConnectRepository`.
-   **`HealthConnectRepository.kt`**: An interface defining the contract for all interactions with Health Connect. This includes checking for availability, managing permissions, and CRUD (Create, Read, Update, Delete) operations on health data. It is implemented in the `core:data` module by `HealthConnectRepositoryImpl`.
-   **UI Components**:
    -   `SessionList.kt`: Displays a list of exercise sessions fetched from Health Connect.
    -   `SessionDetailScreen.kt`: Shows detailed metrics for a selected exercise session.
    -   `HealthActions.kt`: Provides UI buttons for common actions like reading data or generating sample data.

## Core Functionality

-   **Health Connect Availability:** Checks if the Health Connect client is installed and available on the device.
-   **Permissions Management:** Handles the Health Connect permission flow, allowing users to grant or deny access to specific data types.
    -- **Data Synchronization:** Provides the logic to read exercise sessions from Health Connect and, in the context of an app like `AshBike`, to write completed workouts *to* Health Connect.
-   **Data Display:** Offers UI components to visualize the fetched health data in a user-friendly format.

## Dependencies

-   **Health Connect SDK (`androidx.health.connect:connect-client`)**: The core library for all Health Connect interactions.
-   **`core:data`**: Relies on the `HealthConnectRepositoryImpl` from this module to perform the actual data operations.
-   **`core:model`**: Uses shared data models like `ExerciseSessionData` to represent health information consistently.
-   **Jetpack Compose**: For all UI components.
-   **Hilt**: For injecting the `HealthViewModel` and its repository dependencies.

## Usage

This module can be used as a standalone screen to view Health Connect data or as a background service to sync data. An application would typically navigate to `HealthRoute` to allow the user to manage permissions and view their data.

```kotlin
// Example of navigating to the Health Connect screen
navController.navigate("health_route")
```

Files:
Do not name  -- https://developer.android.com/reference/android/health/connect/HealthConnectManager
**HealthConnectManager** - Health Connect integration manager

We use **HealthSessionManager**
This file, `HealthSessionManager`, is a comprehensive manager class that facilitates interactions 
with the Health Connect API for reading and writing health-related data. 
Key features of the class include:
1. **Availability Checking**: It checks if Health Connect is installed, supports the device, 
and allows checking feature availability (e.g., background data read).
2. **Permission Handling**: It verifies if all required Health Connect permissions are granted and 
provides a contract to request missing permissions.
3. **Data Read/Write Operations**: This class provides various functions to read and write different 
health data types, such as weight, steps, and exercise sessions, and includes aggregating records 
(e.g., average weekly weight).
4. **Exercise Session Management**: It supports reading, writing, and aggregating data associated 
with `ExerciseSessionRecord`, and can collect additional session data like heart rate and calories burned.
5. **Changes API**: It allows obtaining and tracking data changes using Health Connect’s differential 
changes API, including token-based changes retrieval to monitor updates over time.
6. **Background Processing**: A `WorkManager` task is scheduled to handle background operations, 
such as reading step data at periodic intervals.
7. **Error Handling**: It has error handling for tokens and connectivity to manage potential issues 
that may arise when interacting with Health Connect.
[example](https://github.com/eevajonnapanula/PeriodApp/blob/main/app/src/main/java/com/eevajonna/period/data/HealthConnectManager.kt)

We do not use HealthConnectProvider (YLabz Github Nourish)
[HealthConnectProvider](health-feature/src/main/java/com/ylabz/nourish/healthfeature/data/connect/HealthConnectProvider.kt)

#### Directory Structure
If other modules need Health data
```plaintext
BasePro/
├── app/                            # Main application module
├── core/
│   ├── data/                       # Core data module (all shared data layer code)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/ylabz/basepro/core/data
│   │   │   │   │   ├── repository/               # Repositories for different features
│   │   │   │   │   │   ├── HealthRepository.kt   # Repository for health data
│   │   │   │   │   ├── service/                  # Services for accessing APIs or system features
│   │   │   │   │   │   └── HealthConnectManager.kt
│   │   │   │   │   └── di/                       # DI setup for data-related components
│   │   └── build.gradle.kts
│
└── feature/
    ├── health/                         # Health feature module (UI code only)
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── AndroidManifest.xml
    │   │   │   ├── java/com/ylabz/basepro/feature/health/ui
    │   │   │   │   ├── HealthUIRoute.kt
    │   │   │   │   ├── HealthViewModel.kt
    │   │   └── build.gradle.kts
```

We do this for now because only this module needs health data
```plaintext
feature/
└── health/
├── data/                   # Data Layer
│   ├── repository/         # Repositories for managing data access
│   │   └── HealthRepository.kt
│   ├── service/            # Service or manager classes (e.g., HealthConnectManager)
│   │   └── HealthConnectManager.kt
│   ├── model/              # Data models and DTOs specific to this feature
│   │   └── HealthDataModel.kt
│   └── datasource/         # Local or remote data sources
│       ├── LocalDataSource.kt
│       └── RemoteDataSource.kt
│
├── domain/                 # Domain Layer
│   ├── model/              # Domain models, if separate from data layer
│   │   └── HealthDomainModel.kt
│   └── usecase/            # Use cases or interactors for business logic
│       └── GetHealthDataUseCase.kt
│
├── di/                     # Dependency Injection (Hilt modules specific to health)
│   └── HealthModule.kt
│
├── presentation/           # Presentation Layer (UI components and ViewModels)
│   ├── ui/                 # Composables for UI
│   │   ├── HealthScreen.kt
│   │   └── components/
│   │       └── HealthComponent.kt
│   ├── viewmodel/          # ViewModels
│   │   └── HealthViewModel.kt
│   └── state/              # UI State and Events
│       ├── HealthUIState.kt
│       └── HealthEvent.kt
│
└── HealthFeatureNavGraph.kt # Navigation graph for health feature
```
We can move them to core if needed
### Explanation

- **core/data/service/HealthConnectManager.kt**: This manager handles the integration with Google Health APIs, making it accessible across features. Centralizing it in `core-data` allows other features to access health data if needed.
- **core/data/repository/HealthRepository.kt**: The repository abstracts data access, providing a clean API to the UI layer without exposing the underlying data sources.
- **feature/health/ui**: Only contains UI-related components for the health feature, such as `HealthUIRoute` and `HealthViewModel`.

### Benefits of This Structure

1. **Centralized Data Access**: All data-related code is located in `core-data`, making it easier to manage and reuse.
2. **Reduced Duplication**: If multiple features require access to health data, this structure ensures that they can all depend on a single `HealthConnectManager`.
3. **Clear Separation of Concerns**: UI code is isolated in feature modules, while data management logic is centralized, following Google’s guidance on a clean and modular architecture.

This structure is particularly useful for larger applications where multiple features might interact with the same data sources or APIs. It simplifies testing, enhances maintainability, and aligns closely with Google’s sample project conventions.

Docs:
[Docs](https://developer.android.com/health-and-fitness/guides/health-connect)

Code:
[Code Labs](https://developer.android.com/codelabs/health-connect)
[DI / HealthConnectManager](https://github.com/android/health-samples/tree/main)

Permissions:
[Example](https://github.com/android/health-samples/tree/main/health-connect/HealthConnectSample)
[Health Connect Sample](https://www.droidcon.com/2024/01/17/exploring-health-connect-pt-1-setting-up-permissions/)
[Example Project](https://proandroiddev.com/exploring-health-connect-pt-1-setting-up-permissions-8c7fa9869f38)
[Add this](https://developer.android.com/codelabs/health-connect#2)

We do not use 
1. HealthSessionManager -- 




