# Feature: Places

## Overview

The `feature:places` module is responsible for finding and displaying points of interest (POIs) near the user's current location. Specifically, this module is configured to search for nearby coffee shops using the Yelp Fusion API.

It serves as a reusable component for any application that needs to provide location-based recommendations or display a list of nearby businesses.

## Key Components

-   **`CoffeeShopUIRoute.kt`**: The main Jetpack Compose UI entry point for the feature. It displays a list of nearby coffee shops.
-   **`CoffeeShopViewModel.kt`**: The ViewModel that manages the UI state (`CoffeeShopUIState`) and handles events (`CoffeeShopEvent`). It uses the `YelpClient` to fetch data and the `LocationRepository` to get the user's current location.
-   **`CoffeeShopItem.kt`**: A composable that displays the information for a single coffee shop in the list, including its name, rating, and distance.
-   **`YelpClient`**: (Located in `core:data`) A Retrofit client specifically configured to make requests to the Yelp Fusion API.

## Core Functionality

-   **Location-Based Search:** Fetches the user's current latitude and longitude.
-   **API Integration:** Performs a network request to the Yelp API to search for businesses with the category "coffee".
-   **Data Display:** Renders a list of the search results, providing key information for each location.

## Dependencies

-   **`core:data`**: This module is highly dependent on the `core:data` layer for two key services:
    -   `LocationRepository`: To get the user's current location to use as the basis for the search.
    -   `YelpClient`: To execute the actual search against the Yelp API.
-   **`core:model`**: Uses the `BusinessInfo` data model to represent the data returned from the Yelp API.
-   **Retrofit & OkHttp**: For handling the network requests to the Yelp API.
-   **Jetpack Compose**: For building the user interface.
-   **Hilt**: For injecting the ViewModel and its repository/client dependencies.

## Usage

This feature can be integrated as a screen in any application. To use it, an app would navigate to the `CoffeeShopUIRoute`. The module handles the rest, from getting the user's location to displaying the final list of coffee shops.

```kotlin
// Example of navigating to the places feature
navController.navigate("places_route")
```