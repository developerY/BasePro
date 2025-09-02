# Design Document: Integrating Google Health Connect into Bike App

## 1. Introduction

**Objective:**
Integrate Google Health Connect into our Modern Android Development (MAD) bike application to record
bike rides (exercise sessions) and associated metrics (distance, calories, GPS track, speed, heart
rate) into the user’s Health Connect database. This enables any Health Connect–aware client (e.g.,
Google Fit) to display completed rides with Peloton‑style dashboards.

**Scope:**

- Core SDK integration
- Dependency injection via Hilt
- HealthConnectClient + permissions management
- Data modeling for sessions & series
- ViewModel & Use‑Case design
- UI container composable for permission flow
- End‑to‑end data flow
- Testing & error handling

## 2. Architecture Overview

```
┌───────────────────┐      ┌────────────────────┐
│   Bike UI Route   │      │ Health UI Route    │
│ (Composable)      │      │ (Permission prompt)│
└────────┬──────────┘      └───────┬────────────┘
         │                         │
         │ injects                 │ injects
         ▼                         ▼
┌───────────────────┐      ┌────────────────────┐
│  BikeViewModel    │      │  HealthViewModel   │
│  (ride logic)     │      │ (permissions +     │
└────────┬──────────┘      │  write methods)    │
         │                 └─────────┬──────────┘
         │ calls                     │ calls
         ▼                           ▼
┌────────────────────────────────────────────────────────┐
│                SaveRideUseCase                         │
│  • snapshot RideTracker                                │
│  • persist to RoomDB                                   │
│  • ensure Health permissions                           │
│  • write session + summary + series to Health Connect  │
└────────────────────────────────────────────────────────┘
```  

## 3. Module Structure

```
/core
  └ healthconnect
      ├ HealthSessionManager.kt
      ├ HealthModule.kt           # Hilt bindings
      └ SaveRideUseCase.kt       # orchestrates DB + Health writes

/app
  └ features
      └ bike
          ├ BikeViewModel.kt
          ├ RideSessionUseCase.kt
          └ BikeDashboardContent.kt
      └ health
          └ HealthViewModel.kt

```  

## 4. Core Layer: HealthSessionManager & Use‑Case

- **HealthSessionManager**
    - Wraps `HealthConnectClient`
    - Exposes:
        - `requestPermissionsActivityContract()`
        - `hasAllPermissions()`
        - `availability: StateFlow<Availability>`
        - `writeBikeRideWithSeries(...)`

- **SaveRideUseCase**
    - Injects: `RideTracker`, `BikeRideRepo`, `HealthSessionManager`
    - `suspend operator fun invoke()`:
        1. `tracker.stopAndGetSession()` → session data
        2. Persist to Room via `BikeRideRepo`
        3. If `availability == SDK_AVAILABLE` and permissions granted → call
           `writeBikeRideWithSeries`

## 5. Feature Layer: ViewModels

### BikeViewModel

- Drives real‑time ride state (start, pause, stop)
- Delegates final save to `SaveRideUseCase`
- Emits `BikeUiState`

### HealthViewModel

- Manages Health Connect permissions & availability
- Exposes:
    - `uiState: HealthUiState` (Uninitialized, PermissionsRequired, Success, Error)
    - `permissionsLauncher` + `permissions` set
- On `Insert` event, forwards to `HealthSessionManager.write...`

## 6. UI Layer: Container Composable (`BikeUiRoute`)

1. **Inject** both `BikeViewModel` & `HealthViewModel`
2. **Collect** `uiState` flows and permission flags
3. **LaunchedEffect** on `HealthUiState.PermissionsRequired` → `permissionsLauncher.launch()`
4. **Render**:
    - Loading / Error / Success states
    - Pass `bikeData` + optional `healthData` into `BikeDashboardContent`

## 7. Permissions Flow

1. On first load, `HealthUiState` is `Uninitialized` → trigger `initialLoad()`
2. If `hasAllPermissions == false`, `HealthUiState.PermissionsRequired`
3. Container composable sees `PermissionsRequired` → fires `permissionsLauncher`
4. On callback, `HealthViewModel.initialLoad()` re-checks perms

## 8. Data Flow Sequence

1. User taps **Start** → `BikeEvent.StartPauseRide`
2. Sensor streams → UI updates via `BikeViewModel`
3. User taps **StopSaveRide** → `BikeEvent.StopSaveRide`
4. `BikeViewModel` calls `SaveRideUseCase`:
    - captures session
    - writes Room
    - writes Health Connect (with summary + series)
5. UI resets to “not started” state
6. Health Connect client shows ride in Google Fit

## 9. Error Handling & Testing

- **Wrap** Health writes in try/catch → emit `HealthUiState.Error`
- **Unit‑test** `SaveRideUseCase` with fake `RideTracker` & `HealthSessionManager`
- **Instrumented tests** for permission flows using Jetpack Compose Test

## 10. Future Enhancements

- Add **ElevationSeriesRecord** for altitude profile
- Support **WorkoutStatsRecord** (heart‑rate zones)
- Enable **background sync** via `FEATURE_READ_HEALTH_DATA_IN_BACKGROUND`
- Expose manual “Log Ride” screen for offline entry

---
*Prepared by the Mobile Dev Team*

