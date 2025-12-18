package com.ylabz.basepro.ashbike.mobile.features.glass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.xr.glimmer.GlimmerTheme
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassApp
import com.ylabz.basepro.core.data.repository.bike.BikeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint // <--- Required for Hilt injection
class GlassesMainActivity : ComponentActivity() {

    // Inject the shared repository instance
    @Inject lateinit var repository: BikeRepository
    private lateinit var audioInterface: AudioInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        audioInterface = AudioInterface(
            this,
            getString(R.string.hello_ai_glasses)
        )
        lifecycle.addObserver(audioInterface)

        // 1a. Session State: Tracks if the app process is alive
        // 1. LAUNCH COROUTINE: Set state to TRUE when app starts
        // We use lifecycleScope.launch because updateGlassConnectionState is a suspend function
        lifecycleScope.launch {
            repository.updateGlassConnectionState(true)
        }

        setContent {
            GlimmerTheme {
                GlassApp(
                    onClose = {

                        /*audioInterface.speak("Goodbye!")
                        // Delay slightly or ensure speak finishes if possible, then finish
                        finish()*/

                        // 2. LAUNCH COROUTINE: Handle the "Goodbye" delay
                        lifecycleScope.launch {
                            audioInterface.speak("Goodbye!")
                            delay(1500) // Wait for audio to finish (approx)
                            finish()    // Close the Activity
                        }
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Do things to make the user aware that this activity is active (for
        // example, play audio), when the display state is off
        // 2. User Feedback: Confirm the system is ready/visible
        // This runs on first launch AND when the screen wakes up from sleep
        // audioInterface.speak(getString(R.string.hello_ai_glasses))
    }

    override fun onStop() {
        super.onStop()
        //Stop all the data source access
        // 3. Battery Saving: If you had heavy sensors (like Camera), pause them here.
        // For simple data syncing, you can often leave it running.
        // If AudioInterface is an Observer, it might auto-mute here.
    }

    override fun onDestroy() {
        super.onDestroy()
        // 2a. Tell the phone we are gone.
        // 4. Cleanup: Tell the phone the connection is fully closed

        // 3. CLEANUP: Set state to FALSE when app is killed/closed
        // We use GlobalScope or a non-cancellable scope here because
        // lifecycleScope is cancelled immediately in onDestroy.
        // HOWEVER, since the repo is a Singleton, simply launching in
        // lifecycleScope might be too late if the repo is cleared.
        //
        // Better approach: Launch a cleanup job before super.onDestroy()
        // or rely on the socket disconnection if using Bluetooth.
        // For local simulation:

        // Note: In a real app, you might want to use ProcessLifecycleOwner
        // or a Service to guarantee this runs, but for this demo:
        kotlinx.coroutines.GlobalScope.launch {
            repository.updateGlassConnectionState(false)
        }
    }
}