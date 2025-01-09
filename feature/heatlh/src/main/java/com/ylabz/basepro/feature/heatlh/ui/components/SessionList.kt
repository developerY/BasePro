package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.ZonedDateTime
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.navigation.compose.rememberNavController
import java.time.Instant
import java.time.ZoneOffset


@Composable
fun SessionList(
    modifier: Modifier = Modifier,
    sessionsList: List<ExerciseSessionRecord>,
    navController: NavController
) {
    // Display session list with LazyColumn for a good scrollable UI
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(sessionsList) { session ->
            ExerciseSessionRow(
                ZonedDateTime.ofInstant(session.startTime, session.startZoneOffset),
                ZonedDateTime.ofInstant(session.endTime, session.endZoneOffset),
                session.metadata.id,
                session.title ?: "no title", //stringResource(R.string.no_title),
                onDetailsClick = { uid ->
                    navController.navigate("exercise_session_detail/$uid")
                },
            )
        }
    }
}
