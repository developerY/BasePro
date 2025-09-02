package com.ylabz.basepro.applications.bike.features.trips.ui.components

// //import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.R
import com.ylabz.basepro.applications.bike.features.trips.R.string.feature_trips_elevation_profile_title
import com.ylabz.basepro.applications.bike.features.trips.ui.components.maps.ElevationProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElevationProfileSection(
    points: List<LatLngWithElev>,
    //locations: List<RideLocationEntity>,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(feature_trips_elevation_profile_title),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) stringResource(R.string.action_hide) else stringResource(
                        R.string.action_show
                    )
                )
            }

            HorizontalDivider()

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                // your existing ElevationProfile
                ElevationProfile(
                    points = points,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun ElevationProfileSectionPreview() {
    val points = listOf(
        LatLngWithElev(LatLng(0.0, 0.0),  10f),
        LatLngWithElev(LatLng(0.0, 0.0),  50f),
        LatLngWithElev(LatLng(0.0, 0.0),  30f),
        LatLngWithElev(LatLng(0.0, 0.0), 100f),
        LatLngWithElev(LatLng(0.0, 0.0),  20f),
    )

    ElevationProfileSection(points = points)
}
*/
