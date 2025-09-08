# Migration to "Navigation 3" (`androidx.navigation3.*`)

## 1. Project Goal

The primary goal is to migrate the application's navigation from the Jetpack Navigation Components (`NavHost`/`NavController`) model to the experimental **Navigation 3** (`androidx.navigation3.*`) model. This involves:

- Managing the navigation backstack manually using `NavBackStack`.
- Displaying content via `NavDisplay` and `entryProvider`.
- Leveraging state-scoped ViewModels using `androidx.lifecycle:lifecycle-viewmodel-navigation3` with Hilt.
- Utilizing Google-provided scene strategies like `ListDetailSceneStrategy` for adaptive layouts.
- Maximizing the reusability of existing screen Composable functions.

---

## 2. Key Libraries & Concepts

- **`androidx.navigation3:navigation3-runtime`**  
  Provides core components like `NavKey`, `NavBackStack`.

- **`androidx.navigation3:navigation3-ui`**  
  Provides `NavDisplay` for rendering the UI based on the backstack.

- **`androidx.compose.material3.adaptive:adaptive-navigation3`**  
  Provides scene strategies like `ListDetailSceneStrategy` and `rememberListDetailSceneStrategy`.

- **`androidx.lifecycle:lifecycle-viewmodel-navigation3`**  
  Enables ViewModel instances to be scoped to individual destinations (`NavKey` entries) on the `NavBackStack`.

- **`NavKey`**  
  Serializable data classes or objects representing unique navigable destinations. They can carry arguments.

- **`rememberNavBackStack()`**  
  Composable function to create and remember an instance of `NavBackStack`.

- **`NavDisplay`**  
  Composable that observes a `NavBackStack` and renders UI using an `entryProvider`.

- **`entryProvider`**  
  Defines how different `NavKey` types are mapped to their corresponding Composable content.

- **Scene Strategies**  
  (e.g., `ListDetailSceneStrategy`) Define how multiple destinations are arranged in the UI, especially for adaptive layouts.

- **Hilt**  
  Used for dependency injection, especially for ViewModels.

---

## 3. File Structure & Entry Point

The "Navigation 3" implementation is primarily housed in a new or refactored Activity (e.g., `BikeAppNav3Activity.kt`) and associated Composables.

**Activity: `BikeAppNav3Activity.kt`**

- Annotated with `@AndroidEntryPoint` for Hilt.
- `setContent` block calls the main "Navigation 3" Composable (e.g., `BikeAppNav3()`).

```kotlin
// File: BikeAppNav3Activity.kt
package com.ylabz.basepro.feature.nav3.ui.content.strategy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BikeAppNav3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BikeAppNav3() // Main Composable for Nav3
            }
        }
    }
}


````

---

## 4. `NavKey` Definitions

Serializable `NavKey`s are defined for each distinct screen or navigation section.

```kotlin
// File: BikeAppNavKeys.kt
package com.ylabz.basepro.feature.nav3.ui.content.strategy

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Top-level keys for the main sections/tabs
@Serializable data object HomeSectionKey : NavKey
@Serializable data object RideSectionKey : NavKey
@Serializable data object SettingsSectionKey : NavKey

