Yes—what you have (with `core/` for shared logic and `feature/` for vertical slices) is essentially
the **layered + feature modules** approach:

- **Core modules** (horizontal/layer modules) like `core/data`, `core/ui`, `core/model`, etc. hold
  cross-cutting concerns and shared functionality.
- **Feature modules** like `feature/camera`, `feature/maps`, `feature/nfc` each encapsulate a
  specific vertical slice of UI + logic.
- **Applications** (e.g., `applications/bike`, `applications/home`) pull in whichever features and
  core modules they need.

This is a very common and effective structure for larger, modular Android projects:

1. **Core** (horizontal layers)
    - Data, domain, and UI utilities used by multiple features.
2. **Features** (vertical slices)
    - Each feature stands alone, with its own composables, ViewModels, and dependencies on core.
3. **Apps**
    - Each app (e.g., “Bike App,” “Home App”) chooses which features (and core modules) to include.

So yes, you’re already doing the approach #3 (layered + feature modules). It’s a clean, scalable
setup for multi-module Compose projects.