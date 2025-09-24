# PhotoDo Database Module (`/db`)

This module is the single source of truth for all data in the PhotoDo application. It is responsible for creating, storing, and managing all persistent data, ensuring a clean separation between the data layer and the rest of the application.

Following the principles of a Modern Android Development (MAD) architecture, this module is designed to be a completely independent and self-contained data layer.

## Architecture

The database module is built on the following key components:

* **Room Database**: We use the Room persistence library to create and manage the SQLite database. This provides an abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.
* **Entities**: These are the Kotlin data classes that represent the tables in our database. Each entity corresponds to a table, and each instance of an entity represents a row in the table.
* **Data Access Object (DAO)**: The DAO is an interface that defines all the database operations (queries, inserts, updates, deletes) that the application can perform. This is the only place where SQL queries are defined.
* **Repository**: The repository is the public API of the database module. It is the only point of contact for the rest of the application (primarily the ViewModels) to interact with the data. It abstracts the data source (in this case, the Room database) from the rest of the app, so that the app doesn't need to know how the data is stored.

## Database Schema

The PhotoDo database is designed around a simple, hierarchical structure of Projects, Tasks, and Photos.

* **`ProjectEntity`**: Represents a project, which is a collection of tasks (e.g., "Shopping List," "Car Maintenance").
* **`TaskEntity`**: Represents a single to-do item within a project. It includes properties such as status, notes, and priority.
* **`PhotoEntity`**: Represents a photo that is associated with a task. It contains a foreign key that links it back to a specific `TaskEntity`.

## Dependency Injection

We use **Hilt** for dependency injection throughout the application. The `DatabaseModule.kt` file within this module is responsible for providing the necessary dependencies for the database, including the DAO and the `PhotoDoRepository`. This allows for a clean, decoupled architecture where the repository can be easily injected into any ViewModel that needs it.

## How It Works

1.  **ViewModel Request**: A ViewModel (e.g., `PhotoDoListViewModel`) needs to get a list of tasks for a project.
2.  **Repository Call**: The ViewModel calls a function on the `PhotoDoRepository` (which has been injected into it by Hilt).
3.  **DAO Operation**: The repository, in turn, calls the appropriate function on the `PhotoDoDao` interface.
4.  **Database Query**: The DAO executes the SQL query on the Room database.
5.  **Data Flow**: The data flows back up the chain, from the database to the DAO, to the repository, and finally to the ViewModel, which then exposes it to the UI.