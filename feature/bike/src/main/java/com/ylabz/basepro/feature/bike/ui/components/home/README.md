Below is a **fully functional** example of a **Bike Home Screen** that uses:

- **Material 3** components (e.g., `Scaffold`, `NavigationBar`, `NavigationBarItem`, `CenterAlignedTopAppBar`).
- **Latest Jetpack Compose UI** patterns for a bottom navigation bar with **3 tabs**.

You can customize the icons, labels, and screen content to suit your app’s needs.

---

### Key Points

1. **CenterAlignedTopAppBar (M3)**
    - We’re using `CenterAlignedTopAppBar` from `androidx.compose.material3` for a modern look.

2. **NavigationBar (M3)**
    - Material 3’s replacement for the old BottomNavigation.
    - `NavigationBarItem` is used to create each tab.

3. **`selectedItem`**
    - A local state in `BikeHomeScreen` that tracks which tab is currently selected.
    - Clicking a tab updates this state, and the content is updated accordingly in the `Scaffold`’s content.

4. **Screen Content**
    - In a real app, each screen (Home, Routes, Settings) might be more elaborate or use the official Navigation Compose library for advanced navigation. This snippet simply demonstrates a straightforward, **single-activity** approach with a bottom nav.

---

This setup gives you a **scaffold** with a **Material 3** top bar and a **bottom navigation bar** of three items. You can extend or modify the screens as needed (e.g., adding your route planning UI, maps, or settings).