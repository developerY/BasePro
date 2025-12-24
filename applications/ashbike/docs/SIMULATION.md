# AshBike Connection Simulation Guide

To facilitate development without physical hardware, AshBike includes a built-in **Simulation Mode**. This allows developers to manually toggle the connection state of the E-Bike and AR Glasses directly from the mobile dashboard.

## 1. Bike Connection (Simulated)

The **"Tap to Connect Bike"** button allows you to simulate a Bluetooth connection to the bike hardware. This toggles the UI between "Standard Ride Mode" and "E-Bike Integration Mode."

### How it Works

* **State Source:** `BikeRepository` maintains a `isSimulatingConnection` flag.
* **Data Interception:** When active, the repository intercepts real GPS/Service data and injects fake e-bike telemetry (Battery 100%, Motor Power 250W).
* **UI Behavior:**
* **Disconnected:** Button is **Green**. Glass HUD shows **Ride Stats Stack** (Distance, Duration, Calories).
* **Connected:** Button is **Red**. Glass HUD shows **Gear Shifter** & Battery info.



### Code Reference

The simulation logic resides in `BikeRepositoryImpl.kt`:

```kotlin
override suspend fun toggleSimulatedConnection() {
    isSimulatingConnection = !isSimulatingConnection
    // Updates _rideInfo with fake isBikeConnected = true/false
}

```

---

## 2. AR Glass Connection (Simulated)

Since the Android Emulator cannot physically detect external USB-C displays (smart glasses), we implemented a manual override to force the "Glass Connected" state.

### The 3-State Logic

The Glass button on the dashboard cycles through three distinct states:

| State | Visual | Description | Action |
| --- | --- | --- | --- |
| **1. No Glasses** | **Gray** | Hardware not detected. | **Tap to FORCE SIMULATION.** (Enables State 2) |
| **2. Ready** | **Purple** | Hardware found (or simulated). | **Tap to START PROJECTION.** (Launches Activity) |
| **3. Active** | **Green** | XR App is running. | **Tap to STOP PROJECTION.** (Kills Activity) |

### Debugging on Emulator

If the button is Gray on the emulator:

1. **Tap it anyway.** We forced `enabled = true` on the "No Glasses" state to aid debugging.
2. This triggers `toggleSimulatedGlassConnection()` in the repository.
3. The button will turn **Purple**, allowing you to launch the Glass UI.

### Code Reference

The override logic combines real hardware detection with our manual flag:

```kotlin
// BikeRepositoryImpl.kt
override val isGlassConnected = combine(
    ProjectedContext.isProjectedDeviceConnected(...), // Real Hardware
    _isGlassConnectionSimulated // Manual Toggle
) { real, sim -> real || sim }

```

---

## Summary of Flows

| Action | Mobile UI Result | Glass HUD Result |
| --- | --- | --- |
| **Tap Bike Button** | Toggles Green/Red | Swaps **Stats Panel** ↔ **Gear Shifter** |
| **Tap Glass Button (Gray)** | Turns Purple | None (Simulates Plug-in) |
| **Tap Glass Button (Purple)** | Turns Green | Launches XR Activity (HUD appears) |