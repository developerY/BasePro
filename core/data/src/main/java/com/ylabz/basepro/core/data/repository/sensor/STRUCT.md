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

com.ylabz.basepro.core.data.repository.sensor
├── heart
│   ├── HeartRateRepository.kt          (Interface)
│   ├── BleHeartRateRepository.kt       (Phone implementation)
│   └── WearHealthHeartRateRepository.kt (Watch implementation)
└── glucose
├── GlucoseRepository.kt            (Interface)
├── BleGlucoseRepository.kt         (Standard Bluetooth)
└── LibreNfcRepository.kt           (Abbott Libre specific)