# Data Residency & Privacy Overview

## 1. Executive Summary

Our app serves as a local staging and offline cache via Room, but uses Google Health Connect (GHC) as the authoritative, 
long‑term store for all health and exercise data when available. Key benefits:

* **Temporary local cache**: Activities are written to a Room database for fast display and offline use when offline or if the user opts out of GHC.
* **Optional fallback**: If users do not have Health Connect installed or choose not to grant permissions, the app continues to function entirely on RoomDB.
* **Zero residual footprint**: Deleting an activity or uninstalling the app wipes all local data—no files or databases remain.
* **Centralized persistence**: When enabled, GHC holds the master copy, accessible to other apps and surviving our app’s removal.

---

## 2. Local Room Cache + Sync to GHC

* **Room for UX**: We maintain a RoomDB to store rides and metrics locally. This provides:

    * Instant UI updates
    * Offline read/write operations
* **GHC fallback & performance**: GHC itself is a local, on‑device store and is just as responsive as Room. We sync to GHC when available, but seamlessly fall back to Room alone if needed.
* **Sync flow**: When the user taps “sync,” we map Room entities to Health Connect `Record`s and call `insertRecords(...)` to push them into GHC.
* **Cache lifecycle**:

    * **On delete**: removing an activity in-app deletes only the Room entry; the Health Connect record remains in the shared, central store if GHC is enabled.
    * **On uninstall**: the Room database is entirely removed, leaving no files or databases behind.

---

## 3. Uninstall & Deletion Behavior

* **Activity deletion**: removes the ride from Room using `bikeRideRepo.deleteById(rideId)`; the corresponding record in Health Connect persists as part of the central repository shared by all apps.
* **App uninstall**: Android automatically clears your app’s Room database and cache directories; no local data remains.
* **Central repository**: Even after deleting local entries or uninstalling apps, all synced sessions still reside in Health Connect until a user or another app with write permissions explicitly deletes them.

---

## 4. Health Connect Persistence

* **System‑managed vault**: GHC stores your sessions, steps, distance, calories, heart rate, etc., independently of any single app.
* **Survives uninstall**: GHC data remains until explicitly deleted—either by user action in the Health Connect UI or by another app with write permission.
* **Shared access**: Other apps (with user‑granted permissions) can read or delete GHC data, ensuring portability and interoperability.

---

## 5. Privacy & Security Advantages

* **Full user control**: Users decide which apps can read or write in GHC; revoking permissions cuts off access immediately.
* **Minimal local footprint**: We only use Room for temporary caching; all permanent storage lives in GHC.
* **Open source transparency**: Our codebase (MIT‑licensed) is publicly auditable, so you can inspect exactly how local caching, sync, and delete operations occur.

---

## 6. User Experience Benefits

* **Responsive UI**: Both Room and GHC are local, on‑device stores—users get instant access regardless of backend.
* **Optional Health Connect**: Enables richer, cross‑app sharing of data when users opt in; otherwise the app continues seamlessly on RoomDB.
* **Simple cleanup**: Deleting an activity or uninstalling our app performs a full wipe of local data—no manual steps required.
* **Data continuity**: Historic sessions live in GHC when enabled, visible to any other compatible app after sync, or remain in RoomDB if GHC is unavailable.

---

## 7. Conclusion

By combining a **temporary Room cache** with **Google Health Connect** as the durable backend, our app delivers:

* **User‑friendly performance**: Fast, offline‑capable UI with local Room storage.
* **Privacy by design**: No lingering data on delete/uninstall; GHC offers centralized control.
* **Open‑source trust**: Transparent implementation and community vetting.

Your data stays yours—cached briefly in our app, then securely managed by GHC, with no residue once you’re done.
