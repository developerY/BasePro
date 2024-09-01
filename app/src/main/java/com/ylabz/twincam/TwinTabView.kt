package com.ylabz.twincam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.twincam.cam.ui.CamUIRoute
import com.ylabz.twincam.settings.ui.SettingsUiRoute

@Composable
fun TwinTabView(
    modifier: Modifier = Modifier,
) {
    val tabTitles = listOf("Camera", "Settings")
    var selectedTabIndex by remember { mutableStateOf(0) }

            Column(modifier = modifier) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add some space between tabs and content

                when (selectedTabIndex) {
                    0 -> CamUIRoute(modifier = modifier)
                    1 -> SettingsUiRoute(modifier = modifier)
                }
            }

}
