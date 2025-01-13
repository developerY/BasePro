package com.ylabz.basepro.feature.wearos.home.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.*
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.ylabz.basepro.feature.wearos.home.navigation.WearRoute

@Composable
fun GreetingScreen(
    navController: NavController,
    greetingName: String
) {
    // Holds vertical scrolling state for the list
    val listState = rememberScalingLazyListState()


    Scaffold(
        timeText = {
            if (!scalingLazyListState.isScrollInProgress) {
                Text("This is the time Text")
            }
        },

        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) { paddingValues ->
        // A vertical scrolling list of items
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Inset for scaffold
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    text = "Hello $greetingName!"
                )
            }

            item {
                Chip(
                    onClick = {
                        navController.navigate(WearRoute.Health.route)
                    },
                    label = {
                        Text("Go to Health Screen")
                    }
                )
            }

            item {
                Chip(
                    onClick = {
                        navController.navigate(WearRoute.Sleep.route)
                    },
                    label = {
                        Text("Go to Sleep Watch Screen")
                    }
                )
            }
        }
    }
}
