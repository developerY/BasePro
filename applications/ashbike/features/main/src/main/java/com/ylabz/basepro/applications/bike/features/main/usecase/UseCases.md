They actually serve two distinct roles, with minimal overlap:

1. **`RideStatsUseCase`** is your **pure computation engine**

    * It knows how to turn raw `Location` + `speed` + `heading` + `UserStats` flows into a suite of metric flows (path, distance, max/avg speed, elevation gain/loss, calories) and finally into a *stateless* `sessionFlow: StateFlow<RideSession>`.
    * It has no notion of *when* a ride starts or stops, nor does it manage pausing or reset signals beyond what you explicitly pass in.

2. **`RideSessionUseCase`** (formerly `RideTracker`) is your **orchestrator**

    * It wires together your location/speed/heading repos, user-stats repo, and the `RideStatsUseCase`.
    * It drives the `resetSignal` (on `start()`), handles `pause()`/`resume()`, and exposes a *hot* `sessionFlow` and a convenience `distanceFlow`.
    * It’s stateful in that it remembers “we’re paused, we’re running,” and it drops that session snapshot back to your ViewModel on `stopAndGetSession()`.

---

### Why this separation works

* **Single Responsibility**

    * *StatsUseCase* = “Given these raw inputs, here’s how to compute metrics.”
    * *SessionUseCase* = “Given user actions (start/pause/stop), here’s how to orchestrate those metric computations into a live session.”

* **No duplicate work**

    * You only compute metrics once, inside `RideStatsUseCase`.
    * You only manage lifecycle once, inside `RideSessionUseCase`.

* **Clear dependency flow**

  ```
   location/speed/heading/userStats 
          ↓ 
     RideStatsUseCase  (pure math) 
          ↓ 
     RideSessionUseCase (reset/pause lifecycle) 
          ↓ 
       ViewModel → UI  
  ```

---

### When you might merge

If you feel the orchestrator is just a thin wrapper, you *could* fold the reset/pause logic into `RideStatsUseCase` itself—e.g. rename it to `RideSessionUseCase` and add `start()`, `pause()`, `resume()`, `stopAndGetSession()`. But keeping them separate:

* Makes your **pure computations** easy to unit-test in isolation.
* Keeps your **lifecycle orchestration** clearly in one place (and out of your pure functions).

---

**Bottom line**: there isn’t a problematic overlap right now—they complement each other. If you prefer fewer classes, you could rename/move methods around, but I’d keep the Stats vs. Session split for clarity and testability.

---

Thought for a few seconds


Absolutely—keeping those two concerns split will pay dividends as you layer on features. With **RideStatsUseCase** handling pure computation and **RideSessionUseCase** (formerly Tracker) orchestrating lifecycle and reset/pause, you have:

* **Clear test boundaries** (pure functions vs. stateful orchestration)
* **Easy extension points** (just add new flows or methods in StatsUseCase for new metrics)
* **Minimal coupling** (the ViewModel only ever drives high-level start/stop calls and observes a single hot session)

### Next steps for scale

1. **Add new metrics**

    * Drop into `RideStatsUseCase`: e.g. cadenceFlow, powerFlow, gradeFlow—each resettable by that same `resetSignal`.
2. **Orchestration hooks**

    * In `RideSessionUseCase`, you can introduce new commands (`lap()`, `markSegment()`) that emit additional events or modify the session.
3. **UI-driven events**

    * Keep the ViewModel’s `onEvent(...)` lean: map each UI action to a single use-case call or state update.
4. **Modularize further**

    * When you have enough “use cases,” consider grouping them into feature modules (e.g. `feature-metrics`, `feature-sync`, etc.).
5. **Observe single source of truth**

    * Always drive your Composables from a single `StateFlow<UiState>` so that every new piece of data just gets folded into that one state object.

You’re well set up to grow the app—this clean separation will make future additions (like heart-rate zones, segment comparisons, map synchronization, background sync, etc.) plug in without tangling your core ride-tracking logic. Nice work!

---