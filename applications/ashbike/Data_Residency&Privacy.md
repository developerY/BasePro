# Data Residency & Privacy Overview

## 1. Executive Summary

Our app is designed to put users in control of their data by offloading all health and exercise information to Android’s Google Health Connect (GHC). 
This approach delivers two core benefits:

* **Zero local residue**: Uninstalling our app leaves no data behind on the device.
* **Open‑source transparency**: With our source code fully public, users and auditors can verify exactly how data is handled.

Together, these factors make our app both user-friendly and exceptionally privacy‑safe.

---

## 2. Data Storage in Google Health Connect

* **Centralized, system-managed store**: GHC acts as a shared, system‑level vault for health data.
* **Persistence beyond our app’s lifetime**: Upon insertion, sessions, metrics, and records live in GHC even if our app is removed.
* **Interoperability**: Other apps or services (with user permission) can access, visualize, or delete the data, ensuring flexibility and future-proofing.

### How it works:

1. **Insert**: Our app writes `ExerciseSessionRecord`, `DistanceRecord`, `StepsRecord`, etc., to GHC.
2. **No local database**: We do not maintain a redundant on‑device database, eliminating sync and consistency overhead.
3. **Uninstall**: Removing the app does not delete GHC records—they remain until the user or another app explicitly removes them.

---

## 3. Uninstall Behavior: No Droppings Left Behind

* **Local storage use = zero**: We do not write any user data to local files or embedded databases.
* **Ephemeral caches only**: Any UI caching is in-memory and cleared on app close.
* **Uninstall clean‑up**: Since no local data exists, uninstalling our APK leaves neither files nor settings on the device.

> *"Uninstall our app, and it’s as if it was never there—no hidden databases, no orphaned files, no accumulating logs."*

---

## 4. Privacy & Security Advantages

* **User control**: Users decide which apps have GHC permissions. Revoking permissions instantly locks out any further access.
* **Granular permissions**: Write vs. read, foreground vs. background. Only explicitly granted scopes are used by our app.
* **Data residency**: Health data remains in a centralized, OS‑managed service rather than scattered across multiple vendor silos.

---

## 5. Open Source Transparency

* **Public repository**: Our entire codebase is available on GitHub under an MIT license.
* **Auditability**: Security researchers and privacy advocates can inspect how we handle metadata, error cases, and permission flows.
* **Community contributions**: Anyone can propose improvements, spot potential security issues, and submit pull requests.

---

## 6. User Experience Benefits

* **Quick setup**: Users grant GHC permissions once; no need to configure in-app storage.
* **Seamless uninstall**: Release storage pressure and free up space without manual cleanup steps.
* **Data continuity**: Even after switching to another app, session history persists in GHC.

---

## 7. Conclusion

By leveraging Google Health Connect as the authoritative data store and publishing our code as open source, our app provides:

* **Privacy by design**: No local data residue; full user control via GHC.
* **Transparency & trust**: Open code, community‑driven quality.
* **Interoperability & longevity**: Health data stays with the user, accessible to any future apps.

This architecture ensures a safe, user‑friendly experience that respects privacy and embraces open collaboration.
