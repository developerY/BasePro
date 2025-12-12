package com.ylabz.basepro.ashbike.glass.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import com.ylabz.basepro.ashbike.glass.R


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
                HomeScreen(onClose = {
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

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onClose: () -> Unit) {
    Box(
        modifier = modifier
            .surface(focusable = false).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            title = { Text(stringResource(id = R.string.app_name)) },
            action = {
                Button(onClick = {
                    onClose()
                }) {
                    Text(stringResource(id = R.string.close))
                }
            }
        ) {
            Text(stringResource(id = R.string.hello_ai_glasses))
        }
    }
}

@Preview(device = "id:ai_glasses_device", backgroundColor = 0x00FF00)
@Composable
fun DefaultPreview() {
    GlimmerTheme {
        HomeScreen(onClose = {})
    }
}