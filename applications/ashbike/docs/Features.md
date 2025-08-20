# AshBike App: Technical Overview

A modern, feature-rich cycling application built with a cutting-edge Android technology stack, offering a seamless experience across mobile and wearable devices.

---

## 🚴 Key Features

* **Live Ride Tracking**: Real-time monitoring of cycling sessions with key metrics.
* **Historical Data**: A detailed log of past rides, allowing users to review and analyze their performance.
* **Health Connect Sync**: Integrates with **Google Health Connect** to share ride data for a holistic health overview.
* **Wear OS Companion App**: A full-featured smartwatch app with **Tiles** and **Complications** for at-a-glance information.
* **Camera & ML Integration**:
    * **Text Recognition**: Uses **ML Kit** to read text from the camera.
    * **QR Code Scanner**: For easy pairing, sharing, or joining events.
* **Advanced Mapping**: Utilizes **Google Maps** for precise location tracking and in-app map displays.
* **Performance & Stability**:
    * **Firebase Crashlytics & Analytics**: For robust crash reporting and usage insights.
    * **Background Processing**: Reliable background operations managed by **WorkManager**.

---

## 🛠️ Core Technologies & Architecture

* **UI (User Interface)**:
    * **Jetpack Compose**: The entire UI is built with this modern, declarative toolkit.
    * **Navigation Compose**: Manages all in-app and screen navigation.
    * **Wear Compose & Horologist**: Powers the companion smartwatch application.

* **Architecture & State Management**:
    * **MVVM (Model-View-ViewModel)**: A clean and scalable architectural pattern.
    * **ViewModel & StateFlow**: Manages UI state in a lifecycle-aware and reactive way.
    * **Hilt**: Handles dependency injection throughout the application for a modular and testable codebase.

* **Data & Networking**:
    * **Room**: For robust local database storage.
    * **DataStore**: For modern, safe storage of key-value data.
    * **Retrofit & Apollo Client**: Flexible networking layer supporting both **RESTful** and **GraphQL** APIs.
    * **Kotlinx Serialization**: Efficiently handles JSON data.

* **Performance Optimization**:
    * **Baseline Profiles**: Pre-compiles the app for significantly faster startup times.
    * **Coil**: A lightweight, modern library for efficient image loading.
    * **Coroutines**: Manages all asynchronous operations to ensure a smooth and responsive UI.