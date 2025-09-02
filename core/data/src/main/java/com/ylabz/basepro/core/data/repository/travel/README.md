### Explanation

- **Raw Location Flow:**  
  We create a callbackFlow that registers a location listener using FusedLocationProviderClient and
  then share it with `shareIn`. This way, multiple downstream flows (speed, elevation, distance) use
  the same source.

- **Speed Flow:**  
  Maps each Location’s speed (in m/s) to km/h by multiplying by 3.6.

- **Elevation Flow:**  
  Maps each Location’s altitude (in meters).

- **Remaining Distance Flow:**  
  We use the `scan` operator on the raw location flow to accumulate the total traveled distance.
    - The accumulator is a Pair, where the first element is the previous location and the second
      element is the total traveled distance (in km).
    - For each new location, we compute the distance from the previous location (using `distanceTo`,
      which returns meters) and convert it to kilometers.
    - We then map the accumulated distance to remaining distance as:  
      `remainingDistance = totalRouteDistanceKm - traveledDistance`
    - The value is clamped to a minimum of 0.

---

Based on the UI of your **bike app screenshot**, which includes speed, compass direction, wind,
weather, elevation, distance, heart rate, and calories, here’s a complete list of **phone sensors
and data sources** needed to make it all work:

---

### 🚴‍♂️ Core Motion & Navigation Sensors

| Feature                       | Sensor Needed                             | Notes                                                                             |
|-------------------------------|-------------------------------------------|-----------------------------------------------------------------------------------|
| **Speed** (km/h)              | 🧭 `GPS` (GNSS)                           | Use `LocationManager` or `FusedLocationProvider` to get real-time speed from GPS. |
| **Direction (e.g., 203° SW)** | 🧲 `Magnetometer` + `Accelerometer`       | Combine to get compass heading (azimuth) with `SensorManager.getOrientation()`.   |
| **Distance (12.5 km)**        | 🛰️ `GPS`                                 | Calculated by tracking location deltas over time.                                 |
| **Elevation (12.0 m)**        | 📡 `GPS` or 🧭 `Barometer` (if available) | Barometer is more accurate for altitude on high-end phones.                       |
| **Wind Speed (5.0 m/s)**      | 🌐 External API (like OpenWeather)        | No phone sensor provides wind speed — must be fetched online.                     |
| **Weather (Rainy)**           | ☁️ External Weather API                   | Also requires internet connectivity and GPS location.                             |
| **Duration (7m)**             | ⏱️ System Clock                           | Just a timer using `System.currentTimeMillis()` at start/stop.                    |

---

### ❤️‍🔥 Health & Biometrics (Optional, but shown)

| Feature                 | Sensor Needed                                                             | Notes                                                                                                             |
|-------------------------|---------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **Heart Rate (-- bpm)** | ❤️ `Heart Rate Sensor` (if available) or 💡 Wearable (via Health Connect) | Most phones don’t have built-in heart rate sensors anymore; rely on wearables via Google Health Connect.          |
| **Calories (-- kcal)**  | 🔢 Computed from motion + Health profile                                  | Estimate using MET formula based on duration, weight, speed, etc. Can integrate Health Connect to fetch calories. |

---

### 📦 Optional Enhancement Sensors

| Feature              | Sensor                           | Purpose                                                       |
|----------------------|----------------------------------|---------------------------------------------------------------|
| **Tilt/Orientation** | 🧠 `Gyroscope` + `Accelerometer` | For showing bike tilt, auto-orienting map, or detecting bumps |
| **Light Sensor**     | 🔆 `Ambient Light Sensor`        | For adaptive UI or detecting day/night conditions             |
| **Proximity Sensor** | 🖐️ `Proximity Sensor`           | Could dim display when covered (e.g., during pocket rides)    |

---

### 🔌 Other Dependencies

- 📶 **Internet Access** – for:
    - Weather & wind info
    - Syncing ride data
    - Health Connect sync (if using wearables)

- 🧠 **Health Connect SDK** – for:
    - Heart rate from wearables
    - Calories burned
    - Syncing health metrics

- 📍 **Location Permissions** – `ACCESS_FINE_LOCATION` and background access if tracking during lock
  screen

- ⚡ **Battery Optimization Exemption** – Optional but recommended for uninterrupted tracking on some
  Android versions

---

### ✅ Summary Checklist

| Type           | Required                                         |
|----------------|--------------------------------------------------|
| GPS            | ✅ Yes                                            |
| Magnetometer   | ✅ Yes                                            |
| Accelerometer  | ✅ Yes                                            |
| Barometer      | 🔄 Optional                                      |
| Heart Rate     | 🔄 Optional (via Health Connect or hardware)     |
| Internet       | ✅ Yes (for weather/wind/calories if cloud-based) |
| Health Connect | 🔄 Optional                                      |
| Gyroscope      | 🔄 Optional                                      |

Let me know if you'd like help writing a `SensorManager` setup or integrating Health Connect for
heart rate and calories!