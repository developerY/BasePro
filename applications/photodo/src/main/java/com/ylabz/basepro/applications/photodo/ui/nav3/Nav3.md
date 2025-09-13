# Migrating the PhotoDo Application to Navigation 3

This guide outlines the plan for migrating the PhotoDo application to use Jetpack Compose Navigation 3, following a similar structure to the AshBike migration documentation.

-----

## Migration Plan 📝

1.  **Create `PhotoDoAppNavKeys.kt`**: This file will define the **NavKeys** for the different screens in the PhotoDo app (Home, List, Settings).

2.  **Create `PhotoDoAppNav3Activity.kt`**: This will be the new main `Activity` for the PhotoDo app, setting up the Nav3 environment.

3.  **Create the main `PhotoDoAppNav3` Composable**: This Composable will manage the **NavBackStack** and **NavDisplay** for the main sections of the app. It will include a bottom navigation bar.

4.  **Adapt existing UI Route Composables**: `PhotoDoHomeUiRoute`, `PhotoDoListUiRoute`, and `PhotoDoSettingsUiRoute` will need to be adapted to be called from the `entryProvider` and receive any necessary ViewModels or navigation lambdas.

5.  **Update ViewModels**: Ensure ViewModels are Hilt-injected and can receive parameters via `SavedStateHandle` if needed.

6.  **Update Gradle Dependencies**: Add the **Navigation 3**, **Hilt**, and **Kotlinx Serialization** dependencies to the appropriate `build.gradle.kts` file for the `photodo` application.

7.  **Update `AndroidManifest.xml`**: Change the launcher activity to `PhotoDoAppNav3Activity`.

-----

## File Location 📂

The new files will be created in a new `nav3` package within the existing `photodo` UI layer.

**Example Path:**

```
/Users/ash/AndroidStudioProjects/BasePro/applications/photodo/src/main/java/com/ylabz/basepro/applications/photodo/ui/nav3/
```

Of course. Here is that information formatted into a clean Markdown file for your Git repository.

---

# PhotoDo List-Detail Feature Implementation

This document outlines the recent changes to implement the list-detail navigation strategy and the next steps required to make the feature fully functional.

---

## ✅ Recap: List-Detail Foundation

Here's a quick summary of the foundational work that has been completed:

1.  **Updated NavKeys**: Added `PhotoDoListContentKey` and `PhotoDoItemDetailKey` to `PhotoDoAppNavKeys.kt`.

2.  **Created List-Detail Strategy**: Implemented `PhotoDoListFeatureWithListDetailStrategy.kt` which now manages the navigation for the list and detail views using `rememberListDetailSceneStrategy`. It currently includes a placeholder composable, `PhotoDoItemDetailScreen`.

3.  **Updated Main Navigation**: In `PhotoDoAppNav3Composables.kt`, the "List" tab (represented by `PhotoDoListSectionKey`) now renders the new `PhotoDoListFeatureWithListDetailStrategy`.

4.  **Modified List Route**: Updated `PhotoDoListUiRoute.kt` to accept a `navToItemDetail: (itemId: String) -> Unit` lambda, allowing navigation to be triggered from the list view.

---

## 🚀 Next Steps: Making it Functional

The following steps are required to complete the feature:

1.  **Implement the List UI in `PhotoDoListUiRoute`**
    Replace the current placeholder UI with the actual list of items. Each item in the list should trigger the `navToItemDetail(itemId)` lambda when selected.

2.  **Implement the Detail UI in `PhotoDoItemDetailScreen`**
    * Currently, `PhotoDoItemDetailScreen` is a simple placeholder inside `PhotoDoListFeatureWithListDetailStrategy.kt`.
    * Expand this screen to display the full details of a "PhotoDo" item based on the `itemId` it receives.
    * Consider moving `PhotoDoItemDetailScreen` to its own file (e.g., `features/photodolist/ui/PhotoDoItemDetailScreen.kt`) if it becomes complex.

3.  **ViewModel Integration 🔗**
    * Create and use a `PhotoDoListViewModel` within `PhotoDoListUiRoute` to fetch and manage the list of items.
    * Create a `PhotoDoItemDetailViewModel` for the `PhotoDoItemDetailScreen`. This ViewModel can receive the `itemId` via `SavedStateHandle` by using `hiltViewModel<PhotoDoItemDetailViewModel>(navKey)` in the `entryProvider`.

4.  **Test on Different Screen Sizes 📱**
    The `ListDetailSceneStrategy` will automatically adapt the layout. Ensure you test the feature on phones, tablets, and foldables (or emulators) to verify the list and detail panes are displayed correctly.
---
Of course. Here is that information formatted into a clean Markdown file.

---

# PhotoDo Navigation 3 Architecture

The structure we've built for the PhotoDo application is designed for a scalable and type-safe navigation system. It is composed of the following core components:

### 🔑 NavKeys
All navigation destinations are defined as `@Serializable` objects or data classes that inherit from `NavKey`. This creates a strongly-typed and self-contained definition for each screen.
* **Examples**: `PhotoDoHomeSectionKey`, `PhotoDoListContentKey`, `PhotoDoItemDetailKey`

---

### 📚 NavBackStack
We use the `rememberNavBackStack()` function to create and manage the history stack for a given navigation flow. This stateful object holds the list of `NavKeys` that represents the user's journey.

---

### 🖼️ NavDisplay
This composable is responsible for rendering the UI. It observes the `NavBackStack` and displays the composable associated with whatever `NavKey` is currently at the top of the stack.

---

### 🚀 Lambdas for Navigation
To decouple the UI from the navigation logic, screen composables receive specific, type-safe lambdas for navigation actions. These lambdas are implemented where the `NavBackStack` is managed, and their sole responsibility is to add or modify `NavKeys` on that stack.
* **Example Lambdas**:
    * `onNavigateToSettings: () -> Unit`
    * `navToItemDetail: (itemId: String) -> Unit`

