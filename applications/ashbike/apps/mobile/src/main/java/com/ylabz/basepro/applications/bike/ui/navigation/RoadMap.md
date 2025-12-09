Project: Migration to "Navigation 3" (androidx.navigation3.*)

Objective: Implement a new navigation flow using the experimental "Navigation 3" libraries, focusing on manual backstack management, state-scoped ViewModels with Hilt, and Google-provided scene strategies for adaptive layouts. This will run in parallel with the existing NavHost-based navigation for evaluation and phased migration.

---
**I. Project Setup & Initial Configuration**
---

1.  **Verify/Update Dependencies (gradle/libs.versions.toml & module build.gradle.kts):**
    *   Ensure `androidx.navigation3:navigation3-runtime` is included.
    *   Ensure `androidx.navigation3:navigation3-ui` is included.
    *   Ensure `androidx.compose.material3.adaptive:adaptive-navigation3` (for scene strategies) is included.
    *   Ensure `androidx.lifecycle:lifecycle-viewmodel-navigation3` (for ViewModel scoping) is included.
    *   Ensure `org.jetbrains.kotlinx:kotlinx-serialization-json` is included (for `@Serializable NavKey`s).
    *   Apply Kotlinx Serialization plugin if not already present.
    *   Keep Hilt dependencies (`com.google.dagger:hilt-android`, `androidx.hilt:hilt-navigation-compose`).

2.  **Create New Activity Entry Point (e.g., `BikeAppNav3Activity.kt`):**
    *   This activity will host the "Navigation 3" implementation.
    *   Annotate with `@AndroidEntryPoint` for Hilt.
    *   In `onCreate`, use `setContent` to call the main "Navigation 3" Composable (e.g., `BikeAppNav3()`).

---
**II. Define Core Navigation Primitives (`NavKey`s)**
---

