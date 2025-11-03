### Plan DB:

1.  **Create a New Module:** Create a new Gradle module at `applications/photodo/database`.
2.  **Copy and Rename:** Copy the contents of `applications/ashbike/database` into your new `photodo/database` module.
3.  **Adapt for PhotoDo:** You will then go through the copied files and adapt them for the `photodo` data models. For example:
    * `BikeRideEntity.kt` becomes `ProjectEntity.kt` and `TaskEntity.kt`.
    * `RideLocationEntity.kt` becomes `PhotoEntity.kt` (which would have a foreign key to a `TaskEntity`).
    * `BikeRideDao.kt` becomes `PhotoDoDao.kt`, with new queries like `getTasksForProject()` or `getPhotosForTask()`.
    * `BikeRideDB.kt` becomes `PhotoDoDB.kt`, updated to include your new entities and DAO.
    * `BikeRideRepo.kt` and its implementation become `PhotoDoRepo.kt`, which will be the single source of truth for your `PhotoDoListViewModel`.