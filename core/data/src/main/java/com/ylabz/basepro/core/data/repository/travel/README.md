### Explanation

- **Raw Location Flow:**  
  We create a callbackFlow that registers a location listener using FusedLocationProviderClient and then share it with `shareIn`. This way, multiple downstream flows (speed, elevation, distance) use the same source.

- **Speed Flow:**  
  Maps each Location’s speed (in m/s) to km/h by multiplying by 3.6.

- **Elevation Flow:**  
  Maps each Location’s altitude (in meters).

- **Remaining Distance Flow:**  
  We use the `scan` operator on the raw location flow to accumulate the total traveled distance.
    - The accumulator is a Pair, where the first element is the previous location and the second element is the total traveled distance (in km).
    - For each new location, we compute the distance from the previous location (using `distanceTo`, which returns meters) and convert it to kilometers.
    - We then map the accumulated distance to remaining distance as:  
      `remainingDistance = totalRouteDistanceKm - traveledDistance`
    - The value is clamped to a minimum of 0.
