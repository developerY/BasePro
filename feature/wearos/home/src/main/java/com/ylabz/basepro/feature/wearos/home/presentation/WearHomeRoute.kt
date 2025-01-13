package com.ylabz.basepro.feature.wearos.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.ylabz.basepro.feature.wearos.health.ui.HealthViewModel
import com.ylabz.basepro.feature.wearos.home.R
import com.ylabz.basepro.feature.wearos.home.navigation.WearRoute
import com.ylabz.basepro.feature.wearos.home.presentation.theme.BaseProTheme

@Composable
fun WearHomeRoute(
    navController: NavController,
) {
    WearApp("Android", navController)
}

@Composable
fun WearApp(
    greetingName: String,
    navController: NavController
) {
    BaseProTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(
                navController = navController,
                greetingName = greetingName
            )
        }
    }
}

@Composable
fun Greeting(
    navController: NavController,
    greetingName: String) {

    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )

    Button(
        onClick = {
            navController.navigate(WearRoute.Health.route)
        }
    ) {
        androidx.wear.compose.material.Text("Go to Health Screen")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GreetingScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        GreetingScreen(navController, "WearUser")
    }
}
