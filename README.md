# BasePro: A Modern Android Application Framework

**Note:** This README and the project documentation are in active **development.**

![Architecture](https://img.shields.io/badge/Architecture-Multi--Module%20Monorepo-blue)
![Language](https://img.shields.io/badge/Language-Kotlin-orange)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-green)
![DI](https://img.shields.io/badge/DI-Hilt-yellow)

Welcome to the BasePro Project! This project is a cutting-edge Android framework built with Jetpack Compose, Hilt, Room, and Navigation. It provides a modular, scalable, and maintainable architecture for building modern Android apps.

## Table of Contents

- [Features](#features)
- [Architectural Vision](#architectural-vision)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- **Jetpack Compose UI**: Modern, declarative UI framework for Android.
- **Hilt for Dependency Injection**: Simplifies dependency management across the app.
- **Room Database Integration**: Provides a robust and easy-to-use abstraction over SQLite.
- **Kotlin Coroutines & Flow**: For modern, structured concurrency and reactive data streams.
- **Jetpack Navigation**: Handles in-app navigation in a type-safe, modular way.
- **Modular Architecture**: Clean separation of concerns into `application`, `feature`, and `core` layers for enhanced scalability and reusability.
- **Gradle Version Catalog**: Centralizes dependency management via `libs.versions.toml` for consistency.
- **REST & GraphQL Support**: Integrated network layers using Retrofit and Apollo for flexible data fetching.
- **Wear OS Integration**: Includes dedicated modules for building companion Wear OS applications.

---

## Architectural Vision

The project is built upon a clean, multi-layered architecture that separates concerns into distinct, well-defined layers. This ensures that the codebase is easy to navigate, test, and scale.

- **📦 Applications Layer (`/applications`)**: Contains the final, shippable application products. Each module in this layer is a standalone app (e.g., `ashbike`, `photodo`) that assembles various feature and core modules into a cohesive user experience.
- **🧩 Features Layer (`/feature`)**: Consists of self-contained feature modules, each encapsulating a specific domain of business logic (e.g., `maps`, `ble`, `health`, `nfc`). These modules are designed to be reusable across different application targets.
- **🏛️ Core Layer (`/core`)**: Provides the foundational building blocks for the entire project. These shared libraries include common utilities, UI components, data models, and data access abstractions.

---

## Project Structure

Here's a detailed file structure for the app, following Modern Android Development practices with a clean, modularized architecture.

```plaintext
BasePro/
│
├── applications/                # Standalone, shippable application modules
│   ├── ashbike/                 # Flagship bike computer application
│   │   ├── features/
│   │   │   ├── main/
│   │   │   ├── settings/
│   │   │   └── trips/
│   │   ├── database/
│   │   └── build.gradle.kts
│   │
│   ├── home/                    # A home/launcher application
│   ├── medtime/                 # A medication reminder application
│   └── photodo/                 # A photo-based task application
│
├── feature/                     # Reusable, domain-specific feature modules
│   ├── alarm/
│   ├── ble/
│   ├── camera/
│   ├── heatlh/
│   ├── listings/
│   ├── maps/
│   ├── ml/
│   ├── nfc/
│   ├── places/
│   ├── qrscanner/
│   ├── settings/
│   └── wearos/                  # Wear OS specific applications and features
│       ├── home/
│       ├── health/
│       └── sleepwatch/
│
├── core/                        # Foundational, shared libraries
│   ├── data/                    # Repositories, Network Clients (Retrofit, Apollo), Services
│   │   ├── src/main/java/com/ylabz/basepro/core/data/
│   │   │   ├── api/
│   │   │   ├── di/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── build.gradle.kts
│   │
│   ├── database/                # Shared Room Database definitions
│   │   ├── src/main/java/com/ylabz/basepro/core/database/
│   │   │   ├── di/
│   │   │   ├── repository/
│   │   │   └── BaseProDB.kt
│   │   └── build.gradle.kts
│   │
│   ├── model/                   # Shared POKO data models
│   │   ├── src/main/java/com/ylabz/basepro/core/model/
│   │   └── build.gradle.kts
│   │
│   ├── ui/                      # Core UI components and themes
│   │   ├── src/main/java/com/ylabz/basepro/core/ui/
│   │   │   └── theme/
│   │   └── build.gradle.kts
│   │
│   └── util/                    # Common utility classes (e.g., Logging)
│       └── src/main/java/com/ylabz/basepro/core/util/Logging.kt
│
├── gradle/
│   └── libs.versions.toml       # Centralized dependency version catalog
│
├── build.gradle.kts             # Root build configuration
└── settings.gradle.kts          # Module inclusion and project settings
````

-----

## Architecture Overview

This project follows a modular, clean architecture pattern with a focus on separation of concerns:

- **UI Layer**: Built with Jetpack Compose, containing stateless composables that render the UI based on `StateFlow` objects exposed by the ViewModel.
- **ViewModel Layer**: Manages UI state and handles business logic. It processes user-initiated events and updates the UI state accordingly, following a Unidirectional Data Flow (UDF) pattern.
- **Use Case Layer**: A distinct layer of business logic that orchestrates data from various repositories. This makes the core logic reusable and independent of the ViewModel.
- **Repository Layer**: Acts as a single source of truth for data, mediating between ViewModels/Use Cases and various data sources (e.g., Room database, network APIs, device sensors).
- **Data Layer**: Contains the implementations for data sources, such as Room databases and Retrofit/Apollo network services.

-----

## Technologies Used

- **Kotlin**: The primary programming language, utilizing coroutines and Flow for asynchronous operations.
- **Jetpack Compose**: For building modern, declarative UI components.
- **Hilt**: For dependency injection, making the code more modular and testable.
- **Room**: For local database management, providing a clean API over SQLite.
- **Jetpack Navigation**: For managing in-app navigation in a type-safe and lifecycle-aware manner.
- **Retrofit**: For type-safe HTTP client for REST APIs.
- **Apollo (GraphQL)**: For type-safe, boilerplate-free GraphQL client.
- **Google Health Connect**: For reading and writing user health and fitness data.
- **Google Maps SDK**: For displaying maps and location data.

-----

## Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

- Android Studio (Iguana | 2023.2.1 or later recommended)
- Java Development Kit (JDK) 17
- Gradle 8.4

### Setup and Installation

1.  **Clone the repository**:

    ```bash
    git clone [https://github.com/developerY/BasePro.git](https://github.com/developerY/BasePro.git)
    cd BasePro
    ```

2.  **Open the project in Android Studio**:

    - Open Android Studio and select "Open an existing project."
    - Navigate to the `BasePro` directory and click "OK."

3.  **Sync the project**:

    - Let Android Studio sync the project and download the necessary dependencies defined in `gradle/libs.versions.toml`.

4.  **Build and Run**:

    - Select the desired application target from the build variants dropdown (e.g., `ashbikeDebug`).
    - Click "Run" to build and deploy the app on your Android device or emulator.

-----

## Contributing

We welcome contributions to the BasePro project\! To contribute:

1.  **Fork the repository**.
2.  **Create a new branch**: `git checkout -b feature/your-feature-name`.
3.  **Commit your changes**: `git commit -m 'Add some feature'`.
4.  **Push to the branch**: `git push origin feature/your-feature-name`.
5.  **Submit a pull request**.

-----

## License

This project is licensed under the MIT License.