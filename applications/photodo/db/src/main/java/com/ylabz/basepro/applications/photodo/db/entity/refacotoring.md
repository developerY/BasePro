# Refactoring Summary

This document provides a recap of the significant refactoring changes made to the **PhotoDo** application.  
The primary goal was to align the codebase with a more intuitive and domain-specific naming convention.

---

## 1. Database Entities
The core data models of the application have been renamed to better reflect their purpose:

- **ProjectEntity → CategoryEntity**  
  Clarifies that a *Project* is a high-level category that contains multiple task lists.

- **TaskEntity → TaskListEntity**  
  Emphasizes that a *Task* is actually a list of tasks, not a single to-do item.

- **TaskWithPhotos → TaskListWithPhotos**  
  Updated to match the new `TaskListEntity` name.

- **PhotoEntity Foreign Key**  
  Corrected to point to the new `task_lists` table, ensuring proper data integrity.

---

## 2. Data Access Layer
The data access objects (DAOs) and repository layers were updated to reflect the new entity names and provide a more intuitive API:

- **PhotoDoDao**: All methods renamed to align with the new entity names (e.g., `getAllCategories`, `getTaskListsForCategory`).
- **PhotoDoRepo** and **PhotoDoRepoImpl**: Updated to use the new DAO methods and return the renamed entity types.

---

## 3. Database & Dependency Injection
The database setup and dependency injection modules were updated to accommodate the new schema:

- **PhotoDoDB**: The Room database class now references `CategoryEntity` and `TaskListEntity`.
- **DatabaseModule**: The Hilt module was updated to pre-populate the database with sample data using the new schema.

---

## 4. Navigation
The navigation keys were renamed for better clarity and to align with the new domain language:

- **PhotoDolListKey → TaskListKey**  
  Renamed to reflect that this key navigates to a screen showing a list of tasks.  
  Parameters were updated to use `categoryId`.

- **PhotoDoDetailKey → TaskListDetailKey**  
  Renamed for consistency. The parameter was updated to `listId`.

---

## 5. UI Layer (ViewModels & Composables)
The UI layer, including ViewModels, UI states, and composables, was updated to use the new entity names and navigation keys:

- **ViewModels** (`HomeViewModel`, `PhotoDoListViewModel`, `PhotoDoDetailViewModel`)  
  Updated to use the new repository methods and `UiState` objects.

- **UiState and Event Files**  
  Updated to use the new entity names (e.g., `HomeUiState` now uses `CategoryEntity`).

- **Composables** (`HomeScreen`, `CategoryList`, `TaskList`, `PhotoDoListUiRoute`, etc.)  
  Updated to accept the new entity types as parameters.

- **MainScreen.kt**  
  The main navigation host was updated to orchestrate the new navigation keys, events, and ViewModel methods, ensuring the entire app functions correctly with the new data model.
