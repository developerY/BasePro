package com.ylabz.basepro.feature.home.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsScreen(
    apps: List<AppModel>,          // your data model for each app
    onLaunchApp: (AppModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(apps) { index, app ->
            FlippableCard( front = {
                GradientApplicationCard(
                    title = app.name,
                    description = "",
                    icon = app.icon,                  // Pass the icon from the model
                    onLaunch = {} //  onLaunchApp(app)
                )
            },
                back = {
                    GradientApplicationCard(
                        title = app.name,
                        description = app.description,
                        icon = app.icon,                  // Pass the icon from the model
                        onLaunch = {} //  onLaunchApp(app)
                    )
                }
            )
        }
    }
}

