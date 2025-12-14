package com.ylabz.basepro.ashbike.mobile.features.glass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.GlimmerTheme
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.GlassApp

class GlassesMainActivity : ComponentActivity() {
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
                GlassApp(onClose = {
                    audioInterface.speak("Goodbye!")
                    finish()
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Do things to make the user aware that this activity is active (for
        // example, play audio), when the display state is off
    }

    override fun onStop() {
        super.onStop()
        //Stop all the data source access
    }
}


@Preview(device = "id:ai_glasses_device", backgroundColor = 0x00FF00)
@Composable
fun DefaultPreview() {
    GlimmerTheme {
        GlassApp(onClose = {})
    }
}