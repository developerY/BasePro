# Material 3 Expressive Theming (feature/material3)

This module provides a custom Material 3 theme that incorporates expressive motion and an expanded typography scale, suitable for creating visually rich and dynamic user interfaces.

## Features

*   **Expressive Motion**: Utilizes `MotionScheme.expressive()` for spirited, physics-based animations across Material components.
*   **Dynamic Color Support**: Adapts to user wallpaper-based colors on Android 12+ while providing custom fallback themes (`DarkColorScheme`, `LightColorScheme`) for older versions or when dynamic color is disabled.
*   **Custom Typography Scale**: Defines baseline text styles in `AppTypography` (located in `ui/theme/Type.kt`) and provides corresponding `Emphasized...` styles (e.g., `EmphasizedTitleLarge`) for more prominent text.
*   **Centralized Theme**: All theming components are organized within the `com.ylabz.basepro.feature.material3.ui.theme` package.

## Core Components

### 1. `MaterialExpressiveTheme`
   (Defined in `ui/theme/Theme.kt`)

   This is the main Composable function to apply the theme to your UI.

   **Usage:**
   ```kotlin
   import com.ylabz.basepro.feature.material3.ui.theme.MaterialExpressiveTheme

   @Composable
   fun MyScreen() {
       MaterialExpressiveTheme(
           // dynamicColor = true by default (uses wallpaper colors on Android 12+)
           // Set to false to always use the defined DarkColorScheme/LightColorScheme
           // dynamicColor = false, 
           // darkTheme = isSystemInDarkTheme() by default
       ) {
           // Your screen content here
           Scaffold(...) { ... }
       }
   }
   ```

### 2. Color Schemes
   (Defined in `ui/theme/Theme.kt`)

   *   `DarkColorScheme`: Fallback dark theme colors.
   *   `LightColorScheme`: Fallback light theme colors (currently uses `Purple40` as primary).

### 3. Typography
   (Baseline styles in `ui/theme/Type.kt` as `AppTypography`, emphasized styles as separate vals like `EmphasizedTitleLarge`)

   The `MaterialExpressiveTheme` applies `AppTypography` by default. You can access these styles via `MaterialTheme.typography`. For emphasized styles, import them directly from `Type.kt`.

   **Accessing Baseline Styles:**
   ```kotlin
   Text(
       text = "Hello",
       style = MaterialTheme.typography.titleLarge // Uses AppTypography.titleLarge
   )
   ```

   **Accessing Emphasized Styles:**
   ```kotlin
   import com.ylabz.basepro.feature.material3.ui.theme.EmphasizedTitleLarge

   Text(
       text = "Important!",
       style = EmphasizedTitleLarge
   )
   ```

## Showcase Example

The `Material3ShowcaseScreen` composable located in `ui/Main.kt` demonstrates the usage of this theme and various Material 3 components.

## How to Customize

*   **Colors**: Modify the `Color.kt` (if you create one, or directly in `Theme.kt`) and the `DarkColorScheme`/`LightColorScheme` definitions in `Theme.kt`.
*   **Typography**: Adjust `AppTypography` and the `Emphasized...` style constants in `Type.kt`.
*   **Shapes**: (Not yet implemented) You can define custom shapes and pass them to `MaterialTheme` within `MaterialExpressiveTheme` for further customization.

