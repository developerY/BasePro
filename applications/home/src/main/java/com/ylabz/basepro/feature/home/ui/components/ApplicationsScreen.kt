package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsScreen(
    apps: List<AppModel>,          // your data model for each app
    navTo: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(apps) { index, app ->
            FlippableCard(
                front = {
                    GradientApplicationCard(
                        appModel = app,             // Pass the icon from the model
                        navTo = null //  onLaunchApp(app)
                    )
                },
                back = {
                    GradientApplicationCard(
                        appModel = app,              // Pass the icon from the model
                        navTo = navTo //  onLaunchApp(app)
                    )
                }
            )
        }
    }
}

val appList = listOf(
    AppModel("Bike", "Electric Bike Application", Icons.Filled.ElectricBike, ""),
    AppModel("Camera", "Camera functionality", Icons.Filled.CameraAlt, ""),
    AppModel("Maps", "Maps functionality", Icons.Filled.Map, ""),
    AppModel("Places", "Places functionality", Icons.Filled.Place, ""),
    AppModel("Health", "Health functionality", Icons.Filled.HealthAndSafety, ""),
    AppModel("BLE", "Bluetooth Low Energy", Icons.Filled.Bluetooth, "")
)

/*@Preview(showBackground = true)
@Composable
fun ApplicationsScreenPreview() {
    ApplicationsScreen(apps = appList, navTo = {})
}


 */
