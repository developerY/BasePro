Perfect 👍 — here’s a **condensed quick-reference cheat sheet** version of your Navigation 3 migration doc. It’s stripped down to just the essentials so your team can onboard quickly:

# 🚀 Navigation 3 Migration Cheat Sheet

## 🎯 Goal
Migrate from Jetpack Navigation (`NavController`) to **Navigation 3** (`androidx.navigation3.*`):
- Manual backstack with `NavBackStack`
- UI rendering via `NavDisplay + entryProvider`
- Scoped ViewModels (`lifecycle-viewmodel-navigation3` + Hilt)
- Adaptive layouts (`ListDetailSceneStrategy`)
- Reusable screens (no `NavController` dependencies)

---

## 📦 Key APIs
- **Backstack:** `rememberNavBackStack()`
- **Destination Keys:** `NavKey` (`@Serializable`)
- **UI Host:** `NavDisplay(backStack, entryProvider)`
- **Scene Strategies:** `ListDetailSceneStrategy`
- **ViewModels:** `hiltViewModel<T>()`

---

## 🗂 File Structure
- `BikeAppNav3Activity.kt` → Entry point
- `BikeAppNavKeys.kt` → All `NavKey` definitions
- Screen composables → Accept `ViewModel` + lambdas

---

## 🔑 NavKey Example
```kotlin
@Serializable data object HomeSectionKey : NavKey
@Serializable data object RideSectionKey : NavKey
@Serializable data class RideDetailContentKey(val rideId: String) : NavKey
````

---

## 🏗 Main Composable

```kotlin
val sectionBackStack = rememberNavBackStack<NavKey>(HomeSectionKey)

Scaffold(
  bottomBar = { AppBottomNavigationBarNav3(sectionBackStack) }
) { padding ->
  NavDisplay(
    backStack = sectionBackStack,
    entryProvider = entryProvider {
      entry<HomeSectionKey> { BikeUiRoute(hiltViewModel()) }
      entry<RideSectionKey> { RideFeatureWithListDetailStrategy() }
      entry<SettingsSectionKey> { SettingsUiRoute(hiltViewModel()) }
    }
  )
}
```

---

## 📋 Ride Section (List/Detail)

```kotlin
val rideBackStack = rememberNavBackStack<NavKey>(RideListContentKey)
val listDetail = rememberListDetailSceneStrategy<NavKey>()

NavDisplay(
  backStack = rideBackStack,
  sceneStrategy = listDetail,
  entryProvider = entryProvider {
    entry<RideListContentKey>(
      metadata = ListDetailSceneStrategy.listPane()
    ) {
      TripsUIRoute(hiltViewModel(), navToRideDetail = {
        rideBackStack.add(RideDetailContentKey(it))
      })
    }
    entry<RideDetailContentKey> { RideDetailScreen(hiltViewModel()) }
  }
)
```

---

## 📱 Bottom Navigation

```kotlin
NavigationBar {
  NavigationBarItem(
    selected = currentSection == tab.key,
    onClick = { sectionBackStack.replace(tab.key) },
    icon = { Icon(tab.icon, null) },
    label = { Text(tab.title) }
  )
}
```

---

## 🧩 ViewModels

* Standard `@HiltViewModel`
* Args auto-injected via `SavedStateHandle` if names match

```kotlin
@HiltViewModel
class RideDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
) : ViewModel() {
  val rideId: String? = savedStateHandle["rideId"]
}
```

---

## 📌 Screen Guidelines

* No `NavController` references
* Accept `ViewModel` + lambdas for navigation
* Example:

```kotlin
RideDetailScreen(viewModel: RideDetailViewModel)
```

---

## ⚙️ Gradle Setup

```kotlin
dependencies {
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.material3.adaptive.navigation3)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)
  implementation(libs.kotlinx.serialization.json)
}

plugins {
  id("org.jetbrains.kotlin.plugin.serialization")
}
```

---

✅ **Remember:** Navigation = `NavBackStack` ops, not `NavController`.


