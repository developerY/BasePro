# BLE Feature

This module provides Bluetooth Low Energy (BLE) communication capabilities within the BasePro application.

## Overview

The BLE feature allows the application to:
- Scan for nearby BLE devices.
- Connect to and disconnect from BLE peripherals.
- Discover services and characteristics offered by connected peripherals.
- Read from and write to characteristics.
- Subscribe to characteristic notifications/indications.

## Integration

To use the BLE feature, ensure this module is included as a dependency in the relevant `build.gradle` file.

Permissions:
Ensure the following permissions are declared in your app's `AndroidManifest.xml`:
- `android.permission.BLUETOOTH`
- `android.permission.BLUETOOTH_ADMIN`
- `android.permission.ACCESS_FINE_LOCATION` (required for BLE scanning on Android 6.0 and above)
- `android.permission.BLUETOOTH_SCAN` (for Android 12 and above)
- `android.permission.BLUETOOTH_CONNECT` (for Android 12 and above)

Specific integration points, such as UI entry points or API contracts for interacting with the BLE functionality, will be detailed in further documentation or code comments within this module.

## Dependencies

This feature primarily relies on:
- Standard Android Bluetooth APIs (`android.bluetooth` and `android.bluetooth.le`).
- Potentially, coroutines or RxJava for handling asynchronous BLE operations.
- Core utility modules within the BasePro project, if any.

## Building and Testing

[Instructions on how to build and test this specific feature module, if there are any special considerations, e.g., requiring specific hardware or mock peripherals.]

## Future Enhancements

- Support for more complex BLE operations (e.g., Long Write, Reliable Write).
- Abstraction layer for easier interaction with specific device types.
- Background BLE operations.
# Feature: Bluetooth Low Energy (BLE)

## Overview

The `feature:ble` module provides a comprehensive and reusable toolkit for all Bluetooth Low Energy (BLE) interactions. It encapsulates the complex processes of scanning for nearby devices, managing connections, and discovering and interacting with GATT (Generic Attribute Profile) services and characteristics.

This module is essential for applications that need to communicate with IoT devices, fitness sensors, or any other BLE-enabled hardware.

## Key Components

-   **`BluetoothLeRoute.kt`**: The primary Jetpack Compose UI entry point for this feature. It orchestrates the display of different screens based on the current BLE state (scanning, connected, disconnected, etc.).
-   **`BluetoothLeViewModel.kt`**: Manages the UI state (`BluetoothLeUiState`) for the entire BLE feature. It handles user events (`BluetoothLeEvent`) like starting a scan or connecting to a device, and it collects data from the `BluetoothLeRepository`.
-   **`BluetoothLeRepository.kt`**: An interface that defines the contract for all BLE operations. It is implemented by `BluetoothLeRepoImpl`.
-   **`BluetoothLeRepoImpl.kt`**: The core implementation that interacts directly with the Android Bluetooth APIs (`BluetoothLeScanner`, `BluetoothGatt`, etc.). It manages the connection lifecycle and data flow.
-   **UI Components**:
    -   `BluetoothLeSuccessScreen.kt`: Displays the main screen when connected to a device, including the list of services.
    -   `GattServicesCarousel.kt` / `GattServicesList.kt`: Components for displaying the GATT services of a connected device.
    -   `GattCharTable.kt`: A detailed view that lists the characteristics for a given service.
    -   `PermissionsDenied.kt` & `PermissionsRationale.kt`: Helper screens for handling the necessary Bluetooth runtime permissions.

## Core Functionality

-   **Device Scanning:** Scans for nearby BLE devices and displays them to the user.
-   **Connection Management:** Handles connecting to and disconnecting from a selected BLE device.
-   **Service Discovery:** Discovers all GATT services and characteristics offered by a connected device.
-   **Data Interaction:** (Implicitly supported) Provides the foundation for reading from, writing to, and receiving notifications from GATT characteristics.
-   **State Management:** Manages and exposes the real-time state of the BLE connection (e.g., `ScanState`, `GattConnectionState`).

## Dependencies

-   **`core:model`**: Uses shared models like `BluetoothDeviceInfo` and `GattChar` to represent BLE data structures.
-   **`core:util`**: For logging and other utility functions.
-   **Android Bluetooth Framework**: Heavily relies on the `android.bluetooth` package.
-   **Jetpack Compose**: For all UI components.
-   **Hilt**: For injecting the `BluetoothLeRepository` and other dependencies.

## Usage

This feature is typically presented as a dedicated screen in an application. An app would navigate to the `BluetoothLeRoute`, which then manages the entire user flow for scanning, connecting, and exploring a BLE device.

```kotlin
// Example of navigating to the BLE feature screen
navController.navigate("bluetooth_le_route")

