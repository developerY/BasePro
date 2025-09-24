This plan is designed to be sequential, ensuring that each layer of the application is built upon a solid foundation.

### **Phase 1: The Data Foundation** 🧱

This initial phase is the most critical, as it establishes the database, data models, and the repository that will serve as the single source of truth for your entire application.

1.  **Create the `photodo/database` Module**: Following the blueprint of your `ashbike/database` module, create a new Gradle module at `applications/photodo/database`.

2.  **Define the Database Entities**: Inside your new module, create the following Room database entities:
    * **`ProjectEntity.kt`**: This will represent a project, such as "Shopping List" or "Home Cleaning."
    * **`TaskEntity.kt`**: This will represent a single to-do item within a project. It should include properties for status, notes, and priority.
    * **`PhotoEntity.kt`**: This will hold the information for each photo, including a foreign key to link it to a `TaskEntity`.

3.  **Implement the Data Access Object (DAO)**: Create a `PhotoDoDao.kt` interface with functions to insert, update, delete, and retrieve your entities from the database. This should include queries like `getTasksForProject()` and `getPhotosForTask()`.

4.  **Build the Database Class**: Create a `PhotoDoDB.kt` class that inherits from `RoomDatabase`. This class will define the database configuration and provide access to your DAO.

5.  **Construct the Repository**:
    * Create a `PhotoDoRepo.kt` interface that defines the functions for interacting with your data, such as `getProjects()`, `getTasks(projectId)`, and `getPhotos(taskId)`.
    * Create a `PhotoDoRepoImpl.kt` class that implements the `PhotoDoRepo` interface. This class will take your `PhotoDoDao` as a dependency and will be the single source of truth for all your application's data.

---

### **Phase 2: The "To-Do" List Feature** 📝

Now that you have a solid data layer, you can build the first user-facing feature: the list of to-do items.

1.  **Create the `PhotoDoListViewModel`**:
    * Inside the `applications/photodo/features/photodolist` module, create a `PhotoDoListViewModel.kt` file.
    * Inject your `PhotoDoRepository` into the ViewModel's constructor. Remember to annotate the ViewModel with `@HiltViewModel`.
    * This ViewModel will be responsible for fetching the list of tasks for a given project from the repository and exposing them to the UI through a `UiState` object.

2.  **Define the UI State and Events**:
    * Create a `PhotoDoListUiState.kt` file to represent the different states of your UI (e.g., `Loading`, `Success`, `Error`).
    * Create a `PhotoDoListEvents.kt` file to define the actions a user can take, such as clicking on a task or adding a new one.

3.  **Build the `PhotoDoListUiRoute`**:
    * Create a `PhotoDoListUiRoute.kt` file. This will be the main entry point for your feature.
    * This composable will receive the `PhotoDoListViewModel` using `hiltViewModel()`.
    * It will observe the `uiState` from the ViewModel and display the appropriate UI. It will also send user events up to the ViewModel.

4.  **Design the Task Card**: Create a `PhotoDoTaskCard.kt` composable to display a single to-do item in the list.

---

### **Phase 3: The Home Screen and Navigation** 🏠

With your list feature complete, you can now build the home screen and integrate your first feature into the app's navigation.

1.  **Create the `PhotoDoDetailViewModel`**:
    * In the `applications/photodo/features/home` module, create a `PhotoDoDetailViewModel.kt`.
    * This ViewModel will be responsible for loading the data for a single, selected to-do item.

2.  **Build the `PhotoDoDetailUiRoute`**: This composable will display the detailed view of a to-do item, including its photos.

3.  **Implement the Main Navigation**:
    * In your main `applications/photodo` module, you will define the navigation graph for the entire application.
    * This will include the navigation logic to go from the home screen (which will list the projects) to the `PhotoDoListUiRoute` (to show the tasks in a project) and then to the `PhotoDoDetailUiRoute` (to show the details of a task).

---

### **Phase 4: Camera and Photo Integration** 📸

This is where your app's "photo-first" concept comes to life.

1.  **Create a Camera Feature Module**: Create a new feature module, `applications/photodo/features/camera`, to encapsulate all camera-related functionality.

2.  **Implement the Camera UI**: Inside this module, create a composable that uses CameraX to provide a camera preview and allow the user to take a picture.

3.  **Integrate with the `PhotoDoDetailViewModel`**:
    * Add a button to your `PhotoDoDetailUiRoute` that navigates to your camera feature.
    * When a photo is taken, the result should be passed back to the `PhotoDoDetailViewModel`, which will then save the photo to the database via the `PhotoDoRepository`.

4.  **Implement Photo to Text (ML)**: Add the necessary ML Kit dependencies to your project. After a photo is taken, process the image to extract any text and populate the "notes" field of the task.

---

### **Phase 5: Advanced Features and Publish** ✨

Once the core functionality is in place, you can add the features that will make your app stand out.

1.  **Implement Alarms**:
    * Create a new feature module for alarms.
    * Use the `AlarmManager` to allow users to set reminders for their tasks.

2.  **Drag and Drop**:
    * Implement drag-and-drop functionality to allow users to reorder tasks and to drag images from other apps into your app, especially on foldable devices.

3.  **Canvas Mode**:
    * Create a "Canvas Mode" for the detail view that allows for freeform arrangement of photos, turning your app into a mini-whiteboard for tasks.

By following these steps, you will be able to build your "PhotoDo" application in a structured and scalable way, leveraging the powerful MAD architecture you have already established. Good luck with the build!