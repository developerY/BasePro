# PhotoDo Database Entities

This document provides a detailed breakdown of the Room database entities used in the **PhotoDo** application.  
These entities define the structure of the application's data and form the foundation of the `applications/photodo/db` module.

---

## 1. `CategoryEntity.kt`

This entity represents a **Category**, which is a high-level container for a group of related task lists.  
For example, a user might have categories such as **"Home Renovations"**, **"Work"**, or **"Shopping"**.

### Purpose

To group and organize task lists into meaningful categories.  
This is the top-level organizational unit in the application.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val name: String,
    val description: String? = null
)
```

### Field-by-Field Breakdown

| **Field** | **Data Type** | **Description** |
|------------|---------------|-----------------|
| `categoryId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each category. |
| `name` | `String` | The name of the category, as provided by the user (e.g., “Groceries,” “Car Maintenance”). This is a required field. |
| `description` | `String?` | An optional, longer description of the category, providing additional context. |

---

## 2. `TaskListEntity.kt`

This entity represents a single **Task List**, which belongs to a specific category.

### Purpose

To store the details of each individual to-do list, including its status, priority, and description.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_lists",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])] // Added index for the foreign key
)
data class TaskListEntity(
    @PrimaryKey(autoGenerate = true)
    val listId: Long = 0,
    val categoryId: Long,
    val title: String,
    val description: String? = null,
    val status: String = "Incomplete", // e.g., "Incomplete", "Completed"
    val priority: Int = 0, // 0 for normal, 1 for high
    val creationTimestamp: Long = System.currentTimeMillis(),
    val dueTimestamp: Long? = null
)
```

### Field-by-Field Breakdown

| **Field** | **Data Type** | **Description** |
|------------|---------------|-----------------|
| `listId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each task list. |
| `categoryId` | `Long` | **Foreign Key**. Links the task list to a `CategoryEntity`, creating a many-to-one relationship (many lists can belong to one category). |
| `title` | `String` | The title of the task list (e.g., “Buy milk and eggs”). |
| `description` | `String?` | Optional notes about the task list. |
| `status` | `String` | The current status of the task list. Default: `"Incomplete"`. |
| `priority` | `Int` | Integer representing the task’s priority. (`0` = normal, `1` = high). |
| `creationTimestamp` | `Long` | The timestamp (in milliseconds) when the task list was created. Automatically set to the current time. |
| `dueTimestamp` | `Long?` | Optional timestamp for the task list’s due date. |

---

## 3. `PhotoEntity.kt`

This entity represents a single **Photo** that is associated with a specific task list.  
This is what makes the application “photo-first.”

### Purpose

To store the URI and metadata for each photo and link it to the task list it belongs to.

### File Content

```kotlin
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = TaskListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val photoId: Long = 0,
    val listId: Long,
    val uri: String,
    val caption: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
```

### Field-by-Field Breakdown

| **Field** | **Data Type** | **Description** |
|------------|---------------|-----------------|
| `photoId` | `Long` | **Primary Key**. A unique, auto-incrementing identifier for each photo. |
| `listId` | `Long` | **Foreign Key**. Links the photo to a `TaskListEntity`, creating a many-to-one relationship (many photos can belong to one list). |
| `uri` | `String` | The URI (Uniform Resource Identifier) of the photo file on the device. Represents the path to the image. |
| `caption` | `String?` | Optional caption for the photo. |
| `timestamp` | `Long` | The timestamp when the photo was added or taken. |
