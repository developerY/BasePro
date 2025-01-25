package com.ylabz.basepro.feature.alarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.core.model.alarm.ProAlarm
import com.ylabz.basepro.core.model.shotime.ShotimeSessionData
import kotlin.random.Random

@Composable
fun AlarmSuccessScreen(
    modifier: Modifier = Modifier,
    data: List<ShotimeSessionData>,
    onAddAlarmClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onToggleAlarm: (Int, Boolean) -> Unit, // Callback for toggling alarms
    onDebugLogClick: () -> Unit // Callback for debug logging
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.shot,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = item.isEnabled,
                        onCheckedChange = { isChecked ->
                            onToggleAlarm(item.id, isChecked)
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddAlarmClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alarm")
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Button(
                onClick = onDeleteAllClick,

            ) {
                Text("Delete All")
            }

            // Debug log button
            Button(
                onClick = onDebugLogClick,
            ) {
                Text(text = "Debug Log")
            }
        }
    }
}
