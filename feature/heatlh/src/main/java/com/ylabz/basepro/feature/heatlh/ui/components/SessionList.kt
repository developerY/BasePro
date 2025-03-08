package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.health.connect.client.records.ExerciseSessionRecord
import java.time.ZonedDateTime


@Composable
fun SessionList(
    modifier: Modifier = Modifier,
    sessionsList: List<ExerciseSessionRecord>,
    navTo: (String) -> Unit,
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
                    navTo("exercise_session_detail/$uid")
                },
            )
        }
    }
}