// Keys for the Ride List/Detail flow
@Serializable data object RideListContentKey : NavKey
@Serializable data class RideDetailContentKey(val rideId: String) : NavKey
```

---

## 5. Main "Navigation 3" Composable (`BikeAppNav3`)

This Composable sets up the primary application structure with a `Scaffold`, bottom navigation, and a `NavDisplay` for switching between main sections.

```kotlin
@Composable
fun BikeAppNav3(modifier: Modifier = Modifier) {
    val sectionBackStack = rememberNavBackStack<NavKey>(HomeSectionKey)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppBottomNavigationBarNav3(
                currentSectionKey = sectionBackStack.lastOrNull(),
                onSectionSelected = { selectedKey ->
                    sectionBackStack.clear() // Simple tab switching
                    sectionBackStack.add(selectedKey)
                }
            )
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = sectionBackStack,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            onBack = { keysToRemove ->
                repeat(keysToRemove) {
                    if (sectionBackStack.size > 1) {
                        sectionBackStack.removeLastOrNull()
                    }
                }
            },
            entryProvider = entryProvider {
                entry<HomeSectionKey> {
                    val bikeViewModel = hiltViewModel<BikeViewModel>()
                    BikeUiRoute(viewModel = bikeViewModel)
                }
                entry<RideSectionKey> {
                    RideFeatureWithListDetailStrategy()
                }
                entry<SettingsSectionKey> {
                    val settingsViewModel = hiltViewModel<SettingsViewModel>()
                    SettingsUiRoute(viewModel = settingsViewModel)
                }
            }
        )
    }
}
```

---

## 6. Ride Section with `ListDetailSceneStrategy`

```kotlin
@Composable
fun RideFeatureWithListDetailStrategy() {
    val rideContentBackStack = rememberNavBackStack<NavKey>(RideListContentKey)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = rideContentBackStack,
        modifier = Modifier.fillMaxSize(),
        onBack = { keysToRemove ->
            repeat(keysToRemove) {
                if (rideContentBackStack.isNotEmpty()) {
                    rideContentBackStack.removeLastOrNull()
                }
            }
        },
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<RideListContentKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { /* Placeholder */ }
                )
            ) {
                val tripsViewModel = hiltViewModel<TripsViewModel>()
                TripsUIRoute(
                    viewModel = tripsViewModel,
                    navToRideDetail = { rideId ->
                        rideContentBackStack.add(RideDetailContentKey(rideId))
                    }
                )
            }
            entry<RideDetailContentKey> { navKey ->
                val rideDetailViewModel = hiltViewModel<RideDetailViewModel>()
                RideDetailScreen(viewModel = rideDetailViewModel)
            }
        }
    )
}
```

---

## 7. Bottom Navigation Bar

```kotlin
private data class TabItem(val title: String, val icon: ImageVector, val key: NavKey)

@Composable
fun AppBottomNavigationBarNav3(
    currentSectionKey: NavKey?,
    onSectionSelected: (NavKey) -> Unit
) {
    val navItems = listOf(
        TabItem("Home", Icons.Filled.Home, HomeSectionKey),
        TabItem("Ride", Icons.Filled.DirectionsBike, RideSectionKey),
        TabItem("Settings", Icons.Filled.Settings, SettingsSectionKey)
    )

    NavigationBar {
        navItems.forEach { tab ->
            NavigationBarItem(
                selected = currentSectionKey == tab.key,
                onClick = { onSectionSelected(tab.key) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) }
            )
        }
    }
}
```

---

## 8. ViewModel Adaptations & Hilt Integration

* Standard `@HiltViewModel` classes.
* Arguments from `NavKey` are auto-injected into `SavedStateHandle` when property names match.

```kotlin
@HiltViewModel
class RideDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val rideId: String? = savedStateHandle.get("rideId")

    init {
        Log.d("RideDetailViewModel", "Initialized with rideId: $rideId")
    }
}
```

---

## 9. Screen Composable Adaptations

* Decouple from `NavController`.
* Accept ViewModels as parameters.
* Use lambdas for navigation actions.

```kotlin
@Composable
fun RideDetailScreen(viewModel: RideDetailViewModel) {
    // UI reads from viewModel
}
```

```kotlin
// Example in TripsUIRoute
navToRideDetail = { rideId ->
    rideContentBackStack.add(RideDetailContentKey(rideId))
}
```

---

## 10. Build Gradle Dependency Changes

```kotlin
dependencies {
    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3.adaptive.navigation3)

    // ViewModel support
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Remove old navigation-compose if fully migrated
    // implementation(libs.androidx.navigation.compose)
}

plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "x.x.x"
}
```