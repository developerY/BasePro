Below is an updated `NfcViewModel` that **does not** start scanning automatically. Instead, it sets the state to **Stopped** (meaning NFC is supported and enabled but not actively scanning) until the user explicitly triggers `NfcReadEvent.StartScan`. At that point, it transitions to **WaitingForTag** and begins collecting from `scannedDataFlow`.

Make sure your `NfcUiState` includes a **Stopped** state (or **Idle**, if you prefer) to represent “NFC is available but not currently scanning.”


### Key Changes

1. **Stopped State**
    - We introduced `NfcUiState.Stopped` to indicate that NFC is **supported** and **enabled**, but **not currently scanning**.
    - After checking capabilities in `init`, if NFC is good to go, we set `_uiState.value = NfcUiState.Stopped`.
    - This ensures we do **not** automatically start scanning.

2. **No Auto-Start**
    - We **removed** the logic in `init` that called `startScanning()`. Now the user must trigger scanning by dispatching `NfcReadEvent.StartScan`.

3. **Transition to `WaitingForTag`**
    - In `startScanning()`, we set `_uiState.value = NfcUiState.WaitingForTag` before collecting from the repository’s flow.
    - That visually signals to the UI: “We are now actively scanning. Tap your NFC tag.”

4. **Retry & EnableNfc**
    - On `NfcReadEvent.Retry` or `NfcReadEvent.EnableNfc`, we simply re-check capabilities. If NFC is now okay, we end up in `Stopped` state again.
    - The user can then press “Start Scan” to actually begin scanning.

With this update, your app will stay in a **stopped** state by default—showing the user that NFC is available but not scanning—until they explicitly tap the **Start Scan** button (or whichever UI event triggers `NfcReadEvent.StartScan`).