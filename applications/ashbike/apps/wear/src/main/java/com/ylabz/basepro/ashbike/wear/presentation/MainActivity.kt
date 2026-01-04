package com.ylabz.basepro.ashbike.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ylabz.basepro.ashbike.wear.app.AshBikeApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the Splash Screen transition
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Set the base theme for the activity window
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            // We do NOT need to start the service here.
            // AshBikeApp -> WearBikeScreen handles the Service Binding
            // automatically once permissions are granted.

            // We also don't need a Box or TimeText here, because
            // AshBikeApp uses AppScaffold/ScreenScaffold which handles that.
            AshBikeApp()
        }
    }
}


/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

/*
@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}
*/
