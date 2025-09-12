# Android Versioning Strategy

A solid versioning strategy uses two distinct but related numbers:
1.  **`android:versionName`**: The public-facing version number, for users.
2.  **`android:versionCode`**: The internal version number, for Google Play.

---

## 1. User-Facing Version: `versionName` (Semantic Versioning)

The `versionName` should be clear and meaningful to users. For this, we use **Semantic Versioning (SemVer)**. It gives a clear, universal structure to your release versions.

The format is **`MAJOR.MINOR.PATCH`** (e.g., `3.1.0`).

* **`MAJOR`**: Increment for incompatible, breaking API changes.
* **`MINOR`**: Increment for new, backward-compatible features.
* **`PATCH`**: Increment for backward-compatible bug fixes.

This system communicates the nature of changes with each new release. A pre-release version can also be denoted by appending a hyphen (e.g., `1.0.0-beta`).

---

## 2. Internal Version: `versionCode` (Integer Scheme)

> We suggest using a version code with at least 7 digits: integers that represent the supported configurations are in the higher order bits, and the version name (from `android:versionName`) is in the lower order bits.

The `versionCode` is a single, positive integer that must increase with every release. The 7-digit scheme below combines API level, screen size, and the `versionName` into one integer.

### `versionCode` Structure

The 7-digit integer is broken down into three parts:

| Digits | Position | Represents | Example (`1134310`) |
| :--- | :--- | :--- | :--- |
| **`XX`**`00000` | 1-2 | **API Level** | `11` |
| `00`**`XX`**`000` | 3-4 | **Screen Size** or **GL Texture Format** | `34` |
| `0000`**`XXX`** | 5-7 | **Application `versionName`** | `310` |

---

### Examples

#### Example 1: Basic Implementation (API Level Only)

For an application with `versionName` **3.1.0**, the `versionCode` would be:
* **API level 4:** `0400310`
* **API level 11:** `1100310`

Here, the first two digits are for the API Level (`04` and `11`), the middle two (`00`) are unused, and the last three (`310`) correspond to the `versionName` of `3.1.0`.

#### Example 2: Advanced Implementation (API Level and Screen Size)

This scheme is powerful enough to generate different APKs based on both API Level and screen size.

**`0412310`**
* **`04`**: API Level 4+
* **`12`**: Screen size (small to normal)
* **`310`**: App `versionName` (3.1.0)

**`1134310`**
* **`11`**: API Level 11+
* **`34`**: Screen size (large to xlarge)
* **`310`**: App `versionName` (3.1.0)