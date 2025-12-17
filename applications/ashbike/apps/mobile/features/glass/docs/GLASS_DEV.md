# Building for Google AI Glasses (Android XR)

## 1. Core Philosophy: "Micro-Utilities"
Unlike mobile apps designed for long sessions, Glass apps are **"Heads-Up, Hands-Free"** utilities.
* **Interaction Time:** < 10 seconds per session.
* **Navigation:** Shallow hierarchy (1-2 levels max). Deep menus are frustrating to navigate with single-temple inputs.
* **Input Model:** Discrete **Key Events** (D-Pad), NOT Touch Screen drags.

## 2. Navigation Architecture
Standard Android `NavController` or `FragmentManager` components are often too heavy or complex for the linear, state-driven nature of Glass apps.

### The Pattern: State-Based Mode Switching
Instead of a back stack, use a top-level State definition to swap "Modes".

```kotlin
enum class AppMode {
    Browsing,   // The top-level Pager (Notification <-> Dashboard <-> Settings)
    Control     // The "Stepped In" mode (Changing gears, adjusting settings)
}

```

## 3. The Input Model (Crucial)

The single biggest pitfall is handling input.

* **Reality:** The temple touchpad sends **Key Events** (`KEYCODE_DPAD_*`), not Touch Coordinates.
* **Pitfall:** Components like `HorizontalPager` or `LazyRow` wait for touch drags by default and will fail on Glass.
* **Fix:** You must disable user scroll and drive navigation via Key Events.

### Mapping Gestures to Code

| User Gesture | Android Event | Action in Code |
| --- | --- | --- |
| **Swipe Forward** | `KEYCODE_DPAD_RIGHT` | Move Focus Next / Next Page |
| **Swipe Backward** | `KEYCODE_DPAD_LEFT` | Move Focus Prev / Prev Page |
| **Tap (Center)** | `KEYCODE_DPAD_CENTER` | **"Step In"** / Select |
| **Swipe Down** | `KEYCODE_BACK` | **"Step Out"** / Exit |

## 4. The "Step-In" Interaction Pattern

Since you only have one axis of input (Left/Right), you cannot navigate pages *and* control UI elements simultaneously. You must implement a "Focus Trap."

### Phase A: Browsing Mode (The Hallway)

* **Visual:** Status bar is Gray. Dots are visible.
* **Action:** Swipes change the *Page* (Dashboard \to Settings).
* **Tap:** Enters "Interaction Mode" for the current page.

### Phase B: Interaction Mode (The Room)

* **Visual:** Status bar is Green. Border appears around content.
* **Action:** Swipes change the *Focus* (Gear [-] \to Gear [+]).
* **Tap:** Actuates the focused button.
* **Swipe Down:** Exits back to "Browsing Mode."

## 5. Implementation Recipe

### A. The Container (NavHost)

Use a `Box` that listens for Key Events to drive a `HorizontalPager` with `userScrollEnabled = false`.

```kotlin
HorizontalPager(
    state = pagerState,
    userScrollEnabled = false, // CRITICAL: Disable touch drag
    modifier = Modifier.fillMaxSize()
) { page ->
    // Content
}

```

### B. The Focus Logic

Handle the "Enter" key to toggle a boolean state `isInteracting`.

```kotlin
// In MainGlassNavigation event listener:
KeyEvent.KEYCODE_DPAD_CENTER -> {
    isInteracting = true // Lock navigation, enable button focus
    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
}

```

### C. The "Back" Logic

Intercept the system "Back" (Swipe Down) to step out, rather than kill the app.

```kotlin
BackHandler(enabled = isInteracting) {
    isInteracting = false // Unlock navigation, return to paging
}

```

## 6. Developing on Emulator vs. Hardware

The emulator does not perfectly simulate the Glass input driver.

* **To Tap:** Use `Enter` key on keyboard, or `adb shell input keyevent 23`.
* **To Swipe:** Use Arrow Keys (Left/Right).
* **To "Swipe Down":** Use `Esc` or Back button.
* **Mouse Click:** Does **NOT** work unless you explicitly add a `.clickable` modifier to your root Composable for debugging.

## 7. UI Best Practices

* **High Contrast:** Use pure Black backgrounds (`#000000`) for transparency.
* **Large Text:** Minimum readable size is often ~24sp. Headlines should be 40sp+.
* **Visual Feedback:** Always provide a visual cue for state changes (Green borders, color shifts) because users lack tactile button feel.

```

```