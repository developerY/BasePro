com.ylabz.basepro.ashbike.wear
├── app
│   └── BaseProWearApp.kt          <-- (NEW) Hilt Application Entry Point
├── di
│   └── WearModule.kt              <-- (NEW) Hilt Module for Wear-specific dependencies
├── presentation
│   ├── MainActivity.kt            <-- @AndroidEntryPoint
│   ├── WearApp.kt                 <-- Root Composable (NavHost)
│   ├── theme/                     <-- Type, Color, Theme
│   ├── components/                <-- Shared Wear Composables (Chip, Button)
│   └── screens
│       └── ride
│           ├── WearBikeScreen.kt
│           └── WearBikeViewModel.kt <-- @HiltViewModel (Moves logic out of UI)
├── service
│   ├── ExerciseService.kt         <-- @AndroidEntryPoint (Your Foreground Service)
│   └── SensorHelpers.kt
├── tile
│   └── MainTileService.kt         <-- @AndroidEntryPoint
└── complication
└── MainComplicationService.kt <-- @AndroidEntryPoint