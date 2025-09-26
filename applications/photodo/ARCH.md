# PhotoDo - TaskView

## Overview

**TaskView** is a "photo-first" to-do application, reimagined for foldable devices like the Pixel Fold.
It moves beyond traditional text-based task lists, offering a more intuitive and visual way to organize your life.
The core idea is to use images as the primary element for your to-do items, making tasks easier to recognize and manage at a glance.

This version is a complete rewrite of the original PhotoDo app, which is currently available on the
Google Play Store. This new version is built from the ground up using the latest in Android development,
including Jetpack Compose, Material 3 Expressive, and a modern, modular architecture.


---

## The Core Concept

Instead of a simple, flat list of tasks, TaskView introduces a gentle hierarchy to keep you organized without unnecessary complexity:

* **Projects**: The main screen of the app is a grid of your "Projects." A project can be anything from a "Shopping List" to "Car Improvements" or "Home Renovation." Each project is represented by a visually rich card, giving you an immediate sense of what it contains.
* **Tasks**: Tapping on a project takes you to a two-pane view, perfect for a foldable screen. The left pane (the master pane) shows the list of tasks within that project. Each task has a status (`To-Do` / `Done`), a notes section, and a priority flag.
* **Interactive Photos**: The right pane (the detail pane) is where the magic happens. This pane displays the photos associated with a task. These aren't just static images; they are interactive checklist items. For example, on your shopping list, as you pick up an item, you can simply tap its photo to mark it as complete.

---

## Key Features

* **Photo-First Approach**: Use your camera to quickly add tasks. Machine learning is used to convert text from photos into to-do items.
* **Foldable-Optimized UI**: The master-detail layout is specifically designed to take full advantage of the screen real estate on foldable devices.
* **Interactive Checklists**: Turn your photos into a tappable to-do list for a more intuitive and satisfying way to track your progress.
* **Quick Annotations**: Add notes or mark up your photos to add extra context to your tasks.
* **Drag & Drop**: Seamlessly drag images from other apps (like a web browser) and drop them directly into your task lists on a foldable device.
* **Canvas Mode**: When your device is fully unfolded, you can switch to a free-form "Canvas Mode" to arrange and organize your task photos in a way that makes sense to you.
* **Reminders**: Set alarms for your tasks so you never miss a deadline.

---

## Architecture

TaskView is built on a modern, modular Android architecture (MAD), ensuring a scalable, testable, and maintainable codebase. The architecture is heavily influenced by the principles established in the `ashbike` sample application.

* **Unidirectional Data Flow (UDF)**: The app follows a strict UDF pattern, where data flows down from the `ViewModel` to the UI, and events flow up from the UI to the `ViewModel`. This makes the state of the app predictable and easy to debug.
* **Separation of Concerns**: The code is clearly divided into layers:
  * **UI Layer**: Composable functions that are stateless and driven by a `UiState` object.
  * **ViewModel Layer**: The "brains" of the operation. It handles business logic, processes events, and prepares the `UiState` for the UI.
  * **Data Layer**: A dedicated `database` module that handles all data persistence. This layer is the single source of truth for the app's data.
* **Dependency Injection with Hilt**: We use Hilt to manage dependencies throughout the app, which is crucial for creating a clean, decoupled, and testable architecture.

---

## Technology Stack

* **UI**: Jetpack Compose, Material 3 Expressive
* **Navigation**: Compose Navigation 3
* **Architecture**: MVVM, Unidirectional Data Flow (UDF)
* **Dependency Injection**: Hilt
* **Database**: Room

---

## Future Enhancements
* **Gemini Nano Integration**: We are exploring the integration of on-device AI with Gemini Nano. This will allow users to get real-time, contextual help with their tasks. For example, you could take a picture of a flat tire, and the app would not only add it to your to-do list but also provide you with step-by-step instructions on how to change it, right on your device.
* **Place Integration**: We plan to integrate with maps to allow you to associate tasks with specific locations.
* **Calendar Integration**: We will be adding the ability to sync your tasks with your calendar.


---
## Future Gemini Nano Integration
### The Power of On-Device AI

* **Contextual Awareness**: Gemini Nano can analyze the image and the text of a to-do item to understand the user's intent. It's not just a to-do item; it's a real-world problem to be solved.
* **Instantaneous Help**: Because Gemini Nano runs on-device, the assistance is immediate. There's no latency from a round trip to a server.
* **Privacy-Focused**: The user's to-do lists, which can be very personal, remain on their device. This is a huge selling point.
* **Actionable Assistance**: Gemini Nano can provide concrete, actionable steps to help the user complete their tasks, making the app incredibly practical.

### User Flow Example

Let's refine the user flow with this new feature:

1.  **Create a Task**: The user takes a picture of a leaky faucet and creates a task: "Fix this drip."
2.  **Get Assistance**: Next to the task, a new "Ask Gemini" button appears. The user taps it.
3.  **Receive On-Device Help**: Gemini Nano analyzes the image and the task name. A chat window appears with a message like: "It looks like you have a leaky faucet. I can help with that. Would you like me to guide you through the repair?" The user can then ask follow-up questions like, "What tools will I need?" or "What's the first step?" and Gemini Nano will provide the answers.

This transforms the app from a simple reminder to an interactive assistant that empowers users to get things done.

---

#### Key Features

* **Photo-First Approach**: Use your camera to quickly add tasks. Machine learning is used to convert text from photos into to-do items.
* **Foldable-Optimized UI**: The master-detail layout is specifically designed to take full advantage of the screen real estate on foldable devices.
* **Interactive Checklists**: Turn your photos into a tappable to-do list for a more intuitive and satisfying way to track your progress.
* **Quick Annotations**: Add notes or mark up your photos to add extra context to your tasks.
* **Drag & Drop**: Seamlessly drag images from other apps (like a web browser) and drop them directly into your task lists on a foldable device.
* **Canvas Mode**: When your device is fully unfolded, you can switch to a free-form "Canvas Mode" to arrange and organize your task photos in a way that makes sense to you.
* **Reminders**: Set alarms for your tasks so you never miss a deadline.

#### Future Enhancements

* **Gemini Nano Integration**: We are exploring the integration of on-device AI with Gemini Nano. This will allow users to get real-time, contextual help with their tasks. For example, you could take a picture of a flat tire, and the app would not only add it to your to-do list but also provide you with step-by-step instructions on how to change it, right on your device.
* **Place Integration**: We plan to integrate with maps to allow you to associate tasks with specific locations.
* **Calendar Integration**: We will be adding the ability to sync your tasks with your calendar.