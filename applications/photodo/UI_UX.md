# PhotoDo: UI and UX Flow for Foldable Devices

This document outlines the user interface (UI) and user experience (UX) flow for the PhotoDo application, with a specific focus on its adaptive behavior for foldable devices.

## Data Structure

The app's data is organized in a clear hierarchy:

1.  **Categories**: High-level groupings of related task lists (e.g., Home, Transportation, Work).
2.  **Lists**: Individual to-do lists within a category (e.g., Grocery List, Car Maintenance).
3.  **Items**: The individual tasks within a list, each represented by a detailed card with an image, text, and other information.

---

## Main Screen (Home Tab)

This is the first screen the user sees when they open the app. It's designed to provide a quick overview of their task lists, organized by category.

**Initial State (MVP):** The app will come pre-loaded with 'Home', 'Family', and 'Work' categories, and sample lists like 'Shopping' and 'Cleaning'. 
For the initial release, these default categories and lists cannot be deleted, but users can add new ones and modify existing ones.

### Folded State

* **Layout**: The screen features a carousel of categories at the top (e.g., Home, Transportation, Work). Below the carousel, there is a list of all the task lists associated with the selected category.
* **Interaction**:
    * The user can swipe through the category carousel to select a different category.
    * When a category is selected, the list below updates to show the relevant task lists (e.g., selecting "Home" shows "Grocery List," "Repair List," etc.).
    * Tapping on a task list (e.g., "Grocery List") navigates the user to the **List Screen** for that list.

### Unfolded State

* **Layout**: The screen is divided into two panes:
    * **Right Pane**: A list of all the categories (e.g., Home, Transportation, Work).
    * **Left Pane**: A list of the task lists for the selected category.
* **Interaction**:
    * The user can tap on a category in the right pane to select it.
    * When a category is selected, the left pane updates to show the corresponding task lists.
    * Tapping on a task list in the left pane navigates the user to the **List Screen**.

---

## List Screen (Tasks Tab)

This screen is where the user manages the individual items within a task list.

### Folded State

* **Layout**: The screen displays a list of all the items in the selected task list.
* **Interaction**:
    * The user can scroll through the list of items.
    * Tapping on an item opens its detailed card.

### Unfolded State

* **Layout**: The screen is divided into two panes:
    * **Right Pane**: A list of all the items in the selected task list.
    * **Left Pane**: A detailed card for the selected item.
* **Interaction**:
    * The user can select an item from the list in the right pane.
    * When an item is selected, its detailed card is displayed in the left pane.

---

## Creating Content

### Main Screen (Home Tab)

A **Split Button Floating Action Button (FAB)** is present to allow users to create new Categories and Lists.
* **Add Category**: Opens a dialog to enter a new category name.
* **Add List**: Opens a dialog to enter a new list name and select which category it belongs to.

### List Screen (Tasks Tab)

A standard **Floating Action Button (FAB)** is present.
* **Add Item**: Opens a camera-first interface to create a new task item within the current list.

---

## The Detail Card

The detail card provides a comprehensive view of a single task item. For the initial version, it will contain:

* **Image**: The primary focus of the card. Tapping the image will mark the item as complete.
* **Title/Text**: A text field for the item's name.
* **Notes**: An optional, expandable text area for more details.
* **Status**: A clear visual indicator (e.g., a checkbox or a prominent "Done" overlay on the image) to show if the item is `To-Do` or `Done`.
* **Progress Bar (MVP)**: A simple binary state (0% for To-Do, 100% for Done) that visually reflects the item's status.
* **Sound (MVP)**: UI elements to allow a user to record a short audio note and play it back.