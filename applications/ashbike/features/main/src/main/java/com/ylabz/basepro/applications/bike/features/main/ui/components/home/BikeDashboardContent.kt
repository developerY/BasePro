package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.SlidableGoogleMap
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSectionType
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeDashboard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatsRow
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import com.ylabz.basepro.core.ui.NavigationCommand

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (NavigationCommand) -> Unit,
    coffeeShops: List<BusinessInfo>, // Added parameter
    placeName: String?, // Added parameter
    onFindCafes: () -> Unit // Added parameter
) {

    var isMapPanelVisible by rememberSaveable { mutableStateOf(false) }
    val bikeRideInfo = uiState.bikeData
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    bikeRideInfo.isBikeConnected
    bikeRideInfo.batteryLevel
    val rideState = bikeRideInfo.rideState
    val currRiding = rideState == RideState.Riding

    val containerColor =
        if (currRiding) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (currRiding) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier.fillMaxSize()) { // Wrap content in a Box
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpeedAndProgressCard(
                uiState = uiState,
                onBikeEvent = onBikeEvent,
                navTo = navTo,
                containerColor = containerColor,
                contentColor = contentColor,
                onShowMapPanel = { isMapPanelVisible = true } // Pass callback to show map
            )

            StatsRow(
                uiState = uiState,
            )

            StatsSection(
                uiState = uiState,
                sectionType = StatsSectionType.HEALTH,
                onEvent = onBikeEvent
            )

            var expanded by rememberSaveable { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.feature_main_ebike_stats_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (expanded) stringResource(R.string.feature_main_action_collapse) else stringResource(
                                R.string.feature_main_action_expand
                            ),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            /*StatsSection(
                                uiState = uiState,
                                sectionType = StatsSectionType.EBIKE,
                                onEvent = onBikeEvent
                            )*/

                            BikeDashboard(
                                uiState = uiState,
                                onBikeEvent = onBikeEvent,
                                // navTo = navTo,
                            )
                        }
                    }
                }
            }
        }

        // Slidable Google Map Panel, aligned to the bottom of the Box
        AnimatedVisibility(
            visible = isMapPanelVisible,
            modifier = Modifier.align(Alignment.BottomCenter), // Ensures it's at the bottom of the Box
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + fadeOut()
        ) {
            SlidableGoogleMap(
                uiState = uiState,
                onClose = { isMapPanelVisible = false },
                showMapContent = false, // Set to false to show green screen fallback
                coffeeShops = coffeeShops, // Passed parameter
                placeName = placeName, // Passed parameter
                onFindCafes = onFindCafes // Passed parameter
            )
        }
    }
}
