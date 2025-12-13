ashbike/
├── build.gradle.kts           (Root build file)
├── settings.gradle.kts        (Includes :mobile, :wear, :xr, :features:...)
│
├── features/                  (SHARED LOGIC - Pure Kotlin/Java, no UI)
│   ├── ride-data/             (e.g., Calculations, Repository)
│   ├── user-session/          (e.g., Auth, Profile data)
│   └── navigation-logic/      (e.g., Routing algorithms)
│
├── mobile/                    (PHONE APP - Material 3 UI)
│   ├── src/main/AndroidManifest.xml (Declares Glass Activity here)
│   └── src/main/java/com/ashbike/mobile/
│       ├── MainActivity.kt    (Phone Entry Point)
│       └── features/
│           └── glass/         (GLASS UI - Compose Glimmer)
│               ├── GlassActivity.kt
│               └── GlassUi.kt
│
├── wear/                      (WATCH APP - Wear Compose UI)
│   └── src/main/java/com/ashbike/wear/
│       └── ... (Imports :features:ride-data)
│
└── xr/                        (HEADSET APP - Spatial UI)
└── src/main/java/com/ashbike/xr/
└── ... (Imports :features:ride-data)