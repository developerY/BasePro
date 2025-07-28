package com.ylabz.basepro.applications.bike

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    private var showRationaleDialog by mutableStateOf(false)

    // --- SOLUTION: Move the permission launcher here ---
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permission Granted")
                // Permission is granted. The content will recompose automatically.
            } else {
                Log.d("MainActivity", "Permission Denied")
                // If permission is denied, renderContent will show the PermissionDeniedScreen
            }
            // Re-render the content after the permission result is received.
            // This is important because even if rationale was shown, the permission state might have changed.
            renderContent()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestLocationPermission()
    }

    private fun checkAndRequestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Permission already granted.")
                renderContent()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("MainActivity", "Showing permission rationale.")
                showRationaleDialog = true // Trigger the rationale dialog
                // renderContent() will be called when the dialog is dismissed or action is taken
                // We also need to render content here to show the dialog itself.
                renderContent()
            }
            else -> {
                Log.d("MainActivity", "Requesting permission (first time or denied permanently).")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                // renderContent() will be called by the launcher's callback.
                // If the user previously selected "Don't ask again", the system won't show a dialog,
                // and isGranted will be false. renderContent() will then show PermissionDeniedScreen.
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
                    // Check if the rationale dialog should be shown
                    if (showRationaleDialog) {
                        LocationPermissionRationaleDialog(
                            onConfirm = {
                                showRationaleDialog = false // Dismiss dialog
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            onDismiss = {
                                showRationaleDialog = false // Dismiss dialog
                                renderContent() // Re-render to show PermissionDeniedScreen if needed
                            }
                        )
                    }

                    // Determine content based on permission status
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        RootNavGraph(navController = rememberNavController())
                    } else {
                        // If rationale is not being shown, and permission is not granted,
                        // show the denied screen.
                        if (!showRationaleDialog) {
                            PermissionDeniedScreen(
                                onGoToSettings = { openAppSettings() },
                                onTryAgain = {
                                    // Re-trigger the permission check and request flow.
                                    // This allows the user to try again if they didn't deny permanently.
                                    checkAndRequestLocationPermission()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LocationPermissionRationaleDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Location Permission Needed") },
            text = { Text("This app needs access to your location to track your bike rides and provide location-based features. Please grant the permission to continue.") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Later")
                }
            }
        )
    }

    @Composable
    fun PermissionDeniedScreen(onGoToSettings: () -> Unit, onTryAgain: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Location permission is required to use this app's core features.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Please grant the location permission in the app settings, or try requesting the permission again.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onGoToSettings) {
                    Text("Open App Settings")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onTryAgain) {
                    Text("Try Again to Grant Permission")
                }
            }
        }
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }
}

// Dummy Greeting and Preview for completeness if needed by IDE
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
        // You could preview PermissionDeniedScreen here for example
        // MainActivity().PermissionDeniedScreen({}, {}) // MainActivity instance needed for context in preview
    }
}
