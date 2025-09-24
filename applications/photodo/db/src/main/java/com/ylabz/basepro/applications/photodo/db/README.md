Of course. Here is a detailed markdown file explaining the database entities for your PhotoDo application.

-----

# PhotoDo Database Entities

This document provides a detailed breakdown of the Room database entities used in the PhotoDo application. These entities define the structure of the application's data and are the foundation of the `photodo/database` module.

## 1\. `ProjectEntity.kt`

This entity represents a "Project," which is a high-level container for a group of related tasks. For example, a user might have projects for "Home Renovations," "Work," or a "Shopping List."

### Purpose

To group and organize tasks into meaningful categories. This is the top-level organizational unit in the application.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val projectId: Long = 0,
    val name: String,
    val description: String? = null
)
```

### Field-by-Field Breakdown

| Field | Data Type | Description |
| :--- | :--- | :--- |
| `projectId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each project. This ensures that every project has a distinct ID. |
| `name` | `String` | The name of the project, as provided by the user (e.g., "Groceries," "Car Maintenance"). This is a required field. |
| `description` | `String?` | An optional, longer description of the project, providing additional context. |

-----

## 2\. `TaskEntity.kt`

This entity represents a single "Task," which is a to-do item that belongs to a specific project. This is the core of the to-do list functionality.

### Purpose

To store the details of each individual to-do item, including its status, priority, and any associated notes.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val projectId: Long,
    var name: String,
    var notes: String? = null,
    var status: String = "To-Do", // "To-Do" or "Done"
    var priority: Int = 0, // 0 for normal, 1 for high
    val creationDate: Long = System.currentTimeMillis(),
    var dueDate: Long? = null
)
```

### Field-by-Field Breakdown

| Field | Data Type | Description |
| :--- | :--- | :--- |
| `taskId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each task. |
| `projectId` | `Long` | **Foreign Key**. This field links the task to a `ProjectEntity`, creating a many-to-one relationship (many tasks can belong to one project). |
| `name` | `String` | The name of the task (e.g., "Buy milk and eggs"). |
| `notes` | `String?` | An optional field for more detailed notes about the task. |
| `status` | `String` | The current status of the task. It is recommended to use an enum or a sealed class for this in practice, but for the database, a `String` is used. The default is "To-Do". |
| `priority` | `Int` | An integer to represent the task's priority. For example, 0 could be normal priority and 1 could be high priority. |
| `creationDate` | `Long` | The timestamp (in milliseconds) of when the task was created. This is automatically set to the current time. |
| `dueDate` | `Long?` | An optional timestamp for the task's due date. |

-----

## 3\. `PhotoEntity.kt`

This entity represents a single photo that is associated with a specific task. This is what makes the application "photo-first."

### Purpose

To store the URI and other metadata for each photo, and to link it to the task it belongs to.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val photoId: Long = 0,
    val taskId: Long,
    val uri: String,
    val caption: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

### Field-by-Field Breakdown

| Field | Data Type | Description |
| :--- | :--- | :--- |
| `photoId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each photo. |
| `taskId` | `Long` | **Foreign Key**. This field links the photo to a `TaskEntity`, creating a many-to-one relationship (many photos can be associated with one task). |
| `uri` | `String` | The URI (Uniform Resource Identifier) of the photo file on the device. This is a `String` that represents the path to the image. |
| `caption` | `String?` | An optional caption for the photo. |
| `timestamp` | `Long` | The timestamp of when the photo was added or taken. |