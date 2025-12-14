package com.ylabz.basepro.ashbike.mobile.features.glass.ui

// Import your internal Activity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import com.ylabz.basepro.ashbike.mobile.features.glass.GlassesMainActivity
import com.ylabz.basepro.ashbike.mobile.features.glass.R

//@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("NewApi")
@OptIn(ExperimentalProjectedApi::class)
@Composable
fun LaunchGlassButton(
    modifier: Modifier = Modifier
) {
    // 1. Safety Check: If not Android 15 (Vanilla Ice Cream), don't render anything
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) return

    val context = LocalContext.current

    // 1. Observe connection (so button disables if you unplug)
    val scope = rememberCoroutineScope()

    // 2. Observe Connection State
    // We observe this using the XR library to react instantly to plug/unplug events
    val isGlassesConnected by remember(context, scope) {
        ProjectedContext.isProjectedDeviceConnected(context, scope.coroutineContext)
    }.collectAsStateWithLifecycle(initialValue = false)

    // 3. Conditional Rendering
    // The button is only visible when the hardware is detected
    if (isGlassesConnected) {
        Button(
            onClick = {
                // --- THIS IS THE "MAKE IT WORK" PART ---

                // A. Create the "Projection" options
                // This bundle contains the instructions to target the external display
                val options = ProjectedContext.createProjectedActivityOptions(context)

                // B. Create the Intent for your Glass Activity
                val intent = Intent(context, GlassesMainActivity::class.java)

                // C. Launch!
                // The 'options.toBundle()' is what redirects it to the glasses
                context.startActivity(intent, options.toBundle())
            },
            modifier = modifier,
            // Use a distinct color so the user knows this is a "special" action
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            // Ensure you have this string in your library's strings.xml
            Text(stringResource(R.string.launch_glass_mode))
        }
    }
}