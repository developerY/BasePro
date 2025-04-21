Yes—this is exactly the recommended “MAD” master/detail pattern in Jetpack Compose:

1. **One‑Activity + NavHost**  
   You host **all** screens in a single `NavHost`, with each destination simply declaring its route and any NavArgs.

2. **One‑Screen, One‑ViewModel**
    - **MasterViewModel** (here your `TripsViewModel`) drives the list of rides.
    - **DetailViewModel** (your new `RideDetailViewModel`) is scoped to the detail Composable and just fetches the one ride via its `rideId` nav‑argument.

3. **UI‑State Hoisting & Stateless Composables**
    - Screens (`TripsUIRoute`, `RideDetailScreen`, etc.) take only the data they need (lists, single ride).
    - Navigation logic lives outside in the graph (`bikeNavGraph`), not buried in your UI.

4. **SavedStateHandle ↔ NavArgs**  
   The detail VM automatically picks up the `rideId` argument via `SavedStateHandle`, so you never manually pass IDs through UI callbacks or `LaunchedEffect`.

This approach gives you:

- **Clear separation** of concerns
- **Lifecycle‑scoped** ViewModels (no stale data)
- **Easy deep‑links** (detail screen can be launched in isolation)
- **Simple tests & previews** (Composables render purely from parameters)

It’s the pattern you’ll see in official Google samples (Sunflower, Now in Android, JetNews) and in most production Compose apps.