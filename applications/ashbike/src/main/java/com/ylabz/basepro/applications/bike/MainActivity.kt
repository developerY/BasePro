package com.ylabz.basepro.applications.bike

//import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // This state will be managed within the composable scope using rememberSaveable
    // The Activity will trigger actions that influence this state.
    private val permissionAction = mutableStateOf<PermissionAction?>(null)

    sealed class PermissionAction {
        object RequestPermission : PermissionAction()
        object ShowRationale : PermissionAction()
        object Granted : PermissionAction()
        object Denied : PermissionAction()
        object CheckPermission : PermissionAction() // Initial action
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permission Granted")
                permissionAction.value = PermissionAction.Granted
            } else {
                Log.d("MainActivity", "Permission Denied")
                permissionAction.value = PermissionAction.Denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the initial action to trigger the permission check flow within Compose
        if (savedInstanceState == null) { // Only on first creation
            permissionAction.value = PermissionAction.CheckPermission
        }

        setContent {
            val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
            var showRationaleDialogState by rememberSaveable { mutableStateOf(false) }
            var currentPermissionStatus by rememberSaveable {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                )
            }

            // Observe the permissionAction from the Activity
            // Using LaunchedEffect here to react to permissionAction changes from the Activity
            // This is a common pattern for bridging Activity logic with Composable state.
            val currentPermissionAction = permissionAction.value

            LaunchedEffect(currentPermissionAction) {
                when (currentPermissionAction) {
                    PermissionAction.CheckPermission -> {
                        when {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                currentPermissionStatus = true
                                permissionAction.value = PermissionAction.Granted // Move to granted state
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                                showRationaleDialogState = true
                                permissionAction.value = null // Consume action
                            }
                            else -> {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                permissionAction.value = null // Consume action
                            }
                        }
                    }
                    PermissionAction.RequestPermission -> { // Can be triggered from rationale dialog
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        permissionAction.value = null // Consume action
                    }
                    PermissionAction.Granted -> {
                        currentPermissionStatus = true
                        showRationaleDialogState = false
                        permissionAction.value = null // Consume action
                    }
                    PermissionAction.Denied -> {
                        currentPermissionStatus = false
                        showRationaleDialogState = false // Ensure rationale dialog is dismissed if it was part of denial
                        permissionAction.value = null // Consume action
                    }
                    PermissionAction.ShowRationale -> { // Can be explicitly set if needed
                        showRationaleDialogState = true
                        permissionAction.value = null
                    }
                    null -> { /* No action */ }
                }
            }


            AshBikeTheme(theme = theme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showRationaleDialogState) {
                        LocationPermissionRationaleDialog(
                            onConfirm = {
                                showRationaleDialogState = false
                                permissionAction.value = PermissionAction.RequestPermission
                            },
                            onDismiss = {
                                showRationaleDialogState = false
                                // If rationale dismissed, treat as denied for current UI update
                                permissionAction.value = PermissionAction.Denied
                            }
                        )
                    } else if (currentPermissionStatus) {
                        RootNavGraph(navController = rememberNavController())
                    } else {
                        // This screen is shown if currentPermissionStatus is false AND rationale is not showing
                        PermissionDeniedScreen(
                            onGoToSettings = { openAppSettings() },
                            onTryAgain = {
                                permissionAction.value = PermissionAction.CheckPermission // Re-trigger the check
                            }
                        )
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

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AshBikeTheme {
        // MainActivity().PermissionDeniedScreen({}, {}) // Preview needs context or a static composable
    }
}



 */