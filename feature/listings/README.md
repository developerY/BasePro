# Feature: Listings

## Overview

The `feature:listings` module provides a generic and reusable implementation of the common
Master-Detail UI pattern. It is designed to display a list of items and allow the user to select an
item to view its details on a separate screen.

This module is intended as a foundational template that can be adapted for various use cases, such
as displaying a list of articles, products, or any other collection of data.

## Key Components

- **`ListUIRoute.kt`**: The main Jetpack Compose entry point for the feature. It displays the master
  list of items.
- **`ListViewModel.kt`**: The ViewModel for the master list screen. It is responsible for fetching
  the list of items and managing the `ListUIState`.
- **`ListCompose.kt`**: The composable function that renders the list of items. It handles user
  clicks and triggers navigation to the detail view.
- **`DetailsRoute.kt`**: The composable function that represents the detail screen. It is
  responsible for displaying the information for a single, selected item.

## Core Functionality

- **Master View:** Displays a scrollable list of items.
- **Detail View:** Displays the full details of a single item selected from the master list.
- **Navigation:** Handles the navigation flow from the master list to the detail view, typically
  passing the ID of the selected item as an argument.

## Dependencies

- **Jetpack Compose**: For building the user interface.
- **Jetpack Navigation Compose**: For handling the navigation between the list and detail screens.
- **`core:model`**: This module would typically depend on a specific data model from `core:model` to
  represent the items in the list.
- **Hilt**: For ViewModel injection.

## Usage

This feature module provides a complete, self-contained screen flow. To use it, an application would
include its navigation graph and navigate to the starting route (`list_route`). The data source for
the `ListViewModel` would need to be configured to provide the specific type of data to be
displayed.

```kotlin
// In a NavGraphBuilder

composable("list_route") {
    ListUIRoute(
        onItemClick = { itemId ->
            navController.navigate("detail_route/$itemId")
        }
    )
}

composable("detail_route/{itemId}") { backStackEntry ->
    val itemId = backStackEntry.arguments?.getString("itemId")
    DetailsRoute(itemId = itemId)
}
```