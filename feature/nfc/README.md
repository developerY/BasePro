Here's a concise summary of the recommended architecture for your multi-module NFC feature:

1. **NFC Module:**
    - Contains its own **Route Composable**, **UIState/Events**, and a dedicated **NfcViewModel**.
    - The repository in this module handles all NFC processing (e.g., reading the tag via NDEF).

2. **Main Activity:**
    - Remains in the main module and is responsible for receiving NFC intents in `onNewIntent()`.
    - It forwards any NFC tag received (via `onNewIntent`) to the NFC module’s ViewModel.
    - You can do this via Hilt: either by using an **EntryPoint** to get the NFC ViewModel or by having your NFC route composable in the navigation graph and ensuring that the NFC intent is forwarded to that same instance.

3. **Integration via Navigation:**
    - Your NFC module exposes a **route composable** (e.g. `NfcReaderRoute()`) that obtains the NFC ViewModel with `hiltViewModel()` and drives the UI solely based on its state and events.
    - The main module’s NavGraph includes this route just like your other modules.
    - MainActivity calls `setContent { NavHost(...) }` and in `onNewIntent()`, it simply passes the tag to the NFC ViewModel (or via an entry point), so the NFC module is fully in charge of its UI logic.

This pattern keeps your NFC logic self-contained in its module while letting MainActivity do what it must (receiving NFC intents). The ViewModel in the NFC module then drives the UI using its UIState/Events, making your composables "dumb" and easy to test.

This approach adheres to Modern Android Architecture principles, maintains separation of concerns, and works well in a multi-module setup.