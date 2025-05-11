You should keep your Health–Connect logic in your HealthSessionManager and then expose it through your HealthViewModel, 
and never call the manager directly from your UI or from BikeViewModel.

# Health Connect Integration Overview

This document explains how the various pieces of the Health Connect integration in your app work together, 
from low‑level session manager through ViewModel to UI.

---

## 1. HealthSessionManager **(Data Layer)**

* **Responsibility**: Encapsulates direct calls to the Health Connect client (read/write).
* **Key functions**:

    * `checkAvailability()` / `availability: StateFlow<Int>` — tracks whether Health Connect SDK is available on the device.
    * `hasAllPermissions(permissions: Set<String>): Boolean` — checks granted Health Connect permissions.
    * `requestPermissionsActivityContract()` — returns an `ActivityResultContract` to prompt the user for Health permissions.
    * `readExerciseSessions(start, end): List<ExerciseSessionRecord>` — reads all exercise sessions.
    * `writeExerciseSession(start, end): InsertRecordsResponse` — writes a new exercise session and its underlying raw data (steps, distance, calories, heart‑rate).
    * `readAssociatedSessionData(uid): ExerciseSessionData` — aggregates metrics (duration, steps, distance, calories, heart‑rate) for a given session.
    * `deleteExerciseSession(uid)` / `deleteAllSessionData()` — remove one or all sessions and their raw records.

**How it’s used**: All Health Connect API calls are funneled through this single class.  
It returns domain records (`ExerciseSessionRecord`, `StepsRecord`, `DistanceRecord`, etc.) that the app then transforms or presents to the UI.

---

## 2. HealthViewModel **(Presentation Layer)**

* **Responsibility**: Orchestrate permission checks, session reads/writes, expose UI state to Composables.
* **State**: `private val _uiState: MutableStateFlow<HealthUiState>` which can be:

    * `Uninitialized` (app start)
    * `PermissionsRequired(message)`
    * `Loading`
    * `Success(data: List<ExerciseSessionRecord>)`
    * `Error(message)`
* **Events**: `HealthEvent` sealed interface:

    * `RequestPermissions` → kick off permission flow
    * `LoadHealthData` → read sessions from the manager
    * `Insert` → write a dummy session (for test)
    * `DeleteAll` → remove all sessions
    * `Retry` → re‑attempt permission + load

**Core methods**:

1. `initialLoad()` — called on init; checks availability, attempts to read data if permissions are already granted.
2. `checkPermissionsAndLoadData()` — verifies permissions via `HealthSessionManager`, updates `_uiState` to `PermissionsRequired` or proceeds to load.
3. `loadHealthData()` — sets `Loading`, calls manager to read sessions, then `Success` or `Error`.
4. `insertExerciseSession()` / `deleteStoredExerciseSession(uid)` — wrap manager writes/deletes in coroutine + permission guard.

**Flow**:

```
UI  --(LaunchedEffect RequestPermissions)-->  HealthViewModel.onEvent(RequestPermissions)
    -> checkPermissionsAndLoadData()
        -> if not granted: HealthUiState.PermissionsRequired
        -> if granted: loadHealthData()
            -> HealthUiState.Loading -> HealthSessionManager.readExerciseSessions -> HealthUiState.Success
```

---

## 3. Integrating into BikeViewModel / Dashboard UI

* **Goal**: Show live heart‑rate (if available) alongside ride metrics.
* **Approach**:

    1. **Inject** `HealthSessionManager` (and/or a `HealthUseCase` wrapper) into your `BikeViewModel`.
    2. Add a new `Flow<Long?>` or `StateFlow<Long?>` that periodically reads the current heart‑rate from Health Connect (e.g. `readAssociatedSessionData(uid).avgHeartRate`).
    3. Combine this heart‑rate stream into your main `combine(...)` that drives the `BikeUiState`.

**Alternatives**:

* Put all Health logic in `HealthViewModel` and expose a `Flow<Long?>` for current heart‑rate, then have `BikeDashboardContent` call `hiltViewModel<HealthViewModel>()` and display heart‑rate separately.
* Or keep everything in `BikeViewModel` if you prefer a single ViewModel per screen.

---

## 4. UI (Compose) Layer

* **BikeUiRoute**:

    * Obtains both `BikeViewModel` and `HealthViewModel` via `hiltViewModel()`.
    * Collects `bikeUiState by bikeViewModel.uiState.collectAsState()` and `healthUiState by healthViewModel.uiState.collectAsState()`.
    * Prompts for Health permissions (when `healthUiState` is `PermissionsRequired`) and for Location/GPS.
    * Once both flows are `Success`, passes:

      ```kotlin
      BikeDashboardContent(
        bikeRideInfo = bikeUiState.bikeData,
        heartRate     = healthUiState.healthData.lastOrNull()?.avgHeartRate,
        …
      )
      ```

* **BikeDashboardContent**:

    * Renders heart‑rate in its own card: shows “-- bpm” until a non‑null value flows in.

---

## 5. Summary of Data Flow

```mermaid
flowchart LR
  subgraph Data Layer
    A[HealthSessionManager] -->|read/write| HealthConnectClient
  end

  subgraph ViewModel Layer
    B[HealthViewModel] -->|calls| A
    C[BikeViewModel] -->|(optional) calls| A
  end

  subgraph UI Layer
    D[BikeUiRoute]
    D --> E[BikeDashboardContent]
    D --> F[WaitingForGpsScreen]
    D --> G[HealthPermissionDialogs]

    B -->|uiState| D
    C -->|uiState| D
  end
```

1. **Permissions**: UI asks HealthViewModel for `RequestPermissions` → HealthSessionManager → system dialog → grant → come back.
2. **Data Loading**: Once granted, HealthViewModel reads sessions and emits `HealthUiState.Success(listOfSessions)`.
3. **Live HR**: Either HealthViewModel streams average HR for the current session, or BikeViewModel reads and combines it directly.
4. **Display**: Compose picks up the latest heart‑rate (bpm) and shows it in the dashboard in real time.

---

By separating concerns—HealthSessionManager for raw API, HealthViewModel for permission & data orchestration, and Compose screens for rendering—the app remains modular, testable, and maintainable. You can extend this same pattern to steps, sleep, weight, or any other Health Connect data type.
