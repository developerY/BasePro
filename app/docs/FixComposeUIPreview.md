Great question — and this is a **very common mistake** when setting up tooling for previews and
builds.

You're currently doing this:

```kotlin
implementation(libs.androidx.ui.tooling) // 👈 included in all variants
debugImplementation(libs.androidx.ui.tooling) // 👈 also included only in debug
```

So the **same dependency (`ui.tooling`) is duplicated**: once in `implementation` (global), and
again in `debugImplementation`.

---

## ✅ Fix: Use the Right Dependency for Compose Preview Tooling

### 🔧 Step-by-Step Fix

1. **Remove the general `implementation(libs.androidx.ui.tooling)` line**.
2. Keep only the `debugImplementation` version (as it should only be used in debug builds for
   preview tooling):

```kotlin
// ❌ REMOVE this:
// implementation(libs.androidx.ui.tooling)

// ✅ KEEP THIS:
debugImplementation(libs.androidx.ui.tooling)
```

This ensures:

- Your **release APK/AAB doesn’t include Compose preview tooling** (saves size & avoids issues)
- Your **debug builds still show previews** in Android Studio

---

## 💡 Tip: Want Preview Annotations in Release Too?

If you use `/*` but also want **design-time rendering (only)** without errors in both
debug/release (but no runtime impact), do this:

```kotlin
implementation(libs.androidx.ui.toolingPreview) // lightweight stub
debugImplementation(libs.androidx.ui.tooling)   // full runtime tooling
```

In `libs.versions.toml` make sure you have:

```toml
androidx-ui-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }
```

Then update your `build.gradle.kts`:

```kotlin
implementation(libs.androidx.ui.toolingPreview)
debugImplementation(libs.androidx.ui.tooling)
```

---

## ✅ Summary

| What to Do                                                 | Why                                          |
|------------------------------------------------------------|----------------------------------------------|
| ✅ Remove `implementation(libs.androidx.ui.tooling)`        | Avoid duplicate in release                   |
| ✅ Use `debugImplementation(libs.androidx.ui.tooling)` only | Preview tools are for dev only               |
| ✅ Optionally add `ui-tooling-preview`                      | Enables preview annotations w/o full runtime |

---

Let me know if you'd like to add this to your shared `libs.versions.toml` too — I can generate the
entry for you.