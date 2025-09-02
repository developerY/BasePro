Absolutely—switching from the `typealias` “identity” mappers to full, separate domain ↔ entity
models later is painless. Your steps will be:

1. **Replace the alias**
    - Delete the `typealias Ride = RideEntity` and the identity `toDomain()/toEntity()` stubs.
    - Introduce your standalone `data class Ride(…)` in the domain layer.

2. **Fill out real mappers**
    - Change those two functions in your `RideMappers.kt` so they explicitly build a `RideEntity`
      from a `Ride` and vice versa.
    - Because your repo and ViewModel already call `.toEntity()` / `.toDomain()`, nothing else needs
      updating.

3. **Update signatures if you like**
    - If you prefer `BikeRideRepo` to deal in `Ride` rather than `RideEntity`, just update its
      return types/parameters. Your DAO stays the same, your mappers handle the translation.

4. **Swap types in one go**
    - A quick find‑and‑replace of `RideEntity` → `Ride` in your repo interfaces plus adding the new
      `Ride` data class is all it takes.

Because you’ve decoupled your layers behind those conversion functions, moving from the identity
alias to a true domain model later will be a few‑minute refactor, with zero risk to the rest of your
code.