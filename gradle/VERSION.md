Of course. Here is the information from your image converted into a clean Markdown format suitable for a Git repository.

---

# Android Version Code Scheme

> We suggest using a version code with at least 7 digits: integers that represent the supported configurations are in the higher order bits, and the version name (from `android:versionName`) is in the lower order bits.

## Version Code Structure

The 7-digit `versionCode` is broken down into three parts:

| Digits | Position | Represents | Example (`1134310`) |
| :--- | :--- | :--- | :--- |
| **`XX`**`00000` | 1-2 | **API Level** | `11` |
| `00`**`XX`**`000` | 3-4 | **Screen Size** or **GL Texture Format** | `34` |
| `0000`**`XXX`** | 5-7 | **Application Version** (`versionName`) | `310` |

---

## Examples

### Example 1: Basic Implementation (API Level Only)

For an application with `versionName` **3.1.0**, the `versionCode` would be:
* **API level 4:** `0400310`
* **API level 11:** `1100310`

In this case, the first two digits are for the API Level (`04` and `11`), the middle two digits (`00`) are unused, and the last three digits (`310`) correspond to the `versionName` of `3.1.0`.

### Example 2: Advanced Implementation (API Level and Screen Size)

This scheme allows for generating different APKs based on both API Level and screen size.

#### `0412310`
* **`04`**: API Level 4+
* **`12`**: Screen size (small to normal)
* **`310`**: App version (3.1.0)

#### `1134310`
* **`11`**: API Level 11+
* **`34`**: Screen size (large to xlarge)
* **`310`**: App version (3.1.0)