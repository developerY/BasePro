# Alarm Feature

This module provides alarm management capabilities within the BasePro application.

## Overview

The alarm feature allows users to:
- Set one-time and recurring alarms.
- Snooze and dismiss active alarms.
- View and manage a list of currently set alarms.
- Customize alarm sounds and vibration patterns (if applicable).

## Integration

To use the alarm feature, ensure this module is included as a dependency in the relevant `build.gradle` file. Specific integration points, such as UI entry points or API contracts, will be detailed in further documentation or code comments within the module.

## Dependencies

This feature primarily relies on:
- Standard Android `AlarmManager` for scheduling alarms.
- Potentially, a local database for persisting alarm details.
- Core utility modules within the BasePro project, if any.

## Building and Testing

[Instructions on how to build and test this specific feature module, if there are any special considerations.]

## Future Enhancements

- Customizable alarm tones.
- Integration with calendar events.
- Advanced snooze options.


# Feature: Alarm

## Overview

The `feature:alarm` module is responsible for all functionality related to scheduling, receiving, and managing time-based alarms within the application ecosystem. It provides a self-contained and reusable component for any feature that needs to trigger an event at a specific time, such as reminders or scheduled data fetches.

This module abstracts the complexity of interacting with Android's `AlarmManager` and provides a simple, clean interface for other parts of the application to use.

## Key Components

-   **`AlarmRoute.kt`**: The main Jetpack Compose entry point for the alarm feature's UI. It observes state from the `AlarmViewModel` and displays the current alarm status.
-   **`AlarmViewModel.kt`**: The ViewModel responsible for managing the UI state (`AlarmUiState`) and handling user events (`AlarmEvent`). It interacts with the `AlarmRepository` to schedule and cancel alarms.
-   **`AlarmRepository.kt`**: An interface defining the contract for scheduling alarms. It is implemented by `AlarmRepositoryImpl`.
-   **`AlarmRepositoryImpl.kt`**: The concrete implementation that uses Android's `AlarmManager` to set exact, time-based alarms.
-   **`AlarmReceiver.kt`**: A `BroadcastReceiver` that listens for the fired alarms from `AlarmManager`. This is the component that executes a task when an alarm goes off.

## Core Functionality

-   **Schedule Alarms:** Provides a simple method (`schedule()`) to set a precise alarm for a future time.
-   **Cancel Alarms:** Provides a corresponding method (`cancel()`) to remove a previously scheduled alarm.
-   **UI State Management:** Manages the UI state to reflect whether an alarm is currently scheduled, has been triggered, or is inactive.

## Dependencies

This module relies on the following key components from other layers:

-   **`core:model`**: Uses shared data models for consistency.
-   **Android Framework**: Directly uses `AlarmManager` and `BroadcastReceiver` from the Android SDK.
-   **Jetpack Compose**: For building the user interface.
-   **Hilt**: For dependency injection to provide the `AlarmRepository` and other necessary components.

## Usage

To use this feature, an application module would typically navigate to the `AlarmRoute` composable. The route handles the UI and the underlying logic for setting a simple, one-time alarm.

```kotlin
// Example of navigating to the alarm feature
navController.navigate("alarm_route")
