package com.ylabz.basepro.feature.wearos.home.presentation

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.ylabz.basepro.feature.wearos.home.R
import com.ylabz.basepro.feature.wearos.home.navigation.GreetingScreen
import com.ylabz.basepro.feature.wearos.home.navigation.WearScreen

@Composable
fun WearHomeRoute(
    navController: NavController,
) {
    GreetingScreen(
        navController = navController,
        greetingName = "WearUser"
    )
}

@Composable
fun Greeting(
    navController: NavController,
    greetingName: String
) {

    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
    Column {
        Button(
            onClick = {
                navController.navigate(WearScreen.Health.route)
            }
        ) {
            Text("Go to Health Screen")
        }

        Button(
            onClick = {
                navController.navigate(WearScreen.Sleep.route)
            }
        ) {
            Text("Go to Sleep Screen")
        }

        Button(
            onClick = {
                navController.navigate(WearScreen.Drunk.route)
            }
        ) {
            Text("Go to Drunk Screen")
        }
    }
}
/*
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GreetingScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        Greeting(navController, "WearUser")
    }
}

 */
