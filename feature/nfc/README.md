# Feature: Near Field Communication (NFC)

## Overview

The `feature:nfc` module provides a complete, self-contained feature for interacting with Near Field
Communication (NFC) tags. It encapsulates the entire workflow, including detecting NFC hardware,
handling enabled/disabled states, and processing foreground dispatches to read from and write to NFC
tags.

This module allows any application in the `BasePro` ecosystem to easily add NFC-based features, such
as tapping a tag to launch an action, storing configuration data on a tag, or sharing small amounts
of data between devices.

## Key Components

- **`NfcUiRoute.kt`**: The main Jetpack Compose UI entry point. It acts as a router, displaying the
  appropriate screen based on the device's NFC capabilities and the current state of the NFC
  interaction.
- **`NfcViewModel.kt`**: Manages the UI state (`NfcUiState`) and handles user events (`NfcRwEvent`).
  It interacts with the `NfcRepository` to get the NFC status and process tag data.
- **`NfcRepository.kt`**: An interface defining the contract for NFC operations. It is implemented
  in the `core:data` module by `NfcRepositoryImpl`.
- **UI Screens**:
- `NfcScanScreen.kt`: The main screen that prompts the user to scan an NFC tag.
- `NfcNotSupportedScreen.kt`: Shown if the device does not have NFC hardware.
- `NfcDisabledScreen.kt`: Shown if NFC is turned off in the device settings.
- `TagScanned.kt`: Displays the data read from a successfully scanned tag.
- `NfcWriteScreen.kt`: Provides an interface for writing data to an NFC tag.

## Core Functionality

- **NFC Hardware Detection:** Checks if the device supports NFC.
- **State Management:** Detects and reacts to the NFC adapter being enabled or disabled.
- **Tag Reading:** Handles the Android `NFC_DISCOVERED` intent to read data from various types of
  NFC tags (e.g., NDEF).
- **Tag Writing:** Provides the logic to format and write NDEF messages to writable NFC tags.
- **User Guidance:** Provides clear UI feedback to the user throughout the scanning and writing
  process.

## Dependencies

- **`core:data`**: Relies on the `NfcRepositoryImpl` to handle the low-level interactions with the
  Android NFC framework.
- **`core:model`**: Uses shared data models to represent NFC tag information.
- **Android NFC Framework**: Directly uses the `android.nfc` package.
- **Jetpack Compose**: For all UI components.
- **Hilt**: For ViewModel injection.

## Usage

To integrate NFC functionality, an application would add the `NfcUiRoute` to its navigation graph.
The route manages the entire user experience, from checking permissions and hardware status to
guiding the user to scan a tag.

```kotlin
// Example of navigating to the NFC feature
navController.navigate("nfc_route")
```

Using a single-Activity approach with Hilt for DI and a ViewModel to manage NFC
state—where the Activity's `onNewIntent` passes tag data to the ViewModel, and the UI observes that
state—is an effective and modern approach for a multi-module app. This method keeps our NFC logic
centralized,
promotes a clean separation of concerns, and scales well across different modules.

Concise summary of the recommended architecture for our multi-module NFC feature:

1. **NFC Module:**
    - Contains its own **Route Composable**, **UIState/Events**, and a dedicated **NfcViewModel**.
    - The repository in this module handles all NFC processing (e.g., reading the tag via NDEF).

2. **Main Activity:**
    - Remains in the main module and is responsible for receiving NFC intents in`onNewIntent()`.
    - It forwards any NFC tag received (via `onNewIntent`) to the NFC module’s ViewModel.
    - You can do this via Hilt: either by using an **EntryPoint** to get the NFC ViewModel or by
      having our NFC route composable in the navigation graph and ensuring that the NFC intent is
      forwarded to that same instance.

3. **Integration via Navigation:**
    - Your NFC module exposes a **route composable** (e.g. `NfcReaderRoute()`) that obtains the NFC
      ViewModel with `hiltViewModel()` and drives the UI solely based on its state and events.
    - The main module’s NavGraph includes this route just like our other modules.
    - MainActivity calls `setContent { NavHost(...) }` and in `onNewIntent()`, it simply passes the
      tag to the NFC ViewModel (or via an entry point), so the NFC module is fully in charge of its
      UI logic.

This pattern keeps our NFC logic self-contained in its module while letting MainActivity do what it
must (receiving NFC intents). The ViewModel in the NFC module then drives the UI using its
UIState/Events, making your composables "dumb" and easy to test.

