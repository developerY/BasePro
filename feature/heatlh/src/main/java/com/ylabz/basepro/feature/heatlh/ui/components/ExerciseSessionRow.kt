package com.ylabz.basepro.feature.heatlh.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// //import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Creates a row to represent an [ExerciseSessionRecord]
 */
@Composable
fun ExerciseSessionRow(
    startTime: ZonedDateTime,
    endTime: ZonedDateTime,
    uid: String,
    title: String,
    onDetailsClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onDetailsClick(uid) },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start: ${startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "End: ${endTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/*
@Preview
@Composable
fun ExerciseSessionRowPreview() {
    MaterialTheme {
        ExerciseSessionRow(
            ZonedDateTime.now().minusMinutes(30),
            ZonedDateTime.now(),
            UUID.randomUUID().toString(),
            "Running",
            onDetailsClick = TODO()
        )
    }
}
*/