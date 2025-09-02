The blog post announces that the Health Connect Jetpack SDK is now in beta and introduces several
important changes to help improve the clarity, completeness, and usefulness of health data. In
particular, if you’re already using Health Connect in your app, you’ll need to update your code to
comply with these new requirements. Here’s a breakdown of the key updates and example code for how
to adapt your implementation:

---
lib:: androidx.health.connect:connect-client

## Key Updates

### 1. Mandatory Recording Method for Data Entries

When writing records (for example, a steps record), you must now provide a recording method via the
metadata. This improves data accuracy and helps provide richer insights for end users.

- **Before:**  
  Previously, you might have created a record without metadata:
  ```kotlin
  // Incorrect – metadata is missing
  val record = StepsRecord(
      count = 888,
      startTime = START_TIME,
      endTime = END_TIME,
  )
  ```

- **After:**  
  Now you need to include metadata using the provided factory methods. For manual entries, for
  instance:
  ```kotlin
  // Correct – metadata with recording method provided
  val record = StepsRecord(
      count = 888,
      startTime = START_TIME,
      endTime = END_TIME,
      metadata = Metadata.manualEntry(clientRecordId = "client id")
  )
  ```
  If your code was previously calling the Metadata constructor directly, update it to use the new
  factory method instead.

### 2. Mandatory Device Type Specification

When creating a `Device` object, you must now specify the device type. This is required for data
recorded automatically or actively.

- **Before:**  
  You might have instantiated a device like this:
  ```kotlin
  // Incorrect – missing device type
  val device = Device()
  ```

- **After:**  
  Update your code to include a device type (e.g., phone):
  ```kotlin
  // Correct – device type provided
  val device = Device(type = Device.Companion.TYPE_PHONE)
  ```

### 3. New Permissions in the Manifest

The update also introduces a dedicated background read permission. If your app needs to access
Health Connect data in the background, you must now declare this permission in your manifest.

- **Manifest Entry:**
  ```xml
  <application>
      ...
      <uses-permission android:name="android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND" />
      ...
  </application>
  ```

Additionally, if you want to access health data beyond the default 30-day window, you’ll need to
request the new `PERMISSION_READ_HEALTH_DATA_HISTORY`.

### 4. Additional Data Types

The SDK now includes new data types such as:

- **Exercise Routes:** For sharing routes between apps.
- **Skin Temperature:** For tracking peripheral body temperature.
- **Planned Exercise:** For managing and reading training plans.

If your app is meant to support these new data types, you’ll want to update your data models and
processing logic accordingly.

---

## What Needs to Change in Your Code?

- **For Writing Records:**  
  Update all instances where you write data to include the mandatory `recordingMethod` via the
  `Metadata` factory methods.

- **For Device Objects:**  
  Modify your device instantiation to specify the device type. This change ensures that data
  recorded by automatic or active methods is properly attributed.

- **For Permissions:**  
  Update your Android manifest to declare the new background reads permission, and if necessary, add
  the permission for reading extended health history.

- **For New Data Types:**  
  If applicable, extend your code to handle new data types like exercise routes, skin temperature,
  or planned exercise.

---

## Example Code Summary

### Updating a Steps Record:

```kotlin
// Before:
val record = StepsRecord(
    count = 888,
    startTime = START_TIME,
    endTime = END_TIME,
) // error: metadata is not provided

// After:
val record = StepsRecord(
    count = 888,
    startTime = START_TIME,
    endTime = END_TIME,
    metadata = Metadata.manualEntry(clientRecordId = "client id")
)
```

### Updating Device Instantiation:

```kotlin
// Before:
val device = Device() // error: type not provided

// After:
val device = Device(type = Device.Companion.TYPE_PHONE)
```

### Manifest Permission for Background Reads:

```xml
<application>
    ...
    <uses-permission android:name="android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND" />
    ...
</application>
```

---

These updates are designed to ensure that your app leverages Health Connect more effectively by
providing richer context with every data entry and improving overall data quality and
interoperability. For further details, you can review the complete release notes and documentation
linked in the blog post.

Sources:  
cite0†Android Developers Blog  
cite24†Beta documentation on Health Connect Jetpack SDK