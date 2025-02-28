**README(s) are in active development**
```markdown
# BasePro Android App

BasePro is a modular, scalable Android app that follows **Modern Android Development (MAD)** best practices. It includes features such as Coffee Shop Finder using Yelp's API and Camera Capture, designed with a layered architecture that includes modularized core and feature modules.

## Table of Contents
- [Project Structure](#project-structure)
- [Features](#features)
- [Core Modules](#core-modules)
- [Feature Modules](#feature-modules)
- [Architecture](#architecture)
- [Technologies and Libraries](#technologies-and-libraries)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)

---

## Project Structure

The project is organized into core and feature modules for better maintainability and scalability.

```plaintext
BasePro/
├── app/                            # Main application module
├── core/
│   ├── data/                       # Data module (Room database, repositories)
│   ├── network/                    # Network module (Apollo, Retrofit)
│   ├── model/                      # Shared data models across the app
│   ├── ui/                         # Shared UI components and themes
│   └── domain/                     # Optional domain layer for business logic
├── feature/                        # Feature modules for specific functionality
│   ├── camera/                     # Camera capture feature
│   ├── coffeeshop/                 # Coffee Shop finder using Yelp API
│   ├── home/                       # Home screen feature
│   └── maps/                       # Maps feature for location-based services
└── build-logic/                    # Centralized build configuration for shared settings
```

---

## Features

### Coffee Shop Finder
- Displays a list of coffee shops near San Francisco using Yelp's API.
- Manages UI states: Loading, Success, and Error.
- Composables:
    - **CoffeeShopUIRoute**: Main screen for displaying coffee shops.
    - **CoffeeShopItem**: Individual item displaying each coffee shop’s details.

### Camera Capture
- Simple camera capture feature with preview functionality.
- Implements `CameraUIRoute` with customizable UI components.

---

## Core Modules

### core-data
- Contains Room database setup, repositories, mappers, and data converters.
- Example components:
    - `BaseProDB`: Main Room database.
    - `BaseProRepo`: Repository pattern for data management.

### core-network
- Manages network communication with Apollo and REST clients.
- Example components:
    - `YelpClient`: Queries Yelp API for business information.
    - `MapsClient`: Queries Google Maps API for directions.

### core-model
- Holds reusable data models shared across the app, including:
    - `BusinessInfo`: Represents business details in the Coffee Shop feature.
    - `Coordinates`: Represents latitude and longitude coordinates.

### core-ui
- Provides shared UI components and themes for consistent styling.
- Components such as custom buttons, text styles, and reusable layouts.

### domain (Optional)
- Contains business logic, such as use cases that orchestrate data from different sources.
- Example: `GetCoffeeShopsUseCase` could handle business logic for fetching and filtering coffee shops.

---

## Feature Modules

### Camera Module
- Handles camera capture functionality.
- Components:
    - `SimpleCameraCaptureWithImagePreview`: Composable for capturing and previewing images.
    - `CameraUIRoute`, `CamViewModel`: Main route and ViewModel.

### CoffeeShop Module
- Displays nearby coffee shops using Yelp's API.
- Components:
    - **`CoffeeShopUIRoute`**: Displays the list of coffee shops and manages navigation.
    - **`CoffeeShopViewModel`**: Manages state and interacts with `YelpClient`.
    - **UI States**: `Loading`, `Success`, `Error`.

### Home Module
- Entry point of the app, providing navigation to other features.

### Maps Module
- Handles location-based services such as finding directions using Google Maps API.

---

## Architecture

This project follows **Modern Android Development (MAD)** best practices:
- **Layered Architecture**: Separates data, domain (optional), and presentation layers for clear separation of concerns.
- **Unidirectional Data Flow (UDF)**: Each screen observes a single source of truth for its state, making the UI predictable and easier to debug.
- **Modularization**: Core functionalities are isolated in modules, making the app more scalable and maintainable.

### Key Patterns and Practices

- **MVVM (Model-View-ViewModel)**: Used for managing UI state in composables.
- **Repository Pattern**: Data access is managed through repositories, providing a single source of truth for each feature.
- **Dependency Injection with Hilt**: Each module has its own DI setup to inject dependencies efficiently.

---

## Technologies and Libraries

- **Kotlin**: The primary language for the project.
- **Jetpack Compose**: Used for building the UI declaratively.
- **Hilt**: Dependency injection framework for managing dependencies.
- **Apollo GraphQL**: Used for consuming GraphQL APIs, particularly Yelp's.
- **Room Database**: Used for local data storage in the `core-data` module.
- **Retrofit**: REST client for network requests.
- **Material 3**: For a consistent, modern UI design.

---

## Setup and Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/BasePro.git
   cd BasePro
   ```

2. **Open the Project** in Android Studio (Arctic Fox or newer is recommended).

3. **Configure API Keys**:
    - Add your Yelp API and Google Maps API keys to the appropriate configuration files (e.g., `local.properties` or a dedicated configuration class).

4. **Build the Project**:
    - Sync Gradle and run a build to ensure all dependencies are set up.

5. **Run the App**:
    - Choose a device/emulator and run the app.

---

## Usage

- **CoffeeShop Feature**: Navigate to the Coffee Shop screen from the Home screen to find coffee shops nearby.
- **Camera Feature**: Access the camera feature to capture and preview images.
- **Maps Feature**: Use the Maps module for location-based functionality.

---

## Contribution

Contributions are welcome! Please open an issue or submit a pull request for improvements, bug fixes, or new features.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact

For questions or support, please contact us at developer@ylabz.com.



### NOTES::
### **Comparison: `LaunchedEffect` vs. State Variable**

| **Feature**                     | **LaunchedEffect**                                     | **State Variable**                              |
|----------------------------------|-------------------------------------------------------|------------------------------------------------|
| **Tied to Lifecycle**            | Yes, automatically cancels when Composable is removed. | No, state persists unless explicitly reset.    |
| **Side Effects**                 | Ideal for triggering effects (e.g., ViewModel events). | Not ideal for side effects; UI-only changes.   |
| **Reactivity**                   | Reacts to external state changes like permission updates. | Reacts to internal state changes in the Composable. |
| **Complexity**                   | Requires coroutines and lifecycle awareness.           | Simpler, no coroutine management needed.       |
| **Example Use Case**             | Fetching data, observing external changes.             | Managing toggle states, simple UI logic.       |

---

Add a module 