package com.ylabz.basepro.ashbike.glass.ui.components

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import com.ylabz.basepro.ashbike.glass.MainActivity
import com.ylabz.basepro.ashbike.glass.R

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalProjectedApi::class)
@Composable
fun ConnectionScreen() {
    val context = LocalContext.current
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.hello_ai_glasses),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            val scope = rememberCoroutineScope()
            val isGlassesConnected by ProjectedContext.isProjectedDeviceConnected(
                context,
                scope.coroutineContext
            ).collectAsStateWithLifecycle(initialValue = false)
            Button(
                onClick = {
                    val options = ProjectedContext.createProjectedActivityOptions(context)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent, options.toBundle())
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGlassesConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                enabled = isGlassesConnected
            ) {
                Text(
                    text = stringResource(id = R.string.launch),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.status_prefix) + if (isGlassesConnected) stringResource(
                    id = R.string.status_connected
                ) else stringResource(id = R.string.status_disconnected),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}