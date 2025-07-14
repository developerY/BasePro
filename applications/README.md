# AshBike: A Feature-Rich Cycling Computer

## 🚴 Overview

Welcome to AshBike, a standalone cycling computer application built on the **BasePro** framework. This application serves as a prime example of how to build a feature-rich, product-focused app by leveraging the shared modules and robust architecture provided by the BasePro project.

AshBike provides cyclists with a comprehensive toolset for tracking, analyzing, and managing their rides, from live metric dashboards to historical data analysis and health platform integration.

## ✨ Key Features

* **Live Ride Dashboard**: A real-time, state-driven UI built with Jetpack Compose that displays critical metrics like speed, distance, elevation gain, and calories burned.
* **Resilient Background Tracking**: Utilizes a `ForegroundService` to ensure accurate and uninterrupted GPS tracking, safeguarding ride data even if the app is backgrounded or the UI is destroyed.
* **Dedicated Local Database**: All ride data, including detailed location paths, is stored in a dedicated Room database, ensuring full offline functionality and data integrity.
* **Health Connect Synchronization**: Seamlessly syncs completed rides with Google Health Connect, allowing users to consolidate their fitness data across multiple platforms.
* **Historical Ride Analysis**: A master-detail interface for browsing past rides, complete with map visualizations of the route taken and detailed performance statistics.
* **Dynamic Theming**: Adapts its visual theme based on user preferences stored in a local DataStore.

## 🏛️ AshBike Architecture

The `ashbike` application is a self-contained product within the BasePro monorepo. It has its own internal feature and data modules while also consuming the shared `core` libraries.

```

applications/ashbike/
│
├── features/                   \# Internal features specific to AshBike
│   ├── main/                   \# Core ride tracking UI, ViewModel, and ForegroundService
│   ├── settings/               \# AshBike-specific settings and user profile management
│   └── trips/                  \# UI and logic for displaying historical ride data
│
├── database/                   \# AshBike's dedicated Room database
│   ├── src/main/java/com/ylabz/basepro/applications/bike/database/
│   │   ├── di/                 \# Hilt modules for providing the database and DAOs
│   │   ├── repository/         \# Repository implementation for ride data
│   │   ├── BikeRideDB.kt       \# The Room database definition
│   │   ├── BikeRideDao.kt      \# Data Access Object for ride entities
│   │   └── BikeRideEntity.kt   \# Room entity for storing ride data
│   └── build.gradle.kts
│
├── src/main/java/com/ylabz/basepro/applications/bike/
│   ├── MainActivity.kt         \# The application's main entry point
│   ├── ui/navigation/          \# Navigation graphs specific to AshBike
│   └── MyApplication.kt        \# Hilt application class
│
└── build.gradle.kts            \# Application-level build script

````

### Architectural Highlights

* **Self-Contained Features**: AshBike's primary functionalities (`main`, `settings`, `trips`) are organized into their own internal `features` modules. This keeps the application's logic isolated and focused.
* **Dedicated Data Persistence**: By having its own `database` module, AshBike ensures its data schema is decoupled from other applications in the monorepo, preventing conflicts and allowing it to evolve independently.
* **Leveraging Core Modules**: AshBike heavily relies on the `core` modules of the BasePro project. It uses `core:ui` for theming, `core:data` for sensor repositories (GPS, Compass), and `core:model` for shared data structures, demonstrating the power of the framework's code reuse strategy.

## 🚀 Getting Started with AshBike

Follow these instructions to build and run the AshBike application specifically.

### Prerequisites

* Android Studio (Iguana | 2023.2.1 or later recommended)
* Java Development Kit (JDK) 17
* Gradle 8.4

### Setup and Installation

1.  **Clone the BasePro repository**:
    ```
    git clone [https://github.com/developerY/BasePro.git](https://github.com/developerY/BasePro.git)
    cd BasePro
    ```
2.  **Open** the project in Android **Studio**.
3.  **Sync the project with Gradle**.
4.  **Build and Run AshBike**:
    * In the top toolbar of Android Studio, find the "Edit Run/Debug Configurations" dropdown.
    * Select `applications.ashbike`.
    * Connect your Android device or start an emulator.
    * Click the "Run" button (▶️) to build and deploy the AshBike app.