This approach adheres to Modern Android Architecture principles, maintains separation of concerns,
and works well in a multi-module setup.

---

Below is a **high-level explanation** of how NFC scanning typically works in Android, along with
**why you usually don’t need a separate “Start Scan” button**.
Instead, you rely on the system’s NFC **foreground dispatch** mechanism and our `Activity`
lifecycle.

---

## 1. NFC Flow in Android

1. **Activity-Level Setup**
    - In our `Activity` (usually `MainActivity`), you obtain the `NfcAdapter` and call
      `enableForegroundDispatch` in `onResume`, then `disableForegroundDispatch` in `onPause`.
    - Whenever a user taps an NFC tag while our `Activity` is in the foreground, Android delivers an
      `Intent` to our `Activity`’s `onNewIntent`.

2. **Handling `onNewIntent`**
    - In `onNewIntent(intent)`, you check if the intent action is one of the NFC actions (e.g.
      `ACTION_NDEF_DISCOVERED`).
    - If so, you retrieve the `Tag` object and pass it to our `NfcViewModel` (e.g.
      `viewModel.onNfcTagScanned(tag)`).

3. **ViewModel Updates UI**
    - The `NfcViewModel` processes the tag data (reading NDEF records, etc.) and updates the
      `uiState` accordingly (e.g. `TagScanned`, `Error`, etc.).
    - Your Composable “Scan” screen observes this state and shows the correct UI.

**Key Point**: For NFC, there is no “long-lived connection” the way you might see in BLE. NFC
scanning is ephemeral—once the user taps a tag, you read it, and you’re done.

---

## 2. Do We Need a “Start Scan” Button?

### Typical Approach: **No Button**

- **Seamless**: The user simply opens our “Scan” screen, sees “Waiting for Tag…,” and taps the NFC
  tag. The system automatically dispatches the tag intent.
- **Expected Behavior**: This is how most NFC apps work (e.g., payment apps). The user expects “tap
  and go.”

### Possible Use-Cases for a Button

- **User Workflow**: If our app requires a multi-step process (e.g., “configure something” before
  scanning), you might want an explicit button that says “Ready to Scan.”
- **Prevent Accidental Reads**: In some scenarios, you might want to disable scanning until the user
  explicitly consents to start scanning.

> However, for most apps, letting the system deliver the NFC intent while the user is on the “Scan”
> screen is simpler and more user-friendly.

---

## 3. Example Lifecycle Flow

Below is a **common pattern** using a single “Scan” screen (a Composable) and a `MainActivity` that
manages NFC foreground dispatch:

1. **User navigates to the “Scan” tab** in our multi-module Compose UI.
2. **`MainActivity.onResume`**:
   ```kotlin
   override fun onResume() {
       super.onResume()
       val pendingIntent = PendingIntent.getActivity(
           this, 0, Intent(this, javaClass).apply {
               addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
           },
           PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
       )
       nfcAdapter.enableForegroundDispatch(
           this, 
           pendingIntent, 
           arrayOf<IntentFilter>(), 
           null
       )
   }
   ```
3. **User taps an NFC tag** while the “Scan” screen is active.
4. **`MainActivity.onNewIntent(intent)`**:
   ```kotlin
   override fun onNewIntent(intent: Intent) {
       super.onNewIntent(intent)
       if (intent.action in listOf(
               NfcAdapter.ACTION_NDEF_DISCOVERED,
               NfcAdapter.ACTION_TECH_DISCOVERED,
               NfcAdapter.ACTION_TAG_DISCOVERED
           )
       ) {
           val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
           tag?.let {
               // Pass it to your NfcViewModel
               nfcViewModel.onNfcTagScanned(it)
           }
       }
   }
   ```
5. **`NfcViewModel` processes the tag**, updates `uiState` to `TagScanned(tagInfo)`.
6. **Compose “Scan” Screen** observes the `uiState` and displays “TagScannedScreen” with the tag
   info.
7. **`MainActivity.onPause`**:
   ```kotlin
   override fun onPause() {
       super.onPause()
       nfcAdapter.disableForegroundDispatch(this)
   }
   ```

---

## 4. Putting It All Together

- **Your multi-module Compose UI** has a tab for “Scan.” When the user selects it, they see a
  “Waiting for NFC Tag” message.
- **Behind the scenes**, the `Activity` calls `enableForegroundDispatch`.
- **When the user taps a tag**, `onNewIntent` fires, and you read the tag in your ViewModel.
- **The ViewModel** updates the UI state to “TagScanned,” and the “Scan” screen shows the scanned
  data.

