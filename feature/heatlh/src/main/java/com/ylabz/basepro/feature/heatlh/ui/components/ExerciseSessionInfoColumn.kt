package com.ylabz.basepro.feature.heatlh.ui.components

////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.ZonedDateTime

@Composable
fun ExerciseSessionInfoColumn(
    start: ZonedDateTime,
    end: ZonedDateTime,
    uid: String,
    name: String,
    onClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier.clickable {
            onClick(uid)
        }
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = "${start.toLocalTime()} - ${end.toLocalTime()}",
            style = MaterialTheme.typography.titleSmall
        )
        Text(name)
        Text(uid)
    }
}

/*
@Preview
@Composable
fun ExerciseSessionInfoColumnPreview() {
    MaterialTheme {
        ExerciseSessionInfoColumn(
            ZonedDateTime.now().minusMinutes(30),
            ZonedDateTime.now(),
            UUID.randomUUID().toString(),
            "Running"
        )
    }
}
*/