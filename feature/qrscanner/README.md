# Feature: QR Code Scanner

## Overview

The `feature:qrscanner` module provides a dedicated, reusable component for scanning and decoding QR
codes using the device's camera. It is built to be a simple, single-purpose utility that can be
easily integrated into any application that requires QR code input.

This module leverages the **Google Code Scanner** library, which provides a reliable, on-device
scanning solution that does not require Google Play Services.

## Key Components

- **`QRCodeScannerScreen.kt`**: This is the single, primary component of the module. It is a Jetpack
  Compose composable that handles:
    - Requesting camera permissions.
    - Initializing the `GmsBarcodeScanner`.
    - Launching the scanner UI.
    - Handling the success or failure result of the scan.

## Core Functionality

- **Launch Scanner:** Provides a button to initiate the QR code scanning process.
- **Camera Permission Handling:** Manages the runtime permission request for the camera.
- **Decode QR Codes:** Uses the underlying ML Kit Barcode Scanning API (via the Google Code Scanner
  library) to automatically detect and decode a QR code from the camera feed.
- **Result Handling:** Captures the raw value (the text or URL) encoded in the QR code and displays
  it to the user.

## Dependencies

- **Google Code Scanner (`com.google.android.gms:play-services-code-scanner`)**: The core library
  that provides the complete scanning UI and decoding logic.
- **Jetpack Compose**: For building the simple UI of the screen.
- **Accompanist Permissions**: To simplify the camera permission request flow within Compose.

## Usage

This feature is designed to be used as a standalone screen. An application would navigate to the
`QRCodeScannerScreen` composable. The screen handles the entire scanning flow. The scanned result
can be passed back to the calling screen upon successful decoding.

```kotlin
// In a NavGraphBuilder
composable("qr_scanner_route") {
    QRCodeScannerScreen(
        onScanResult = { result ->
            // Do something with the scanned result string
            navController.popBackStack()
        }
    )
}

// To launch the scanner
navController.navigate("qr_scanner_route")
```