Of course. I will create the `FAB.md` design document. Here is the content for the file.

***

# Design Document: Floating Action Button (FAB) Management

This document outlines the design, behavior, and implementation plan for the context-aware Floating 
Action Button (FAB) in the PhotoDo application.

## 1. Guiding Principle

The FAB's primary purpose is to provide the most relevant "add" action based on the user's current context. 
Its state (icon, text, and action) is not static but dynamically changes depending on the content visible on the screen. 
This creates an intuitive and powerful user experience, ensuring the user always knows what action the FAB will perform.

The logic is driven by the application's navigation state, making it inherently adaptive and consistent 
across all screen sizes, from compact phones ("closed") to large tablets ("open").

---

## 2. Behavior Across Application Tabs

### 2.1. Home Tab

The Home tab has the most complex behavior as its content changes to display one, two, or three panes of 
information on expanded screens. The FAB adapts to the most detailed pane currently in focus.

#### Context A: Viewing Only the Category List (1 Pane)
* **User View:** The initial state of the Home tab. The first column (Categories) is visible, and a placeholder is shown in the second column.
* **FAB State:** **"Add Category"**
* **Action:** Triggers an event to add a new top-level category. This will likely open a bottom sheet for the user to enter the new category's name.

#### Context B: A Category is Selected (2 Panes)
* **User View:** The user has clicked a category. The first column (Categories) remains visible, and the second column now shows the list of tasks for that selected category.
* **FAB State:** **"Add List"**
* **Action:** Triggers an event to add a new task list *to the currently selected category*. This will likely open a bottom sheet for the user to enter the new list's name.

#### Context C: A Task List is Selected (3 Panes)
* **User View:** The user has clicked a task list in the second column. All three columns are now visible: Categories, Task Lists, and the Task Detail view.
* **FAB State:** **"Add Item"**
* **Action:** Triggers an event to add a new photo/item *to the currently viewed task list*. This is expected to navigate to a full-screen camera or photo picker interface.

### 2.2. Tasks Tab

The "Tasks" tab provides a focused view of the task lists for a single category.

#### Context A: Viewing a Task List
* **User View:** The user is on the Tasks tab, viewing the list of tasks for the last-interacted-with category.
* **FAB State:** **"Add List"**
* **Action:** Triggers an event to add a new task list to the current category.

#### Context B: Viewing Task Details
* **User View:** The user has clicked on a task list and has navigated to its detail screen.
* **FAB State:** **"Add Item"**
* **Action:** Identical to the Home tab's detail view. It triggers an event to add a new photo/item to the currently viewed task list.

### 2.3. Settings Tab

The Settings tab is for configuration and information, not content creation.

* **FAB State:** **Hidden**
* **Action:** None.

---

## 3. Implementation Strategy

The FAB is a single, global `ExtendedFloatingActionButton` composable located in `MainScreen.kt`. Its state is controlled by a `FabState` data class instance, which is hoisted in `MainScreen`.

Each navigation destination (`entry` within the `NavDisplay`) is responsible for determining the correct FAB state for its context. It does this by calling the `setFabState` function (which is passed down) from within a `LaunchedEffect`.

-   **Hiding the FAB:** A screen can hide the FAB by calling `setFabState(null)`.
-   **Changing the FAB:** A screen changes the FAB's text and action by calling `setFabState(FabState("New Text") { /* new action */ })`.
-   **Context-Aware Logic:** For screens with multiple contexts (like the `HomeScreen`), a `LaunchedEffect` observes the relevant state (e.g., `uiState.selectedCategory`) and calls `setFabState` whenever that state changes.

## 4. Summary Table

| Current Screen/Pane in Focus                                | FAB Text         | FAB Action                               | Device State      |
| :---------------------------------------------------------- | :--------------- | :--------------------------------------- | :---------------- |
| **Home Tab:** Category List (Pane 1)                        | **"Add Category"** | Add a new Category                       | Open or Closed    |
| **Home Tab:** Task List (Pane 2)                            | **"Add List"** | Add a new List to the selected Category  | Open or Closed    |
| **Tasks Tab:** Task List                                    | **"Add List"** | Add a new List to the current Category   | Open or Closed    |
| **Any Tab:** Task Detail (Pane 3)                           | **"Add Item"** | Add a new Photo/Item to the current List | Open or Closed    |
| **Settings Tab** | **(Hidden)** | None                                     | Open or Closed    |