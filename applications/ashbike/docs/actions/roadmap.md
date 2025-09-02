# Project Roadmap - [Your Project Name]

**Last Updated:** [Date]

This document outlines the high-level plan and key milestones for the [Your Project Name] project.

Features and Tasks:
<font color="green">Green Project Name</font>
**Just get speed working when walking on device**

## Overall Vision

[Briefly describe the overall goal and vision for this project.]

## Key Themes/Phases

This project will be developed in the following key themes or phases:

### Phase 1: [Phase 1 Name] - [Start Date] to [End Date]

* **Goal:** [Describe the main goal of this phase]
* **Key Deliverables:**
    * [Deliverable 1]
    * [Deliverable 2]
    * [Deliverable 3]
* **Key Milestones:**
    * [Milestone 1 - Date]
    * [Milestone 2 - Date]

### Phase 2: [Phase 2 Name] - [Start Date] to [End Date]

* **Goal:** [Describe the main goal of this phase]
* **Key Deliverables:**
    * [Deliverable 1]
    * [Deliverable 2]
* **Key Milestones:**
    * [Milestone 1 - Date]
    * [Milestone 2 - Date]

### Phase 3: [Phase 3 Name] - [Start Date] to [End Date] (Optional)

* **Goal:** [Describe the main goal of this phase]
* **Key Deliverables:**
    * [Deliverable 1]
* **Key Milestones:**
    * [Milestone 1 - Date]

## Future Considerations (Optional)

* [Ideas for future phases or enhancements]

## Notes

* [Any important notes or assumptions about the roadmap]

---

### Flow

Exactly—here’s the high-level flow you want, all driven by your ViewModel and
`BigBikeProgressIndicator`:

1. **Start**

- User taps **Start/Pause** → ViewModel handles `BikeEvent.StartPauseRide`  
  • Calls your tracker/use-case to reset & begin collecting GPS fixes  
  • Updates `uiState.bikeData.rideState = Riding` and zeroes `currentTripDistance`
- **UI** immediately shows a centered bike (since no `totalDistance` yet)

2. **Set & Track**

- User taps the **bike icon** (only while `rideState == Riding`)  
  • Opens a dialog to enter a “planned” distance → ViewModel handles `SetTotalDistance(km)`  
  • `uiState.bikeData.totalTripDistance` goes from `null` → some Float
- **BigBikeProgressIndicator** sees `totalDistance != null` and:  
  • Draws the track + 0/mid/full markers  
  • Begins animating the bike along as `currentTripDistance` updates from your `distanceFlow`

3. **Stop**

- User taps **Stop/Save** → ViewModel handles `BikeEvent.StopSaveRide`  
  • Calls your tracker/use-case to stop location updates and grab the final session snapshot  
  • Persists the ride into Room  
  • Updates `uiState.bikeData.rideState = NotStarted` and clears out both
    - `totalTripDistance = null` (so the track disappears)
    - `currentTripDistance = 0f` (so the bike recenters)

Because your composable is purely a function of `(currentDistance, totalDistance)`, whenever the
ViewModel resets those back to `(0f, null)` the UI automatically shows exactly one centered gray
bike again—no extra logic needed in the Composable itself.

That gives you:

- **Start** → reset & start tracking
- **Tap bike** → set planned total & show track
- **Live ride** → bike moves along track
- **Stop** → save, reset, and return to initial state

---

Yes—every bit of ride orchestration and state mutation lives in your ViewModel (or in use-cases it
calls). Your Composables are completely “dumb,” simply rendering whatever state the ViewModel
exposes.

### How it breaks down

1. **ViewModel owns all ride state**

- A `MutableStateFlow<BikeUiState>` holds your `BikeRideInfo` (with fields like `rideState`,
  `currentTripDistance`, `totalTripDistance`).
- `onEvent(BikeEvent…)` in the ViewModel updates that `StateFlow`—starting/stopping the tracker,
  resetting distances, persisting to Room, clearing totals, etc.

2. **UI observes but never mutates state**

