Here’s a self‑contained helper you can drop into your `BikeViewModel`. It takes a `Location`, does
the one‑off Retrofit call on `weatherRepo`, maps it into your `BikeWeatherInfo`, and merges it into
the existing `_uiState`.

```kotlin
// inside BikeViewModel…

// guard so we only ever fetch once
private var weatherFetched = false

/**
 * Fetches current weather for [loc] and merges into the UI state
 */
private fun fetchAndMergeWeather(loc: Location) = viewModelScope.launch {
    if (weatherFetched) return@launch
    weatherFetched = true

    // 1) Call your WeatherRepo
    val resp = runCatching {
        weatherRepo.openCurrentWeatherByCoords(loc.latitude, loc.longitude)
    }.onFailure {
        Log.e("BikeViewModel", "Weather API failed", it)
    }.getOrNull()

    // 2) Map to your UI model
    val info = resp?.let { weather ->
        BikeWeatherInfo(
            windDegree    = weather.wind.deg,
            windSpeed     = (weather.wind.speed * 3.6f).toFloat(),
            conditionText = weather.weatherOne
                ?.firstOrNull()
                ?.main
                ?: "Unknown"
        ).also {
            Log.d("BikeViewModel", "Mapped weatherInfo=$it")
        }
    }

    // 3) Fold into current BikeUiState.Success
    (_uiState.value as? BikeUiState.Success)?.let { current ->
        _uiState.value = current.copy(
            bikeData = current.bikeData.copy(
                bikeWeatherInfo = info
            )
        )
        Log.d("BikeViewModel", "UI state updated with weatherInfo")
    }
}
```

**How to wire it up in `init { … }`:**

```kotlin
viewModelScope.launch {
  // get the very first GPS fix
  val firstLoc = unifiedLocationRepository.locationFlow.first()
  fetchAndMergeWeather(firstLoc)
}

// … and separately continue collecting sensorDataFlow as before …
```

This is about as simple as it gets: one function, one flag, one coroutine, and it merges cleanly
into your existing StateFlow.