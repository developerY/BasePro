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
        ProjectionState.setProjecting(true)
        // Do things to make the user aware that this activity is active (for
        // example, play audio), when the display state is off
        // 2. User Feedback: Confirm the system is ready/visible
        // This runs on first launch AND when the screen wakes up from sleep
        // audioInterface.speak(getString(R.string.hello_ai_glasses))
    }

    override fun onStop() {
        super.onStop()
        ProjectionState.setProjecting(false)
        //Stop all the data source access
        // 3. Battery Saving: If you had heavy sensors (like Camera), pause them here.
        // For simple data syncing, you can often leave it running.
        // If AudioInterface is an Observer, it might auto-mute here.
    }

    override fun onDestroy() {
        super.onDestroy()
        // 2a. Tell the phone we are gone.
        // 4. Cleanup: Tell the phone the connection is fully closed
        // Best Practice: Use the Process scope.
        // This survives the Activity destruction but is tied to the App lifecycle.
    }
}