package com.ylabz.basepro.feature.wearos.home.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
//import androidx.compose.material.icons.Icons
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Icon
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun GreetingScreen(
    navController: NavController,
    greetingName: String
) {
    val listState = rememberScalingLazyListState()

    AppScaffold {
        // TODO: Swap to ScalingLazyColumnState
        /*
         * Specifying the types of items that appear at the start and end of the list ensures that the
         * appropriate padding is used.
         */
        val listState = rememberResponsiveColumnState(
            contentPadding = ScalingLazyColumnDefaults.padding(
                first = ItemType.SingleButton,
                last = ItemType.Chip,
            ),
        )
        // Modifiers used by our Wear composables.
        val contentModifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        val iconModifier = Modifier.size(24.dp).wrapContentSize(align = Alignment.Center)

        /* *************************** Part 4: Wear OS Scaffold *************************** */
        // TODO (Start): Create a ScreenScaffold (Wear Version)
        /*
         * [Horologist] ScreenScaffold is used in conjunction with AppScaffold and adds a
         * position indicator to the list by default.
         * */
        ScreenScaffold(
            scrollState = listState,
        ) {
            /* *************************** Part 3: ScalingLazyColumn *************************** */
            // TODO: Swap a ScalingLazyColumn (Wear's version of LazyColumn)
            /*
             * [Horologist] ScalingLazyColumn applies padding for elements in the list to
             * make sure no elements are clipped on different screen sizes.
             * */
            ScalingLazyColumn(
                columnState = listState,
            ) {
                /* ******************* Part 1: Simple composables ******************* */
                item {
                    Button(
                        onClick = {
                            navController.navigate(WearScreen.Health.route)
                        }
                    ) {
                        Text("Go to Health Screen")
                    }
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate(WearScreen.Sleep.route)
                        }
                    ) {
                        Text("Go to Sleep Screen")
                    }
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate(WearScreen.Drunk.route)
                        }
                    ) {
                        Text("Go to Drunk Screen")
                    }
                }
            }

            // TODO (End): Create a ScreenScaffold (Wear Version)
        }
        // TODO (End): Create a AppScaffold (Wear Version)
    }

}

// TODO: Create a Button Composable (with a Row to center)
@Composable
fun ButtonExample(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Button
        Button(
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize),
            onClick = { /* ... */ },
        ) {
            /*Icon(
                imageVector = Icons.Rounded.Phone,
                contentDescription = "triggers phone action",
                modifier = iconModifier,
            )*/
        }
    }
}