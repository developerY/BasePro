### Proposed `data` Module Structure

```plaintext
core/
├── data/
│   ├── src/
│   │   └── main/
│   │       ├── graphql/                             # GraphQL query files
│   │       │   ├── SearchQuery.graphql
│   │       │   └── schema.json
│   │       ├── java/com/ylabz/basepro/core/data
│   │       │   ├── api/                             # API interfaces and clients
│   │       │   │   ├── interfaces/                  # Interface definitions for APIs
│   │       │   │   │   ├── MapsAPI.kt
│   │       │   │   │   └── YelpAPI.kt
│   │       │   │   ├── client/                      # Client configurations for API
│   │       │   │   │   ├── MapsClient.kt
│   │       │   │   │   └── YelpClient.kt
│   │       │   │   └── apollo/                      # GraphQL-specific setup
│   │       │   │       └── Apollo.kt
│   │       │   ├── dto/                             # Data Transfer Objects (DTOs)
│   │       │   │   ├── BusinessInfo.kt
│   │       │   │   └── Coordinates.kt
│   │       │   ├── mappers/                         # Mappers for transforming data
│   │       │   │   └── GraphQLMappers.kt
│   │       │   ├── repository/                      # Repositories for managing data sources
│   │       │   │   ├── DrivingPtsRepository.kt
│   │       │   │   ├── DrivingPtsRepImp.kt
│   │       │   │   └── YelpRepository.kt
│   │       │   ├── di/                              # Dependency Injection
│   │       │   │   ├── DataModule.kt
│   │       │   │   └── NetworkModule.kt
│   │       │   └── rest/                            # REST-related configurations, if applicable
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts                             # Build configuration for core-data
```

### Explanation of Each Directory

1. **`api/`**: Contains all classes related to APIs. This includes:
    - **`interfaces/`**: Interfaces defining the API endpoints like `MapsAPI` and `YelpAPI`.
    - **`client/`**: Client implementations, such as `MapsClient` and `YelpClient`.
    - **`apollo/`**: Specific configurations for GraphQL, e.g., `Apollo.kt`.

2. **`dto/`**: Data Transfer Objects, which are used for holding data from API responses and passing
   them to other layers, such as `BusinessInfo` and `Coordinates`.

3. **`mappers/`**: Mappers transform network data models (DTOs) into entities that are useful for
   the app, especially for cases where `GraphQLMappers.kt` converts GraphQL responses to usable app
   data structures.

4. **`repository/`**: Contains repository classes, which manage different data sources. For
   instance, `YelpRepository` combines data from `YelpClient` and performs any required data
   processing.

5. **`di/`**: Holds Dependency Injection modules, such as `DataModule.kt` for repositories and
   `NetworkModule.kt` for API and network dependencies.

6. **`rest/`**: This is an optional directory if you also have REST API calls. This could hold
   Retrofit or other REST-specific configurations.

### Why This Structure?

This setup is modular, where each responsibility is separated, and it’s easier to manage
dependencies and testing. All network calls and data transformations are now centralized in
`core/data`, which aligns with Google’s **Now in Android** approach where **data** encapsulates all
data management (network, local DB, etc.).

### What About `core/model`?

You might still want **`core/model`** if you have:

- **Data Models** that are shared across features and not specific to network or repository
  implementations.
- **Domain Models** that represent clean, core data structures for the application, especially if
  you need transformations between network data (DTOs) and domain-specific data (entities).

In this setup:

- **DTOs** specific to APIs or repositories are kept in `data`.
- **Domain Models** that are shared and need to be accessed across multiple features would go in *
  *`core/model`**.

This provides a separation where **`core/data`** manages the data sources and **`core/model`**
provides app-wide reusable models.

Let me know if this breakdown clarifies your structure or if you have further questions!