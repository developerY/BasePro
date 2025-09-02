Below is a **step-by-step user flow** for using **NFC or QR** to configure a BLE connection to the
bike and display its battery level (and other stats) in your app’s UI. This approach gives you a *
*clear, guided experience** from **unconnected** to **connected**.

---

## 1) App Launch / Dashboard

1. **User opens the app** and lands on the **bike dashboard** (like the screenshot).
2. Initially, the bike metrics (e.g., battery level, motor power, etc.) might be **unavailable** or
   **dimmed** because the app isn’t yet connected to the bike via BLE.

---

## 2) Prompt User to Connect

1. **Prominent “Connect Bike” Button** (or “Add Bike”) on the dashboard (or in a “Settings” /
   “Devices” screen).
2. Tapping **“Connect Bike”** opens a **connection wizard** (or a bottom sheet, or a new screen)
   that explains how to set up the bike.

---

## 3) Choose NFC or QR Scan

On this “Connect Bike” screen, the user can choose:

- **Option A: NFC** – “Tap your phone on the bike’s NFC tag.”
- **Option B: QR** – “Scan the QR code on the bike.”

### A) NFC Flow

1. **App listens** for NFC events.
2. **User taps** phone on the bike’s NFC tag.
3. **NFC Tag Data** is read (e.g., `BLEDeviceID` or other relevant info).
4. **App displays** a short message like “Bike ID found. Connecting to BLE...”

### B) QR Flow

1. **App launches** camera-based QR scanner.
2. **User scans** the bike’s QR code.
3. **QR code** yields the same or similar info (e.g., `BLEDeviceID`).
4. **App displays** “Bike ID found. Connecting to BLE...”

---

## 4) BLE Connection Attempt

1. Using the **BLE Device ID** from NFC/QR, the app **attempts to connect** to the bike via BLE.
2. Show a **progress indicator** (“Connecting…”) or a short text indicating the app is
   pairing/connecting.
3. If successful:
    - **App** updates the stored bike ID (so future connections are faster).
    - **UI** transitions to a “Connected” state.
    - The **battery level**, **motor power**, etc. appear in real time on the dashboard.
4. If connection fails:
    - Show an **error message** (“Could not connect. Please ensure the bike is powered on and within
      range.”).
    - Option to **retry** or **go back** to scanning.

---

## 5) Confirmation / Dashboard Update

Once connected:

1. **Dashboard** is updated with real-time stats (battery level, speed, etc.).
2. **Bike ID** or **Bike Name** is displayed somewhere (e.g., “Connected to Bike #1234”).
3. The user can now **ride** and see **live** updates for battery, motor, and other metrics.

---

## 6) Future Launches

- Next time the user opens the app, it can **auto-connect** to the previously scanned bike if BLE is
  enabled.
- If the user wants to connect to a **different** bike, they can repeat the NFC/QR flow to pair with
  a new BLE ID.

---

## Visual Overview (Example)

1. **Dashboard** → “Not connected” or “Tap here to connect.”
2. **Connect Bike** Screen → “Scan with NFC” or “Scan QR.”
3. **NFC**: Tap phone → “Reading Tag… got Bike ID #1234.” → “Connecting BLE…” → success → back to
   Dashboard with updated stats.
4. **QR**: Open camera → scan code → “Found Bike ID #1234.” → “Connecting BLE…” → success → updated
   Dashboard.

---

## Implementation Tips

1. **Storing the Bike ID**: Once scanned, save the BLE ID in a **ViewModel** or local storage (e.g.,
   `SharedPreferences` or a local database).
2. **Auto-Reconnect**: On app launch, if a saved bike ID exists, auto-attempt BLE connection. Show a
   subtle loading state.
3. **Edge Cases**:
    - **NFC Not Supported**: Show a fallback (QR).
    - **BLE Not Enabled**: Prompt user to enable Bluetooth.
    - **QR Scanner Permissions**: Request camera permission if not granted.
4. **UI Feedback**: Provide clear status messages: scanning, connecting, connected, or error.
5. **Security**: If the bike or BLE connection requires authentication, prompt for a PIN or passkey.

---

### Conclusion

This flow ensures users **intentionally** scan the bike’s NFC tag or QR code, **automatically**
configures BLE with the right device ID, and then transitions to the **dashboard** with **real-time
** battery/motor data. It’s a **user-friendly** pattern that balances **guidance** (a “Connect Bike”
wizard) with the **flexibility** of scanning via NFC or QR.