1.  **Create `NavKey` Serializable Data Classes/Objects (e.g., in `BikeAppNavKeys.kt`):**
    *   `HomeSectionKey: NavKey` (data object)
    *   `RideSectionKey: NavKey` (data object)
    *   `SettingsSectionKey: NavKey` (data object, or data class if it needs top-level arguments)
    *   `RideListContentKey: NavKey` (data object, for the list part of the Ride section)
    *   `RideDetailContentKey(val rideId: String): NavKey` (data class, `rideId` property must match ViewModel's `SavedStateHandle` key)
    *   *Consider: If `SettingsSectionKey` needs arguments (e.g., `initialCardKeyToExpand`), define it as a data class with corresponding properties.*

---
**III. Implement Main Application Structure (`BikeAppNav3` Composable)**
---

1.  **Create the `BikeAppNav3` Composable (e.g., in `BikeAppNav3Activity.kt`):**
    *   Initialize top-level `sectionBackStack`: `val sectionBackStack = rememberNavBackStack<NavKey>(HomeSectionKey)`.
    *   Use `androidx.compose.material3.Scaffold`.

2.  **Implement `AppBottomNavigationBarNav3` Composable:**
    *   Accept `currentSectionKey: NavKey?` and `onSectionSelected: (NavKey) -> Unit`.
    *   Define tab items (Home, Ride, Settings) mapping to their respective `NavKey`s (`HomeSectionKey`, `RideSectionKey`, `SettingsSectionKey`).
    *   Use `androidx.compose.material3.NavigationBar` and `NavigationBarItem`.
    *   `selected` state of `NavigationBarItem` based on `currentSectionKey == tab.key`.
    *   `onClick` for `NavigationBarItem` should call `onSectionSelected(tab.key)`.
    *   In `BikeAppNav3`, `onSectionSelected` for the bottom bar will:
        *   `sectionBackStack.clear()`
        *   `sectionBackStack.add(selectedKey)`

3.  **Implement Top-Level `NavDisplay` within `BikeAppNav3`'s `Scaffold`:**
    *   Bind to `sectionBackStack`.
    *   Implement `onBack` lambda (e.g., pop from `sectionBackStack` if size > 1).
    *   Define `entryProvider`:
        *   **`entry<HomeSectionKey>`:**
            *   Instantiate `BikeViewModel` using `hiltViewModel<BikeViewModel>()`.
            *   Call reusable `BikeUiRoute(...)`, passing the ViewModel.
            *   *Define `navTo` lambda for `BikeUiRoute` if it needs to trigger Nav3 actions (e.g., `sectionBackStack.add(...)`).*
        *   **`entry<RideSectionKey>`:**
            *   Call a new Composable: `RideFeatureWithListDetailStrategy()`.
        *   **`entry<SettingsSectionKey>`:**
            *   Instantiate `SettingsViewModel` using `hiltViewModel<SettingsViewModel>()`.
            *   Call reusable `SettingsUiRoute(...)`, passing the ViewModel.
            *   *If `SettingsSectionKey` has arguments, extract them from the `navKey` instance and pass to `SettingsUiRoute` or ensure `SettingsViewModel` picks them up via `SavedStateHandle`.*
            *   *Define `navTo` lambda for `SettingsUiRoute` for Nav3 actions.*

---
**IV. Implement Ride Section with `ListDetailSceneStrategy` (`RideFeatureWithListDetailStrategy` Composable)**
---

1.  **Create the `RideFeatureWithListDetailStrategy` Composable:**
    *   Initialize nested `rideContentBackStack`: `val rideContentBackStack = rememberNavBackStack<NavKey>(RideListContentKey)`.
    *   Initialize strategy: `val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()`.

2.  **Implement Nested `NavDisplay` within `RideFeatureWithListDetailStrategy`:**
    *   Bind to `rideContentBackStack`.
    *   Set `sceneStrategy = listDetailStrategy`.
    *   Implement `onBack` lambda (e.g., pop from `rideContentBackStack`).
    *   Define `entryProvider`:
        *   **`entry<RideListContentKey>`:**
            *   Set `metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { ... })`.
            *   Instantiate `TripsViewModel` (create if it doesn't exist) using `hiltViewModel<TripsViewModel>()`.
            *   Call reusable `TripsUIRoute(...)`, passing the ViewModel.
            *   `navToRideDetail` lambda for `TripsUIRoute` will call `rideContentBackStack.add(RideDetailContentKey(rideId = rideId))`.
        *   **`entry<RideDetailContentKey>`:**
            *   Set `metadata = ListDetailSceneStrategy.detailPane()`.
            *   The `navKey` parameter is `RideDetailContentKey`.
            *   Instantiate `RideDetailViewModel` using `hiltViewModel<RideDetailViewModel>()`. (Hilt + `lifecycle-viewmodel-navigation3` should pass `navKey.rideId` to `SavedStateHandle`).
            *   Call reusable `RideDetailScreen(...)`, passing the ViewModel.

---
**V. ViewModel Adaptations for "Navigation 3" & Hilt**
---

1.  **Review/Update All Relevant ViewModels (`BikeViewModel`, `TripsViewModel`, `RideDetailViewModel`, `SettingsViewModel`):**
    *   Ensure they are annotated with `@HiltViewModel`.
    *   Ensure they use `@Inject constructor(...)`.
    *   **Argument Handling:** For ViewModels needing arguments (e.g., `RideDetailViewModel` for `rideId`, `SettingsViewModel` for `initialCardKeyToExpand` if applicable):
        *   Retrieve arguments from `SavedStateHandle`.
        *   Ensure the key used in `savedStateHandle.get("keyName")` EXACTLY matches the property name in the corresponding `NavKey` data class (e.g., `RideDetailContentKey.rideId` maps to `savedStateHandle.get("rideId")`).

---
**VI. Screen Composable Adaptations for Reusability**
---

1.  **Review/Refactor Screen Composables (`BikeUiRoute`, `TripsUIRoute`, `RideDetailScreen`, `SettingsUiRoute`):**
    *   **Primary State Source:** Modify them to primarily accept their state and business logic via their respective `@HiltViewModel` instance passed as a parameter.
    *   **Navigation Callbacks:**
        *   Replace direct `NavController.navigate()` calls with lambda parameters (e.g., `onNavigateToDetail: (String) -> Unit`, `onNavigateToProfile: () -> Unit`).
        *   The implementation of these lambdas will be provided by the `entryProvider` in `NavDisplay`, and they will call methods on the appropriate `NavBackStack` instance (e.g., `backStack.add(NewKey)`).
    *   **Argument Passing:** Ensure screens that display data based on arguments (e.g., `RideDetailScreen`) source this data from their ViewModel, which in turn gets it from `SavedStateHandle` (populated by the `NavKey`).

---
**VII. Testing & Iteration**
---

1.  **Build and Run on `BikeAppNav3Activity`:**
2.  **Test Tab Navigation:**
    *   Switching between Home, Ride, Settings sections.
    *   Verify correct content display for each tab.
    *   Verify bottom bar selection indicator updates correctly.
3.  **Test Ride List/Detail Flow (ListDetailSceneStrategy):**
    *   Navigate to the Ride section.
    *   Select a ride from the list.
    *   Verify the detail screen appears (correctly in dual-pane on large screens if strategy is configured).
    *   Verify `RideDetailViewModel` receives the correct `rideId`.
4.  **Test Back Navigation:**
    *   From Ride Detail to Ride List.
    *   From Ride List (if it's the only item in `rideContentBackStack`) back to the previous section in `sectionBackStack`.
    *   From a section (e.g., Settings) back to the previous section (e.g., Home), ensuring the app doesn't close if `sectionBackStack` has more than one item.
    *   System back button behavior at all levels.
5.  **Test Argument Passing:**
    *   Confirm ViewModels are receiving arguments from `NavKey`s via `SavedStateHandle`.
6.  **Test State Scoping:**
    *   Verify ViewModel instances are retained correctly when navigating away and back to a `NavKey` entry (e.g., scroll position in a list, data loaded in a ViewModel).
7.  **Test Adaptive Layouts:**
    *   On different screen sizes/orientations to see `ListDetailSceneStrategy` behavior.
8.  **Address TODOs:** Implement any placeholder navigation actions within screen composables (e.g., `navTo` lambdas).

---
**VIII. Future Considerations (Post-Initial Implementation)**
---
1.  **Complex Tab Backstacks:** If individual tabs need to maintain their own internal backstacks when switching tabs, the simple `sectionBackStack.clear()` and `add()` logic will need to be made more sophisticated (e.g., saving/restoring tab-specific backstacks).
2.  **Custom Scene Strategies:** Explore if more custom scene strategies are needed beyond `ListDetailSceneStrategy`.
3.  **Animations:** Investigate and implement custom transition animations if required.
4.  **Full Migration:** Plan for removing the old `NavHost`-based system if "Navigation 3" proves suitable.

