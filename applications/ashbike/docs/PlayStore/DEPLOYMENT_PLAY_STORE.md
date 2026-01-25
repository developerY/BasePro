# 🚀 Google Play Store Deployment Guide

This document outlines the **standard, production-ready steps** to deploy our Android app to the **Google Play Store**. It is written to be repeatable, auditable, and friendly for both first-time and experienced Android developers.

---

## 1. Prerequisites

Before starting, ensure the following are in place:

- ✅ Google Play Developer account
- ✅ App has a unique `applicationId`
- ✅ App icon, feature graphic, and screenshots prepared
- ✅ Privacy Policy URL available
- ✅ App complies with Google Play policies

---

## 2. App Configuration

### 2.1 Application ID

Ensure the `applicationId` is **final and immutable**:

```gradle
android {
    defaultConfig {
        applicationId "com.yourcompany.yourapp"
    }
}
```

⚠️ Changing this later will require publishing a *new app*.

---

### 2.2 Versioning

Update version values before every release:

```gradle
android {
    defaultConfig {
        versionCode 42
        versionName "1.3.0"
    }
}
```

**Guidelines**:
- `versionCode` → increment **every release**
- `versionName` → user-visible semantic version

---

## 3. Signing Configuration

### 3.1 Generate Upload Keystore

```bash
keytool -genkeypair \
  -keystore upload-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias upload
```

🔐 **Store securely**:
- Keystore file
- Alias
- Passwords

---

### 3.2 Configure Signing in Gradle

```gradle
android {
    signingConfigs {
        release {
            storeFile file("upload-keystore.jks")
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

✅ Use **environment variables**, never hardcode secrets.

---

## 4. Build Release Artifact

Google Play requires **Android App Bundles (AAB)**.

### 4.1 Build AAB

```bash
./gradlew bundleRelease
```

Output:
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 5. Play Console Setup

### 5.1 Create App

1. Open **Google Play Console**
2. Click **Create app**
3. Select:
   - Default language
   - App name
   - App type (App / Game)
   - Free or Paid

---

### 5.2 App Information

Fill out the following sections:

- 📄 App details
- 🖼 Store listing
- 🛡 Privacy Policy
- 👶 Target audience
- 📋 Data safety form
- 📱 App access (login instructions if required)

⚠️ All sections must be complete before publishing.

---

## 6. Upload Release

### 6.1 Choose Track

Recommended flow:

- **Internal testing** → quick validation
- **Closed testing** → QA & stakeholders
- **Open testing** → beta users
- **Production** → public release

---

### 6.2 Upload AAB

1. Go to **Release → Testing → Internal / Closed / Production**
2. Create new release
3. Upload `app-release.aab`
4. Add release notes

---

## 7. Review & Publish

### 7.1 Pre-Launch Checks

- ✅ App installs successfully
- ✅ No crashes on launch
- ✅ Permissions justified
- ✅ Screenshots match app behavior

---

### 7.2 Submit for Review

- Click **Review Release**
- Resolve all warnings/errors
- Submit for Google review

⏳ Review time: typically **a few hours to 2 days**

---

## 8. Post-Launch

After approval:

- 📊 Monitor **Play Console → Vitals**
- 🐞 Watch crash & ANR rates
- 💬 Review user feedback
- 🚀 Plan next release

---

## 9. CI/CD (Optional but Recommended)

For production teams:

- GitHub Actions / GitLab CI
- Automated versioning
- Gradle `bundleRelease`
- Play Publisher API
- Fastlane integration

---

## 10. Common Pitfalls

- ❌ Lost keystore (irreversible without Play App Signing)
- ❌ Missing privacy policy
- ❌ Incorrect target SDK
- ❌ Forgetting to bump `versionCode`

---

## 11. References

- Google Play Console
- Android App Bundles (AAB)
- Play App Signing
- Play Store Policy Center

---

✅ **This document should live in the repo root as:**

```
/DEPLOYMENT_PLAY_STORE.md
```

---

Happy shipping 🚢

