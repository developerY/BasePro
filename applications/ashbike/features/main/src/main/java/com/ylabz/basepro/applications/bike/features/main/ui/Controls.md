Yes—that’s exactly how it works:

| Current State    | Start/Pause Click            | Stop Click                       |
|------------------|------------------------------|----------------------------------|
| **NotStarted**   | → Riding (start timer)       | → Ended & saved (empty ride)     |
| **Riding**       | → Paused (pause timer)       | → Ended & saved (full ride)      |
| **Paused**       | → Riding (resume timer)      | → Ended & saved (partial ride)   |
| **Ended**        | → Riding (fresh new ride)    | → Ended & saved (no change)      |

- **Start/Pause** toggles between **Riding** and **Paused** (or starts a new ride if you press it after the ride has ended).
- **Stop** always ends the ride and immediately saves it, then resets the live-tracking fields so you’re back in **NotStarted** state.

With this setup, users can:
1. **Start** a ride → it begins tracking.
2. **Pause** (via the same button) → it stops the timer but keeps the partial track.
3. **Resume** (same button again) → it continues from where you left off.
4. **Stop** (the “Save” button) at any time → it marks the ride ended, persists it, and resets the UI for the next ride.

That covers all the “start → pause or done” and “pause → continue or done” flows you described.