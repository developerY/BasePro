# Google AI Glass (Android XR) Documentation Index

A curated list of official Google resources for building on the AI Glass platform.

[Code](https://developer.android.com/develop/xr/jetpack-xr-sdk/ai-glasses/build)
[DeSign](https://developer.android.com/design/ui/ai-glasses)
[Sample Code](https://github.com/android/ai-samples/tree/prototype-ai-glasses/samples/gemini-live-todo)

## 📚 Core Documentation
* **[Official Home - AI Glasses Design](https://developer.android.com/design/ui/ai-glasses)**
    * The central hub for all design and development guidelines for the transparent glass form factor.
* **[Surface Overview](https://developer.android.com/design/ui/ai-glasses/surfaces/overview)**
    * Explains the difference between "App View" (Main UI) and System UI overlays.

## 👆 Inputs & Interaction
* **[Input Methods & Hardware Controls](https://developer.android.com/design/ui/ai-glasses/guides/interaction/inputs)**
    * **Crucial:** Defines the specific Key Codes for Touchpad Swipes (`KEYCODE_DPAD_*`), Taps, and the "System Back" gesture.
* **[Focus Management](https://developer.android.com/design/ui/ai-glasses/guides/interaction/focus)**
    * How to handle the single-threaded focus model (Navigation vs. Interaction modes).
* **[Audio & Voice](https://developer.android.com/design/ui/ai-glasses/guides/interaction/audio)**
    * Best practices for earcons, voice feedback, and handling audio focus.

## 🎨 UI & Styles
* **[Visual Styles & Color](https://developer.android.com/design/ui/ai-glasses/styles/color)**
    * Guidelines on high-contrast design (Black backgrounds) for transparent optical displays.
* **[Typography](https://developer.android.com/design/ui/ai-glasses/styles/typography)**
    * Recommended font sizes to ensure readability while moving (minimums typically ~24sp).
* **[Components](https://developer.android.com/design/ui/ai-glasses/components/overview)**
    * Standard UI elements like Cards, Lists, and Buttons optimized for glass.
* [Glimmer](https://developer.android.com/develop/xr/jetpack-xr-sdk/jetpack-compose-glimmer)*

## 🤖 AI Integration
* **[AI Patterns](https://developer.android.com/design/ui/ai-glasses/guides/interaction/ai-patterns)**
    * How to display "Thinking" states and stream multimodal data (Camera/Mic) to Gemini.
* **[Permissions](https://developer.android.com/design/ui/ai-glasses/guides/interaction/permissions)**
    * Handling privacy-sensitive sensors (Camera/Microphone) transparently.

## 🛠 Tools & Samples
* **[Android XR SDK](https://developer.android.com/xr)** (General XR Hub)
* **[Gemini Multimodal Live Templates](https://github.com/android/generative-ai-android)** (GitHub)
    * Starter code for streaming video/audio to Gemini (often serves as the base "Glass" example).

---
*Note: This platform relies heavily on **Standard Android APIs** (Jetpack Compose, Activity, KeyEvent), but interaction patterns differ significantly from mobile.*