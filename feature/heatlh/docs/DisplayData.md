You’ll want to strike the right balance between giving the user an at-a-glance summary of their
overall fitness, and letting them drill into the individual rides they actually did. Here’s a
pattern I’ve seen work well, whether you’re purely a Health Connect viewer or mixing in your own
Room cache:

---

## 1. Dashboard / Home screen: high-level summary + trends

**Why:** Users often open a fitness app to see “How am I doing this week?” or “What does my
heart-rate look like over time?”
**What to show:**

* **Totals & averages** for the primary metrics (Distance, Calories, Sessions, Avg HR) over a
  selectable window (today / 7 days / 30 days).
* A **sparkline** or **bar chart** showing the trend of calories burned or distance per day.
* A **quick‐access “Sync” button** to push any pending RoomDB rides into Health Connect (if using
  local fallback).

```kotlin
// Pseudocode for summary row
Row {
  SummaryTile("Distance", "24 km")
  SummaryTile("Calories", "1 850 kcal")
  SummaryTile("Rides", "5")
  SummaryTile("Avg HR", "118 bpm")
}
LineChart(data = weeklyDistance)   // e.g. seven points, one per day
```

---

## 2. Ride List: individual sessions

**Why:** Sometimes you want the detail—“Which ride burned the most calories?” or “What was my pace
on Friday?”
**What to show:**

* A vertical list of **ExerciseSessionRecord** entries, sorted newest→oldest.
* Each card includes:

    * **Date & time** (“May 17, 7:30 AM”)
    * **Duration** (“45 min”)
    * **Key metrics** (“12 km • 380 kcal • Avg HR 112”)
    * **Sync state icon** (if you still support local RoomDB as a cache)
* Tapping a card navigates to a **Ride Detail** screen.

```kotlin
@Composable
fun RideCard(session: ExerciseSessionRecord, isSynced: Boolean, onClick: ()->Unit) {
  Row(modifier = clickable(onClick)) {
    Column(modifier = weight(1f)) {
      Text(session.startTime.format("MMM d, h:mm a"))
      Text("${session.duration.toMinutes()} min • ${session.steps} steps")
    }
    Icon(if (isSynced) Icons.Check else Icons.Sync, contentDescription = null)
  }
}
```

---

## 3. Ride Detail screen

**Why:** To let the user explore every facet of that one ride.
**What to show:**

* **Interactive chart** of heart-rate vs. time (or pace vs. time).
* **Map preview** of the route (if you recorded GPS).
* A **breakdown** of steps, distance, calories, min/max/avg heart-rate.
* A **Delete** button that triggers your cascade-delete helper.

```kotlin
Column {
  Text(session.title ?: "Bike Ride")
  HeartRateChart(samples = samples)
  Text("Distance: ${distanceMeters / 1000} km")
  Text("Calories: ${calories} kcal")
  Button("Delete Ride", onClick = { viewModel.onDeleteSession(session.id) })
}
```

---

## 4. Fallback & Settings

* If Health Connect isn’t installed or permissions aren’t granted, fall back to your **RoomDB**
  cache: show the same UI but driven from local data.
* In **Settings**, let the user link/unlink Health Connect, clear the local cache, or revoke
  permissions.

---

### Why this feels natural to users

* **Top-level summary** answers the “How am I doing?” question instantly.
* **List of rides** shows the raw activities they can tap into.
* **Detail view** gives the full picture of a workout.
* **Health Connect backing** means data persists beyond your app, and can be read by other apps (
  Fit, watch faces, etc.).
* **RoomDB fallback** keeps the app functional even without GHC, and you can surface a “Sync now”
  prompt.

This three-tier approach (Dashboard → Ride List → Ride Detail) is exactly how Google’s own Fitness
app and many MAD samples structure their health and workout UIs—it gives both breadth and depth
without overwhelming the user.