- In your screen Composable you do something like:
  ```kotlin
  val uiState by viewModel.uiState.collectAsState()
  val bike    = (uiState as? BikeUiState.Success)?.bikeData
  ```
- You then pass `bike.currentTripDistance` and `bike.totalTripDistance` into
  `BigBikeProgressIndicator`.
- When the user taps Start/Stop or the bike icon, you simply call `viewModel.onEvent(...)`.

3. **No local UI state for ride data**

- Don’t store `manualTotal` or `currentDistance` in the Composable with `remember { … }`.
- Let the ViewModel be the single source of truth—you remove that local state and drive everything
  from `bikeRideInfo`.

---

### Example wiring

@Composable
fun RideScreen(viewModel: BikeViewModel = hiltViewModel()) {
BikePathWithControls( )
}

class BikeViewModel @Inject constructor(
) : ViewModel() {
private fun startRide() {
private fun setPlannedDistance(km: Float) {
private fun stopAndSaveRide() {

With this setup:

- **Start** → `startRide()` resets and begins tracking
- **Tap bike** → `SetTotalDistance` populates `totalTripDistance` so the track appears
- **Live ride** → `distanceFlow` updates `currentTripDistance` in the ViewModel, recomposing the UI
- **Stop** → `stopAndSaveRide()` persists and then resets all ride-related fields back to their
  initial state

That way, your Composables never hold ride logic—everything stays in the ViewModel (or in use-cases
it calls).

---

How it works --
How it behaves
Tap 1 (NotStarted → Riding)
• Calls startRide() → emits resetSignal → distance resets to 0 and begins.
• UI turns the bike icon green.

Tap 2 (Riding → Paused)
• Calls pauseRide() → stops collecting location but does NOT reset.
• UI turns the bike icon gray but leaves the track & current distance intact.

Tap 3 (Paused → Riding)
• Calls resumeRide() → continues collecting from where you left off.
• UI turns the bike green again and the distance resumes ticking.

Stop
• Finalizes and persists, then resets everything back to NotStarted.

This pin-wheel of Start → Pause → Resume → Stop all lives in one StartPauseRide handler, and your
indicator will animate exactly the way you expect.

Bike Icon Interactions

* Start/Stop in your ViewModel only drives the live currentTripDistance and rideState.

* Planned distance lives purely in the Composable (plannedDistance), never reset by Start/Stop.

* BigBikeProgressIndicator draws a centered bike until you set a plan, then shows a track and
  animates the icon along it.

* If your actual distance exceeds the planned, it just stays at the end.

* You can change the plan at any time—the UI immediately repositions the bike.

---

Below are the minimal edits to your existing BikeViewModel so that:

Distance only accumulates while riding (resets to 0 when stopped)

Speed always updates, even when stopped

Start emits the resetTrigger to zero your distanceFlow

Stop clears both currentTripDistance and totalTripDistance in your UI state



---

What this achieves
On Start (StartPauseRide):
• resetTrigger.emit(Unit) wipes out the old scan, so your distanceFlow immediately re-emits 0f.
• rideState flips to Riding.

sensorDataFlow:
• Always updates currentSpeed.
• Updates currentTripDistance only if rideState == Riding, otherwise forces it back to 0f.

On Stop (StopSaveRide):
• rideState flips to Ended.
• You clear both currentTripDistance and any totalTripDistance plan.
• Tracker is stopped and the session is persisted.

With those two small changes—emitting the reset on start, and gating the distance emission on your
state—your UI will:

Show zero distance when stopped

Allow Start → distance begins at 0 again

Stop resets everything to the centered gray bike
---
How it behaves
Tap “▶️” (NotStarted → Riding): calls tracker.start(), emits a reset, and begins location polling.

Tap “⏸️” (Riding → Paused): calls tracker.pauseRide(), stopping further distance updates but keeping
your total.

Tap “▶️” again (Paused → Riding): calls tracker.resumeRide(), resuming distance accumulation where
you left off.

Tap “■” at any time: stops completely, saves, and resets back to NotStarted.

With those three small methods and the state-machine in your ViewModel, Pause/Resume is fully
supported!
