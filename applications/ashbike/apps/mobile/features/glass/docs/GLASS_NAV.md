# Glass UI Architecture & Navigation Patterns

## The "Missing" Navigation
If you are coming from mobile Android development, you will notice the official Google AI/XR Glass samples lack standard navigation components (no `NavController`, no `NavHost`, no `FragmentManager`).

**This is intentional, but confusing.** Google's design philosophy for Glass is "Micro-Utilities":
1.  **One-Shot Interactions:** Apps are designed to launch, perform one action, and close.
2.  **System-Level Routing:** Navigation is expected to happen via Voice Intents (*"Hey Google, Start a Ride"*) rather than in-app menus.
3.  **Shallow Hierarchy:** Deep navigation stacks require repeated hardware gestures (swipes) to exit, which is poor UX on wearables.

However, for a persistent application like **AshBike** (Live Dashboard ↔ Summary ↔ Settings), we require internal navigation.

## The Pattern: State-Based Mode Switching
Instead of a complex Navigation Graph, Glass apps should use **State-Based Mode Switching**. We do not "navigate" to screens; we swap the active Composable based on a high-level state variable.

### 1. Define Destinations as State
Do not define routes as URLs or Strings. Use a simple Enum to define the "Modes" of the application.

```kotlin
enum class AppMode {
    LiveDashboard, // The main active view
    RideSummary,   // Post-ride stats
    Settings       // Configuration
}

```

### 2. The "GlassNavHost"

We build a lightweight router at the top level of our Compose hierarchy. This avoids the overhead of the Jetpack Navigation library.

```kotlin
@Composable
fun AshBikeApp() {
    // The "Navigator" is simply a state variable
    var currentMode by remember { mutableStateOf(AppMode.LiveDashboard) }

    GlassScaffold {
        when (currentMode) {
            AppMode.LiveDashboard -> LiveDashboardView(
                // Pass navigation actions as simple lambdas
                onRideFinished = { currentMode = AppMode.RideSummary },
                onSettingsClicked = { currentMode = AppMode.Settings }
            )
            
            AppMode.RideSummary -> SummaryView(
                // Handle "Back" manually by resetting state
                onBack = { currentMode = AppMode.LiveDashboard },
                onExit = { /* Close Activity */ }
            )
            
            AppMode.Settings -> SettingsView(
                onBack = { currentMode = AppMode.LiveDashboard }
            )
        }
    }
}

```

## Handling Hardware Gestures (The "Back" Problem)

On Glass hardware, the "Back" action is usually triggered by a **Swipe Down** or a tap on the stem. This sends a `KEYCODE_BACK` event.

Because we aren't using a standard back stack, the system will default to closing the app instantly on a swipe. We must intercept this to provide intuitive navigation (e.g., going from Settings back to Dashboard).

### Approach A: Compose BackHandler (Preferred)

Use this inside your sub-screens (`SettingsView`, `SummaryView`).

```kotlin
@Composable
fun SettingsView(onBack: () -> Unit) {
    // Intercepts the hardware swipe/back gesture
    BackHandler(enabled = true) {
        onBack() // Execute our state change instead of closing the app
    }
    
    Column {
        Text("Settings Menu")
        // ... UI Content
    }
}

```

### Approach B: Activity Key Override (Fallback)

If the hardware driver does not propagate the swipe to Compose correctly, override it in the Activity.

```kotlin
override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        // Custom logic: Check current state and route accordingly
        // If on Home screen, allow super.onKeyUp to close app
        return true 
    }
    return super.onKeyUp(keyCode, event)
}

```

## Summary Checklist

* [ ] **Avoid Deep Stacks:** Try to keep the app to 2 levels max (Main View -> Detail View).
* [ ] **Use Enums:** Manage screens via a simple `currentScreen` state.
* [ ] **Intercept Back:** Ensure "Swipe Down" navigates up one level, rather than killing the app immediately.
* [ ] **Voice First:** Wherever possible, allow users to jump to a specific `AppMode` via voice intents on launch.