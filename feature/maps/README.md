# Feature: Maps

## Overview

The `feature:maps` module provides a reusable and focused component for displaying interactive maps
within the application. It encapsulates the setup and management of a Google Maps instance, handling
the map lifecycle, UI settings, and the rendering of location-based data.

This module is a critical dependency for any feature that needs to visualize geographic information,
such as displaying a user's current location, showing a route, or plotting points of interest.

## Key Components

- **`MapUIRoute.kt`**: The primary Jetpack Compose entry point for displaying a map screen. It
  manages the UI state and user interactions related to the map.
- **`MapViewModel.kt`**: The ViewModel that holds the map-related state (`MapUIState`), such as the
  camera position, map properties, and any markers or polylines to be displayed. It fetches location
  data from a repository.
- **`MapContent.kt`**: The core composable that embeds the `GoogleMap` view. It is responsible for
  applying UI settings, camera position updates, and drawing overlays (like markers and polylines)
  on the map based on the state provided by the `MapViewModel`.
- **`ErrorOverlay.kt` / `LoadingScreen.kt`**: UI components to handle non-success states, such as
  when location data is being fetched or if an error occurs.

## Core Functionality

- **Map Display:** Renders an interactive Google Map.
- **Camera Control:** Manages the map's camera position, allowing it to be centered on a specific
  location with a desired zoom level.
- **Data Overlay:** Provides the capability to draw shapes on the map, including:
    - **Markers:** To indicate specific points.
    - **Polylines:** To draw paths or routes.
- **UI Customization:** Allows for the configuration of map UI elements like zoom controls and the
  compass.

## Dependencies

- **Google Maps Compose Library (`com.google.maps.android:maps-compose`)**: The core dependency for
  integrating Google Maps within Jetpack Compose.
- **`core:data`**: Depends on repositories from the data layer (e.g., `LocationRepository`) to fetch
  the geographic data that needs to be displayed.
- **Jetpack Compose**: For all UI components.
- **Hilt**: For ViewModel injection.

## Usage

To display a map, an application would navigate to the `MapUIRoute`. The `MapViewModel` for this
route would be configured to fetch the specific location data required for the use case (e.g., a
saved trip path from `AshBike` or a list of nearby places).

```kotlin
// Example of navigating to the map screen
navController.navigate("map_route")
