com.ylabz.basepro.core.data.repository
├── bike
│   ├── BikeRepository.kt
│   └── BikeRepositoryImpl.kt
├── sensor
│   ├── HeartRateRepository.kt          <-- Interface
│   ├── BleHeartRateRepository.kt       <-- Phone Implementation
│   └── WearHealthHeartRateRepository.kt <-- Watch Implementation (The file you just moved)
└── weather
└── WeatherRepo.kt