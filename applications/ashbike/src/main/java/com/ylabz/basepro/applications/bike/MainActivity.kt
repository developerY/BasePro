package com.ylabz.basepro.applications.bike

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel
import com.ylabz.basepro.applications.bike.ui.navigation.root.RootNavGraph
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    // --- SOLUTION: Move the permission launcher here ---
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permission Granted")
                // Permission is granted. The content will recompose automatically.
            } else {
                Log.d("MainActivity", "Permission Denied")
                // Handle the case where the user denies the permission.
                // You might want to show a message explaining why the permission is needed.
            }
            // Re-render the content after the permission result is received.
            renderContent()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- SOLUTION: Check for permission here, before setting content ---
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, render the main UI
                renderContent()
            }
            else -> {
                // Request permission. The result will be handled by the launcher,
                // which will then call renderContent().
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun renderContent() {
        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            AshBikeTheme(theme = theme) {
                // Now, the UI just needs to display the content, not manage permissions.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // We can check one last time here to show a message if permission is still denied.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        RootNavGraph(navController = rememberNavController())
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Location permission is required to use this app.")
                        }
                    }
                }
            }
        }
    }
}

// You can remove the AppUI composable as its logic is now in renderContent()

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello Bike App $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AshBikeTheme {
        Greeting("Android")
    }
}