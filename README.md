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

## Project Structure

```
BasePro/
│
├── app/                     # Main application module
│   ├── src/
│   └── build.gradle.kts
│
├── data/                    # Data module (Room database, repositories)
│   ├── src/
│   └── build.gradle.kts
│
├── feature/                 # Feature modules (UI, ViewModels, etc.)
│   ├── cam/                 # Camera feature module
│   ├── settings/            # Settings feature module
│   └── build.gradle.kts
│
├── build.gradle.kts
└── settings.gradle.kts
```

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
