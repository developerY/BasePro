## 🚀 Core Features

* **Seamless Hardware Integration & Connectivity**
  The app features a fully operational **Bluetooth Low Energy (BLE)** service to connect with any external sensor, **NFC** for tap-to-pair functionality, and an integrated **QR Code scanner**. This deep hardware access is a core strength of native development.

* **A Full Wear OS Companion App** (Framework in place)
  This isn't just a mirrored notification. It's a high-performance smartwatch application built with **Wear Compose** and **Horologist**, featuring custom **Tiles** and **Complications**. This creates a true ecosystem experience that keeps users engaged.

* **Intelligent, On-Device Features** (Famework in place)
  Leveraging the device's full power, we integrated **CameraX** with **ML Kit** for on-the-fly text recognition—a feature that runs smoothly and instantly, without relying on a server.

* **Guaranteed Performance & Reliability**
  The live-tracking dashboard runs flawlessly in a **Foreground Service**, ensuring that critical, real-time data is never lost, even when the app is in the background. This is the level of reliability that builds user trust.

---

## 🛠️ The Architectural Foundation of Excellence

These features are not accidents; they are the result of a deliberate and robust architectural strategy. This is the blueprint for a scalable, maintainable, and high-performance application:

* **Core Architecture**: A clean **MVVM (Model-View-ViewModel)** pattern separates concerns, making the app easy to test and scale.
* **State Management**: We use **ViewModel** and **StateFlow** to create a predictable, one-way data flow, which eliminates a whole class of bugs and makes the UI incredibly stable.
* **User Interface**: The entire UI is built with **Jetpack Compose**, a modern, declarative toolkit that allows for beautiful, responsive interfaces with less code and better performance.
* **Dependency Injection**: **Hilt** manages dependencies throughout the app, which is critical for building a modular and maintainable codebase that can grow without collapsing under its own weight.
* **Data & Networking**: With **Room** for local database management and both **Retrofit** and **Apollo Client** for networking, the data layer is flexible, efficient, and completely decoupled from the UI.
* **Performance Optimization**: We've even included **Baseline Profiles**, an advanced optimization that pre-compiles the app for significantly faster startup times and smoother animations.
* **App Supports**: Mode: Dark & Light / Lang: English, Spanish (& French in development)).
---

### **Resources**

* **Android Source (MAD)**: [github.com/developerY/BasePro](https://github.com/developerY/BasePro)
    * *The AshBike project is under `applications/ashbike`*
* **iOS Source (Native)**: [github.com/developerY/AshBike](https://github.com/developerY/AshBike)
* **Video Demo**: [youtube.com/watch?v=dLmREN9eUdw](https://www.youtube.com/watch?v=dLmREN9eUdw)